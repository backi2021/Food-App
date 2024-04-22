package com.example.application;


import com.example.application.service.DateTimeService;
import org.joda.time.DateTime;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
public class DateTimeServiceTest {

    private DateTimeService dateTimeService = new DateTimeService();

    @Test
    public void shouldShowActualTime(){
        DateTimeService dateTimeService = new DateTimeService();

        dateTimeService.overWriteDefaultTime(LocalDateTime.of(1992,11,25,0 ,0 ,0,0));

        dateTimeService.resetToDefaultTime();

        assertThat(dateTimeService.getLocalDateTime().withNano(0)).isEqualTo(LocalDateTime.now().withNano(0));

    }

    @Test
    public void shouldShowCustomTime(){
        DateTimeService dateTimeService = new DateTimeService();

        dateTimeService.overWriteDefaultTime(LocalDateTime.of(1992,11,25,0 ,0 ,0,0));



        assertThat(dateTimeService.getLocalDateTime().withNano(0)).isEqualTo(LocalDateTime.of(1992,11,25,0 ,0 ,0,0));

    }
}
