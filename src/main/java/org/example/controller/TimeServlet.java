package org.example.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.service.TimeResponseBuilder;
import org.example.util.ErrorResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.ZoneId;

@WebServlet(value = "/time")
public class TimeServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(TimeServlet.class);

    private TimeResponseBuilder timeResponseBuilder;

    public TimeServlet() {
        this.timeResponseBuilder = new TimeResponseBuilder();
    }

    public void setTimeResponseBuilder(TimeResponseBuilder timeResponseBuilder) {
        this.timeResponseBuilder = timeResponseBuilder;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        ZoneId zoneId = (ZoneId) req.getAttribute("zoneId");

        if (zoneId == null) {
            logger.error("ZoneId not found in request attributes.");
            ErrorResponseUtil.sendBadRequest(req, resp, "Timezone information missing.");
            return;
        }

        String formattedTime = timeResponseBuilder.getFormattedTime(zoneId);

        req.setAttribute("formattedTime", formattedTime);
        req.setAttribute("zoneId", zoneId.getId());

        logger.debug("Forwarding to JSP with zoneId: {}", zoneId.getId());

        RequestDispatcher dispatcher = req.getRequestDispatcher("/WEB-INF/views/time.jsp");
        dispatcher.forward(req, resp);

        logger.info("TimeServlet request completed successfully.");
    }
}