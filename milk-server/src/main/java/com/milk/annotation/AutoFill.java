package com.milk.annotation;

import com.milk.enumeration.OperationType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * ClassName: AutoFill
 * Package: com.milk.annotation
 * Description:自定义注解
 *
 * @Author 何坤燃
 * @Create 2025/2/2 0:36
 * @Version 1.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AutoFill {
    OperationType value();
}
