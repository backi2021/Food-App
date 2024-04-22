package com.example.application.service;

import com.example.application.data.entity.*;
import com.example.application.data.entity.user.Customer;
import com.example.application.data.repository.ItemRepository;
import com.example.application.data.repository.MealRepository;
import com.example.application.data.repository.OrderRepository;
import com.example.application.data.repository.customerRepository.CustomerRepository;
import com.example.application.service.email.Email;
import com.vaadin.flow.server.VaadinSession;

import org.aspectj.weaver.ast.Or;
import org.hibernate.criterion.Order;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Scope("prototype")
public class OrderService {

    // für Email
    String tempOrderID ;
    String emailSubject = "Bestellung ";


    private final OrderRepository orderRepository;
    private final ItemRepository itemRepository;
    private final MealService mealService;
    private final DateTimeService dateTimeService;
    private final CustomerRepository customerRepository;


    public OrderService(OrderRepository orderRepository, ItemRepository itemRepository, CustomerRepository customerRepository,  MealService mealService, DateTimeService dateTimeService){
        this.orderRepository = orderRepository;
        this.itemRepository = itemRepository;
        this.customerRepository = customerRepository;
        this.mealService = mealService;

        this.dateTimeService = dateTimeService;

    }

    public void saveOrder(Ordering ordering, List<Meal> meals){
        String confirmationMessage = "Ihre Bestellung wurde eingereicht!";
        String messageText;
        orderRepository.save(ordering);
        for(Meal meal : meals){
            OrderItem item = new OrderItem();
            item.setMealId(meal.getId());
            item.setOrderId(ordering.getId());
            item.setOrderTime(ordering.getOrderTime()); //Julia
            tempOrderID = ordering.getId().toString(); // Änderung
            itemRepository.save(item);
        }

        if (ordering.getStatus() == OrderStatus.ACCEPTED) {
            messageText = " Vielen Dank für Ihre Bestellung bei " + ordering.getRestaurantName() +"!" + "\n" + "Die Kosten von " + ordering.getOrderTotal() + " Euro wurden von Ihrem Guthabenkonto abgebucht!" + "\n" + "Ihre Bestellnummer lautet: ";
        } else  {
            messageText = " Vielen Dank für Ihre Bestellung bei " + ordering.getRestaurantName() +"!" + "\n" + "Die Kosten werden von Ihrem Guthabenkonto abgebucht, sobald das Restaurant Ihre Bestellung bestätigt!" + "\n" + "Ihre Bestellnummer lautet: ";
        }

        Email mail = new Email();
        try { // Änderung
            //messageText = " Vielen Dank für Ihre Bestellung bei " + ordering.getRestaurantName() +"!" + "\n" + "Die Kosten von " + ordering.getOrderTotal() + " Wurden Ihrem Guthabenkonto abgebucht!" + "\n" + "Ihre Bestellnummer lautet: ";
            mail.sendEmail(VaadinSession.getCurrent().getAttribute(Customer.class).getCustomer_email(),emailSubject ,confirmationMessage + messageText ,tempOrderID );
        } catch (Exception e) {
            //e.printStackTrace();
        }

    }
    public List<Ordering> findAllByCustomerId(Integer customerId){
        List<Ordering> orderings = orderRepository.findByCustomerId(customerId);
        List<Ordering> newOrderings = new ArrayList<>();

        for (Ordering o : orderings) {
            if (o.getOrderTime().isBefore(dateTimeService.getLocalDateTime()) || o.getOrderTime().equals(dateTimeService.getLocalDateTime())) {
                newOrderings.add(o);
            }
        }
        return newOrderings;
    }


    //Von Julia:

    public List<Ordering> findAllByRestaurantId(Integer restaurantId) {
        List<Ordering> orderings = orderRepository.findByRestaurantId(restaurantId);
        List<Ordering> newOrderings = new ArrayList<>();

        for (Ordering o : orderings) {
            if (o.getOrderTime().isBefore(dateTimeService.getLocalDateTime()) || o.getOrderTime().equals(dateTimeService.getLocalDateTime())) {
                newOrderings.add(o);
            }
        }
        return newOrderings;
    }

    /*public List<Ordering> findAllOrderings(String stringFilter) {
        if (stringFilter == null || stringFilter.isEmpty()) {
            return orderRepository.findAll();
        } else {
            return orderRepository.search(stringFilter);
        }
    }*/

