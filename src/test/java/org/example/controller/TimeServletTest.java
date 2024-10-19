package org.example.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.service.TimeResponseBuilder;
import org.example.service.TimezoneCookieService;
import org.example.util.ThymeleafRenderer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.thymeleaf.TemplateEngine;

import java.io.IOException;
import java.time.DateTimeException;
import java.time.ZoneId;

import static org.mockito.Mockito.*;

class TimeServletTest {

    @Mock
    private TimeResponseBuilder timeResponseBuilder;

    @Mock
    private TimezoneCookieService timezoneCookieService;

    @Mock
    private TemplateEngine templateEngine;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private ThymeleafRenderer thymeleafRenderer;

    @InjectMocks
    private TimeServlet timeServlet;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        timeServlet.setThymeleafRenderer(thymeleafRenderer);
    }

    @Test
    void testDoGetWithValidTimezone() throws ServletException, IOException {
        String validTimezone = "America/New_York";
        ZoneId zoneId = ZoneId.of(validTimezone);

        when(request.getParameter("timezone")).thenReturn(validTimezone);
        when(timeResponseBuilder.getFormattedTime(zoneId)).thenReturn("10:00 AM");

        timeServlet.doGet(request, response);

        verify(timezoneCookieService).storeTimezoneInCookie(response, zoneId);
        verify(timeResponseBuilder).getFormattedTime(zoneId);
        verify(response, never()).sendError(anyInt());
    }

    @Test
    void testDoGetWithInvalidTimezone() throws ServletException, IOException {
        String invalidTimezone = "Invalid/Zone";
        when(request.getParameter("timezone")).thenReturn(invalidTimezone);
        doThrow(DateTimeException.class).when(timezoneCookieService).storeTimezoneInCookie(any(), any());

        timeServlet.doGet(request, response);

        verify(timeResponseBuilder, never()).getFormattedTime(any());
        verify(timezoneCookieService, never()).storeTimezoneInCookie(any(), any());
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    void testDoGetWithoutTimezoneParameter() throws ServletException, IOException {
        ZoneId defaultZoneId = ZoneId.of("UTC");
        when(request.getAttribute("zoneId")).thenReturn(defaultZoneId);
        when(timeResponseBuilder.getFormattedTime(defaultZoneId)).thenReturn("15:00");

        timeServlet.doGet(request, response);

        verify(timeResponseBuilder).getFormattedTime(defaultZoneId);
        verify(response, never()).sendError(anyInt());
    }
}