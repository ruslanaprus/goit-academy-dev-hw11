package org.example.util;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.mockito.Mockito.*;

class ErrorResponseUtilTest {
    private HttpServletRequest mockRequest;
    private HttpServletResponse mockResponse;
    private RequestDispatcher mockDispatcher;

    @BeforeEach
    void setUp() throws IOException {
        mockRequest = mock(HttpServletRequest.class);
        mockResponse = mock(HttpServletResponse.class);
        mockDispatcher = mock(RequestDispatcher.class);
        when(mockRequest.getRequestDispatcher("/WEB-INF/views/error.jsp")).thenReturn(mockDispatcher);
    }

    @Test
    void testSendRequest_SetsStatusAndContentType() throws IOException, ServletException {
        String errorMessage = "Invalid inout!";

        // call the method under test
        ErrorResponseUtil.sendBadRequest(mockRequest, mockResponse, errorMessage);

        // verify that the response status was set to 400
        verify(mockResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);

        // verify that the request attributes were set correctly
        verify(mockRequest).setAttribute("statusCode", HttpServletResponse.SC_BAD_REQUEST);
        verify(mockRequest).setAttribute("message", errorMessage);

        // verify that the RequestDispatcher was used to forward the request
        verify(mockDispatcher).forward(mockRequest, mockResponse);
    }
}