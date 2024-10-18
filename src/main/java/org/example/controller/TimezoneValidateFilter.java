package org.example.controller;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.service.TimezoneService;
import org.example.util.ThymeleafRenderer;
import org.example.service.TimezoneCookieService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.TemplateEngine;

import java.io.IOException;
import java.time.DateTimeException;
import java.time.ZoneId;

@WebFilter(value = "/time")
public class TimezoneValidateFilter extends HttpFilter {
    private static final Logger logger = LoggerFactory.getLogger(TimezoneValidateFilter.class);
    private TimezoneService timezoneService;
    private TemplateEngine engine;
    private TimezoneCookieService timezoneCookieService;

    public TimezoneValidateFilter() {
        this.timezoneService = new TimezoneService();
        this.timezoneCookieService = new TimezoneCookieService(timezoneService);
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
        ZoneId zoneId = null;

        if (timezoneParam != null) {
            try {
                zoneId = timezoneService.getZoneId(timezoneParam);
            } catch (DateTimeException e) {
                logger.warn("Invalid timezone parameter: {}", timezoneParam, e);
                ThymeleafRenderer.renderErrorPage(res, engine, "You shall not paws!", HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
        } else {
            zoneId = timezoneCookieService.getTimezoneFromCookies(req);
        }

        req.setAttribute("zoneId", zoneId);
        chain.doFilter(req, res);
    }
}