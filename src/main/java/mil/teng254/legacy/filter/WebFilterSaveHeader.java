package mil.teng254.legacy.filter;

import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;

@WebFilter(filterName = "WebFilterSaveHeaderName", urlPatterns = "/*", asyncSupported = true)
@Slf4j
//http-заголовки
// "X-Cust-Alfa"
// "X-Cust-Bravo"
// "X-Cust-Kilo"
public class WebFilterSaveHeader implements Filter {

    private static final String CUST_HTTP_HEADER_ALFA = "X-Cust-Alfa";

    private static final String CUST_HTTP_HEADER_BRAVO = "X-Cust-Bravo";
    private static final ThreadLocal<String> CUST_BRAVO = new ThreadLocal<>();

    private static final String CUST_HTTP_HEADER_KILO = "X-Cust-Kilo";
    public static final String CUST_LOG4J_PROP_KILO = "X-Cust-Kilo";

    public static String getBravoData() {
        return CUST_BRAVO.get();
    }

    public static String getHeaderAlfa() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder
                .currentRequestAttributes())
                .getRequest();
        String res = request.getHeader(CUST_HTTP_HEADER_ALFA);
        return res;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.debug("init called");
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        try {
            putHeader(servletRequest);
            filterChain.doFilter(servletRequest, servletResponse);
        } finally {
            dropHeader();
        }
    }

    private void putHeader(ServletRequest servletRequest) {
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        String custB = req.getHeader(CUST_HTTP_HEADER_BRAVO);
        if (StringUtils.hasText(custB)) {
            CUST_BRAVO.set(custB);
        }
        String custK = req.getHeader(CUST_HTTP_HEADER_KILO);
        if (StringUtils.hasText(custK)) {
            //usage ThreadContext.get(CUST_HTTP_HEADER_KILO)
            ThreadContext.put(CUST_LOG4J_PROP_KILO, custK);
        }
    }

    private void dropHeader() {
        CUST_BRAVO.remove();
        Map<String, String> context = ThreadContext.getContext();
        if (context.containsKey(CUST_LOG4J_PROP_KILO)) {
            ThreadContext.remove(CUST_LOG4J_PROP_KILO);
        }
    }

    @Override
    public void destroy() {

    }
}
