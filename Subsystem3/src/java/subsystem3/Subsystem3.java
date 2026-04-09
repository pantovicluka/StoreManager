/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package subsystem3;

import entities.User;
import entities.Delivery;
import entities.Item;
import entities.Transaction;
import java.util.List;
import javax.annotation.Resource;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.TemporaryQueue;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 *
 * @author lukapantovic
 */

public class Subsystem3 {

    static EntityManagerFactory emf = Persistence.createEntityManagerFactory("Subsystem3PU");

    @Resource(lookup = "jms/ConnectionFactory")
    private static ConnectionFactory connectionFactory;

    @Resource(lookup = "jms/UserTopic")
    private static Topic topicUser;

    @Resource(lookup = "jms/PaymentTopic")
    private static Topic topicPayment;

    @Resource(lookup = "jms/Subsystem3Queue")
    private static Queue queue3;

    private void respondToSender(MapMessage originalMessage, String text) {
        try (JMSContext context = connectionFactory.createContext("admin", "admin")) {
            Destination replyTo = originalMessage.getJMSReplyTo();
            if (replyTo != null) {
                TextMessage response = context.createTextMessage(text);
                response.setJMSCorrelationID(originalMessage.getJMSMessageID());
                context.createProducer().send(replyTo, response);
            }
        } catch (JMSException e) {
            System.out.println("Error with response: " + e.getMessage());
        }
    }

    public void request14(MapMessage mes) throws JMSException {
        int idU = mes.getInt("idU");

        try (JMSContext context = connectionFactory.createContext("admin", "admin")) {
            TemporaryQueue tempQ = context.createTemporaryQueue();
            JMSConsumer consumerS2 = context.createConsumer(tempQ);

            MapMessage notification = context.createMapMessage();
            notification.setInt("request", 14);
            notification.setInt("idK", idU);
            notification.setJMSReplyTo(tempQ);

            context.createProducer().send(topicPayment, notification);

            Message m1 = consumerS2.receive(6000);

            if (!(m1 instanceof TextMessage)) {
                respondToSender(mes, "Error: Subsystem 2 did not respond on time.");
                return;
            }

            String dataFromBasket = ((TextMessage) m1).getText();
            if (dataFromBasket == null || dataFromBasket.isEmpty()) {
                respondToSender(mes, "Error: Basket is empty.");
                return;
            }

            EntityManager em = emf.createEntityManager();
            try {
                em.getTransaction().begin();

                User u = em.find(User.class, idU);
                if (u == null) {
                    respondToSender(mes, "Error: User does not exist in subsystem 3.");
                    em.getTransaction().rollback();
                    return;
                }

                Delivery d = new Delivery();
                d.setIdU(u);
                d.setValue(0);
                d.setTime(new java.util.Date());
                d.setAddress("Marnijeva 5");
                d.setCity("Beograd");
                em.persist(d);
                em.flush();

                double totalSum = 0;
                String[] dataS2Raw = dataFromBasket.split(";");

                for (String sRaw : dataS2Raw) {
                    if (sRaw.trim().isEmpty()) continue;
                    String[] dat = sRaw.split(":");

                    Item i = new Item();
                    i.setIdD(d);
                    i.setIdItem(Integer.parseInt(dat[0]));
                    i.setAmount(Integer.parseInt(dat[1]));
                    i.setPricePerUnit((int) Double.parseDouble(dat[2]));

                    User seller = em.find(User.class, Integer.parseInt(dat[3]));
                    i.setIdSell(seller);

                    totalSum += (i.getPricePerUnit() * i.getAmount());
                    em.persist(i);
                }

                d.setValue((int) totalSum);

                Transaction t = new Transaction();
                t.setIdD(d);
                t.setAmount((int) totalSum);
                t.setTime(new java.util.Date());
                em.persist(t);

                em.getTransaction().commit();
                respondToSender(mes, "Successful payment. Order ID: " + d.getIdD() + ", Sum: " + totalSum);

            } catch (Exception e) {
                if (em.getTransaction().isActive()) em.getTransaction().rollback();
                respondToSender(mes, "Error writing transaction into database.");
                e.printStackTrace();
            } finally {
                em.close();
            }
        }
    }

