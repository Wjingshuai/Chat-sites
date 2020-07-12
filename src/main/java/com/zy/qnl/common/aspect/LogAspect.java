package com.zy.qnl.common.aspect;

import com.zy.qnl.common.annotation.Log;
import com.zy.qnl.common.utils.ByteUtils;
import com.zy.qnl.common.utils.HttpContextUtils;
import com.zy.qnl.common.utils.IPUtils;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;


/**
 * 日志，切面处理类
 *
 */
@Slf4j
@Aspect
@Component
public class LogAspect {
	
	@Pointcut("@annotation(com.zy.qnl.common.annotation.Log)")
	public void logPointCut() { 
		
	}

	@Around("logPointCut()")
	public Object around(ProceedingJoinPoint point) throws Throwable {
		long beginTime = System.currentTimeMillis();
		//执行方法
		Object result = point.proceed();
		//执行时长(毫秒)
		long time = System.currentTimeMillis() - beginTime;
		//保存日志
		saveLog(point, time,result);

		return result;
	}

	private void saveLog(ProceedingJoinPoint joinPoint, long time,Object result) {
		MethodSignature signature = (MethodSignature) joinPoint.getSignature();

		//请求的方法名
		String className = joinPoint.getTarget().getClass().getName();
		String methodName = signature.getName();

		//请求的参数
		Object[] args = joinPoint.getArgs();
		String params = null;
		try{
			params = new Gson().toJson(args[0]);
		}catch (Exception e){
			log.error("Gson().toJson方法异常:{}",e.getMessage());
		}

		//获取request
		HttpServletRequest request = HttpContextUtils.getHttpServletRequest();
		//设置IP地址
		IPUtils.getIpAddr(request);

		Method method = signature.getMethod();
		Log logValue = method.getAnnotation(Log.class);
		if(logValue != null){
			//注解上的描述
			log.info("描述:{}| 方法:{}| 耗时:{}| IP:{}| 入参:{}| 出参:{}",logValue.value(),className + "." + methodName + "()",time,IPUtils.getIpAddr(request),params,result);
		}

		// 打印JVM信息。
		if (log.isDebugEnabled()){
			Runtime runtime = Runtime.getRuntime();
			log.debug("URI:{} |总内存:{} |已用内存:{}",request.getRequestURI(), ByteUtils.formatByteSize(runtime.totalMemory()), ByteUtils.formatByteSize(runtime.totalMemory()-runtime.freeMemory()));
		}

	}
}
