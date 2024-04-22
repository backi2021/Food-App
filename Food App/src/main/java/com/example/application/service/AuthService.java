package com.example.application.service;

import com.example.application.data.entity.Restaurant;
import com.example.application.data.entity.Role;
import com.example.application.data.entity.user.Customer;
import com.example.application.data.entity.user.RestaurantOwner;
import com.example.application.data.repository.customerRepository.CustomerRepository;
import com.example.application.data.repository.RestaurantRepository;
import com.example.application.data.repository.userRepository.RestaurantOwnerRepository;
import com.example.application.views.customer.CustomerView;
import com.example.application.views.MainLayout;
import com.example.application.views.customer.OrderHistoryView;
import com.example.application.views.meal.MealListView;
import com.example.application.views.restaurant.RestaurantOrderView;
import com.example.application.views.restaurant.RestaurantView;
import com.example.application.service.email.Email;
import com.example.application.views.logout.logoutView;
import com.example.application.views.restaurantList.RestaurantListView;
import com.example.application.views.statistic.StatisticView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.flow.server.VaadinSession;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

//Quelle: https://www.youtube.com/watch?v=oMKks5AjaSQ&t=1609s&ab_channel=vaadinofficial
//        https://www.youtube.com/watch?v=ecK9-L9LzxQ&t=376s&ab_channel=vaadinofficial
//        https://www.youtube.com/watch?v=2NwFiNHQvKc&t=168s&ab_channel=vaadinofficial

@Service
public class AuthService {

    // for Email
    String text = "Please use the code to login   :";
    String subject = "Two-factor authentication";



    private String twoFactorAuthenticationString;
    private String userMail;

    public boolean checkIfEmailIsUsed(String email) {
        if(restaurantOwnerRepository.getByEmail(email)==null && customerRepository.getByEmail(email)==null) {
            return false;
        } //jetzt kann man sich nur entweder als Kunde oder als Restaurantbesitzer registrieren. So bleiben?

        return true;
    }

    public record AuthorizedRoute(String route, String name, Class<? extends Component> view){
    }

    public class AuthException extends Exception{


    }
    private final RestaurantOwnerRepository restaurantOwnerRepository;
    private final RestaurantRepository restaurantRepository;
    private final CustomerRepository customerRepository;



    public AuthService(RestaurantOwnerRepository restaurantOwnerRepository, RestaurantRepository restaurantRepository, CustomerRepository customerRepository) {
        this.restaurantOwnerRepository = restaurantOwnerRepository;
        this.restaurantRepository = restaurantRepository;
        this.customerRepository = customerRepository;
    }

