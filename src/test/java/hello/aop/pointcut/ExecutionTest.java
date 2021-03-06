package hello.aop.pointcut;

import hello.aop.member.MemberServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Pointcut;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.*;

@Slf4j
public class ExecutionTest {
    AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
    Method helloMethod;

    @BeforeEach
    public void init() throws NoSuchMethodException {
        helloMethod = MemberServiceImpl.class.getMethod("hello", String.class);
    }

    @Test
    void printMethod() {
        // execution(* ..package..Class.)
        // public java.lang.String hello.aop.member.MemberServiceImpl.hello(java.lang.String)
        // 여태까지 사용했던 execution 은 위에서 우리가 `.getMethod()` 메서드로 클래스 내부 메서드를 찾았던 것과 동일하게 메서드를 찾아낸다.
        log.info("helloMethod={}", helloMethod);
    }

    @Test
    // 아주 정확히 일치하는 예
    void exactMatch() {
        // public java.lang.String hello.aop.member.MemberServiceImpl.hello(java.lang.String)
        pointcut.setExpression("execution(public String hello.aop.member.MemberServiceImpl.hello(String))");
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }

    @Test
    void allMatch() {
        // 반환 타입(return-type-pattern) = *, 이름 패턴(name-pattern) = *
        pointcut.setExpression("execution(* *(..))");
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }

    @Test
    void nameMatch() {
        // 반환 타입(return-type-pattern) = *, 이름 패턴(name-pattern) = hello
        pointcut.setExpression("execution(* hello(..))");
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }

    @Test
    void namePatternMatch() {
        // 반환 타입(return-type-pattern) = *, 이름 패턴(name-pattern) = hel*
        pointcut.setExpression("execution(* hel*(..))");
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }

    @Test
    void namePatternMatch2() {
        // 반환 타입(return-type-pattern) = *, 이름 패턴(name-pattern) = *el*
        pointcut.setExpression("execution(* *el*(..))");
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }

    @Test
    void nameMatchFalse() {
        // 반환 타입(return-type-pattern) = *, 이름 패턴(name-pattern) = eel*
        pointcut.setExpression("execution(* eel*(..))");
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isFalse();
    }

    @Test
    void packageExactMatch() {
        // 패키지를 정확하게 매칭
        pointcut.setExpression("execution(* hello.aop.member.MemberServiceImpl.hello(..))");
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }

    @Test
    void packageExactFalse() {
        // 패키지를 정확하게 매칭
        // .이 하나일 때는, 바로 하위의 패키지만 가리키기 때문에 false 가 나온다.
        pointcut.setExpression("execution(* hello.aop.*.*(..))");
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isFalse();
    }

    @Test
    void packageMatchSubPackage1() {
        // 패키지를 정확하게 매칭
        // .. 두개 쓰면 모든 하위 패키지까지 가리킨다.
        pointcut.setExpression("execution(* hello.aop..*.*(..))");
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }

    @Test
    void packageMatchSubPackage2() {
        // 패키지를 정확하게 매칭
        // .. 두개 쓰면 모든 하위 패키지까지 가리킨다.
        pointcut.setExpression("execution(* hello.aop.member..*.*(..))");
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }

    @Test
    @DisplayName("패키지 주소를 정확하게 매칭")
    void typeExactMatch() {
        pointcut.setExpression("execution(* hello.aop.member.MemberServiceImpl.*(..))");
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }

    @Test
    @DisplayName("상위(Super) 타입 매칭")
    void typeMatchSuperType() {
        // MemberServiceImpl은 인터페이스인 MemberService의 구현체(하위 타입)이기 때문에 매칭된다.
        pointcut.setExpression("execution(* hello.aop.member.MemberService.*(..))");
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }

    @Test
    @DisplayName("상위 타입인 인터페이스에 정의되지 않은 메서드는 자바의 기본 다형성 원리와 똑같이 지원하지 않는다.")
    void typeMatchNoSuperTypeMethodFalse() throws NoSuchMethodException {
        pointcut.setExpression("execution(* hello.aop.member.MemberService.*(..))");

        Method internalMethod = MemberServiceImpl.class.getMethod("internal", String.class);
        assertThat(pointcut.matches(internalMethod, MemberServiceImpl.class)).isFalse();
    }

    @Test
    @DisplayName("당연히 인터페이스 상위타입 안하고 구체 타입으로 하면 모든 메서드 잡아낸다.")
    void typeMatchConcreteType() throws NoSuchMethodException {
        pointcut.setExpression("execution(* hello.aop.member.MemberServiceImpl.*(..))");

        Method internalMethod = MemberServiceImpl.class.getMethod("internal", String.class);
        assertThat(pointcut.matches(internalMethod, MemberServiceImpl.class)).isTrue();
    }

    @Test
    @DisplayName("String 타입의 파라미터로 매칭")
    void argsMatch() {
        pointcut.setExpression("execution(* *(String))");
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }

    @Test
    @DisplayName("파라미터가 없는 메서드 매칭")
    void argsMatchNoArgs() {
        pointcut.setExpression("execution(* *())");
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isFalse();
    }

    @Test
    @DisplayName("정확히 하나의 파라미터만 허용함 (대신 모든 타입 허용)")
    void argsMatchOneArg() {
        pointcut.setExpression("execution(* *(*))");
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }

    @Test
    @DisplayName("모든 파라미터 허용")
    void argsMatchAllArgs() throws NoSuchMethodException {
        pointcut.setExpression("execution(* *(..))");
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();

        Method internalMethod = MemberServiceImpl.class.getMethod("internal", String.class);
        assertThat(pointcut.matches(internalMethod, MemberServiceImpl.class)).isTrue();
    }

    @Test
    @DisplayName("첫번째 파라미터는 String 이되, 나머지는 자유")
    void argsMatchFirstArgIsString() throws NoSuchMethodException {
        pointcut.setExpression("execution(* *(String, ..))");
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();

        Method internalMethod = MemberServiceImpl.class.getMethod("internal", String.class);
        assertThat(pointcut.matches(internalMethod, MemberServiceImpl.class)).isTrue();
    }

    @Test
    @DisplayName("첫번째 파라미터는 String 이되, 나머지는 자유, 근데 파라미터 개수는 2개")
    void argsMatchFirstArgIsString2() throws NoSuchMethodException {
        pointcut.setExpression("execution(* *(String, *))");
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();

        Method internalMethod = MemberServiceImpl.class.getMethod("internal", String.class);
        assertThat(pointcut.matches(internalMethod, MemberServiceImpl.class)).isTrue();
    }
}
