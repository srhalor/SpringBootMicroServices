package com.fmd.spring_jpa_demo.logging;

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
 * <p>
 * Example log output:
 * <pre>
 *   TRACE Entering method: com.fmd.spring_jpa_demo.service.StudentService.getStudent()
 *   TRACE Exiting method: com.fmd.spring_jpa_demo.service.StudentService.getStudent()
 * </pre>
 *
 * @author Shailesh Halor
 */
@Slf4j
@Aspect
@Component
public class LoggingAspect {

    /**
     * Aspect for logging exceptions, entry and exit of public methods in the application.
     * This advice logs method entry and exit at TRACE level if enabled, and also logs exceptions.
     *
     * @param joinPoint the join point representing the intercepted method
     * @return the result of the method execution
     * @throws Throwable if the intercepted method throws any exception
     */
    @Around("execution(public * com.fmd.spring_jpa_demo..*(..))")
    public Object logEntryExit(ProceedingJoinPoint joinPoint) throws Throwable {
        // Log method entry if TRACE is enabled
        if (log.isTraceEnabled()) {
            log.trace("Entering method: {}.{}()", joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName());
        }
        try {
            Object result = joinPoint.proceed();
            // Log method exit if TRACE is enabled
            if (log.isTraceEnabled()) {
                log.trace("Exiting method: {}.{}()", joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName());
            }
            return result;
        } catch (Throwable t) {
            // Log exception if TRACE is enabled
            if (log.isTraceEnabled()) {
                log.trace("Exception in method: {}.{}()", joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName());
            }
            throw t;
        }
    }
}
