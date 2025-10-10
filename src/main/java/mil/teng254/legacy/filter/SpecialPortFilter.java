package mil.teng254.legacy.filter;

import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.AnnotationUtils;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Provider
@Slf4j
public class SpecialPortFilter implements ContainerRequestFilter {
    private int specialPort;
    private Map<String, Class<?>> specialControllers = new HashMap<>();
    @Context
    private HttpServletRequest httpServletRequest;

    public SpecialPortFilter() {
        log.debug(".ctor called. this={}", System.identityHashCode(this));
        String portStr = System.getenv("SPECIAL_PORT");
        if (portStr == null) {
            throw new RuntimeException("SPECIAL_PORT environment variable is required");
        }
        try {
            specialPort = Integer.parseInt(portStr);
        } catch (NumberFormatException e) {
            throw new RuntimeException("Invalid SPECIAL_PORT value: " + portStr, e);
        }
        if (specialPort != -1 && (specialPort < 0 || specialPort > 65535)) {
            throw new RuntimeException("SPECIAL_PORT must be between 0 and 65535, or -1");
        }

        // Предварительная регистрация путей специальных контроллеров
        specialControllers.put("special/", mil.teng254.legacy.controller.SpecialController.class);
    }

    @Override
    public ContainerRequest filter(ContainerRequest request) {
        String path = request.getPath();
        log.debug("filter-for [{}]", path);
        int requestPort = request.getBaseUri().getPort();

        // Проверяем, относится ли запрос к специальному контроллеру
        boolean isSpecialController = false;
        Class<?> controllerClass = null;

        for (String specialPath : specialControllers.keySet()) {
            if (path.startsWith(specialPath)) {
                isSpecialController = true;
                controllerClass = specialControllers.get(specialPath);
                break;
            }
        }

        //TODO: customize error on WebApplicationException
        if (isSpecialController && controllerClass != null) {
            // Проверяем наличие аннотации @SpecialPort на классе контроллера
            SpecialPort annotation = AnnotationUtils.findAnnotation(controllerClass, SpecialPort.class);
            if (annotation != null) {
                if (httpServletRequest == null) {
                    String uuid = UUID.randomUUID().toString();
                    log.error("special-controller. {}. httpServletRequest is empty", uuid);
                    throw new WebApplicationException(Response.Status.FORBIDDEN);
                }
                String remoteIpAddress = httpServletRequest.getRemoteAddr();
                if (specialPort == -1) {
                    log.error("special-controller. only-port(-1). from={}", remoteIpAddress);
                    throw new WebApplicationException(Response.Status.FORBIDDEN);
                } else if (requestPort != specialPort) {
                    log.error("special-controller. only-port({}). from={}", specialPort, remoteIpAddress);
                    throw new WebApplicationException(Response.Status.FORBIDDEN);
                }
            }
        }

        return request;
    }
}