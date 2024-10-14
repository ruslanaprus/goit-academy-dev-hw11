package org.example.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class TimeResponseBuilder {
    private static final Logger logger = LoggerFactory.getLogger(TimeResponseBuilder.class);
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public String getFormattedTime(ZoneId zoneId) {
        ZonedDateTime zonedDateTime = ZonedDateTime.now(zoneId);
        String formattedTime = zonedDateTime.format(FORMATTER);
        String formattedTimeWithZone = formattedTime + " UTC" + zonedDateTime.getOffset().getId();

        logger.info("Formatted time for zone {}: {}", zoneId.getId(), formattedTimeWithZone);

        return formattedTimeWithZone;
    }
}