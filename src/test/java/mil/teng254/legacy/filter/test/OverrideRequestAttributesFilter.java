//src/test/java/mil/teng254/legacy/filter/test/OverrideRequestAttributesFilter.java
package mil.teng254.legacy.filter.test;

import lombok.extern.slf4j.Slf4j;
import mil.teng254.legacy.controller.StaticHolder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Slf4j
public class OverrideRequestAttributesFilter implements Filter {

    public OverrideRequestAttributesFilter() {
        log.debug(".ctor called");
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.debug("init called. {}", filterConfig);
    }

    //@Override
    public void doFilter2(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        final String PREFIX = "doFilter:";
        HttpServletRequest httpRequest = (HttpServletRequest) request;

        // Извлекаем testId из заголовка
        String testId = httpRequest.getHeader(StaticHolder.HTTP_HEADER_TEST_ID);
        if (testId != null && !testId.isEmpty()) {
            ServletRequestAttributes attr;
            // ДО установки контекста
//            attr = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
//            log.debug(PREFIX + "BEFORE-Context: thId={} attr.id={} localPort={}",
//                    Thread.currentThread().getId(),
//                    attr == null ? "!null" : System.identityHashCode(attr),
//                    attr.getRequest().getLocalPort()
//            );
            StaticHolder.overrideRequestAttributes(testId, httpRequest);
            // ПОСЛЕ установки контекста
            attr = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

            log.debug(PREFIX + "AFTER-Context: thId={} attr.id={} localPort={}",
                    Thread.currentThread().getId(),
                    attr == null ? "!null" : System.identityHashCode(attr),
                    attr.getRequest().getLocalPort());
            try {
                chain.doFilter(request, response);
            } finally {
                RequestContextHolder.resetRequestAttributes();
            }
        } else {
            log.debug(PREFIX + "no testId");
            chain.doFilter(request, response);
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String testId = httpRequest.getHeader("X-Cust-Teng-Test-ID");

        ServletRequestAttributes previousAttributes = null;
        boolean attributesRestored = false;

        try {
            if (testId != null && !testId.trim().isEmpty()) {
                // Сохраняем текущие атрибуты
                previousAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

                // Восстанавливаем из хранилища или создаем новые
                ServletRequestAttributes storedAttributes = StaticHolder.get(testId);
                if (storedAttributes != null) {
                    // Создаем новый объект с текущим request, но сохраняем атрибуты
                    ServletRequestAttributes newAttributes = new ServletRequestAttributes(httpRequest);

                    // Копируем атрибуты из сохраненных
                    copyAttributes(storedAttributes, newAttributes);

                    RequestContextHolder.setRequestAttributes(newAttributes);
                    attributesRestored = true;
                }
            }

            chain.doFilter(request, response);

        } finally {
            if (testId != null && !testId.trim().isEmpty()) {
                ServletRequestAttributes currentAttributes =
                        (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

                if (currentAttributes != null) {
                    // Сохраняем атрибуты в хранилище
                    StaticHolder.set(testId, currentAttributes);
                }

                // Восстанавливаем предыдущие атрибуты если нужно
                if (attributesRestored && previousAttributes != null) {
                    RequestContextHolder.setRequestAttributes(previousAttributes);
                } else if (attributesRestored) {
                    RequestContextHolder.resetRequestAttributes();
                }
            }
        }
    }

    private void copyAttributes(ServletRequestAttributes source, ServletRequestAttributes target) {
        // Копируем request атрибуты
        String[] requestAttributeNames = source.getAttributeNames(ServletRequestAttributes.SCOPE_REQUEST);
        for (String name : requestAttributeNames) {
            Object value = source.getAttribute(name, ServletRequestAttributes.SCOPE_REQUEST);
            target.setAttribute(name, value, ServletRequestAttributes.SCOPE_REQUEST);
        }

        // Копируем session атрибуты если session существует
        if (source.getSessionId() != null) {
            String[] sessionAttributeNames = source.getAttributeNames(ServletRequestAttributes.SCOPE_SESSION);
            for (String name : sessionAttributeNames) {
                Object value = source.getAttribute(name, ServletRequestAttributes.SCOPE_SESSION);
                target.setAttribute(name, value, ServletRequestAttributes.SCOPE_SESSION);
            }
        }
    }

    @Override
    public void destroy() {
        // Очистка, если необходимо
    }
}