    //Liste mit allen MealIDs zu 1 Order
    public List<OrderItem> findAllByOrderId(Integer orderId){
        return itemRepository.findByOrderId(orderId);
    }

    /*public List<Meal> findAllById(List<OrderItem> orderItem) {
        return mealRepository.findById(orderItem);
    }*/



    public Ordering findByOrderId(Integer orderId){
        return orderRepository.findFirstById(orderId);
    }

    public void deleteOrder(Ordering order){
        orderRepository.delete(order);
    }
    //ZUM TESTEN
    public Ordering findFirstByCustomerId(Integer orderId) {
        return orderRepository.findFirstByCustomerId(orderId);
    }

    public void saveOrdering(Ordering ordering) {
        if (ordering == null) {
            System.err.println("Ordering is NULL");
            return;
        }
        orderRepository.save(ordering);
    }

    public List<Meal> findMealsByOrderId(Integer id) {
        List<Meal> meals = new ArrayList<>();

        List<OrderItem> items = findAllByOrderId(id);

        for (OrderItem i : items){
            Meal addMeal = mealService.findMeal(i.getMealId());
            meals.add(addMeal);
        }

        return meals;

    }


    public List<OrderItem> findAllOrderItems(){
        return itemRepository.findAll();
    }

    public Integer calculateDeliveryTime(Integer distInMeter, Integer restaurantId) {
        Integer fiveKm = distInMeter / 5000;

        List<Ordering> orders = findAllByRestaurantId(restaurantId);

        Integer sumOpenOrders = 0;

        System.out.println("RestaurantID:" + restaurantId);
        System.out.println("Alle Bestellungen:" + orders.size());

        for (Ordering order : orders) {
            //System.out.println(order.getStatus().toString());
            if (order.getStatus().equals(OrderStatus.ACCEPTED)){
                sumOpenOrders = sumOpenOrders +1;
            }
        }

        System.out.println("Bestellungen: " + sumOpenOrders);
        System.out.println("5er: " + fiveKm);
        System.out.println(distInMeter);

        Integer timeForWorkload;

        if (sumOpenOrders > 2) {
            timeForWorkload = 10;
        } else {
            timeForWorkload = 0;
        }

        return 10 + timeForWorkload + (fiveKm * 10) + 10;
        // Zubereitung + Auslastung + Fahrzeit

    }

    public void rejectOrder(Ordering selectedOrder, String message) {
        selectedOrder.setStatus(OrderStatus.CANCEL);
        orderRepository.save(selectedOrder);

        Email mail = new Email();
        try {

            mail.sendEmail(selectedOrder.getCustomerEmail(),"Ihre Bestellung wurde abgelehnt" ,"Ihre Bestellung wurde vom Restaurant abgelehnt. Als Grund wurde: - " + message +" - angegeben. " , "Die Bestellnummer lautet " + selectedOrder.getId());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public boolean payOrder(Ordering order, Double extraCost){
        Customer customer = customerRepository.getByEmail(order.getCustomerEmail());
        System.out.println(customer.getEmail());

        if(order.getOrderTotal() + extraCost <= customer.getGuthabenkonto()) {
            customer.setGuthabenkonto(customer.getGuthabenkonto() - (order.getOrderTotal() + extraCost));
            customerRepository.save(customer);
            order.setOrderTotal(order.getOrderTotal() + extraCost);
            order.setStatus(OrderStatus.ACCEPTED);
            orderRepository.save(order);

            Email mail = new Email();
            try {

                mail.sendEmail(order.getCustomerEmail(),"Ihre Bestellung wurde angenommen" ,"Ihre Bestellung wurde vom Restaurant angenommen. Die Extrakosten betragen " + extraCost + " Euro. Der Gesammtbetrag von " + order.getOrderTotal() + " Euro wurde von Ihrem Konto abgebucht. " , "Die Bestellnummer lautet " + order.getId());
            } catch (Exception e) {
                e.printStackTrace();
            }

            return true;

        } else {
            order.setStatus(OrderStatus.CANCEL);
            orderRepository.save(order);

            Email mail = new Email();
            try {

                mail.sendEmail(order.getCustomerEmail(),"Ihre Bestellung wurde abgelehnt" ,"Ihre Bestellung wurde vom Restaurant abgelehnt. Ihr Guthaben reicht nicht aus. " , "Die Bestellnummer lautet " + order.getId());
            } catch (Exception e) {
                e.printStackTrace();
            }

            return false;
        }
    }
}
