package com.driver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.*;

@Service
public class OrderService {
    @Autowired
    OrderRepository orderRepository;
    //1 Add an Order
    public void addOrder(Order order) {
        orderRepository.addOrder(order);
    }
    //2 Add a delivery Partner
    public void addPartner(String partnerId){
        orderRepository.addPartner(partnerId);
    }
    //3 Assign an order to delivery partner
    public void addOrderPartnerPair(String orderId,String partnerId){
        orderRepository.addOrderPartnerPair(orderId,partnerId);
    }
    //4 Get order by order id
    public Order getOrderById(String orderId){
        HashMap<String,Order> orderDb=orderRepository.getAllOrders();
        if(!orderDb.containsKey(orderId)){
            return null;
        }
        for(String order : orderDb.keySet()){
            if(order.equals(orderId)){
                return orderDb.get(order);
            }
        }
        return null;
    }
    //5 Get partner by partnerId
    public DeliveryPartner getPartnerById(String partnerId){
        HashMap<String,DeliveryPartner> partnerDb=orderRepository.getAllDeliveryPartners();
        if(!partnerDb.containsKey(partnerId)){
            return null;
        }
        for(String partner: partnerDb.keySet()){
            if(partner.equals(partnerId)){
                return partnerDb.get(partner);
            }
        }
        return null;
    }
    //6 Get number of orders assigned to given partnerId
    public int getOrderCountByPartnerId(String partnerId){
        HashMap<String,HashSet<String>> pairDb=orderRepository.getAllDeliveryOrderPairs();
        HashSet<String> set=pairDb.get(partnerId);
        return set.size();
    }
    //7 Get List of all orders assigned to given partnerId
    public List<String> getOrdersByPartnerId(String partnerId){
        HashMap<String,HashSet<String>> pairDb=orderRepository.getAllDeliveryOrderPairs();
        if(!pairDb.containsKey(partnerId)){
            return null;
        }
        if(pairDb.containsKey(partnerId)){
            HashSet<String> orders=pairDb.get(partnerId);
            return new ArrayList<>(orders);
        }
        return null;
    }
    //8 Get List of all orders
    public List<String> getAllOrders() {
        HashMap<String,Order> orderDb=orderRepository.getAllOrders();
        return new ArrayList<>(orderDb.keySet());
    }
    //9 Get count of orders which are not assigned to any partner
    public int getCountOfUnassignedOrders(){
        HashMap<String,String> orderPartnerDb=orderRepository.getAllOrderStatus();
        HashMap<String,Order> orderDb=orderRepository.getAllOrders();
        return orderDb.size()-orderPartnerDb.size();
    }
    //10 Get count of orders which are left undelivered by partnerId after given time
    public int getOrdersLeftAfterGivenTimeByPartnerId(int time, String partnerId){
        HashMap<String,HashSet<String>> pairDb=orderRepository.getAllDeliveryOrderPairs();
        HashMap<String,Order> orderDb=orderRepository.getAllOrders();
        HashSet<String> orderList=pairDb.get(partnerId);
        int count=0;
        for(String order:orderList){
            Order a=orderDb.get(order);
            if(a.getDeliveryTime()>time){
                count++;
            }
        }
        return count;
    }
    //11 Get the time at which the last delivery is made by given partner
    public String getLastDeliveryTimeByPartnerId(String partnerId){
        HashMap<String,HashSet<String>> pairDb=orderRepository.getAllDeliveryOrderPairs();
        HashSet<String> orderList=pairDb.get(partnerId);
        HashMap<String,Order> orderDb=orderRepository.getAllOrders();
        int time=0;
        for(String order:orderList){
            Order a=orderDb.get(order);
            if(a.getDeliveryTime()>time){
                time=a.getDeliveryTime();
            }
        }
        if(time>0){
            String lastDeliveryTime=getLastTime(time);
            return lastDeliveryTime;
        }
        return "00:00";
    }
    //12 Delete a partner and the corresponding orders should be unassigned
    public void deletePartnerById(String partnerId){
        HashMap<String,HashSet<String>> pairDb=orderRepository.getAllDeliveryOrderPairs();
        HashMap<String,DeliveryPartner> partnerDb=orderRepository.getAllDeliveryPartners();
        HashMap<String,String> orderPartnerDb=orderRepository.getAllOrderStatus();
        HashSet<String> set=new HashSet<>();
        if(pairDb.containsKey(partnerId)){
            set=pairDb.get(partnerId);
        }
        pairDb.remove(partnerId);
        partnerDb.remove(partnerId);
        for(String order:set){
            if(orderPartnerDb.get(order).equals((partnerId))){
                orderPartnerDb.remove(order);
            }
        }
    }
    //13 Delete an order and the corresponding partner should be unassigned
    public void deleteOrderById(String orderId){
        HashMap<String,HashSet<String>> pairDb=orderRepository.getAllDeliveryOrderPairs();
        HashMap<String,Order> orderDb=orderRepository.getAllOrders();
        HashMap<String,String> orderPartnerDb=orderRepository.getAllOrderStatus();
        HashMap<String,DeliveryPartner> partnerDb=orderRepository.getAllDeliveryPartners();
        String partnerId="";
        for(String order:orderDb.keySet()){
            if(order.equals(orderId)){
                partnerId=orderPartnerDb.get(orderId);
            }
        }
        orderPartnerDb.remove(orderId);
        orderDb.remove(orderId);
        HashSet<String> list=pairDb.get(partnerId);
        for(String order:list){
            if(order.equals(orderId)){
                list.remove(orderId);
            }
        }
        DeliveryPartner p=partnerDb.get(partnerId);
        p.setNumberOfOrders(p.getNumberOfOrders()-1);
    }

    public String getLastTime(int time){
        int hours=time/60;
        int minutes=time%60;
        StringBuilder lastTime = new StringBuilder();
        if(hours<10){
            lastTime.append("0");
            lastTime.append(hours);
        }
        else{
            lastTime.append(hours);
        }
        lastTime.append(":");
        if(minutes<10){
            lastTime.append("0");
            lastTime.append(minutes);
        }
        else{
            lastTime.append(minutes);
        }
        return lastTime.toString();
    }
}
