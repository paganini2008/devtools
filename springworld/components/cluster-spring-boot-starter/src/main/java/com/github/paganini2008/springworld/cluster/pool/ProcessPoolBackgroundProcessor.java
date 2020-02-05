package com.github.paganini2008.springworld.cluster.pool;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.github.paganini2008.devtools.StringUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * ProcessPoolBackgroundProcessor
 *
 * @author Fred Feng
 * @created 2020-01
 * @revised 2020-02
 * @version 1.0
 */
@Slf4j
@Aspect
public class ProcessPoolBackgroundProcessor {

	@Autowired
	private ProcessPool processPool;

	@Pointcut("execution(public * *(..))")
	public void signature() {
	}

	@Around("signature() && @annotation(com.github.paganini2008.springworld.cluster.pool.BackgroundProcessing)")
	public Object arround(ProceedingJoinPoint pjp) throws Throwable {
		long startTime = System.currentTimeMillis();
		Class<?> beanClass = pjp.getSignature().getDeclaringType();
		String beanClassName = pjp.getSignature().getDeclaringTypeName();
		Component component = beanClass.getAnnotation(Component.class);

		String beanName = component.value();
		if (StringUtils.isBlank(beanName)) {
			beanName = StringUtils.firstCharToLowerCase(beanClassName);
		}
		String methodName = pjp.getSignature().getName();
		Object[] arguments = pjp.getArgs();
		Throwable throwing = null;
		try {
			processPool.submit(beanName, beanClass, methodName, arguments);
			return null;
		} catch (Throwable e) {
			log.error(e.getMessage(), e);
			throwing = e;
			throw e;
		} finally {
			StringBuilder str = new StringBuilder();
			long elapsed = System.currentTimeMillis() - startTime;
			str.append("Consuming: " + elapsed);

			if (log.isTraceEnabled()) {
				log.trace(str.toString());
			}
		}

	}

}
