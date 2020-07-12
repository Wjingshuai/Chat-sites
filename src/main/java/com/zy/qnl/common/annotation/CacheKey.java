package com.zy.qnl.common.annotation;

import java.lang.annotation.*;

/**
 * 缓存KEY。同一个方法设置多个参数时，只有第一个会生效。
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface CacheKey {
}
