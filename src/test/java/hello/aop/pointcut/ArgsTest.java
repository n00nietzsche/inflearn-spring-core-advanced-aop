package hello.aop.pointcut;

import hello.aop.member.MemberServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.*;

public class ArgsTest {
    Method helloMethod;

    @BeforeEach
    public void init() throws NoSuchMethodException {
        helloMethod = MemberServiceImpl.class.getMethod("hello", String.class);
    }

    /**
     * poincut은 한번 생성 후 `setExpression`을 통해 표현식을 넣으면, 불변이기 때문에
     * 매번 새로 만드는 불편함과 코드의 복잡도를 줄이기 위해 메서드를 만들어주었다.
     * @param expression
     * @return
     */
    private AspectJExpressionPointcut pointcut(String expression) {
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression(expression);
        return pointcut;
    }

    @Test
    @DisplayName("다양한 예제, execution과 크게 다르지 않다.")
    void args() {
        // 매개변수에 `String` 타입이 오는지, True
        assertThat(pointcut("args(String)")
                .matches(helloMethod, MemberServiceImpl.class)).isTrue();

        // 매개변수에 `Object` 타입이 오는지, True (String의 상위 타입)
        assertThat(pointcut("args(Object)")
                .matches(helloMethod, MemberServiceImpl.class)).isTrue();

        // 매개변수가 아무것도 없는지, False
        assertThat(pointcut("args()")
                .matches(helloMethod, MemberServiceImpl.class)).isFalse();

        // 매개변수에 상관없이 집기, True
        assertThat(pointcut("args(..)")
                .matches(helloMethod, MemberServiceImpl.class)).isTrue();

        // 매개변수가 어떤 타입이든 1개가 오는지, True
        assertThat(pointcut("args(*)")
                .matches(helloMethod, MemberServiceImpl.class)).isTrue();

        // 매개변수가 `String` 1개로 시작하는지, True
        assertThat(pointcut("args(String, ..)")
                .matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }

    /**
     * <p><pre>execution(* *(java.io.Serializable))</pre>: 메서드의 시그니처로 판단 (정적)</p>
     * <p><pre>args(java.io.Serializable)</pre>: 런타임에 전달된 인수로 판단 (동적)</p>
     */
    @Test
    @DisplayName("execution은 메서드의 시그니처로 판단(정적), args는 런타임에 전달된 인수로 판단(동적)")
    void argsVsExecution() {
        /* String은 여러가지 인터페이스들을 구현하고 있다.
           implements java.io.Serializable, Comparable<String>, CharSequence, Constable, ConstantDesc

           args를 이용한 표현식에서는 상위 인터페이스를 이용해도 매칭이 되는 반면,
           execution을 이용한 표현식에서는 상위 인터페이스로는 매칭이 되지 않는다.

           within과 비교했을 때는 인터페이스를 클래스처럼 잡을 수 있어서 좋았지만,
           args와 비교했을 때는 상위 타입으로 매개변수를 잡을 수는 없다.
         */
        assertThat(pointcut("args(String)")
                .matches(helloMethod, MemberServiceImpl.class)).isTrue();
        assertThat(pointcut("args(java.io.Serializable)")
                .matches(helloMethod, MemberServiceImpl.class)).isTrue();
        assertThat(pointcut("args(Object)")
                .matches(helloMethod, MemberServiceImpl.class)).isTrue();

        assertThat(pointcut("execution(* *(String))")
                .matches(helloMethod, MemberServiceImpl.class)).isTrue();
        assertThat(pointcut("execution(* *(java.io.Serializable))")
                .matches(helloMethod, MemberServiceImpl.class)).isFalse();
        assertThat(pointcut("execution(* *(Object))")
                .matches(helloMethod, MemberServiceImpl.class)).isFalse();
    }
}
