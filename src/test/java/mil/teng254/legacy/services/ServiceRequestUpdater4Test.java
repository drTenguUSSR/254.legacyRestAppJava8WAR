package mil.teng254.legacy.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.mock.web.MockHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

@Slf4j
public class ServiceRequestUpdater4Test implements ServiceRequestUpdater {
    @Override
    public void doUpdate(HttpServletRequest dstHttpRequestI, HttpServletRequest srcHttpRequest) {
        log.debug("ServiceRequestUpdater4Test:doUpdate called [");
        MockHttpServletRequest dstHttpRequest = (MockHttpServletRequest) dstHttpRequestI;
        Enumeration<String> srcHttpHeaders = srcHttpRequest.getHeaderNames();
        while (srcHttpHeaders.hasMoreElements()) {
            String key = srcHttpHeaders.nextElement();
            Enumeration<String> valOne = srcHttpRequest.getHeaders(key);
            while (valOne.hasMoreElements()) {
                String valSub = valOne.nextElement();
                log.debug("copy header: ({},{})",key,valSub);
                dstHttpRequest.addHeader(key, valSub);
            }
        }
        log.debug("]");
    }
}
