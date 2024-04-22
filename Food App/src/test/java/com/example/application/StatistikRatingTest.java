package com.example.application;

import com.example.application.data.entity.Ordering;
import com.example.application.views.statistic.StatisticView;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
public class StatistikRatingTest {


    @Test
    public void shouldCalculateQualityStats(){
        //Vorbedingung
        LocalDateTime start = LocalDateTime.of(2022,01,01,0,0,0);
        LocalDateTime end = LocalDateTime.of(2022,01,8,0,0,0);

        Ordering order = new Ordering();
        order.setOrderTime(LocalDateTime.of(2022,01,2,0,0,0));
        order.setQualityRating(5);
        order.setRatingSubmitted(true);

        Ordering order1 = new Ordering();
        order1.setOrderTime(LocalDateTime.of(2022,01,3,0,0,0));
        order1.setQualityRating(5);
        order1.setRatingSubmitted(true);

        Ordering order2 = new Ordering();
        order2.setOrderTime(LocalDateTime.of(2022,01,4,0,0,0));
        order2.setQualityRating(5);
        order2.setRatingSubmitted(true);

        Ordering order3 = new Ordering();
        order3.setOrderTime(LocalDateTime.of(2022,01,9,0,0,0));
        order3.setQualityRating(1);
        order3.setRatingSubmitted(true);

        List<Ordering> ordering = new ArrayList<>();
        ordering.add(order);
        ordering.add(order1);
        ordering.add(order2);
        ordering.add(order3);
        //Wenn
        StatisticView statisticView = Mockito.mock(StatisticView.class);
        Mockito.doCallRealMethod().when(statisticView).getAverageQualityRatingForInterval(Mockito.any(),Mockito.any(),Mockito.any());
        Double result = statisticView.getAverageQualityRatingForInterval(start, end, ordering);

        //Dann
        assertThat(result).isEqualTo(5);
    }

    @Test
    public void shouldCalculateDeliveryStats(){
        //Vorbedingung
        LocalDateTime start = LocalDateTime.of(2022,01,01,0,0,0);
        LocalDateTime end = LocalDateTime.of(2022,01,8,0,0,0);

        Ordering order = new Ordering();
        order.setOrderTime(LocalDateTime.of(2022,01,2,0,0,0));
        order.setDeliveryRating(5);
        order.setRatingSubmitted(true);

        Ordering order1 = new Ordering();
        order1.setOrderTime(LocalDateTime.of(2022,01,3,0,0,0));
        order1.setDeliveryRating(5);
        order1.setRatingSubmitted(true);

        Ordering order2 = new Ordering();
        order2.setOrderTime(LocalDateTime.of(2022,01,4,0,0,0));
        order2.setDeliveryRating(5);
        order2.setRatingSubmitted(true);

        Ordering order3 = new Ordering();
        order3.setOrderTime(LocalDateTime.of(2022,01,9,0,0,0));
        order3.setDeliveryRating(5);
        order3.setRatingSubmitted(true);

        List<Ordering> ordering = new ArrayList<>();
        ordering.add(order);
        ordering.add(order1);
        ordering.add(order2);
        ordering.add(order3);
        //Wenn
        StatisticView statisticView = Mockito.mock(StatisticView.class);
        Mockito.doCallRealMethod().when(statisticView).getAverageDeliveryRatingForInterval(Mockito.any(),Mockito.any(),Mockito.any());
        Double result = statisticView.getAverageDeliveryRatingForInterval(start, end, ordering);

        //Dann
        assertThat(result).isEqualTo(5);
    }
}
