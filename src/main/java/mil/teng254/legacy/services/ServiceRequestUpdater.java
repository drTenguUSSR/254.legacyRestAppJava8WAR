package mil.teng254.legacy.services;

import javax.servlet.http.HttpServletRequest;

public interface ServiceRequestUpdater {
    void doUpdate(HttpServletRequest dstHttpRequest,HttpServletRequest srcHttpRequest);
}
