package org.example.controller;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.service.TimezoneCookieService;
import org.example.service.TimezoneService;
import org.example.util.ThymeleafRenderer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
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
    }

//    @Test
//    public void testInvalidTimezoneParameter() throws ServletException, IOException {
//        when(request.getParameter("timezone")).thenReturn("Invalid/Timezone");
//        when(timezoneService.getZoneId("Invalid/Timezone")).thenThrow(new DateTimeException("Invalid timezone"));
//
//        // Mock ThymeleafRenderer static method
//        try (MockedStatic<ThymeleafRenderer> mockedRenderer = mockStatic(ThymeleafRenderer.class)) {
//            // Act
//            filter.doFilter(request, response, filterChain);
//
//            // Assert
//            verify(request, never()).setAttribute(eq("zoneId"), any(ZoneId.class));
//            verify(filterChain, never()).doFilter(request, response);
//            verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
//
//            // Verify that ThymeleafRenderer.renderErrorPage was called with the right arguments
//            mockedRenderer.verify(() ->
//                    ThymeleafRenderer.renderErrorPage(eq(response), eq(templateEngine), eq("You shall not paws!"), eq(HttpServletResponse.SC_BAD_REQUEST))
//            );
//        }
//    }

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