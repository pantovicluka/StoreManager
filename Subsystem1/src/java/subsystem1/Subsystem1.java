/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package subsystem1;

import entities.City;
import entities.Role;
import entities.User;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
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

/**
 *
 * @author lukapantovic
 */
public class Subsystem1 {

    static EntityManagerFactory emf = Persistence.createEntityManagerFactory("Subsystem1PU");

    @Resource(lookup = "jms/ConnectionFactory")
    private static ConnectionFactory connectionFactory;

    @Resource(lookup = "jms/UserTopic")
    private static Topic topicUser;

    @Resource(lookup = "jms/PaymentTopic")
    private static Topic topicPayment;

    @Resource(lookup = "jms/Subsystem1Queue")
    private static Queue queue1;

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

    public void request1(MapMessage mes) throws JMSException {
        String userName = mes.getString("userName");
        String password = mes.getString("password");

        EntityManager em = emf.createEntityManager();
        try {
            User u = em.createQuery(
                    "SELECT u FROM User u WHERE u.userName = :userName AND u.password = :password", User.class)
                    .setParameter("userName", userName)
                    .setParameter("password", password)
                    .getSingleResult();

            List<Integer> results = em.createNativeQuery(
                    "SELECT IdR FROM User_Role WHERE IdU = ?1 AND IdR = 1")
                    .setParameter(1, u.getIdU())
                    .getResultList();

            int roleId = results.isEmpty() ? 2 : 1;

            respondToCentral(mes, u.getIdU() + "," + roleId);

        } catch (NoResultException e) {
            respondToCentral(mes, "ERROR");
        } finally {
            em.close();
        }
    }

