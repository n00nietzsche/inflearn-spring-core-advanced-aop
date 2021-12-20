package hello.aop.member.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// 클래스에 붙이는 애노테이션임을 명시
@Target(ElementType.TYPE)
// 런타임 때까지 애노테이션이 살아있다.
@Retention(RetentionPolicy.RUNTIME)
public @interface ClassAop {

}
