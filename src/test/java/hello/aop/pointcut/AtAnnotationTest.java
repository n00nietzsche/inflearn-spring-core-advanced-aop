package hello.aop.pointcut;

import hello.aop.member.MemberService;
import hello.aop.member.MemberServiceImpl;
import hello.aop.member.MyClass;
import hello.aop.member.annotation.ArgsAop;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Slf4j
@Import({AtAnnotationTest.AtAnnotationAspect.class})
@SpringBootTest
public class AtAnnotationTest {
    @Autowired MemberServiceImpl memberService;

    @Test
    void success() {
        log.info("memberService Proxy={}", memberService.getClass());

        @ArgsAop String helloA = "helloA";
        memberService.hello(helloA);
        memberService.testMethod(new MyClass());
    }

    @Slf4j
    @Aspect
    static class AtAnnotationAspect {
        // MethodAop는 이전에 만든 사용자 정의 애노테이션이다.
        @Around("execution(* hello..*(..)) && @annotation(hello.aop.member.annotation.MethodAop)")
        public Object doAnnotation(ProceedingJoinPoint joinPoint) throws Throwable {
            log.info("[@annotation] {} MethodAop", joinPoint.getSignature());
            return joinPoint.proceed();
        }

        // MethodAop는 이전에 만든 사용자 정의 애노테이션이다.
        @Around("execution(* hello..*(..)) && @args(hello.aop.member.annotation.ArgsAop)")
        public Object doAnnotation2(ProceedingJoinPoint joinPoint) throws Throwable {
            log.info("[@args] {} ArgsAop", joinPoint.getSignature());
            return joinPoint.proceed();
        }
    }
}
