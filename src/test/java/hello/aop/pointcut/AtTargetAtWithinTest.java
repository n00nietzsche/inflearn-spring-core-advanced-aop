package hello.aop.pointcut;

import hello.aop.member.annotation.ClassAop;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@Slf4j
@Import({AtTargetAtWithinTest.Config.class})
@SpringBootTest
public class AtTargetAtWithinTest {
    @Autowired Child child;

    static class Config {
        @Bean public Parent parent() { return new Parent(); }
        @Bean public Child child() { return new Child(); }
        @Bean public AtTargetAtWithinAspect atTargetAtWithinAspect() { return new AtTargetAtWithinAspect(); }
    }

    @Test
    void success() {
        log.info("child Proxy={}", child.getClass());
        child.childMethod();
        child.parentMethod();
    }

    static class Parent {
        public void parentMethod() {} // 부모에만 있는 메서드
    }

    @ClassAop
    static class Child extends Parent{
        public void childMethod() {} // 자식에만 있는 메서드
    }

    /**
     * 이번에만 `@Aspect`를 이용해 따로 클래스를 만드는 이유는 동적인 부분을 인식시키기 위해서이다.
     * 이전처럼 `Pointcut.setExpression`과 같이 작성하면 정적인 부분은 잘 인식되지만, 동적인 부분은 인식할 수 없다.
     */
    @Slf4j
    @Aspect
    static class AtTargetAtWithinAspect {
        // `@target`: 인스턴스를 기준으로 모든 메서드의 조인 포인트를 선정, 부모 타입의 메서드도 적용
        @Around("execution(* hello.aop..*(..)) && @target(hello.aop.member.annotation.ClassAop)")
        public Object atTarget(ProceedingJoinPoint joinPoint) throws Throwable {
            log.info("[@target] {}", joinPoint.getSignature());
            return joinPoint.proceed();
        }

        // `@within`: 선택된 클래스 내부의 메서드들만 조인 포인트로 선정, 부모 타입의 메서드는 적용되지 않음
        @Around("execution(* hello.aop..*(..)) && @within(hello.aop.member.annotation.ClassAop)")
        public Object atWithin(ProceedingJoinPoint joinPoint) throws Throwable {
            log.info("[@within] {}", joinPoint.getSignature());
            return joinPoint.proceed();
        }
    }
}
