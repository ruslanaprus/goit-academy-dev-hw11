package org.example.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.service.TimeResponseBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.ZoneId;

import static org.mockito.Mockito.*;

class TimeServletTest {

    private TimeServlet servlet;
    private HttpServletRequest mockRequest;
    private HttpServletResponse mockResponse;
    private RequestDispatcher mockDispatcher;
    private TimeResponseBuilder mockTimeResponseBuilder;
    private StringWriter stringWriter;
    private PrintWriter printWriter;

    @BeforeEach
    void setUp() throws IOException {
        servlet = new TimeServlet();
        mockRequest = mock(HttpServletRequest.class);
        mockResponse = mock(HttpServletResponse.class);
        mockDispatcher = mock(RequestDispatcher.class);
        mockTimeResponseBuilder = mock(TimeResponseBuilder.class);

        // inject the mock TimeResponseBuilder
        servlet.setTimeResponseBuilder(mockTimeResponseBuilder);

        // set up the StringWriter and PrintWriter to capture the response output
        stringWriter = new StringWriter();
        printWriter = new PrintWriter(stringWriter);
        when(mockResponse.getWriter()).thenReturn(printWriter);
    }

    @Test
    void testDoGet_ValidZoneId_ForwardsToJsp() throws IOException, ServletException {
        ZoneId zoneId = ZoneId.of("UTC+2");
        String formattedTime = "2024-10-12 15:30:00 UTC+02:00";

        when(mockRequest.getAttribute("zoneId")).thenReturn(zoneId);
        when(mockTimeResponseBuilder.getFormattedTime(zoneId)).thenReturn(formattedTime);
        when(mockRequest.getRequestDispatcher("/WEB-INF/views/time.jsp")).thenReturn(mockDispatcher);

        servlet.doGet(mockRequest, mockResponse);

        verify(mockTimeResponseBuilder).getFormattedTime(zoneId);
        verify(mockRequest).setAttribute("formattedTime", formattedTime);
        verify(mockRequest).setAttribute("zoneId", zoneId.getId());
        verify(mockDispatcher).forward(mockRequest, mockResponse);
        verify(mockResponse, never()).setStatus(anyInt());
    }

    @Test
    void testDoGet_MissingZoneId_SendsBadRequest() throws IOException, ServletException {
        when(mockRequest.getAttribute("zoneId")).thenReturn(null);

        // mock the RequestDispatcher for the error page
        RequestDispatcher errorDispatcher = mock(RequestDispatcher.class);
        when(mockRequest.getRequestDispatcher("/WEB-INF/views/error.jsp")).thenReturn(errorDispatcher);

        servlet.doGet(mockRequest, mockResponse);

        // verify that the error response was set correctly
        verify(mockResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verify(mockRequest).setAttribute("statusCode", HttpServletResponse.SC_BAD_REQUEST);
        verify(mockRequest).setAttribute("message", "Timezone information missing.");

        // verify that the error page is forwarded
        verify(errorDispatcher).forward(mockRequest, mockResponse);
    }
}