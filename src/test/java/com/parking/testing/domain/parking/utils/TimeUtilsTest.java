package com.parking.testing.domain.parking.utils;


import com.parking.domain.parking.utils.TimeUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TimeUtilsTest {

    private LocalDateTime localDateTime;
    private LocalDateTime now;

    @BeforeEach
    void setUp(){
        localDateTime = LocalDateTime.now().minus(40L, ChronoUnit.MINUTES);
        now = LocalDateTime.now();
    }

    @Test
    void whenCalculateDiffMinutes_thenReturnTimeSpent() {

        assertEquals("0 hours 40 minutes 0 seconds", TimeUtils.diffTime(localDateTime.toLocalTime(), now.toLocalTime()));
    }

    @Test
    void whenCalculateDiffHours_thenReturnTimeSpent() {
        localDateTime = LocalDateTime.now().minus(80L, ChronoUnit.MINUTES);
        assertEquals("1 hours 19 minutes 59 seconds", TimeUtils.diffTime(localDateTime.toLocalTime(), now.toLocalTime()));
    }

    @Test
    void whenCalculateDiffSeconds_thenReturnTimeSpent() {
        localDateTime = LocalDateTime.now().minus(20L, ChronoUnit.SECONDS);
        assertEquals("0 hours 0 minutes 19 seconds", TimeUtils.diffTime(localDateTime.toLocalTime(), now.toLocalTime()));
    }
}
