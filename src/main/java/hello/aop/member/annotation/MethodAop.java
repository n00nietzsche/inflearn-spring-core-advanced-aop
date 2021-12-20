package hello.aop.member.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
// SOURCE와 같은 폴리시는 컴파일 뒤에 사라져버림
@Retention(RetentionPolicy.RUNTIME)
public @interface MethodAop {
    String value();
}
