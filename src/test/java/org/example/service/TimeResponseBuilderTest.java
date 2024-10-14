package org.example.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;

class TimeResponseBuilderTest {
    private TimeResponseBuilder timeResponseBuilder;

    @BeforeEach
    void setUp() {
        timeResponseBuilder = new TimeResponseBuilder();
    }

    @Test
    void testGetFormattedTime_ValidZoneId(){
        ZoneId zoneId = ZoneId.of("UTC+2");
        String formattedTime = timeResponseBuilder.getFormattedTime(zoneId);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        ZonedDateTime now = ZonedDateTime.now(zoneId);
        String expectedTime = now.format(formatter) + " UTC" + now.getOffset().getId();

        assertEquals(expectedTime, formattedTime);
    }

    @Test
    void testGetFormattedTime_UTC(){
        ZoneId zoneId = ZoneId.of("UTC");
        String formattedTime = timeResponseBuilder.getFormattedTime(zoneId);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        ZonedDateTime now = ZonedDateTime.now(zoneId);
        String expectedTime = now.format(formatter) + " UTC" + now.getOffset().getId();

        assertEquals(expectedTime, formattedTime);
    }

    @Test
    void testGetFormattedTime_TimeZoneId(){
        ZoneId zoneId = ZoneId.of("Europe/Berlin");
        String formattedTime = timeResponseBuilder.getFormattedTime(zoneId);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        ZonedDateTime now = ZonedDateTime.now(zoneId);
        String expectedTime = now.format(formatter) + " UTC" + now.getOffset().getId();

        assertEquals(expectedTime, formattedTime);
    }
}