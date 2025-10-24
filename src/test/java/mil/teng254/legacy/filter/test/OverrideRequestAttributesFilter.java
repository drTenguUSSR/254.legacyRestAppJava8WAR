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

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        final String PREFIX = "doFilter:";
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        //HttpServletResponse httpResponse = (HttpServletResponse) response;

        // Извлекаем testId из заголовка
        String testId = httpRequest.getHeader(StaticHolder.HTTP_HEADER_TEST_ID);
        if (testId != null && !testId.isEmpty()) {
            ServletRequestAttributes attr;
            // ДО установки контекста
            attr = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            log.debug(PREFIX + "BEFORE-Context: thId={} attr.id={} localPort={}",
                    Thread.currentThread().getId(),
                    attr == null ? "!null" : System.identityHashCode(attr),
                    attr.getRequest().getLocalPort()
            );
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
            chain.doFilter(request, response);
        }
    }

    @Override
    public void destroy() {
        // Очистка, если необходимо
    }
}