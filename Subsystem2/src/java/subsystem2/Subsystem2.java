/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package subsystem2;

import entities.Basket;
import entities.BasketItem;
import entities.BasketItemPK;
import entities.Category;
import entities.Item;
import entities.User;
import entities.WishList;
import entities.WishListItem;
import entities.WishListItemPK;
import java.util.Date;
import java.util.List;
import javax.annotation.Resource;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Queue;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 *
 * @author lukapantovic
 */

public class Subsystem2 {

    static EntityManagerFactory emf = Persistence.createEntityManagerFactory("Subsystem2PU");

    @Resource(lookup = "jms/ConnectionFactory")
    private static ConnectionFactory connectionFactory;

    @Resource(lookup = "jms/UserTopic")
    private static Topic topicUser;

    @Resource(lookup = "jms/PaymentTopic")
    private static Topic topicPayment;

    @Resource(lookup = "jms/Subsystem2Queue")
    private static Queue queue2;

    private void respondToCentral(MapMessage originalMessage, String text) {
        try (JMSContext context = connectionFactory.createContext("admin", "admin")) {
            Destination replyTo = originalMessage.getJMSReplyTo();
            if (replyTo != null) {
                TextMessage response = context.createTextMessage(text);
                response.setJMSCorrelationID(originalMessage.getJMSMessageID());
                context.createProducer().send(replyTo, response);
            }
        } catch (JMSException e) {
            System.out.println("Error sending response: " + e.getMessage());
        }
    }

