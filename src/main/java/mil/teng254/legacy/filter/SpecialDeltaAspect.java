//src/main/java/mil/teng254/legacy/filter/SpecialDeltaAspect.java
package mil.teng254.legacy.filter;


import lombok.extern.slf4j.Slf4j;
import mil.teng254.legacy.services.SpecialPortService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
@Aspect
@Component
@Slf4j
public class SpecialDeltaAspect {

    @Autowired
    private SpecialPortService specialPortService;

    @Around("@within(mil.teng254.legacy.filter.SpecialDeltaPort)" +
            " || @annotation(mil.teng254.legacy.filter.SpecialDeltaPort)")
    public Object checkPort(ProceedingJoinPoint joinPoint) throws Throwable {
        ServletRequestAttributes atts = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        log.debug("before-validate. atts.id={}", System.identityHashCode(atts));
        HttpServletRequest httpRequest=atts.getRequest();
        specialPortService.validateCall(httpRequest);
        log.debug("after-validate");
        return joinPoint.proceed();
    }
}