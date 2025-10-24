//src/main/java/mil/teng254/legacy/services/SpecialPortService.java
package mil.teng254.legacy.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

@Component
@Slf4j
public class SpecialPortService {
    private int specialPort;

    public SpecialPortService() {
        log.error(".ctor called");
    }

    @PostConstruct
    private void init() {
        log.error("init called");
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
        log.debug("specialPort={}", specialPort);
    }

    public void validateCall(HttpServletRequest httpServletRequest) {
        int requestPort = httpServletRequest.getLocalPort();
        String remoteIpAddress = httpServletRequest.getRemoteAddr();
        log.debug("validateCall: fromIp={} toPort={} allowedPort={}", remoteIpAddress, requestPort, specialPort);
        log.debug("validateCallExt la={} lp={} sn={} ra={} rp={} me={} ruri={} ruser={}"
                , httpServletRequest.getLocalAddr()
                , httpServletRequest.getLocalPort()
                , httpServletRequest.getServerName()
                , httpServletRequest.getRemoteAddr()
                , httpServletRequest.getRemotePort()
                , httpServletRequest.getMethod()
                , httpServletRequest.getRequestURI()
                , httpServletRequest.getRemoteUser()
        );
        if (specialPort == -1) {
            log.error("special-controller. only-port(-1). fromIp={}", remoteIpAddress);
            throw new WebApplicationException(Response.Status.FORBIDDEN);
        } else if (requestPort != specialPort) {
            log.error("special-controller. only-port({}). fromIp={} usedPort={}"
                    , specialPort, remoteIpAddress, requestPort);
            throw new WebApplicationException(Response.Status.FORBIDDEN);
        }
    }
}
