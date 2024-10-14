package org.example.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.DateTimeException;
import java.time.ZoneId;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.*;

class TimezoneServiceTest {

    private TimezoneService timezoneService;

    @BeforeEach
    public void setUp() {
        timezoneService = new TimezoneService();
    }

    @Test
    void testGetZoneId_NullInput_DefaultsToUTC() {
        ZoneId zoneId = timezoneService.getZoneId(null);
        assertEquals(ZoneId.of("UTC"), zoneId);
    }

    @Test
    void testGetZoneId_EmptyInput_DefaultsToUTC() {
        ZoneId zoneId = timezoneService.getZoneId("   ");
        assertEquals(ZoneId.of("UTC"), zoneId);
    }

    @Test
    void testGetZoneIdWithValidTimezone() {
        ZoneId zoneId = timezoneService.getZoneId("America/New_York");
        assertEquals(ZoneId.of("America/New_York"), zoneId);
    }

    @Test
    void testGetZoneIdWithUTCOffset() {
        ZoneId zoneId = timezoneService.getZoneId("UTC+5");
        assertEquals(ZoneId.ofOffset("UTC", ZoneOffset.of("+05:00")), zoneId);
    }

    @Test
    void testGetZoneId_ValidUTCOffset_Negative() {
        ZoneId zoneId = timezoneService.getZoneId("UTC-3");
        assertEquals(ZoneId.ofOffset("UTC", ZoneOffset.ofHours(-3)), zoneId);
    }

    @Test
    void testGetZoneId_UTCOffsetWithoutPlusOrMinus() {
        ZoneId zoneId = timezoneService.getZoneId("UTC5");
        assertEquals(ZoneId.ofOffset("UTC", ZoneOffset.ofHours(5)), zoneId);
    }

    @Test
    void testGetZoneId_InvalidUTCOffset_FormatException() {
        assertThrows(DateTimeException.class, () -> {
            timezoneService.getZoneId("UTC++5");
        });
    }

    @Test
    void testGetZoneIdWithInvalidTimezone() {
        assertThrows(DateTimeException.class, () -> {
            timezoneService.getZoneId("Invalid/Timezone");
        });
    }

    @Test
    void testGetZoneId_ValidUTCOffsetWithMinutes() {
        ZoneId zoneId = timezoneService.getZoneId("UTC+05:30");
        assertEquals(ZoneId.ofOffset("UTC", ZoneOffset.ofHoursMinutes(5, 30)), zoneId);
    }

    @Test
    void testGetZoneId_NoOffsetAfterUTC_DefaultsToUTC() {
        ZoneId zoneId = timezoneService.getZoneId("UTC");
        assertEquals(ZoneId.of("UTC"), zoneId);
    }
}
