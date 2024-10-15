package org.example.util;

import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.IOException;
import java.io.PrintWriter;

public class ThymeleafRenderer {
    private static final Logger logger = LoggerFactory.getLogger(ThymeleafRenderer.class);

    public static void render(HttpServletResponse res, TemplateEngine engine, String templateName, Context context) throws IOException {
        res.setContentType("text/html");
        res.setCharacterEncoding("UTF-8");

        try (PrintWriter writer = res.getWriter()) {
            engine.process(templateName, context, writer);
        } catch (IOException e) {
            logger.error("Error rendering Thymeleaf template '{}'", templateName, e);
            throw e;
        }
    }

    public static void renderErrorPage(HttpServletResponse res, TemplateEngine engine, String errorMessage, int statusCode) throws IOException {
        res.setStatus(statusCode);

        Context context = new Context();
        context.setVariable("statusCode", statusCode);
        context.setVariable("message", errorMessage);

        render(res, engine, "error", context);
        logger.debug("Rendered error page with status {} and message: {}", statusCode, errorMessage);
    }
}