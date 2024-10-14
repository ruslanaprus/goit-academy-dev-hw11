package org.example.controller;

import jakarta.servlet.FilterChain;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.service.TimezoneService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.DateTimeException;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class TimezoneValidateFilterTest {
    private TimezoneValidateFilter filter;
    private TimezoneService mockTimezoneService;
    private HttpServletRequest mockRequest;
    private HttpServletResponse mockResponse;
    private RequestDispatcher mockDispatcher;
    private FilterChain mockFilterChain;
    private StringWriter stringWriter;
    private PrintWriter printWriter;

    @BeforeEach
    void setUp() throws IOException {
        filter = new TimezoneValidateFilter();
        mockTimezoneService = mock(TimezoneService.class);
        filter.setTimezoneService(mockTimezoneService);

        mockRequest = mock(HttpServletRequest.class);
        mockResponse = mock(HttpServletResponse.class);
        mockDispatcher = mock(RequestDispatcher.class);
        mockFilterChain = mock(FilterChain.class);

        // mock the RequestDispatcher to avoid NullPointerException
        when(mockRequest.getRequestDispatcher("/WEB-INF/views/error.jsp")).thenReturn(mockDispatcher);

        // set up the StringWriter and PrintWriter to capture the response output
        stringWriter = new StringWriter();
        printWriter = new PrintWriter(stringWriter);
        when(mockResponse.getWriter()).thenReturn(printWriter);
    }

    @Test
    void testDoFilter_ValidTimezone_ContinuesFilterChain() throws IOException, ServletException {
        // arrange: Simulate valid timezone parameter
        String timezoneParam = "UTC+2";
        ZoneId zoneId = ZoneId.of("UTC+02:00");
        when(mockRequest.getParameter("timezone")).thenReturn(timezoneParam);
        when(mockTimezoneService.getZoneId(timezoneParam)).thenReturn(zoneId);

        // act: Invoke doFilter
        filter.doFilter(mockRequest, mockResponse, mockFilterChain);

        // assert: Verify that zoneId is set as request attribute
        verify(mockRequest).setAttribute("zoneId", zoneId);

        // assert: Verify that the filter chain was continued
        verify(mockFilterChain).doFilter(mockRequest, mockResponse);

        // assert: Verify that setStatus and setContentType were not called
        verify(mockResponse, never()).setStatus(anyInt());
        verify(mockResponse, never()).setContentType(anyString());

        // assert: Verify that no content was written to the response
        assertEquals("", stringWriter.toString());
    }

    @Test
    void testDoFilter_InvalidTimezone_SendsBadRequest() throws IOException, ServletException {
        String timezoneParam = "Invalid/Timezone";

        when(mockRequest.getParameter("timezone")).thenReturn(timezoneParam);
        when(mockTimezoneService.getZoneId(timezoneParam)).thenThrow(new DateTimeException("Invalid timezone"));

        filter.doFilter(mockRequest, mockResponse, mockFilterChain);

        // verify that the response status was set to 400 and content type is text/html
        verify(mockResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verify(mockRequest).setAttribute("statusCode", HttpServletResponse.SC_BAD_REQUEST);
        verify(mockRequest).setAttribute("message", "Invalid timezone parameter!");

        // verify that forward was called to the error page
        verify(mockDispatcher).forward(mockRequest, mockResponse);

        // verify that the filter chain was not continued
        verify(mockFilterChain, never()).doFilter(mockRequest, mockResponse);
    }

    @Test
    void testDoFilter_NoTimezoneParameter_DefaultsToUTC() throws IOException, ServletException {
        String nullTimezone = null;
        ZoneId defaultZoneId = ZoneId.of("UTC");

        when(mockRequest.getParameter("timezone")).thenReturn(nullTimezone);
        when(mockTimezoneService.getZoneId(nullTimezone)).thenReturn(defaultZoneId);

        filter.doFilter(mockRequest, mockResponse, mockFilterChain);

        verify(mockTimezoneService).getZoneId(nullTimezone);
        verify(mockRequest).setAttribute("zoneId", defaultZoneId);
        verify(mockFilterChain).doFilter(mockRequest, mockResponse);
        verify(mockResponse, never()).setStatus(anyInt());
    }
}