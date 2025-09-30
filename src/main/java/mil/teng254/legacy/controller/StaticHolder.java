package mil.teng254.legacy.controller;

import com.google.common.base.Strings;
import org.springframework.util.Assert;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.concurrent.ConcurrentHashMap;

public class StaticHolder {
    public static final String HTTP_HEADER_TEST_ID="X-Cust-Teng-Test-ID";
    private static final ConcurrentHashMap<String,RequestAttributes> raHolder =new ConcurrentHashMap<>();

    public static RequestAttributes get(String key) {
        return raHolder.get(key);
    }

    public static void set(String key,RequestAttributes val) {
        raHolder.put(key, val);
    }

    public static RequestAttributes remove(String key) {
        return raHolder.remove(key);
    }


    public static void overrideRequestAttributes(String testId) {
        if (Strings.isNullOrEmpty(testId)) {
            return;
        }
        RequestAttributes attrs = raHolder.get(testId);
        Assert.notNull(attrs,"not found ra for testId=["+testId+"]");
        RequestContextHolder.setRequestAttributes(attrs);
    }
}
