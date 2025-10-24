package mil.teng254.legacy.services;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;

@Slf4j
public class ServiceRequestUpdater2Empty implements ServiceRequestUpdater {
    @Override
    public void doUpdate(HttpServletRequest dstHttpRequest, HttpServletRequest srcHttpRequest) {
        log.warn("ServiceRequestUpdaterImpl:doUpdate called HttpServletRequest");
    }
}
