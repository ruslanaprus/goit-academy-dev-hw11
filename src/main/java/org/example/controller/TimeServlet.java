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
        engine = new TemplateEngine();

        FileTemplateResolver resolver = new FileTemplateResolver();
        resolver.setPrefix(getServletContext().getRealPath("/WEB-INF/templates/"));
        resolver.setSuffix(".html");
        resolver.setTemplateMode("HTML5");
        resolver.setCharacterEncoding("UTF-8");
        resolver.setOrder(engine.getTemplateResolvers().size());
        resolver.setCacheable(false);
        engine.addTemplateResolver(resolver);
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

        try {
            engine.process("time", context, res.getWriter());
            logger.info("TimeServlet request completed successfully.");
        } catch (Exception e){
            logger.error("TimeServlet request failed.", e);
            sendThymeleafError(res, "Internal server error", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } finally {
            res.getWriter().close();
        }
    }

    private void sendThymeleafError(HttpServletResponse res, String errorMessage, int statusCode) throws IOException {
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