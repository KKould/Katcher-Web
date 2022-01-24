package com.kould.katcher.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

@Inherited
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Component
/**
 * 用于修饰控制器类
 * 类似SpringMVC的@Controller
 * 被该注解修饰的类的uri()可以让该类下的所有方法附加前缀
 */
public @interface Controller {
    String uri() default "";
}
