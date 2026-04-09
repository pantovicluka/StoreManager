package com.mycompany.centralsystem.resources;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Resource;
import javax.inject.Inject;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.TemporaryQueue;
import javax.jms.TextMessage;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;

@Path("Central")
public class CentralResource {

    @Inject
    private JMSContext context;

    @Resource(lookup = "jms/Subsystem1Queue")
    private Queue queue1;

    @Resource(lookup = "jms/Subsystem2Queue")
    private Queue queue2;

    @Resource(lookup = "jms/Subsystem3Queue")
    private Queue queue3;

    // --- Subsystem 1: User management ---

    @GET @Path("1")
    public Response request1(@QueryParam("userName") String userName, @QueryParam("password") String password) {
        Map<String, Object> params = new HashMap<>();
        params.put("userName", userName);
        params.put("password", password);
        return Response.ok(sendRequest(1, params)).build();
    }

    @POST @Path("2")
    public Response request2(@QueryParam("name") String name) {
        Map<String, Object> params = new HashMap<>();
        params.put("name", name);
        return Response.ok(sendRequest(2, params)).build();
    }

    @POST @Path("3")
    public Response request3(@QueryParam("userName") String userName, @QueryParam("password") String password,
                             @QueryParam("firstName") String firstName, @QueryParam("surname") String surname,
                             @QueryParam("address") String address, @QueryParam("cityId") int cityId,
                             @QueryParam("balance") int balance) {
        Map<String, Object> params = new HashMap<>();
        params.put("userName", userName);
        params.put("password", password);
        params.put("name", firstName);
        params.put("surname", surname);
        params.put("address", address);
        params.put("balance", balance);
        params.put("idC", cityId);
        return Response.ok(sendRequest(3, params)).build();
    }

    @PUT @Path("4")
    public Response request4(@QueryParam("userId") int userId, @QueryParam("amount") int amount) {
        Map<String, Object> params = new HashMap<>();
        params.put("idU", userId);
        params.put("amount", amount);
        return Response.ok(sendRequest(4, params)).build();
    }

    @PUT @Path("5")
    public Response request5(@QueryParam("userId") int userId, @QueryParam("address") String address,
                             @QueryParam("cityId") int cityId) {
        Map<String, Object> params = new HashMap<>();
        params.put("idU", userId);
        params.put("address", address);
        params.put("idC", cityId);
        return Response.ok(sendRequest(5, params)).build();
    }

    @GET @Path("15")
    public Response request15() {
        return Response.ok(sendRequest(15, new HashMap<>())).build();
    }

    @GET @Path("16")
    public Response request16() {
        return Response.ok(sendRequest(16, new HashMap<>())).build();
    }

    // --- Subsystem 2: Catalog, basket, wish list ---

    @POST @Path("6")
    public Response request6(@QueryParam("name") String name, @QueryParam("parentId") int parentId) {
        Map<String, Object> params = new HashMap<>();
        params.put("name", name);
        params.put("idPar", parentId);
        return Response.ok(sendRequest(6, params)).build();
    }

    @POST @Path("7")
    public Response request7(@QueryParam("name") String name, @QueryParam("description") String description,
                             @QueryParam("price") int price, @QueryParam("discount") int discount,
                             @QueryParam("categoryId") int categoryId, @QueryParam("creatorId") int creatorId) {
        Map<String, Object> params = new HashMap<>();
        params.put("name", name);
        params.put("description", description);
        params.put("price", price);
        params.put("discount", discount);
        params.put("idC", categoryId);
        params.put("idCrea", creatorId);
        return Response.ok(sendRequest(7, params)).build();
    }

    @PUT @Path("8")
    public Response request8(@QueryParam("itemId") int itemId, @QueryParam("price") int price) {
        Map<String, Object> params = new HashMap<>();
        params.put("idI", itemId);
        params.put("price", price);
        return Response.ok(sendRequest(8, params)).build();
    }

    @PUT @Path("9")
    public Response request9(@QueryParam("itemId") int itemId, @QueryParam("discount") int discount) {
        Map<String, Object> params = new HashMap<>();
        params.put("idI", itemId);
        params.put("discount", discount);
        return Response.ok(sendRequest(9, params)).build();
    }

