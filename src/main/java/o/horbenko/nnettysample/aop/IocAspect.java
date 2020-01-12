package o.horbenko.nnettysample.aop;

import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

@Aspect
@Log4j2
public class IocAspect {

    @Around("@annotation(javax.inject.Inject) && execution(* *(..))")
    public Object log(final ProceedingJoinPoint joinPoint) throws Throwable {
        log.debug("Before method execution...");
        Object result = joinPoint.proceed();
        log.debug("After method execution.");
        return result;
    }

}