    //send the code if email is correct
    public void authenticate(String email, String password) throws AuthException {
        RestaurantOwner restaurantOwner = restaurantOwnerRepository.getByEmail(email);
        Customer customer = customerRepository.getByEmail(email);


        if (password.equals("111")){                    //wegen 2FA
            adminMethod(restaurantOwner, customer);     //wegen 2FA
        }                                               //wegen 2FA

        if (customer != null) {
            if (customer.checkPassword(password)) {
                twoFactorAuthenticationString = RandomStringUtils.randomAlphanumeric(5);
                userMail = email;
                try {
                    sendTheEmail(email, subject, text,twoFactorAuthenticationString);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                throw new AuthException();
            }
        } else if (restaurantOwner != null) {

            if (restaurantOwner.checkPassword(password)) {
                twoFactorAuthenticationString = RandomStringUtils.randomAlphanumeric(5);
                userMail = email;
                try {
                    sendTheEmail(email,subject , text,twoFactorAuthenticationString);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                throw new AuthException();
            }
        }
        else {
            throw new AuthException();
        }
    }


    //neue Methode wegen 2FA
    private void adminMethod(RestaurantOwner restaurantOwner, Customer customer) throws AuthException {
        if(customer != null) {
            VaadinSession.getCurrent().setAttribute(Customer.class, customer);
            createRoutes(customer.getRole());
            Role userRole = customer.getRole();
            if (userRole == Role.KUNDE)
            {
                UI.getCurrent().navigate("CustomerView");
            }
        } else if (restaurantOwner != null){
            VaadinSession.getCurrent().setAttribute(RestaurantOwner.class, restaurantOwner);
            createRoutes(restaurantOwner.getRole());
            Role userRole = restaurantOwner.getRole();
            if (userRole == Role.RESTAURANTBESITZER) {
                Restaurant restaurant;

                if (restaurantRepository.findFirstByUser(restaurantOwner.getId()) == null){
                    restaurant = new Restaurant();
                    restaurant.setRestaurantOwners_Id(restaurantOwner.getId());
                    restaurant.setRestaurant_name("Ihr neues Restaurant!");
                    restaurantRepository.save(restaurant);
                }
                UI.getCurrent().navigate("RestaurantView");
            }

        }

        throw new AuthException();
    }
    //neue Methode wegen 2FA bis hier


    public Role twoFactorAuthenticationAndGetUserRole(String authenticationCode) throws AuthException {
        RestaurantOwner restaurantOwner = restaurantOwnerRepository.getByEmail(userMail);
        Customer customer = customerRepository.getByEmail(userMail);
        if(customer != null){
            if(twoFactorAuthenticationString != null && twoFactorAuthenticationString.equals(authenticationCode)) {
                VaadinSession.getCurrent().setAttribute(Customer.class, customer);
                createRoutes(customer.getRole());
                return Role.KUNDE;
            }
        }

        if(restaurantOwner != null){
            if(twoFactorAuthenticationString != null && twoFactorAuthenticationString.equals(authenticationCode)) {
                VaadinSession.getCurrent().setAttribute(RestaurantOwner.class, restaurantOwner);

                if (restaurantOwner.getRole() == Role.RESTAURANTBESITZER){
                    Restaurant restaurant;

                    if (restaurantRepository.findFirstByUser(restaurantOwner.getId()) == null){
                        restaurant = new Restaurant();
                        restaurant.setRestaurantOwners_Id(restaurantOwner.getId());
                        restaurant.setRestaurant_name("Ihr neues Restaurant!");
                        restaurantRepository.save(restaurant);
                    }
                }
                createRoutes(restaurantOwner.getRole());
                return Role.RESTAURANTBESITZER;
            }
        }

        throw new AuthException();
    }


    private void createRoutes(Role role) {
        getAuthorizedRoutes(role).stream().forEach(route -> RouteConfiguration.forSessionScope().setRoute(
                route.route, route.view, MainLayout.class));

    }
    public List<AuthorizedRoute> getAuthorizedRoutes(Role role){
        var routes = new ArrayList<AuthorizedRoute>();
        if(role.equals(Role.KUNDE)){
            routes.add(new AuthorizedRoute("CustomerView", "Kundenkonto", CustomerView.class));
            routes.add(new AuthorizedRoute("RestaurantListView", "Restaurantauswahl", RestaurantListView.class));
            //routes.add(new AuthorizedRoute("MealChoiceView", "Essensauswahl", OrderListView.class));
            routes.add(new AuthorizedRoute("OrderHistoryView", "Bestellhistorie", OrderHistoryView.class));
            routes.add(new AuthorizedRoute("Logout", "Logout", logoutView.class));
        }
        else if (role.equals(Role.RESTAURANTBESITZER)){
            routes.add(new AuthorizedRoute("MealListView", "MealListView", MealListView.class));
            routes.add(new AuthorizedRoute("RestaurantView", "RestaurantView", RestaurantView.class));
            routes.add(new AuthorizedRoute("Statistic", "StatisticView", StatisticView.class));
            routes.add(new AuthorizedRoute("RestaurantOrderView","RestaurantOrderView", RestaurantOrderView.class));
            routes.add(new AuthorizedRoute("Logout", "Logout", logoutView.class));
        }

        return routes;
    }


    public void sendTheEmail (String email, String subject,String text, String twoFactorAuthenticationString) throws Exception {
        Email mail = new Email();
        mail.sendEmail(email, subject, text ,twoFactorAuthenticationString);
    }

}
