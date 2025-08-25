package mil.teng254.legacy.filter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class PortFilter implements Filter {
    private int specialPort = -1;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        String portStr = System.getenv("SPECIAL_PORT");
        if (portStr != null) {
            try {
                specialPort = Integer.parseInt(portStr);
            } catch (NumberFormatException e) {
                throw new ServletException("Invalid SPECIAL_PORT value: " + portStr);
            }
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        int requestPort = request.getServerPort();

        String path = httpRequest.getRequestURI();
        if (path.contains("/special/")) {
            if (specialPort == -1) {
                ((HttpServletResponse) response).sendError(HttpServletResponse.SC_FORBIDDEN,
                        "Special port functionality is disabled");
                return;
            } else if (requestPort != specialPort) {
                ((HttpServletResponse) response).sendError(HttpServletResponse.SC_FORBIDDEN,
                        "Access to this endpoint is only allowed on port " + specialPort);
                return;
            }
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // Очистка ресурсов, если необходимо
    }
}