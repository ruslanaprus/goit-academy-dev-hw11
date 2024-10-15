package org.example.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.service.TimeResponseBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.FileTemplateResolver;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.ZoneId;

@WebServlet(value = "/time")
public class TimeServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(TimeServlet.class);

    private TimeResponseBuilder timeResponseBuilder;
    private TemplateEngine engine;

    public TimeServlet() {
        this.timeResponseBuilder = new TimeResponseBuilder();
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

        if (zoneId == null) {
            logger.error("ZoneId not found in request attributes.");
            sendThymeleafError(res, "Timezone information missing.", HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        String formattedTime = timeResponseBuilder.getFormattedTime(zoneId);

        res.setContentType("text/html");
        res.setCharacterEncoding("UTF-8");

        Context context = new Context();
        context.setVariable("zoneId", zoneId.getId());
        context.setVariable("formattedTime", formattedTime);

        try (PrintWriter writer = res.getWriter()) {
            engine.process("time", context, res.getWriter());
            logger.info("TimeServlet request completed successfully.");
        } catch (IOException e) {
            logger.error("I/O error occurred.", e);
            sendThymeleafError(res, "Internal server error", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Template processing error.", e);
            sendThymeleafError(res, "Internal server error", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void sendThymeleafError(HttpServletResponse res, String errorMessage, int statusCode) throws IOException {
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