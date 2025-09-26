package mil.teng254.legacy.controller;

import org.springframework.mock.web.MockServletContext;
import org.springframework.web.context.ConfigurableWebApplicationContext;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.XmlWebApplicationContext;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

public class TestContextLoaderListener extends ContextLoaderListener {

    @Override
    public void contextInitialized(ServletContextEvent event) {
        ServletContext servletContext = event.getServletContext();

        // Если это MockServletContext, заранее устанавливаем атрибуты
        if (servletContext instanceof MockServletContext) {
            MockServletContext mockContext = (MockServletContext) servletContext;

            // Создаем и настраиваем контекст вручную
            ConfigurableWebApplicationContext context = new XmlWebApplicationContext();
            context.setConfigLocation("classpath:test-applicationContext.xml");
            context.setServletContext(mockContext);
            context.refresh();

            // Устанавливаем атрибут, который ожидает Spring
            mockContext.setAttribute(
                    WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE,
                    context
            );
        }

        super.contextInitialized(event);
    }
}