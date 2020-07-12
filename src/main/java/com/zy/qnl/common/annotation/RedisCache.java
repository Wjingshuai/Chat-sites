package com.zy.qnl.common.annotation;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * 元注解 用来标识查询数据库的方法
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RedisCache {
    /**
     * 使用的缓存名称，默认是方法全名+签名
     *
     * @return
     */
    String cacheName() default "";


    /**
     * 过期时间。默认0，由缓存系统决定。
     * 单位为秒
     * @return
     */
    int expire() default 0;


    /**
     * 时间单位.默认秒.
     *
     * @return
     */
    TimeUnit unit() default TimeUnit.SECONDS;
}
