package com.parking.domain.parking.utils;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

public class TimeUtils {
    private TimeUtils(){}

    public static String diffTime(LocalTime registrationTime, LocalTime exitTime){
        long hours = ChronoUnit.HOURS.between(registrationTime, exitTime);
        long minutes = ChronoUnit.MINUTES.between(registrationTime, exitTime) % 60;
        long seconds = ChronoUnit.SECONDS.between(registrationTime, exitTime) % 60;
        return  hours + " hours " + minutes + " minutes " + seconds + " seconds";
    }
}