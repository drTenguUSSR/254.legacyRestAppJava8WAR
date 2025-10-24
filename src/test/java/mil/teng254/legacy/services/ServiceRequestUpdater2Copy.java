package mil.teng254.legacy.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.mock.web.MockHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

@Slf4j
public class ServiceRequestUpdater2Copy implements ServiceRequestUpdater {
    @Override
    public void doUpdate(HttpServletRequest dstHttpRequestI, HttpServletRequest srcHttpRequest) {
        log.debug("ServiceRequestUpdater2Copy:doUpdate HttpServletRequest called [");
        MockHttpServletRequest dstHttpRequest = (MockHttpServletRequest) dstHttpRequestI;
        updateSysHeaders(dstHttpRequest,srcHttpRequest);
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

    private void updateSysHeaders(MockHttpServletRequest dstHttpRequest, HttpServletRequest srcHttpRequest) {
        dstHttpRequest.setLocalAddr(srcHttpRequest.getLocalAddr());
        dstHttpRequest.setLocalPort(srcHttpRequest.getLocalPort());
        dstHttpRequest.setServerName(srcHttpRequest.getServerName());
        dstHttpRequest.setRemoteAddr(srcHttpRequest.getRemoteAddr());
        dstHttpRequest.setRemotePort(srcHttpRequest.getRemotePort());
        dstHttpRequest.setMethod(srcHttpRequest.getMethod());
        dstHttpRequest.setRequestURI(srcHttpRequest.getRequestURI());
        dstHttpRequest.setRemoteUser(srcHttpRequest.getRemoteUser());
    }

}
