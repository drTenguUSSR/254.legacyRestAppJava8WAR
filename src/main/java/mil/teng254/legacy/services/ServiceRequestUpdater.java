package mil.teng254.legacy.services;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MultivaluedMap;

public interface ServiceRequestUpdater {
    void doUpdate(HttpServletRequest dstHttpRequest,HttpServletRequest srcHttpRequest);
    void doUpdate(HttpServletRequest dstHttpRequest,MultivaluedMap<String, String> pHeaders);
}
