package hello.aop.order.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Slf4j
@Aspect
public class AspectV3 {
    // hello.aop.order 패키지와 하위 패키지
    @Pointcut("execution(* hello.aop.order..*(..))") // pointcut expression
    private void allOrder() {} // pointcut signature

    // 트랜잭션은 `*Service` 라는 패턴의 이름을 가진 메서드에만 적용된다고 가정한다.
    @Pointcut("execution(* *..*Service.*(..))")
    private void allService() {} // pointcut signature

    @Around("allOrder()")
    public Object doLog(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("[log] {}", joinPoint.getSignature());
        return joinPoint.proceed();
    }

    // hello.aop.order 패키지 안에 속하면서 메서드 이름 패턴이 `*Service*`에 일치하는 것만
    @Around("allOrder() && allService()")
    public Object doTransaction(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result = null;

        try {
            log.info("[트랜잭션 시작] {}", joinPoint.getSignature());
            result = joinPoint.proceed();
            log.info("[트랜잭션 커밋] {}", joinPoint.getSignature());
        } catch (Exception e) {
            log.info("[트랜잭션 롤백] {}", joinPoint.getSignature());
            throw e;
        } finally {
            log.info("[리소스 릴리즈] {}", joinPoint.getSignature());
        }

        return result;
    }
}
