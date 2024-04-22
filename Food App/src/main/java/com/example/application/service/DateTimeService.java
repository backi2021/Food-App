package com.example.application.service;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
@Service
public class DateTimeService {
    //LocalDateTime.now() zeigt das Aktuelle Datum und Zeit an
    //LocalDateTime.of(year: ..., month: ..., dayOfMonth: ...,) änderbar auf einen bestimmten Tag
    //private LocalDateTime dateTime = LocalDateTime.of(2022,1,20,12,36,0);
    private LocalDateTime dateTime;
    private Boolean isCustomDate = false;

    public void overWriteDefaultTime(LocalDateTime newDateTime) {
        dateTime = newDateTime;
        isCustomDate = true;
    }

    public void resetToDefaultTime() {
        isCustomDate = false;
    }

    public LocalDateTime getLocalDateTime() {
        if (isCustomDate) {
            return dateTime;

        } else {
            return LocalDateTime.now();
        }


    }
}
