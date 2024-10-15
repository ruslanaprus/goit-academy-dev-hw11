package org.example.controller;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.service.TimezoneService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.DateTimeException;
import java.time.ZoneId;

@WebFilter(value = "/time")
public class TimezoneValidateFilter extends HttpFilter {
    private static final Logger logger = LoggerFactory.getLogger(TimezoneValidateFilter.class);

    private TimezoneService timezoneService;
    private TemplateEngine engine;

    public TimezoneValidateFilter() {
        this.timezoneService = new TimezoneService();
    }

    public void setTimezoneService(TimezoneService timezoneService) {
        this.timezoneService = timezoneService;
    }

    @Override
    public void init() throws ServletException {
        super.init();
        this.engine = (TemplateEngine) getServletContext().getAttribute("templateEngine");
    }

    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {
        String timezoneParam = req.getParameter("timezone");
        logger.debug("Validating timezone parameter: {}", timezoneParam);

        ZoneId zoneId;
        try {
            zoneId = timezoneService.getZoneId(timezoneParam);
            req.setAttribute("zoneId", zoneId);
        } catch (DateTimeException | IllegalArgumentException e) {
            logger.error("Invalid timezone parameter: {}", timezoneParam, e);
            renderErrorPage(res, "Invalid timezone parameter!", HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        chain.doFilter(req, res);
    }

    private void renderErrorPage(HttpServletResponse res, String errorMessage, int statusCode) throws IOException {
        res.setStatus(statusCode);
        res.setContentType("text/html");
        res.setCharacterEncoding("UTF-8");

        Context context = new Context();
        context.setVariable("statusCode", statusCode);
        context.setVariable("message", errorMessage);

        try (PrintWriter writer = res.getWriter()) {
            engine.process("error", context, writer);
        }

        logger.debug("Rendered error page with status {} and message: {}", statusCode, errorMessage);
    }
}