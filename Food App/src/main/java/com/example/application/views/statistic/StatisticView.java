package com.example.application.views.statistic;

import com.example.application.data.entity.Meal;
import com.example.application.data.entity.OrderItem;
import com.example.application.data.entity.Ordering;
import com.example.application.data.entity.Restaurant;
import com.example.application.data.entity.user.RestaurantOwner;
import com.example.application.service.DateTimeService;
import com.example.application.service.MealService;
import com.example.application.service.OrderService;
import com.example.application.service.RestaurantService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.charts.Chart;
import com.vaadin.flow.component.charts.model.*;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import org.joda.time.DateTime;

import javax.xml.crypto.Data;
import java.time.*;
import java.util.List;

@Route ("Statistic")
@PageTitle("StatisticView")
public class StatisticView extends VerticalLayout {


    MealService mealService;
    OrderService orderService;
    RestaurantService restaurantService;
    DateTimeService dateTimeService;

    H3 mealTextHead = new H3("Top 5 Gerichte der letzten Woche:");
    H4 mealText1 = new H4(" ");
    H4 mealText2 = new H4(" ");
    H4 mealText3 = new H4(" ");
    H4 mealText4 = new H4(" ");
    H4 mealText5 = new H4(" ");
    H2 header = new H2("Ihre Statistiken der letzten Woche:");
    H4 ratingText1 = new H4(" ");
    H4 ratingText2 = new H4(" ");


    public StatisticView(MealService mealService, OrderService orderService, RestaurantService restaurantService, DateTimeService dateTimeService) {
        this.mealService = mealService;
        this.orderService = orderService;
        this.restaurantService = restaurantService;
        this.dateTimeService = dateTimeService;




        //int restaurantId = 2; //Für Build

        int restaurantOwnerId = VaadinSession.getCurrent().getAttribute(RestaurantOwner.class).getId();
        Restaurant restaurant = restaurantService.findRestaurant(restaurantOwnerId);
        int restaurantId = restaurant.getId();

        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        countAndSetMeals(restaurantId);
        configurateRatingText(restaurantId);

        add(header, getMealsChart(restaurantId)/*, getMealStats(restaurantId)*/, mealTextHead, mealText1, mealText2, mealText3, mealText4,
                mealText5, getRatingChart(restaurantId), ratingText1, ratingText2);
    }

    private void configurateRatingText(int restaurantId) {
        List<Ordering> allOrderings = orderService.findAllByRestaurantId(restaurantId);
        LocalDateTime end = LocalDateTime.of(LocalDate.from(dateTimeService.getLocalDateTime()), LocalTime.MAX);
        LocalDateTime start = LocalDateTime.of(LocalDate.from(dateTimeService.getLocalDateTime()), LocalTime.MIN).minusDays(7);

        ratingText1.setText("Die durchschnittliche Bewertung der Lieferung für diese Woche liegt bei: " + getAverageDeliveryRatingForInterval(start, end, allOrderings));
        ratingText2.setText("Die durchschnittliche Bewertung der Qualität für diese Woche liegt bei: " + getAverageQualityRatingForInterval(start, end, allOrderings));
    }


    private void countAndSetMeals(int restaurantId) {

        List<Meal> allMealsByRestaurantId = mealService.findAllMealsById(restaurantId);
        List<OrderItem> orderItems = orderService.findAllOrderItems();
        int zaehler = 0;
        int[] countArray = new int[allMealsByRestaurantId.size()];
        String[] nameArray = new String[allMealsByRestaurantId.size()];
        int i = 0;
        for (Meal m : allMealsByRestaurantId) {
            zaehler = countMeal(m, orderItems);
            countArray[i] = zaehler;
            nameArray[i] = m.getMealName();
            i++;
        }

        nameArray = sortNameArray(countArray, nameArray);
        countArray = sortCountArray(countArray);

        configureMealText(nameArray, countArray);

    }

    private void configureMealText(String[] nameArray, int[] countArray) {
        int limit = countArray.length-1;
        int i = 0;
        if (i <= limit) {
            mealText1.setText("1: " + nameArray[0] + ". Es wurde insgesamt " + countArray[0] + " mal bestellt.");
        }
        i++;

        if (i <= limit) {
            mealText2.setText("2: " + nameArray[1] + ". Es wurde insgesamt " + countArray[1] + " mal bestellt.");
        }
        i++;

        if (i <= limit) {
            mealText3.setText("3: " + nameArray[2] + ". Es wurde insgesamt " + countArray[2] + " mal bestellt.");
        }
        i++;

        if (i <= limit) {
            mealText4.setText("4: " + nameArray[3] + ". Es wurde insgesamt " + countArray[3] + " mal bestellt.");
        }
        i++;

        if (i <= limit) {
            mealText5.setText("5: " + nameArray[4] + ". Es wurde insgesamt " + countArray[4] + " mal bestellt.");
        }
        i++;
    }


    //Bubblesort
    private String[] sortNameArray(int[] countArray, String[] nameArray){

        for (int k = 1; k < countArray.length; k++) {
            for (int i = 0; i < countArray.length - k; i++) {
                if (countArray[i] < countArray[i + 1]) {
                    int zwischenspeicher = countArray[i + 1];
                    String zsp = nameArray[i + 1];
                    countArray[i + 1] = countArray[i];
                    nameArray[i + 1] = nameArray[i];
                    countArray[i] = zwischenspeicher;
                    nameArray[i] = zsp;
                }
            }
        }
        return nameArray;
    }


