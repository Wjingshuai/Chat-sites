package com.zy.qnl.common.aspect;

import com.zy.qnl.common.annotation.CacheKey;
import com.zy.qnl.common.annotation.RedisCache;
import com.zy.qnl.common.utils.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

@Slf4j
@Aspect
@Configuration
public class RedisCacheAspect{

    //是否开启redis缓存  true开启   false关闭
    @Value("${spring.redis.open: false}")
    private boolean open;

    @Autowired
    private RedisUtils redisUtils;

    /**
     * 拦截所有元注解RedisCache注解的方法
     * 写缓存使用
     */
    @Pointcut("@annotation(com.zy.qnl.common.annotation.RedisCache)")
    public void pointcutMethod() {
    }

    /**
     * 环绕处理，先从Redis里获取缓存,查询不到，就查询MySQL数据库，
     * 然后再保存到Redis缓存里
     *
     * @param joinPoint
     * @return
     */
    @Around("pointcutMethod()")
    public Object around(ProceedingJoinPoint joinPoint) throws NoSuchMethodException {
        if(!open){
            log.warn("redis没有开启不允许 缓存操作失败");
            return null;
        }
        //前置：从Redis里获取缓存
        //先获取目标方法参数
        long startTime = System.currentTimeMillis();
        String applId = null;
        Object[] args = joinPoint.getArgs();
        if (args != null && args.length > 0) {
            applId = String.valueOf(args[0]);
        }

        //获取目标方法所在类
        String target = joinPoint.getTarget().toString();
        String className = target.split("@")[0];

        //获取目标方法的方法名称
        String methodName = joinPoint.getSignature().getName();

        //redis中key格式：    applId:方法名称
        //String redisKey = applId + ":" + className + "." + methodName;
        Method targetMethon = getMethod(joinPoint);
        String cacheKey = getCacheKeyInArgs(args, targetMethon);
        RedisCache cacheMethod = getAnnotationOnMethod(RedisCache.class, joinPoint);
        String cacheName = getCacheName(cacheMethod, targetMethon);
        String redisKey = cacheName+"-"+cacheKey;

        Object obj = redisUtils.get(redisKey, targetMethon.getReturnType());

        if (obj != null) {
            log.info("**********从Redis中查到了数据**********");
            log.info("Redis的KEY值:" + redisKey);
            log.info("REDIS的VALUE值:" + obj.toString());
            return obj;
        }
        long endTime = System.currentTimeMillis();
        log.info("Redis缓存AOP处理所用时间:" + (endTime - startTime));
        log.info("**********没有从Redis查到数据**********");
        log.info("**********开始从MySQL查询数据**********");
        try {
            obj = joinPoint.proceed();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        //后置：将数据库查到的数据保存到Redis  RedisAspect监控是否成功
        int exp = targetMethon.getAnnotation(RedisCache.class).expire();
        if(exp == 0){
            redisUtils.set(redisKey, obj);
        }else {
            redisUtils.set(redisKey, obj, exp);
        }
        log.info("**********数据成功保存到Redis缓存!!!**********");
        log.info("Redis的KEY值:" + redisKey);
        log.info("REDIS的VALUE值:" + obj.toString());
        return obj;
    }

    private String getCacheName(RedisCache cacheMethod, Method targetMethod) {
        String cacheName = cacheMethod.cacheName();
        if (isEmpty(cacheName)) {
            //如果注解没设置缓存名称，则使用方法+签名字符串。
            cacheName = targetMethod.toString();
        }
        return cacheName;
    }

    private String getCacheKeyInArgs(Object[] args, Method targetMethod) {

        String key = "";
        if (args != null && args.length > 0) {
            //有很多参数的方法
            List<Object> annotationArgs = getArgsOf(CacheKey.class, targetMethod, args);
            if (annotationArgs != null && !annotationArgs.isEmpty()) {
                Object k = safeGetOne(annotationArgs, null);
                if (k == null) {
                    return null;
                } else {
                    return String.valueOf(k);
                }
            } else {
                log.warn("NO CACHE KEY FOUND IN ARGS {}", targetMethod.getName());
            }
        }

        return key;
    }


    public <T> T safeGetOne(List<Object> list, Class<T> clazz) {
        if (list != null && !list.isEmpty()) {
            if (clazz == null) {
                return (T) list.get(0);
            } else {
                Iterator var4 = list.iterator();

                while(var4.hasNext()) {
                    Object o = var4.next();
                    if (clazz.isInstance(o)) {
                        return (T) o;
                    }
                }

                return null;
            }
        } else {
            return null;
        }
    }

    public  boolean isNotEmpty(String key) {
        return key != null && !"".equals(key) && !"".equals(key.trim());
    }

    public  boolean isEmpty(String key) {
        return !this.isNotEmpty(key);
    }

    public String nullToEmpty(String val) {
        return val == null ? "" : val;
    }

    protected List<Object> getArgsOf(Class<? extends Annotation> annotation, Method targetMethod, Object[] args) {
        Annotation[][] annotations = targetMethod.getParameterAnnotations();
        if (annotations.length > 0) {
            List<Object> list = new ArrayList(3);
            this.compareAndGetValue(annotation, args, annotations, list);
            return list;
        } else {
            return Collections.emptyList();
        }
    }

    private void compareAndGetValue(Class<? extends Annotation> annotation, Object[] args, Annotation[][] annotations, List<Object> list) {
        for(int i = 0; i < annotations.length; ++i) {
            for(int j = 0; j < annotations[i].length; ++j) {
                if (annotations[i][j].annotationType() == annotation) {
                    list.add(args[i]);
                }
            }
        }

    }

    public final Method getMethod(ProceedingJoinPoint joinPoint) throws NoSuchMethodException {
        MethodSignature signature = (MethodSignature)joinPoint.getSignature();
        return signature.getMethod().getDeclaringClass().isInterface() ? joinPoint.getTarget().getClass().getDeclaredMethod(signature.getName(), signature.getParameterTypes()) : signature.getMethod();
    }

    public final <T extends Annotation> T getAnnotationOnMethod(Class<T> annotationClass, ProceedingJoinPoint joinPoint) throws NoSuchMethodException {
        return this.getMethod(joinPoint).getAnnotation(annotationClass);
    }
}
