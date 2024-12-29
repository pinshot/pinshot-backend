package com.example.pinshot.global.logging;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Aspect
public class LoggingAspect { // 로깅을 AOP로 처리하기 위한 클래스
    @Pointcut("execution(* com.example.pinshot..*(..))") // 이렇게 하면 메소드의 내부 메소드도 전부 포인트컷 대상이 됨
    private void pointCut(){}

    @Before("pointCut()") // 메소드 실행 직전의 로깅
    public void before(JoinPoint joinPoint) {
        log.info("[START] Method Name : {}",joinPoint.toString());
    }

    @AfterReturning("pointCut()") // 메소드 실행 완료 후의 로깅
    public void afterReturning(JoinPoint joinPoint){
        log.info("[END] Method Name : {}",joinPoint.toString());
    }

    @AfterThrowing(value = "pointCut()", throwing = "exception") // 메소드에서 예외가 발생했을 때 로깅
    public void afterThrowing(JoinPoint joinPoint, Exception exception){
        log.error("[EXCEPTION] Method Name : {}",joinPoint.toString());
        log.error("[EXCEPTION] Error Message : {}", exception.getMessage());
    }
}
