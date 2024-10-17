package org.example.service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.DateTimeException;
import java.time.ZoneId;

public class TimezoneCookieService {
    private static final Logger logger = LoggerFactory.getLogger(TimezoneCookieService.class);
    private static final String LAST_TIMEZONE_COOKIE_NAME = "lastTimezone";
    private static final int COOKIE_MAX_AGE = 60 * 60;

    private TimezoneService timezoneService;

    public TimezoneCookieService(TimezoneService timezoneService) {
        this.timezoneService = timezoneService;
    }

    public ZoneId getTimezoneFromCookies(HttpServletRequest req) {
        Cookie[] cookies = req.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (LAST_TIMEZONE_COOKIE_NAME.equals(cookie.getName())) {
                    String timezone = cookie.getValue();
                    try {
                        return timezoneService.getZoneId(timezone);
                    } catch (DateTimeException e) {
                        logger.warn("Invalid timezone in cookie: {}", timezone, e);
                    }
                }
            }
        }
        // fallback to UTC if no valid timezone found in cookies
        return ZoneId.of("UTC");
    }

    public void storeTimezoneInCookie(HttpServletResponse res, ZoneId zoneId) {
        Cookie cookie = new Cookie(LAST_TIMEZONE_COOKIE_NAME, zoneId.getId());
        cookie.setMaxAge(COOKIE_MAX_AGE);
        cookie.setPath("/time");
        res.addCookie(cookie);
        logger.info("Stored timezone in cookie: {}", zoneId.getId());
    }
}