    private int[] sortCountArray(int[] countArray){

        for (int k = 1; k < countArray.length; k++) {
            for (int i = 0; i < countArray.length - k; i++) {
                if (countArray[i] < countArray[i + 1]) {
                    int zwischenspeicher = countArray[i + 1];
                    countArray[i + 1] = countArray[i];
                    countArray[i] = zwischenspeicher;
                }
            }
        }
        return countArray;
    }




// Sieht nicht so schön aus
    /*private Component getMealStats(int restaurantId) {
        Span stats = new Span(mealService.countMealsById(restaurantId) + " meals");
        stats.addClassNames("text-xl", "mt-m");
        return stats;
    }*/

    private Component getMealsChart(int restaurantId) {
        Chart chart = new Chart(ChartType.PIE);
        List<OrderItem> orderItems = orderService.findAllOrderItems();
        DataSeries dataSeries = new DataSeries();
        mealService.findAllMealsById(restaurantId).forEach(meal -> dataSeries.add(new DataSeriesItem(meal.getMealName(), countMeal(meal, orderItems))));
        chart.getConfiguration().setSeries(dataSeries);
        return chart;
    }

    private int countMeal(Meal meal, List<OrderItem> orderItems) {
        LocalDateTime today = dateTimeService.getLocalDateTime();
        LocalDateTime lastDay = today.minusDays(7);
        int zaehler = 0;
        for (OrderItem o : orderItems) {
            if (o.getMealId() == meal.getId() && o.getOrderTime().isAfter(lastDay)) {
                zaehler++;
            }
        }
        return zaehler;
    }






    //__________________________________________________________________________________________Ab hier Bewertung
    //Quelle:https://stackoverflow.com/questions/23944370/how-to-get-milliseconds-from-localdatetime-in-java-8
    private Component getRatingChart(int restaurantId) {

        int daysInChart = 7;
        Chart chart = new Chart(ChartType.LINE);
        LocalDateTime ldt = LocalDateTime.of(LocalDate.from(dateTimeService.getLocalDateTime()), LocalTime.MIN);
        List<Ordering> allOrderings = orderService.findAllByRestaurantId(restaurantId);
        long today = ldt.atZone(ZoneId.of("Europe/Berlin")).toInstant().toEpochMilli();


        Configuration configuration = chart.getConfiguration();
        configuration.getTitle().setText("Entwicklung der Bewertungen in der letzten Woche");
        configuration.getyAxis().setMax(5);
        configuration.getyAxis().setMin(0);
        //configuration.getxAxis().setMax(today);
        //configuration.getxAxis().setMin(lastDay);

        configuration.getxAxis().setType(AxisType.DATETIME);


        DataSeries deliveryRatingDataSeries = new DataSeries();
        DataSeries qualityRatingDataSeries = new DataSeries();
        deliveryRatingDataSeries.setId("dataseries1");
        qualityRatingDataSeries.setId("dataseries2");
        deliveryRatingDataSeries.setName("Durchschnittliche Lieferungsbewertung");
        qualityRatingDataSeries.setName("Durchschnittliche Qualitätsbewertung");

        for (int i = 0; i <= daysInChart; i++) {
            long day = ldt.minusDays(i).atZone(ZoneId.of("Europe/Berlin")).toInstant().toEpochMilli();

            LocalDateTime startOfDay = LocalDateTime.of(LocalDate.from(dateTimeService.getLocalDateTime()).minusDays(i), LocalTime.MIN);
            LocalDateTime endOfDay = LocalDateTime.of(LocalDate.from(dateTimeService.getLocalDateTime()).minusDays(i), LocalTime.MAX);

            double averageDeliveryRating = getAverageDeliveryRatingForInterval(startOfDay, endOfDay, allOrderings);
            DataSeriesItem deliveryRatingEntry = new DataSeriesItem(day, averageDeliveryRating);
            deliveryRatingDataSeries.add(deliveryRatingEntry);

            double averageQualityRating = getAverageQualityRatingForInterval(startOfDay, endOfDay, allOrderings);
            DataSeriesItem qualityRatingEntry = new DataSeriesItem(day, averageQualityRating);
            qualityRatingDataSeries.add(qualityRatingEntry);
        }

        configuration.setSeries(deliveryRatingDataSeries, qualityRatingDataSeries);
        return chart;
    }


    public double getAverageDeliveryRatingForInterval(LocalDateTime start, LocalDateTime end, List<Ordering> orderedMeals) {

        double ergebnis = 0;
        int anzahlElemente = 0;

        for (Ordering o : orderedMeals) {
            if(o.getOrderTime().isBefore(end) && o.getOrderTime().isAfter(start) && o.getRatingSubmitted()) {
                ergebnis+=o.getDeliveryRating();
                anzahlElemente++;
            }
        }
        if (ergebnis == 0.0 && anzahlElemente == 0) {
            return 0;
        }
        return ergebnis/anzahlElemente;
    }

    public double getAverageQualityRatingForInterval(LocalDateTime start, LocalDateTime end, List<Ordering> orderedMeals) {

        double ergebnis = 0;
        int anzahlElemente = 0;

        for (Ordering o : orderedMeals) {
            if(o.getOrderTime().isBefore(end) && o.getOrderTime().isAfter(start) && o.getRatingSubmitted()) {
                ergebnis+=o.getQualityRating();
                anzahlElemente++;
            }
        }
        if (ergebnis == 0.0 && anzahlElemente == 0) {
            return 0;
        }
        return ergebnis/anzahlElemente;
    }

}
