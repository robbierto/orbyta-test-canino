package com.orbyta.banking.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
public class LoggingAspect {

    /**
     * Pointcut per i metodi del controller
     */
    @Pointcut("execution(* com.orbyta.banking.controller.*.*(..))")
    private void controllerMethods() {
    }

    /**
     * Pointcut per i metodi del servizio
     */
    @Pointcut("execution(* com.orbyta.banking.service.*.*(..))")
    private void serviceMethods() {
    }

    /**
     * Log around per i metodi del controller
     */
    @Around("controllerMethods()")
    public Object logAroundController(ProceedingJoinPoint joinPoint) throws Throwable {
        return logMethodExecution(joinPoint, "CONTROLLER");
    }

    /**
     * Log around per i metodi del servizio
     */
    @Around("serviceMethods()")
    public Object logAroundService(ProceedingJoinPoint joinPoint) throws Throwable {
        return logMethodExecution(joinPoint, "SERVICE");
    }

    /**
     * Log delle eccezioni lanciate dai controller o dai servizi
     */
    // @AfterThrowing(pointcut = "controllerMethods() || serviceMethods()", throwing = "exception")
    // public void logAfterThrowing(JoinPoint joinPoint, Throwable exception) {
    //     Logger logger = LoggerFactory.getLogger(joinPoint.getSignature().getDeclaringType());
    //     logger.error(
    //             "Exception in {}.{}() with cause = {}",
    //             joinPoint.getSignature().getDeclaringTypeName(),
    //             joinPoint.getSignature().getName(),
    //             exception.getCause() != null ? exception.getCause() : "NULL");
    //     logger.error("Exception details:", exception);
    // }

    /**
     * Metodo helper per loggare l'esecuzione dei metodi
     */
    private Object logMethodExecution(ProceedingJoinPoint joinPoint, String type) throws Throwable {
        Logger logger = LoggerFactory.getLogger(joinPoint.getSignature().getDeclaringType());

        // Log prima dell'esecuzione del metodo
        if (logger.isDebugEnabled()) {
            logger.debug(
                    "[{}] Enter: {}.{}() with argument[s] = {}",
                    type,
                    joinPoint.getSignature().getDeclaringTypeName(),
                    joinPoint.getSignature().getName(),
                    Arrays.toString(joinPoint.getArgs()));
        } else {
            logger.info(
                    "[{}] Enter: {}.{}()",
                    type,
                    joinPoint.getSignature().getDeclaringTypeName(),
                    joinPoint.getSignature().getName());
        }

        try {
            // Esecuzione del metodo
            Object result = joinPoint.proceed();

            // Log dopo l'esecuzione del metodo
            if (logger.isDebugEnabled()) {
                logger.debug(
                        "[{}] Exit: {}.{}() with result = {}",
                        type,
                        joinPoint.getSignature().getDeclaringTypeName(),
                        joinPoint.getSignature().getName(),
                        result);
            } else {
                logger.info(
                        "[{}] Exit: {}.{}() completed",
                        type,
                        joinPoint.getSignature().getDeclaringTypeName(),
                        joinPoint.getSignature().getName());
            }

            return result;
        } catch (IllegalArgumentException e) {
            logger.error(
                    "[{}] Illegal argument: {} in {}.{}()",
                    type,
                    Arrays.toString(joinPoint.getArgs()),
                    joinPoint.getSignature().getDeclaringTypeName(),
                    joinPoint.getSignature().getName());
            throw e;
        }
    }
}
