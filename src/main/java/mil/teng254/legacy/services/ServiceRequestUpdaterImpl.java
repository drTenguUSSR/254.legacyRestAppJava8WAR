package mil.teng254.legacy.services;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MultivaluedMap;

@Slf4j
public class ServiceRequestUpdaterImpl implements ServiceRequestUpdater {
    @Override
    public void doUpdate(HttpServletRequest dstHttpRequest, HttpServletRequest srcHttpRequest) {
        log.warn("ServiceRequestUpdaterImpl:doUpdate called HttpServletRequest");
    }

    @Override
    public void doUpdate(HttpServletRequest dstHttpRequest, MultivaluedMap<String, String> pHeaders) {
        log.warn("ServiceRequestUpdaterImpl:doUpdate called MultivaluedMap");
    }
}
