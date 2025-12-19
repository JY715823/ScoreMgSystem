package cn.edu.ctbu.scoremg.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

@Component
@Aspect
@Slf4j
public class HttpAspect {

    @Pointcut("execution(* cn.edu.ctbu.scoremg.api.StudentApiController.*(..))")
    public void log() {
    }

    @Before("log()")
    public void doBefore() {
        log.info("准备开始执行api...");
    }

    @After("log()")
    public void doAfter() {
        log.info("执行api完,准备返回...");
    }

    @AfterReturning(returning = "object", pointcut = "log()")
    public void AfterR(Object object) {
        log.info("response={}", object);
    }

    @Around("log()")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Object obj = pjp.proceed(pjp.getArgs());
        stopWatch.stop();
        long cost = stopWatch.getTotalTimeMillis();
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        String methodName = signature.getDeclaringTypeName() + "." + signature.getName();
        log.info("------------ 执行" + methodName + "方法, 用时: " + cost + "ms ------------");
        return obj;
    }
}
