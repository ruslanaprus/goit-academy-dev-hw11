package org.example.controller;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.text.StringEscapeUtils;
import org.example.service.TimezoneService;
import org.example.util.ErrorResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.DateTimeException;
import java.time.ZoneId;

@WebFilter(value = "/time")
public class TimezoneValidateFilter extends HttpFilter {
    private static final Logger logger = LoggerFactory.getLogger(TimezoneValidateFilter.class);

    private TimezoneService timezoneService;

    public TimezoneValidateFilter() {
        this.timezoneService = new TimezoneService();
    }

    public void setTimezoneService(TimezoneService timezoneService) {
        this.timezoneService = timezoneService;
    }

    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {
        String timezoneParam = StringEscapeUtils.escapeHtml4(req.getParameter("timezone"));
        logger.debug("Validating timezone parameter: {}", timezoneParam);

        ZoneId zoneId = null;
        try {
            zoneId = timezoneService.getZoneId(timezoneParam);
            req.setAttribute("zoneId", zoneId);
        } catch (DateTimeException | IllegalArgumentException e) {
            logger.error("Invalid timezone parameter: {}", timezoneParam, e);
            ErrorResponseUtil.sendBadRequest(req, res, "Invalid timezone parameter!");
            return;
        }

        chain.doFilter(req, res);
    }
}