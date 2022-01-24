package com.kould.katcher.annotation;


import com.kould.katcher.status.HttpMethod;

import java.lang.annotation.*;

/**
 * 用于修饰经@Controller的类下需要对外的方法
 */
@Inherited
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Mapping {
    String uri();
    HttpMethod method();
}
