package com.github.paganini2008.springworld.cluster.pool;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.github.paganini2008.devtools.StringUtils;
import com.github.paganini2008.springworld.cluster.multicast.ContextMulticastGroup;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * ProcessPoolExecutor
 *
 * @author Fred Feng
 * @created 2020-01
 * @revised 2020-02
 * @version 1.0
 */
@Slf4j
@Aspect
@Component
public class ProcessPoolExecutor implements ProcessPool {

	public static final String TOPIC_IDENTITY = "PROCESS_POOL";

	@Autowired
	private ClusterLatch clusterLatch;

	@Autowired
	private RedisTemplate<String, Object> redisTemplate;

	@Autowired
	private ContextMulticastGroup contextMulticastGroup;

	@Pointcut("@annotation(com.github.paganini2008.springworld.cluster.pool.BackgroundProcessing)")
	public void signature(BackgroundProcessing backgroundProcessing) {
	}

	@Around("com.github.paganini2008.springworld.cluster.pool.ProcessPoolExecutor.signature(backgroundProcessing)")
	public Object arround(ProceedingJoinPoint pjp, BackgroundProcessing backgroundProcessing) throws Throwable {
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
		if (arguments == null) {
			arguments = new Object[0];
		}
		SignatureInfo signatureInfo = new SignatureInfo(beanName, beanClassName, methodName);
		signatureInfo.setArguments(arguments);
		Throwable throwing = null;
		try {
			contextMulticastGroup.unicast(TOPIC_IDENTITY, signatureInfo);
			return null;
		} catch (Throwable e) {
			log.error(e.getMessage(), e);
			throwing = e;
			throw e;
		} finally {
			StringBuilder str = new StringBuilder();
			long elapsed = System.currentTimeMillis() - startTime;
			str.append("Signature: " + signatureInfo + ", Consuming: " + elapsed);

			if (log.isTraceEnabled()) {
				log.trace(str.toString());
			}
		}

	}

}