    public void request6(MapMessage mes) throws JMSException {
        String name = mes.getString("name");
        Object idParObj = mes.getObject("idPar");
        int idPar = Integer.parseInt(idParObj.toString());
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Category cat = new Category();
            cat.setName(name);
            if (idPar > 0) {
                cat.setIdPar(idPar);
            } else {
                cat.setIdPar(null);
            }
            em.persist(cat);
            em.getTransaction().commit();
            respondToCentral(mes, "Category successfully created: " + name);
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            respondToCentral(mes, "Error creating category.");
        } finally { em.close(); }
    }

    public void request7(MapMessage mes) throws JMSException {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Item item = new Item();
            item.setName(mes.getString("name"));
            item.setDescription(mes.getString("description"));
            item.setPrice(mes.getInt("price"));
            item.setDiscount(mes.getInt("discount"));

            Category cat = em.find(Category.class, mes.getInt("idC"));
            User creator = em.find(User.class, mes.getInt("idCrea"));

            if (cat == null || creator == null) {
                respondToCentral(mes, "Error: Category or creator do not exist.");
                em.getTransaction().rollback();
                return;
            }
            item.setIdC(cat);
            item.setIdCrea(creator);
            em.persist(item);
            em.getTransaction().commit();
            respondToCentral(mes, "Item successfully created: " + item.getName());
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            respondToCentral(mes, "Error creating item.");
        } finally { em.close(); }
    }

    public void request8(MapMessage mes) throws JMSException {
        int idI = mes.getInt("idI");
        int newPrice = mes.getInt("price");
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Item item = em.find(Item.class, idI);
            if (item != null) {
                item.setPrice(newPrice);
                em.getTransaction().commit();
                respondToCentral(mes, "Price of item " + idI + " successfully updated.");
            } else {
                respondToCentral(mes, "Item not found.");
            }
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            respondToCentral(mes, "Error updating price.");
        } finally { em.close(); }
    }

    public void request9(MapMessage mes) throws JMSException {
        int idI = mes.getInt("idI");
        int discount = mes.getInt("discount");
        if (discount >= 100 || discount < 0) {
            respondToCentral(mes, "Invalid discount percentage.");
            return;
        }
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Item item = em.find(Item.class, idI);
            if (item != null) {
                item.setDiscount(discount);
                em.getTransaction().commit();
                respondToCentral(mes, "Discount successfully set to " + discount + "%");
            } else {
                respondToCentral(mes, "Item not found.");
            }
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            respondToCentral(mes, "Error setting discount.");
        } finally { em.close(); }
    }

    public void request10(MapMessage mes) throws JMSException {
        int idU = mes.getInt("idU");
        int idI = mes.getInt("idI");
        int amount = mes.getInt("amount");
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Basket basket = em.find(Basket.class, idU);
            Item item = em.find(Item.class, idI);
            if (basket == null || item == null) {
                respondToCentral(mes, "Error: Basket or item do not exist.");
                em.getTransaction().rollback();
                return;
            }
            BasketItemPK biPK = new BasketItemPK(idI, basket.getIdB());
            BasketItem bi = em.find(BasketItem.class, biPK);
            if (bi == null) {
                bi = new BasketItem(idI, basket.getIdB());
                bi.setAmount(amount);
                em.persist(bi);
            } else {
                bi.setAmount(bi.getAmount() + amount);
            }
            basket.setValue(basket.getValue() + item.getPrice() * amount);
            em.getTransaction().commit();
            respondToCentral(mes, "Item added to basket. New basket value: " + basket.getValue());
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            respondToCentral(mes, "Error adding item to basket.");
        } finally { em.close(); }
    }

    public void request11(MapMessage mes) throws JMSException {
        int idU = mes.getInt("idU");
        int idI = mes.getInt("idI");
        int amount = mes.getInt("amount");
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Basket basket = em.find(Basket.class, idU);
            Item item = em.find(Item.class, idI);
            if (basket == null || item == null) {
                respondToCentral(mes, "Error: Basket or item do not exist.");
                em.getTransaction().rollback();
                return;
            }
            BasketItemPK biPK = new BasketItemPK(idI, basket.getIdB());
            BasketItem bi = em.find(BasketItem.class, biPK);
            if (bi != null) {
                int toRemove = Math.min(bi.getAmount(), amount);
                int newAmount = bi.getAmount() - toRemove;
                if (newAmount <= 0) em.remove(bi);
                else bi.setAmount(newAmount);

                basket.setValue(Math.max(0, basket.getValue() - (item.getPrice() * toRemove)));
                em.getTransaction().commit();
                respondToCentral(mes, "Removed " + toRemove + " unit(s) from basket.");
            } else {
                respondToCentral(mes, "Item is not in the basket.");
            }
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            respondToCentral(mes, "Error removing item from basket.");
        } finally { em.close(); }
    }

    public void request12(MapMessage mes) throws JMSException {
        int idU = mes.getInt("idU");
        int idI = mes.getInt("idI");
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            WishList wl = em.createQuery("SELECT w FROM WishList w WHERE w.idU.idU = :idU", WishList.class)
                            .setParameter("idU", idU).getSingleResult();
            Item item = em.find(Item.class, idI);

            WishListItemPK pk = new WishListItemPK(wl.getIdW(), idI);
            if (em.find(WishListItem.class, pk) == null) {
                WishListItem wli = new WishListItem(wl.getIdW(), idI);
                wli.setTime(java.time.LocalDateTime.now().toString());
                em.persist(wli);
                em.getTransaction().commit();
                respondToCentral(mes, "Item added to wish list.");
            } else {
                respondToCentral(mes, "Item is already in the wish list.");
                em.getTransaction().rollback();
            }
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            respondToCentral(mes, "Error adding to wish list.");
        } finally { em.close(); }
    }

    public void request13(MapMessage mes) throws JMSException {
        int idU = mes.getInt("idU");
        int idI = mes.getInt("idI");
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            WishList wl = em.createQuery("SELECT w FROM WishList w WHERE w.idU.idU = :idU", WishList.class)
                            .setParameter("idU", idU).getSingleResult();
            WishListItemPK pk = new WishListItemPK(wl.getIdW(), idI);
            WishListItem wli = em.find(WishListItem.class, pk);
            if (wli != null) {
                em.remove(wli);
                em.getTransaction().commit();
                respondToCentral(mes, "Item removed from wish list.");
            } else {
                respondToCentral(mes, "Item was not in the wish list.");
                em.getTransaction().rollback();
            }
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            respondToCentral(mes, "Error removing from wish list.");
        } finally { em.close(); }
    }

    public void request17(MapMessage mes) throws JMSException {
        EntityManager em = emf.createEntityManager();
        try {
            List<Category> categories = em.createQuery("SELECT c FROM Category c", Category.class).getResultList();
            StringBuilder sb = new StringBuilder("CATEGORIES:\n");
            for (Category c : categories) sb.append(c.getIdC()).append(": ").append(c.getName()).append("\n");
            respondToCentral(mes, sb.toString());
        } finally { em.close(); }
    }

    public void request18(MapMessage mes) throws JMSException {
        int idU = mes.getInt("idU");
        EntityManager em = emf.createEntityManager();
        try {
            List<Item> items = em.createQuery("SELECT i FROM Item i WHERE i.idCrea.idU = :id", Item.class)
                                 .setParameter("id", idU).getResultList();
            StringBuilder sb = new StringBuilder("MY ITEMS:\n");
            for (Item i : items) sb.append(i.getIdI()).append(": ").append(i.getName())
                                   .append(" (").append(i.getPrice()).append(" RSD)\n");
            respondToCentral(mes, sb.toString());
        } finally { em.close(); }
    }

    public void request19(MapMessage mes) throws JMSException {
        int idU = mes.getInt("idU");
        EntityManager em = emf.createEntityManager();
        try {
            List<BasketItem> items = em.createQuery(
                "SELECT bi FROM BasketItem bi WHERE bi.basket.idU.idU = :idU", BasketItem.class)
                .setParameter("idU", idU).getResultList();
            StringBuilder sb = new StringBuilder("BASKET CONTENTS:\n");
            for (BasketItem bi : items) sb.append(bi.getItem().getName()).append(" x").append(bi.getAmount()).append("\n");
            respondToCentral(mes, sb.toString());
        } finally { em.close(); }
    }

    public void request20(MapMessage mes) throws JMSException {
        int idU = mes.getInt("idU");
        EntityManager em = emf.createEntityManager();
        try {
            List<WishListItem> items = em.createQuery(
                "SELECT wli FROM WishListItem wli WHERE wli.wishList.idU.idU = :idU", WishListItem.class)
                .setParameter("idU", idU).getResultList();
            StringBuilder sb = new StringBuilder("WISH LIST:\n");
            for (WishListItem wli : items) sb.append("- ").append(wli.getItem().getName()).append("\n");
            respondToCentral(mes, sb.toString());
        } finally { em.close(); }
    }

    public void addUser(MapMessage mes) throws JMSException {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            User u = new User(mes.getInt("idU"), mes.getString("userName"));
            em.persist(u);

            Basket basket = new Basket();
            basket.setIdU(u);
            basket.setValue(0);
            em.persist(basket);

            WishList wl = new WishList();
            wl.setIdU(u);
            wl.setDate(new Date());
            em.persist(wl);

            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
        } finally { em.close(); }
    }

    private void processPayment(MapMessage mes) throws JMSException {
        int idU = mes.getInt("idU");
        EntityManager em = emf.createEntityManager();
        try {
            List<BasketItem> items = em.createQuery(
                "SELECT bi FROM BasketItem bi WHERE bi.basket.idU.idU = :idU", BasketItem.class)
                .setParameter("idU", idU).getResultList();

            StringBuilder sb = new StringBuilder();
            for (BasketItem bi : items) {
                sb.append(bi.getItem().getIdI()).append(":").append(bi.getAmount()).append(":")
                  .append(bi.getItem().getPrice()).append(":").append(bi.getItem().getIdCrea().getIdU()).append(";");
            }
            respondToCentral(mes, sb.toString());

            em.getTransaction().begin();
            em.createQuery("DELETE FROM BasketItem bi WHERE bi.basket.idU.idU = :idU")
              .setParameter("idU", idU).executeUpdate();
            em.createQuery("UPDATE Basket b SET b.value = 0 WHERE b.idU.idU = :idU")
              .setParameter("idU", idU).executeUpdate();
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
        } finally { em.close(); }
    }

    private void processMessage(MapMessage mes) {
        try {
            int request = mes.getInt("request");
            switch (request) {
                case 0:  addUser(mes);       break;
                case 6:  request6(mes);      break;
                case 7:  request7(mes);      break;
                case 8:  request8(mes);      break;
                case 9:  request9(mes);      break;
                case 10: request10(mes);     break;
                case 11: request11(mes);     break;
                case 12: request12(mes);     break;
                case 13: request13(mes);     break;
                case 14: processPayment(mes); break;
                case 17: request17(mes);     break;
                case 18: request18(mes);     break;
                case 19: request19(mes);     break;
                case 20: request20(mes);     break;
            }
        } catch (JMSException e) { e.printStackTrace(); }
    }

    public static void main(String[] args) {
        Subsystem2 s2 = new Subsystem2();

        java.util.Map<String, String> properties = new java.util.HashMap<>();
        properties.put("javax.persistence.jdbc.url",
            "jdbc:mysql://localhost:3306/Subsystem2?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC");
        emf = Persistence.createEntityManagerFactory("Subsystem2PU", properties);

        try {
            JMSContext context = connectionFactory.createContext("admin", "admin");

            context.setClientID("Subsystem2_ID");

            JMSConsumer consumerUser = context.createDurableConsumer(topicUser, "Subsystem2_UserSub");
            consumerUser.setMessageListener(m -> {
                if (m instanceof MapMessage) s2.processMessage((MapMessage) m);
            });

            JMSConsumer consumerPayment = context.createDurableConsumer(topicPayment, "Subsystem2_PaymentSub");
            consumerPayment.setMessageListener(m -> {
                if (m instanceof MapMessage) s2.processMessage((MapMessage) m);
            });

            JMSConsumer consumerQueue = context.createConsumer(queue2);
            consumerQueue.setMessageListener(m -> {
                if (m instanceof MapMessage) s2.processMessage((MapMessage) m);
            });

            System.out.println("Subsystem 2 is running...");

            while (true) {
                Thread.sleep(1000);
            }
        } catch (Exception e) {
            System.err.println("Error running subsystem 2: " + e.getMessage());
            e.printStackTrace();
        }
    }
}