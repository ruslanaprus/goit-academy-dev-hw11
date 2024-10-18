package org.example.util;

import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.IOException;
import java.io.PrintWriter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class ThymeleafRendererTest {

    @Mock
    private HttpServletResponse response;

    @Mock
    private TemplateEngine templateEngine;

    @Mock
    private PrintWriter writer;

    @Mock
    private Logger logger;

    @InjectMocks
    private ThymeleafRenderer thymeleafRenderer;

    @BeforeEach
    public void setUp() throws IOException {
        MockitoAnnotations.openMocks(this);
        when(response.getWriter()).thenReturn(writer);
    }

    @Test
    public void testRenderSuccess() throws IOException {
        Context context = new Context();
        String templateName = "testTemplate";

        ThymeleafRenderer.render(response, templateEngine, templateName, context);

        verify(response).setContentType("text/html");
        verify(response).setCharacterEncoding("UTF-8");
        verify(templateEngine).process(eq(templateName), eq(context), eq(writer));
        verify(writer).close();
    }

    @Test
    public void testRenderThrowsIOException() throws IOException {
        Context context = new Context();
        String templateName = "testTemplate";

        when(response.getWriter()).thenThrow(new IOException("Writer error"));

        assertThrows(IOException.class, () -> {
            ThymeleafRenderer.render(response, templateEngine, templateName, context);
        });

        verify(response).setContentType("text/html");
        verify(response).setCharacterEncoding("UTF-8");
        verifyNoMoreInteractions(templateEngine);  // ensure template engine is not used when writer fails
    }

    @Test
    public void testRenderErrorPage() throws IOException {
        String errorMessage = "Not Found";
        int statusCode = 404;

        // capture the Context passed to the render method
        ArgumentCaptor<Context> contextCaptor = ArgumentCaptor.forClass(Context.class);

        // call the renderErrorPage method
        ThymeleafRenderer.renderErrorPage(response, templateEngine, errorMessage, statusCode);

        // verify status code set in the response
        verify(response).setStatus(statusCode);

        // verify render method is called with the error template and the captured context
        verify(templateEngine).process(eq("error"), contextCaptor.capture(), eq(writer));

        // verify the context contains the correct error message and status code
        Context capturedContext = contextCaptor.getValue();
        assertEquals(statusCode, capturedContext.getVariable("statusCode"));
        assertEquals(errorMessage, capturedContext.getVariable("message"));
    }
}