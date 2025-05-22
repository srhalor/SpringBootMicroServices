package com.fmd.spring_jpa_demo.security;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * Aspect for logging entry and exit of methods at TRACE level if enabled.
 * <p>
 * This aspect intercepts all public method calls within the {@code com.fmd.spring_jpa_demo}
 * package and its subpackages. If the logging level TRACE is enabled, it logs the entry,
 * exit, and exception (if any) for each intercepted method.
 * <p>
 * Usage:
 * <ul>
 *   <li>Enable TRACE logging for {@code com.fmd.spring_jpa_demo} in your application configuration.</li>
 *   <li>All public methods in the specified package will have entry and exit logs at TRACE level.</li>
 * </ul>
 *
 * Example log output:
 * <pre>
 *   TRACE Entering method: com.fmd.spring_jpa_demo.service.StudentService.getStudent()
 *   TRACE Exiting method: com.fmd.spring_jpa_demo.service.StudentService.getStudent()
 * </pre>
 */
@Slf4j
@Aspect
@Component
public class LoggingAspect {

    /**
     * Logs entry and exit of all public methods in the com.fmd.spring_jpa_demo package and subpackages.
     *
     * @param joinPoint the join point representing the method being intercepted
     * @return the result of the method execution
     * @throws Throwable if the intercepted method throws any exception
     */
    @Around("execution(public * com.fmd.spring_jpa_demo..*(..))")
    public Object logEntryExit(ProceedingJoinPoint joinPoint) throws Throwable {
        if (log.isTraceEnabled()) {
            log.trace("Entering method: {}.{}()", joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName());
        }
        try {
            Object result = joinPoint.proceed();
            if (log.isTraceEnabled()) {
                log.trace("Exiting method: {}.{}()", joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName());
            }
            return result;
        } catch (Throwable t) {
            if (log.isTraceEnabled()) {
                log.trace("Exception in method: {}.{}()", joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName());
            }
            throw t;
        }
    }
}