    public void request2(MapMessage mes) throws JMSException {
        String name = mes.getString("name");
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            City city = new City();
            city.setName(name);
            em.persist(city);
            em.getTransaction().commit();
            respondToCentral(mes, "City created successfully: " + name);
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            respondToCentral(mes, "Error creating city.");
        } finally {
            em.close();
        }
    }

    public void request3(MapMessage mes) throws JMSException {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            User user = new User();
            user.setUserName(mes.getString("userName"));
            user.setPassword(mes.getString("password"));
            user.setName(mes.getString("name"));
            user.setSurname(mes.getString("surname"));
            user.setAddress(mes.getString("address"));

            Object balanceObj = mes.getObject("balance");
            System.out.println("DEBUG balance: " + balanceObj);
            int balance = balanceObj != null ? Integer.parseInt(balanceObj.toString()) : 0;
            user.setBalance(new BigDecimal(balance));

            City city = em.find(City.class, mes.getInt("idC"));
            if (city == null) {
                respondToCentral(mes, "Error: City does not exist.");
                em.getTransaction().rollback();
                return;
            }
            user.setIdC(city);

            user.setRoleList(new ArrayList<>());
            Role role = em.find(Role.class, 3);
            if (role != null) {
                user.getRoleList().add(role);
            }
            em.persist(user);
            em.getTransaction().commit();

            sendUser(user.getIdU(), user.getUserName());

            respondToCentral(mes, "User created successfully: " + user.getUserName());
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            e.printStackTrace();
            respondToCentral(mes, "Error creating user: " + e.getMessage());
        } finally {
            em.close();
        }
    }

    public void request4(MapMessage mes) throws JMSException {
        Object idUObj = mes.getObject("idU");
        Object amountObj = mes.getObject("amount");

        int idU = Integer.parseInt(idUObj.toString());
        int amount = Integer.parseInt(amountObj.toString());

        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            User user = em.find(User.class, idU);
            if (user != null) {
                user.setBalance(user.getBalance().add(new BigDecimal(amount)));
                em.getTransaction().commit();
                respondToCentral(mes, "Funds added successfully. New balance: " + user.getBalance());
            } else {
                em.getTransaction().rollback();
                respondToCentral(mes, "User not found.");
            }
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            respondToCentral(mes, "Error adding funds.");
        } finally {
            em.close();
        }
    }

    public void request5(MapMessage mes) throws JMSException {
        EntityManager em = emf.createEntityManager();
        try {
            int idU = mes.getInt("idU");
            String newAddress = mes.getString("address");
            int idC = mes.getInt("idC");

            em.getTransaction().begin();

            User user = em.find(User.class, idU);
            if (user == null) {
                respondToCentral(mes, "Error: User with ID " + idU + " does not exist.");
                em.getTransaction().rollback();
                return;
            }

            City city = em.find(City.class, idC);
            if (city == null) {
                respondToCentral(mes, "Error: City with ID " + idC + " does not exist.");
                em.getTransaction().rollback();
                return;
            }

            user.setAddress(newAddress);
            user.setIdC(city);
            em.getTransaction().commit();

            respondToCentral(mes, "Address and city updated for user: " + user.getUserName());
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            e.printStackTrace();
            respondToCentral(mes, "Error updating address: " + e.getMessage());
        } finally {
            em.close();
        }
    }

    public void request15(MapMessage mes) throws JMSException {
        EntityManager em = emf.createEntityManager();
        try {
            List<City> cities = em.createQuery("SELECT c FROM City c", City.class).getResultList();
            StringBuilder sb = new StringBuilder("CITY LIST:\n");
            for (City c : cities) {
                sb.append("ID: ").append(c.getIdC()).append(", Name: ").append(c.getName()).append("\n");
            }
            respondToCentral(mes, sb.toString());
        } finally {
            em.close();
        }
    }

    public void request16(MapMessage mes) throws JMSException {
        EntityManager em = emf.createEntityManager();
        try {
            List<User> users = em.createQuery("SELECT u FROM User u", User.class).getResultList();
            StringBuilder sb = new StringBuilder("USER LIST:\n");
            for (User u : users) {
                sb.append("ID: ").append(u.getIdU()).append(", Username: ").append(u.getUserName()).append("\n");
            }
            respondToCentral(mes, sb.toString());
        } finally {
            em.close();
        }
    }

    public void processPayment(MapMessage mes) throws JMSException {
        int idU = mes.getInt("idU");
        EntityManager em = emf.createEntityManager();
        try {
            User user = em.find(User.class, idU);
            String address = user.getAddress();
            String cityName = user.getIdC().getName();
            respondToCentral(mes, address + ";" + cityName);
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            System.out.println("Error processing payment address lookup: " + e.getMessage());
        } finally {
            em.close();
        }
    }

    private void processMessage(MapMessage mes) {
        try {
            int requestNum = mes.getInt("request");
            switch (requestNum) {
                case 1:  request1(mes);       break;
                case 2:  request2(mes);       break;
                case 3:  request3(mes);       break;
                case 4:  request4(mes);       break;
                case 5:  request5(mes);       break;
                case 14: processPayment(mes); break;
                case 15: request15(mes);      break;
                case 16: request16(mes);      break;
            }
        } catch (Exception e) {
            System.out.println("Error processing message: " + e.getMessage());
        }
    }

    // Broadcast new user to other subsystems via UserTopic
    public void sendUser(int userId, String userName) {
        try (JMSContext context = connectionFactory.createContext("admin", "admin")) {
            MapMessage mes = context.createMapMessage();
            mes.setInt("request", 0);
            mes.setInt("idU", userId);
            mes.setString("userName", userName);
            context.createProducer().send(topicUser, mes);
            System.out.println("Subsystem 1 broadcast new user to topic");
        } catch (Exception e) {
            System.out.println("Error broadcasting user: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        Subsystem1 s1 = new Subsystem1();

        java.util.Map<String, String> properties = new java.util.HashMap<>();
        properties.put("javax.persistence.jdbc.driver", "com.mysql.cj.jdbc.Driver");
        properties.put("javax.persistence.jdbc.url",
            "jdbc:mysql://localhost:3306/Subsystem1?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC");

        emf = Persistence.createEntityManagerFactory("Subsystem1PU", properties);

        try {
            JMSContext context = connectionFactory.createContext("admin", "admin");
            context.setClientID("Subsystem1_ID");

            JMSConsumer consumerQueue = context.createConsumer(queue1);
            consumerQueue.setMessageListener(m -> {
                if (m instanceof MapMessage) s1.processMessage((MapMessage) m);
            });

            System.out.println("Subsystem 1 is running...");

            while (true) {
                Thread.sleep(1000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
