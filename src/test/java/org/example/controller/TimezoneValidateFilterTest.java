package org.example.controller;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.service.TimezoneCookieService;
import org.example.service.TimezoneService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.thymeleaf.TemplateEngine;

import java.io.IOException;
import java.time.DateTimeException;
import java.time.ZoneId;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class TimezoneValidateFilterTest {

    @Mock
    private TimezoneService timezoneService;

    @Mock
    private TimezoneCookieService timezoneCookieService;

    @Mock
    private TemplateEngine templateEngine;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private TimezoneValidateFilter filter;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        filter.setTimezoneService(timezoneService);
    }

    @Test
    public void testValidTimezoneParameter() throws ServletException, IOException {
        when(request.getParameter("timezone")).thenReturn("America/New_York");
        when(timezoneService.getZoneId("America/New_York")).thenReturn(ZoneId.of("America/New_York"));

        filter.doFilter(request, response, filterChain);

        verify(request).setAttribute(eq("zoneId"), eq(ZoneId.of("America/New_York")));
        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(templateEngine);
        verify(response, never()).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    void testDoFilter_InvalidTimezone() throws IOException, ServletException {
        when(request.getParameter("timezone")).thenReturn("Invalid/Timezone");
        when(timezoneService.getZoneId("Invalid/Timezone")).thenThrow(new DateTimeException("Invalid timezone"));

        filter.doFilter(request, response, filterChain);

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verify(response).setContentType("text/html");
        verify(response).setCharacterEncoding("UTF-8");
        verify(filterChain, never()).doFilter(any(HttpServletRequest.class), any(HttpServletResponse.class));
    }

    @Test
    public void testNoTimezoneParameterValidCookie() throws ServletException, IOException {
        when(request.getParameter("timezone")).thenReturn(null);
        when(timezoneCookieService.getTimezoneFromCookies(request)).thenReturn(ZoneId.of("Europe/London"));

        filter.doFilter(request, response, filterChain);

        verify(request).setAttribute(eq("zoneId"), eq(ZoneId.of("Europe/London")));
        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(templateEngine);
    }

    @Test
    public void testNoTimezoneParameterNoValidCookie() throws ServletException, IOException {
        when(request.getParameter("timezone")).thenReturn(null);
        when(timezoneCookieService.getTimezoneFromCookies(request)).thenReturn(ZoneId.of("UTC"));

        filter.doFilter(request, response, filterChain);

        verify(request).setAttribute(eq("zoneId"), eq(ZoneId.of("UTC")));
        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(templateEngine);
    }

    @Test
    public void testFilterChainContinuesForValidTimezone() throws ServletException, IOException {
        when(request.getParameter("timezone")).thenReturn("America/New_York");
        when(timezoneService.getZoneId("America/New_York")).thenReturn(ZoneId.of("America/New_York"));

        filter.doFilter(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    public void testFilterDoesNotContinueOnError() throws ServletException, IOException {
        when(request.getParameter("timezone")).thenReturn("Invalid/Timezone");
        when(timezoneService.getZoneId("Invalid/Timezone")).thenThrow(new DateTimeException("Invalid timezone"));

        filter.doFilter(request, response, filterChain);

        verify(filterChain, never()).doFilter(request, response);
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }
}