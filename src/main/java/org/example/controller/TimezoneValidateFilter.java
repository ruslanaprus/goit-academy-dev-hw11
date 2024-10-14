package org.example.controller;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.text.StringEscapeUtils;
import org.example.service.TimezoneService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.FileTemplateResolver;

import java.io.IOException;
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
        engine = new TemplateEngine();

        FileTemplateResolver resolver = new FileTemplateResolver();
        resolver.setPrefix(getServletContext().getRealPath("/WEB-INF/templates/"));
        resolver.setSuffix(".html");
        resolver.setTemplateMode("HTML5");
        resolver.setCacheable(false);
        engine.addTemplateResolver(resolver);
    }

    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {
        String timezoneParam = StringEscapeUtils.escapeHtml4(req.getParameter("timezone"));
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

        engine.process("error", context, res.getWriter());
        res.getWriter().close();

        logger.debug("Rendered error page with status {} and message: {}", statusCode, errorMessage);
    }
}