    @POST @Path("10")
    public Response request10(@QueryParam("userId") int userId, @QueryParam("itemId") int itemId,
                              @QueryParam("amount") int amount) {
        Map<String, Object> params = new HashMap<>();
        params.put("idU", userId);
        params.put("idI", itemId);
        params.put("amount", amount);
        return Response.ok(sendRequest(10, params)).build();
    }

    @DELETE @Path("11")
    public Response request11(@QueryParam("userId") int userId, @QueryParam("itemId") int itemId,
                              @QueryParam("amount") int amount) {
        Map<String, Object> params = new HashMap<>();
        params.put("idU", userId);
        params.put("idI", itemId);
        params.put("amount", amount);
        return Response.ok(sendRequest(11, params)).build();
    }

    @POST @Path("12")
    public Response request12(@QueryParam("userId") int userId, @QueryParam("itemId") int itemId) {
        Map<String, Object> params = new HashMap<>();
        params.put("idU", userId);
        params.put("idI", itemId);
        return Response.ok(sendRequest(12, params)).build();
    }

    @DELETE @Path("13")
    public Response request13(@QueryParam("userId") int userId, @QueryParam("itemId") int itemId) {
        Map<String, Object> params = new HashMap<>();
        params.put("idU", userId);
        params.put("idI", itemId);
        return Response.ok(sendRequest(13, params)).build();
    }

    @GET @Path("17")
    public Response request17() {
        return Response.ok(sendRequest(17, new HashMap<>())).build();
    }

    @GET @Path("18")
    public Response request18(@QueryParam("userId") int userId) {
        Map<String, Object> params = new HashMap<>();
        params.put("idU", userId);
        return Response.ok(sendRequest(18, params)).build();
    }

    @GET @Path("19")
    public Response request19(@QueryParam("userId") int userId) {
        Map<String, Object> params = new HashMap<>();
        params.put("idU", userId);
        return Response.ok(sendRequest(19, params)).build();
    }

    @GET @Path("20")
    public Response request20(@QueryParam("userId") int userId) {
        Map<String, Object> params = new HashMap<>();
        params.put("idU", userId);
        return Response.ok(sendRequest(20, params)).build();
    }

    // --- Subsystem 3: Orders and transactions ---

    @POST @Path("14")
    public Response request14(@QueryParam("userId") int userId) {
        Map<String, Object> params = new HashMap<>();
        params.put("idU", userId);
        return Response.ok(sendRequest(14, params)).build();
    }

    @GET @Path("21")
    public Response request21(@QueryParam("userId") int userId) {
        Map<String, Object> params = new HashMap<>();
        params.put("idU", userId);
        return Response.ok(sendRequest(21, params)).build();
    }

    @GET @Path("22")
    public Response request22() {
        return Response.ok(sendRequest(22, new HashMap<>())).build();
    }

    @GET @Path("23")
    public Response request23() {
        return Response.ok(sendRequest(23, new HashMap<>())).build();
    }

    private String sendRequest(int requestNumber, Map<String, Object> params) {
        try {
            TemporaryQueue tempQueue = context.createTemporaryQueue();
            MapMessage mapMsg = context.createMapMessage();
            mapMsg.setInt("request", requestNumber);

            for (Map.Entry<String, Object> entry : params.entrySet()) {
                mapMsg.setObject(entry.getKey(), entry.getValue());
            }

            mapMsg.setJMSReplyTo(tempQueue);

            Queue target;
            if (requestNumber <= 5 || requestNumber == 15 || requestNumber == 16) target = queue1;
            else if ((requestNumber >= 6 && requestNumber <= 13) || (requestNumber >= 17 && requestNumber <= 20)) target = queue2;
            else target = queue3;

            context.createProducer().send(target, mapMsg);

            JMSConsumer consumer = context.createConsumer(tempQueue);
            Message reply = consumer.receive(10000);

            if (reply instanceof TextMessage) {
                return ((TextMessage) reply).getText();
            }
            return "Subsystem did not respond.";

        } catch (JMSException e) {
            return "JMS Error: " + e.getMessage();
        }
    }
}
