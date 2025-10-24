package mil.teng254.legacy.filter;

import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;
import lombok.extern.slf4j.Slf4j;
import mil.teng254.legacy.controller.SpecialBravoController;
import mil.teng254.legacy.services.SpecialPortService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
@Slf4j
public class SpecialBravoPortFilter implements ContainerRequestFilter {
    @Autowired
    SpecialPortService specialPortService;

    private Map<String, Class<?>> specialControllers = new HashMap<>();

    @Context
    private HttpServletRequest httpServletRequest;

    public SpecialBravoPortFilter() {
        log.debug(".ctor called. this={}", System.identityHashCode(this));
        // Предварительная регистрация путей специальных контроллеров
        specialControllers.put("special-bravo/", SpecialBravoController.class);
    }

    @Override
    public ContainerRequest filter(ContainerRequest request) {
        String path = request.getPath();
        log.debug("filter-for [{}] specialPortConfig={}", path, System.identityHashCode(specialPortService));
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
            // Проверяем наличие аннотации @SpecialBravoPort на классе контроллера
            SpecialBravoPort annotation = AnnotationUtils.findAnnotation(controllerClass, SpecialBravoPort.class);
            if (annotation != null) {
                if (httpServletRequest == null) {
                    String uuid = UUID.randomUUID().toString();
                    log.error("special-controller. {}. httpServletRequest is empty", uuid);
                    throw new WebApplicationException(Response.Status.FORBIDDEN);
                }
                specialPortService.validateCall(httpServletRequest);
            }
        }

        return request;
    }
}