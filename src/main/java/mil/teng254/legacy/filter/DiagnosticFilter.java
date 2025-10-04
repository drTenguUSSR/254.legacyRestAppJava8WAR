//src/main/java/mil/teng254/legacy/filter/DiagnosticFilter.java
package mil.teng254.legacy.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import javax.servlet.*;
import java.io.IOException;

@Slf4j
public class DiagnosticFilter implements Filter {
    private String filterName;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.filterName = filterConfig.getFilterName();
        log.debug("init. thId={} name=!{}! this={}", Thread.currentThread().getId(),
                filterName,
                System.identityHashCode(this));
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        RequestAttributes attr;
        attr = RequestContextHolder.getRequestAttributes();
        log.debug("DiagnosticFilter-Before. thId={} name=!{}!  this={} attr: {}",
                Thread.currentThread().getId(), filterName, System.identityHashCode(this),
               attr == null ? "!null": System.identityHashCode(attr));

        chain.doFilter(request, response);

        attr = RequestContextHolder.getRequestAttributes();
        log.debug("DiagnosticFilter-After. thId={} name=!{}!  this={} attr: {}",
                Thread.currentThread().getId(), filterName, System.identityHashCode(this),
                attr == null ? "!null": System.identityHashCode(attr));
    }

    @Override
    public void destroy() {

    }
}