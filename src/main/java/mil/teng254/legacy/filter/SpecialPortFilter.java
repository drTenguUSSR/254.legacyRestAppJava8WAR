package mil.teng254.legacy.filter;

import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.util.HashMap;
import java.util.Map;

@Provider
@Component
public class SpecialPortFilter implements ContainerRequestFilter {

    private int specialPort;
    private Map<String, Class<?>> specialControllers = new HashMap<>();

    public SpecialPortFilter() {
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
        specialControllers.put("/api/special/", mil.teng254.legacy.controller.SpecialController.class);
    }

    @Override
    public ContainerRequest filter(ContainerRequest request) {
        String path = request.getPath();
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

        if (isSpecialController && controllerClass != null) {
            // Проверяем наличие аннотации @SpecialPort на классе контроллера
            SpecialPort annotation = AnnotationUtils.findAnnotation(controllerClass, SpecialPort.class);
            if (annotation != null) {
                if (specialPort == -1) {
                    throw new WebApplicationException(Response.Status.FORBIDDEN);
                } else if (requestPort != specialPort) {
                    throw new WebApplicationException(Response.Status.FORBIDDEN);
                }
            }
        }

        return request;
    }
}