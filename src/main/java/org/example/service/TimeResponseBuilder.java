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

    public String getCatImageForTime(ZonedDateTime zonedDateTime) {
        int hour = zonedDateTime.getHour();

        return switch (hour) {
            case 0, 1, 2, 3, 4 -> "/images/sleeping_cat_at_night.png";
            case 5 -> "/images/playful_cat.png";
            case 6, 11, 17, 20 -> "/images/hungry_cat.png";
            case 7, 14, 16, 22 -> "/images/sleepy_cat.png";
            case 8 -> "/images/friendly_cat.png";
            case 9 -> "/images/adventure_cat.png";
            case 10 -> "/images/cute_cat.png";
            case 12, 13 -> "/images/playing_with_friend.png";
            case 15, 23 -> "/images/stretching_cat.png";
            case 18 -> "/images/devious_cat.png";
            case 19 -> "/images/cat_with_human.png";
            case 21 -> "/images/cat_in_a_box.png";
            default -> "/images/sleepy_cat.png";
        };
    }

    public String getCatActivityMessage(ZonedDateTime zonedDateTime) {
        int hour = zonedDateTime.getHour();
        String activityMessage;

        switch (hour) {
            case 0, 1, 2, 3, 4 -> activityMessage = "The cat is sleeping. Don't dare to make a noise";
            case 5 -> activityMessage = "Beware! The cat is ready to wake the human up";
            case 6, 11, 17, 20 -> activityMessage = "The cat is hungry. Feed the cat!";
            case 7, 14, 16, 22 -> activityMessage = "The cat is still sleepy. Don't disturb";
            case 8 -> activityMessage = "The cat is on an adventure";
            case 9 -> activityMessage = "The cat is exploring the world";
            case 10 -> activityMessage = "The cat is ready to take a nap";
            case 12, 13 -> activityMessage = "The cat is meeting with friends";
            case 15, 23 -> activityMessage = "The cat is getting sleepy";
            case 18 -> activityMessage = "The cat is feeling mischievous";
            case 19 -> activityMessage = "The cat needs attention";
            case 21 -> activityMessage = "The cat looks cute!";
            default -> activityMessage = "The cat's activity is unknown.";
        }
        return activityMessage;
    }
}