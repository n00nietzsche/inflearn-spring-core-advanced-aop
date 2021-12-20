package hello.aop.order.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;

@Slf4j
@Aspect
public class AspectV6Advice {
    @Around("hello.aop.order.aop.Pointcuts.orderAndService()")
    public Object doTransaction(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result = null;

        try {
            // @Before
            log.info("[트랜잭션 시작] {}", joinPoint.getSignature());
            result = joinPoint.proceed();
            log.info("[joinPoint.toString()] {}", joinPoint.toString());

            // @AfterReturning
            log.info("[트랜잭션 커밋] {}", joinPoint.getSignature());
        } catch (Exception e) {

            // @AfterThrowing
            log.info("[트랜잭션 롤백] {}", joinPoint.getSignature());
            throw e;
        } finally {

            // @After
            log.info("[리소스 릴리즈] {}", joinPoint.getSignature());
        }

        return result;
    }

    @Before("hello.aop.order.aop.Pointcuts.orderAndService()")
    // 여기서는 `@Around`처럼 `ProceedingJoinPoint` 타입의 파라미터를 받을 수 없다.
    public void doBefore(JoinPoint joinPoint) {
        log.info("[before] {}", joinPoint.getSignature());
    }

    @AfterReturning(value = "hello.aop.order.aop.Pointcuts.orderAndService()", returning = "result")
    // return 값을 출력해볼 수 있지만, `@Around` 애노테이션이 붙은 메서드처럼 return 값을 직접 변경하는 것은 불가능하다.
    public void doReturn(JoinPoint joinPoint, Object result) {
        log.info("[return] {} return={}", joinPoint.getSignature(), result);
    }

    @AfterThrowing(value = "hello.aop.order.aop.Pointcuts.orderAndService()", throwing = "ex")
    public void doThrowing(JoinPoint joinPoint, Exception ex) {
        log.info("[ex] {} message={}", joinPoint.getSignature(), ex.getMessage());
    }

    @After(value = "hello.aop.order.aop.Pointcuts.orderAndService()")
    public void doAfter(JoinPoint joinPoint) {
        log.info("[after] {}", joinPoint.getSignature());
    }
}
