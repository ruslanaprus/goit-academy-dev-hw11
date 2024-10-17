package org.example.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.service.TimeResponseBuilder;
import org.example.service.TimezoneService;
import org.example.util.ThymeleafRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.IOException;
import java.time.DateTimeException;
import java.time.ZoneId;

@WebServlet(value = "/time")
public class TimeServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(TimeServlet.class);

    private TimeResponseBuilder timeResponseBuilder;
    private TimezoneService timezoneService;
    private TemplateEngine engine;

    public TimeServlet() {
        this.timeResponseBuilder = new TimeResponseBuilder();
        this.timezoneService = new TimezoneService();
    }

    public void setTimeResponseBuilder(TimeResponseBuilder timeResponseBuilder) {
        this.timeResponseBuilder = timeResponseBuilder;
    }

    @Override
    public void init() throws ServletException {
        super.init();
        this.engine = (TemplateEngine) getServletContext().getAttribute("templateEngine");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        ZoneId zoneId = (ZoneId) req.getAttribute("zoneId");

        String timezoneParam = req.getParameter("timezone");

        if (timezoneParam != null) {
            try {
                zoneId = timezoneService.getZoneId(timezoneParam);

                // store the valid time zone in a cookie if it's passed in the query
                Cookie lastTimezoneCookie = new Cookie("lastTimezone", zoneId.getId());
                lastTimezoneCookie.setMaxAge(60 * 60);
                lastTimezoneCookie.setPath("/time");
                res.addCookie(lastTimezoneCookie);
            } catch (DateTimeException e) {
                logger.warn("Invalid timezone parameter: {}", timezoneParam, e);
                ThymeleafRenderer.renderErrorPage(res, engine, "Invalid timezone parameter!", HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
        } else if (zoneId == null) {
            zoneId = ZoneId.of("UTC");
        }

        String formattedTime = timeResponseBuilder.getFormattedTime(zoneId);

        res.setContentType("text/html");
        res.setCharacterEncoding("UTF-8");

        Context context = new Context();
        context.setVariable("zoneId", zoneId.getId());
        context.setVariable("formattedTime", formattedTime);

        try {
            ThymeleafRenderer.render(res, engine, "time", context);
            logger.info("TimeServlet request completed successfully.");
        } catch (IOException e) {
            logger.error("I/O error occurred.", e);
            ThymeleafRenderer.renderErrorPage(res, engine, "Internal server error", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Template processing error.", e);
            ThymeleafRenderer.renderErrorPage(res, engine, "Internal server error", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}