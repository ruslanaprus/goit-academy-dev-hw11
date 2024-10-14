package org.example.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.DateTimeException;
import java.time.ZoneId;
import java.time.ZoneOffset;

public class TimezoneService {
    private static final Logger logger = LoggerFactory.getLogger(TimezoneService.class);
    private static final String DEFAULT_TIMEZONE = "UTC";
    private static final String UTC_PREFIX = "UTC";

    public ZoneId getZoneId(String timezoneParam) {
        if (timezoneParam == null || timezoneParam.trim().isEmpty()) {
            return ZoneId.of(DEFAULT_TIMEZONE);
        }

        String trimmedParam = timezoneParam.trim();

        if (trimmedParam.length() >= UTC_PREFIX.length() &&
                trimmedParam.substring(0, UTC_PREFIX.length()).equalsIgnoreCase(UTC_PREFIX)) {

            String offsetStr = trimmedParam.substring(UTC_PREFIX.length()).trim();

            if (offsetStr.isEmpty()) {
                return ZoneId.of(DEFAULT_TIMEZONE);
            }

            if (!offsetStr.startsWith("+") && !offsetStr.startsWith("-")) {
                offsetStr = "+" + offsetStr;
            }

            try {
                ZoneOffset offset = ZoneOffset.of(offsetStr);
                ZoneId zoneId = ZoneId.ofOffset(UTC_PREFIX, offset);
                logger.info("Parsed ZoneId from UTC offset: {}", zoneId);
                return zoneId;
            } catch (DateTimeException e) {
                logger.error("Invalid UTC offset format: '{}'", offsetStr, e);
                throw new DateTimeException("Invalid UTC offset format: " + offsetStr, e);
            }
        }

        try {
            ZoneId zoneId = ZoneId.of(trimmedParam);
            logger.info("Parsed ZoneId: {}", zoneId);
            return zoneId;
        } catch (DateTimeException e) {
            logger.error("Invalid timezone format: '{}'", trimmedParam, e);
            throw new DateTimeException("Invalid timezone format: " + trimmedParam, e);
        }
    }
}