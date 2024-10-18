package org.example.service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.DateTimeException;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class TimezoneCookieServiceTest {

    @Mock
    private TimezoneService timezoneService;

    @InjectMocks
    private TimezoneCookieService timezoneCookieService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetTimezoneFromCookies_ValidCookie() {
        HttpServletRequest req = mock(HttpServletRequest.class);
        Cookie[] cookies = { new Cookie("lastTimezone", "America/New_York") };
        when(req.getCookies()).thenReturn(cookies);
        when(timezoneService.getZoneId("America/New_York")).thenReturn(ZoneId.of("America/New_York"));

        ZoneId result = timezoneCookieService.getTimezoneFromCookies(req);

        assertEquals(ZoneId.of("America/New_York"), result);
    }

    @Test
    void testGetTimezoneFromCookies_InvalidCookie() {
        HttpServletRequest req = mock(HttpServletRequest.class);
        Cookie[] cookies = { new Cookie("lastTimezone", "Invalid/Timezone") };
        when(req.getCookies()).thenReturn(cookies);
        when(timezoneService.getZoneId("Invalid/Timezone")).thenThrow(new DateTimeException("Invalid timezone"));

        ZoneId result = timezoneCookieService.getTimezoneFromCookies(req);

        assertEquals(ZoneId.of("UTC"), result);
    }

    @Test
    void testGetTimezoneFromCookies_NoCookies() {
        HttpServletRequest req = mock(HttpServletRequest.class);
        when(req.getCookies()).thenReturn(null);

        ZoneId result = timezoneCookieService.getTimezoneFromCookies(req);

        assertEquals(ZoneId.of("UTC"), result);
    }

    @Test
    void testGetTimezoneFromCookies_NoValidTimezoneCookie() {
        HttpServletRequest req = mock(HttpServletRequest.class);
        Cookie[] cookies = { new Cookie("someOtherCookie", "value") };
        when(req.getCookies()).thenReturn(cookies);

        ZoneId result = timezoneCookieService.getTimezoneFromCookies(req);

        assertEquals(ZoneId.of("UTC"), result);
    }

    @Test
    void testStoreTimezoneInCookie() {
        HttpServletResponse res = mock(HttpServletResponse.class);
        ZoneId zoneId = ZoneId.of("Europe/Paris");

        timezoneCookieService.storeTimezoneInCookie(res, zoneId);

        Cookie expectedCookie = new Cookie("lastTimezone", "Europe/Paris");
        expectedCookie.setMaxAge(60 * 60);
        expectedCookie.setPath("/time");

        verify(res).addCookie(argThat(cookie ->
                cookie.getName().equals(expectedCookie.getName()) &&
                        cookie.getValue().equals(expectedCookie.getValue()) &&
                        cookie.getMaxAge() == expectedCookie.getMaxAge() &&
                        "/time".equals(cookie.getPath())
        ));
    }
}