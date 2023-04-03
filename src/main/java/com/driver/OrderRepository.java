package com.driver;

import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.HashSet;

@Repository
public class OrderRepository {
    public HashMap<String,Order> orderDb; //OrderId as Key,Order as value
    public HashMap<String,DeliveryPartner> deliveryPartnerDb; //partnerId as Key,Partner as value
    public HashMap<String,HashSet<String>> partnerOrderDb; //PartnerId as key, OrderId as value
    public HashMap<String,String> orderPartnerDb; //OrderId as key,Boolean as value

    public OrderRepository() {
        this.orderDb=new HashMap<>();
        this.deliveryPartnerDb=new HashMap<>();
        this.partnerOrderDb=new HashMap<>();
        this.orderPartnerDb=new HashMap<>();
    }

    //1 Add an Order
    public void addOrder(Order order) {
        String id=order.getId();
        if(!orderDb.containsKey(id)) {
            orderDb.put(id, order);
        }
    }
    //2 Add a delivery Partner
    public void addPartner(String partnerId) {
        if(!deliveryPartnerDb.containsKey(partnerId)) {
            deliveryPartnerDb.put(partnerId,new DeliveryPartner(partnerId));
        }
    }
    //3 Assign an order to delivery partner
    public void addOrderPartnerPair(String orderId,String partnerId){
        HashSet<String> orderList=partnerOrderDb.get(partnerId);
        if(orderList==null){
            orderList=new HashSet<>();
        }
        if(!orderPartnerDb.containsKey(orderId)){
            orderList.add(orderId);
            partnerOrderDb.put(partnerId,orderList);
        }
        orderPartnerDb.put(orderId,partnerId);

        DeliveryPartner p=deliveryPartnerDb.get(partnerId);
        p.setNumberOfOrders(p.getNumberOfOrders()+1);
    }
    //Get all orders List
    public HashMap<String,Order> getAllOrders() {
        return orderDb;
    }
    //Get all partners List
    public HashMap<String,DeliveryPartner> getAllDeliveryPartners() {
        return deliveryPartnerDb;
    }
    //Get all partner-order pair List
    public HashMap<String,HashSet<String>> getAllDeliveryOrderPairs() {
        return partnerOrderDb;
    }
    //Get all Order status List
    public HashMap<String,String> getAllOrderStatus() {
        return orderPartnerDb;
    }
}
