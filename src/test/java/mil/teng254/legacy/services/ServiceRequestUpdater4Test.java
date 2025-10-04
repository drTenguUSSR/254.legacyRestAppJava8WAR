package mil.teng254.legacy.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.mock.web.MockHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MultivaluedMap;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Slf4j
public class ServiceRequestUpdater4Test implements ServiceRequestUpdater {
    @Override
    public void doUpdate(HttpServletRequest dstHttpRequestI, HttpServletRequest srcHttpRequest) {
        log.debug("ServiceRequestUpdater4Test:doUpdate HttpServletRequest called [");
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

    @Override
    public void doUpdate(HttpServletRequest dstHttpRequestI, MultivaluedMap<String, String> pHeaders) {
        log.debug("ServiceRequestUpdater4Test:doUpdate MultivaluedMap called [");
        MockHttpServletRequest dstHttpRequest = (MockHttpServletRequest) dstHttpRequestI;
        Iterator<Map.Entry<String, List<String>>> headersIterator = pHeaders.entrySet().iterator();
        while(headersIterator.hasNext()) {
            Map.Entry<String, List<String>> valOne = headersIterator.next();
            String key = valOne.getKey();
            Iterator<String> valsOneAll = valOne.getValue().iterator();
            while(valsOneAll.hasNext()) {
                String valSub = valsOneAll.next();
                log.debug("copy header: ({},{})",key,valSub);
                dstHttpRequest.addHeader(key, valSub);
            }
        }
        log.debug("]");
    }
}
