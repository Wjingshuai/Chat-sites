package com.zy.qnl.common.annotation;

import java.lang.annotation.*;

/**
 *日志
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Log {

	String value() default "";
}
