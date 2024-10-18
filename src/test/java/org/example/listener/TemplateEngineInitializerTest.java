package org.example.listener;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templateresolver.FileTemplateResolver;
import org.thymeleaf.templatemode.TemplateMode;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class TemplateEngineInitializerTest {

    private TemplateEngineInitializer initializer;
    private ServletContextEvent servletContextEvent;
    private ServletContext servletContext;

    @BeforeEach
    void setUp() {
        initializer = new TemplateEngineInitializer();
        servletContextEvent = mock(ServletContextEvent.class);
        servletContext = mock(ServletContext.class);

        when(servletContextEvent.getServletContext()).thenReturn(servletContext);
    }

    @Test
    void testContextInitialized() {
        when(servletContext.getRealPath("/WEB-INF/templates/")).thenReturn("/mocked/path/to/templates/");

        initializer.contextInitialized(servletContextEvent);

        // verify that the "templateEngine" attribute is set in the ServletContext
        ArgumentCaptor<TemplateEngine> captor = ArgumentCaptor.forClass(TemplateEngine.class);
        verify(servletContext).setAttribute(eq("templateEngine"), captor.capture());

        // capture the template engine instance
        TemplateEngine templateEngine = captor.getValue();
        assertNotNull(templateEngine, "TemplateEngine should not be null");

        // verify the template engine's resolver configuration
        assertEquals(1, templateEngine.getTemplateResolvers().size(), "TemplateEngine should have one resolver");

        // verify the resolver settings
        FileTemplateResolver resolver = (FileTemplateResolver) templateEngine.getTemplateResolvers().iterator().next();
        assertEquals("/mocked/path/to/templates/", resolver.getPrefix());
        assertEquals(".html", resolver.getSuffix());
        assertEquals(TemplateMode.HTML, resolver.getTemplateMode(), "Template mode should be HTML");
        assertEquals("UTF-8", resolver.getCharacterEncoding());
        assertEquals(true, resolver.isCacheable(), "Cacheable should be true");
    }
}