    public void request21(MapMessage mes) throws JMSException {
        int idU = mes.getInt("idU");
        EntityManager em = emf.createEntityManager();
        try {
            List<Delivery> list = em.createQuery(
                "SELECT d FROM Delivery d WHERE d.idU.idU = :id", Delivery.class)
                .setParameter("id", idU)
                .getResultList();
            StringBuilder sb = new StringBuilder("ORDERS FOR USER " + idU + ":\n");
            for (Delivery d : list) {
                sb.append("ID: ").append(d.getIdD())
                  .append(" | Value: ").append(d.getValue())
                  .append(" | Time: ").append(d.getTime())
                  .append("\n");
            }
            respondToSender(mes, sb.toString());
        } finally {
            em.close();
        }
    }

    public void request22(MapMessage mes) throws JMSException {
        EntityManager em = emf.createEntityManager();
        try {
            List<Delivery> list = em.createQuery(
                "SELECT d FROM Delivery d", Delivery.class)
                .getResultList();
            StringBuilder sb = new StringBuilder("ALL ORDERS IN SYSTEM:\n");
            for (Delivery d : list) {
                sb.append("ID: ").append(d.getIdD())
                  .append(" | User: ").append(d.getIdU().getUserName())
                  .append(" | Value: ").append(d.getValue())
                  .append("\n");
            }
            respondToSender(mes, sb.toString());
        } finally {
            em.close();
        }
    }

    public void request23(MapMessage mes) throws JMSException {
        EntityManager em = emf.createEntityManager();
        try {
            List<Transaction> list = em.createQuery(
                "SELECT t FROM Transaction t", Transaction.class)
                .getResultList();
            StringBuilder sb = new StringBuilder("ALL TRANSACTIONS:\n");
            for (Transaction t : list) {
                sb.append("ID: ").append(t.getIdT())
                  .append(" | Order: ").append(t.getIdD().getIdD())
                  .append(" | Amount: ").append(t.getAmount())
                  .append(" | Time: ").append(t.getTime())
                  .append("\n");
            }
            respondToSender(mes, sb.toString());
        } finally {
            em.close();
        }
    }

    public void addUser(MapMessage mes) throws JMSException {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            User u = new User(mes.getInt("idU"), mes.getString("userName"));
            em.persist(u);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
        } finally {
            em.close();
        }
    }

    private void processMessage(MapMessage mes) {
        try {
            int request = mes.getInt("request");
            switch (request) {
                case 0:  addUser(mes);    break;
                case 14: request14(mes);  break;
                case 21: request21(mes);  break;
                case 22: request22(mes);  break;
                case 23: request23(mes);  break;
            }
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Subsystem3 s3 = new Subsystem3();

        java.util.Map<String, String> properties = new java.util.HashMap<>();
        properties.put("javax.persistence.jdbc.url",
            "jdbc:mysql://localhost:3306/Subsystem3?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC");

        emf = Persistence.createEntityManagerFactory("Subsystem3PU", properties);

        try {
            JMSContext context = connectionFactory.createContext("admin", "admin");

            context.setClientID("Subsystem3_ID");

            JMSConsumer consumerUser  = context.createDurableConsumer(topicUser, "Subsystem3_SUB");
            JMSConsumer consumerQueue = context.createConsumer(queue3);

            consumerUser.setMessageListener(m -> {
                if (m instanceof MapMessage) s3.processMessage((MapMessage) m);
            });

            consumerQueue.setMessageListener(m -> {
                if (m instanceof MapMessage) s3.processMessage((MapMessage) m);
            });

            System.out.println("Subsystem 3 is running...");

            while (true) {
                Thread.sleep(1000);
            }
        } catch (Exception e) {
            System.err.println("Error running subsystem 3: " + e.getMessage());
            e.printStackTrace();
        }
    }
}