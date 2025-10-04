package mil.teng254.legacy.controller;

import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import mil.teng254.legacy.services.ServiceRequestUpdater;
import mil.teng254.legacy.util.SpringContextHolder;
import org.springframework.util.Assert;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MultivaluedMap;
import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class StaticHolder {
    public static final String HTTP_HEADER_TEST_ID = "X-Cust-Teng-Test-ID";
    private static final ConcurrentHashMap<String, RequestAttributes> raHolder = new ConcurrentHashMap<>();

    public static RequestAttributes get(String key) {
        return raHolder.get(key);
    }

    public static void set(String key, RequestAttributes val) {
        raHolder.put(key, val);
    }

    public static RequestAttributes remove(String key) {
        return raHolder.remove(key);
    }


    public static void overrideRequestAttributes(String testId, HttpServletRequest pHttpRequest) {
        if (Strings.isNullOrEmpty(testId)) {
            return;
        }
        ServletRequestAttributes attrs = (ServletRequestAttributes) raHolder.get(testId);
        Assert.notNull(attrs, "not found ra for testId=[" + testId + "]");
        HttpServletRequest xreq = attrs.getRequest();
        Enumeration<String> pHttpHeaders = pHttpRequest.getHeaderNames();
        ServiceRequestUpdater wrkUpdater = SpringContextHolder.getBean("serviceRequestUpdater", ServiceRequestUpdater.class);
        wrkUpdater.doUpdate(xreq, pHttpRequest);
        RequestContextHolder.setRequestAttributes(attrs);
    }

    public static void overrideRequestAttributes(String testId, MultivaluedMap<String, String> pHeaders) {
        if (Strings.isNullOrEmpty(testId)) {
            return;
        }
        ServletRequestAttributes attrs = (ServletRequestAttributes) raHolder.get(testId);
        Assert.notNull(attrs, "not found ra for testId=[" + testId + "]");
        HttpServletRequest xreq = attrs.getRequest();
        ServiceRequestUpdater wrkUpdater = SpringContextHolder.getBean("serviceRequestUpdater", ServiceRequestUpdater.class);
        wrkUpdater.doUpdate(xreq, pHeaders);
        RequestContextHolder.setRequestAttributes(attrs);
    }


    public static void dumpRequestInfo(String prefix, HttpServletRequest httpRequest) {
        Enumeration<String> headersEnum = httpRequest.getHeaderNames();
        if (headersEnum == null) {
            log.debug("info-headers-!{}! is null", prefix);
        } else {
            log.debug("info-headers-!{}!=[", prefix);
            while (headersEnum.hasMoreElements()) {
                String key = headersEnum.nextElement();
                log.debug("- [{}] -> [{}]", key, httpRequest.getHeader(key));
            }
            log.debug("]");
        }
    }

    public static void cleanupAll() {
        int size = raHolder.size();
        raHolder.clear();
        log.debug("StaticHolder: Cleaned up all {} contexts", size);
    }
}
