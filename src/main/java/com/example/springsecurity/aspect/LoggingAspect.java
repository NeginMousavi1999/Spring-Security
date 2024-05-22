package com.example.springsecurity.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.stream.Collectors;

@Aspect
@Component
@Slf4j
public class LoggingAspect {
    @Pointcut("within(com.example.springsecurity.controller.*) && " +
            "!within(com.example.springsecurity.controller.ControllerAdvisor ) && " +
            "!within(com.example.springsecurity.controller.HelloController)")
    public void controllersPointcut() {
        // user controller pointcut
    }

    @Pointcut("within(com.example.springsecurity.controller.HelloController)")
    public void helloControllerPointcut() {
        // hello controller pointcut
    }

    @Around("helloControllerPointcut()")
    public Object logAroundTheToken(ProceedingJoinPoint joinPoint) throws Throwable {
        logInput("[]", joinPoint);
        try {
            Object result = joinPoint.proceed();
            log(
                    "... Exit: {}.{}() with result = {} ...",
                    joinPoint,
                    result
            );
            return result;
        } catch (IllegalArgumentException e) {
            logIllegalArgumentException(joinPoint);
            throw e;
        }
    }

    @Around("controllersPointcut()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        logInput(joinPoint);
        try {
            Object result = joinPoint.proceed();
            log(
                    "... Exit: {}.{}() with result = {} ...",
                    joinPoint,
                    result
            );
            return result;
        } catch (IllegalArgumentException e) {
            logIllegalArgumentException(joinPoint);
            throw e;
        }
    }

    private void logInput(String arguments, ProceedingJoinPoint joinPoint) {
        log(
                "... Enter: {}.{}() with argument[s] = {} ...",
                joinPoint,
                arguments
        );
    }

    private void logInput(ProceedingJoinPoint joinPoint) {
        String arguments;
        try {
            arguments = Arrays
                    .stream(joinPoint.getArgs())
                    .collect(Collectors.toList())
                    .stream()
                    .map(Object::toString)
                    .filter(s -> !s.contains("Bearer"))
                    .collect(Collectors.toList())
                    .toString();
        } catch (Exception e) {
            arguments = "nothing";
        }
        logInput(arguments, joinPoint);
    }

    private void logIllegalArgumentException(ProceedingJoinPoint joinPoint) {
        log.error(
                "... Illegal argument: {} in {}.{}() ...",
                Arrays.toString(joinPoint.getArgs()),
                joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName()
        );
    }

    private static void log(String pattern, ProceedingJoinPoint joinPoint, Object result) {
        log.info(
                pattern,
                joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(),
                result
        );
    }
}