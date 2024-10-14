package org.example.util;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class ErrorResponseUtil {
    private static final Logger logger = LoggerFactory.getLogger(ErrorResponseUtil.class);

    public static void sendBadRequest(HttpServletRequest req, HttpServletResponse res, String errorMessage) throws IOException, ServletException {
        res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        req.setAttribute("statusCode", HttpServletResponse.SC_BAD_REQUEST);
        req.setAttribute("message", errorMessage);
        logger.debug("Forwarding to error page with status 400 and message: {}", errorMessage);

        RequestDispatcher dispatcher = req.getRequestDispatcher("/WEB-INF/views/error.jsp");
        dispatcher.forward(req, res);
    }
}