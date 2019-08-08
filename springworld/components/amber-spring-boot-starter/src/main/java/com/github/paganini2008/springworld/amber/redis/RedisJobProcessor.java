package com.github.paganini2008.springworld.amber.redis;

import org.apache.commons.lang3.reflect.MethodUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.data.redis.core.StringRedisTemplate;

import com.github.paganini2008.devtools.StringUtils;
import com.github.paganini2008.springworld.amber.config.JobBeanFactory;
import com.github.paganini2008.springworld.amber.config.JobDispatcher;
import com.github.paganini2008.springworld.amber.config.JobListener;
import com.github.paganini2008.springworld.amber.config.JobParameter;
import com.github.paganini2008.springworld.amber.config.JobParameterImpl;
import com.github.paganini2008.springworld.amber.utils.JsonUtils;

/**
 * 
 * RedisJobProcessor
 * 
 * @author Fred Feng
 * @create 2018-03
 */
public class RedisJobProcessor implements ApplicationListener<JobServerEvent> {

	private static final Logger logger = LoggerFactory.getLogger(RedisJobProcessor.class);

	@Autowired
	private JobBeanFactory beanFactory;

	@Autowired
	private StringRedisTemplate redisTemplate;

	@Autowired
	private RedisClientQueue clientQueue;
	
	public RedisJobProcessor() {
	}

	@Value("${amber.redis.channel}")
	private String channel;

	public void onApplicationEvent(JobServerEvent event) {
		final String eventId = event.getEventId();
		switch (event.getEventType()) {
		case ON_CONNECTION:
			onConnection(eventId);
			break;
		case ON_MESSAGE:
			onMessage(eventId);
			break;
		case ON_DISCONNECTION:
			onDisconnection(eventId);
			break;
		}
	}

	protected void onDisconnection(String clientId) {
		logger.info("On Disconnection: " + clientId);
		clientQueue.removeId(clientId);
	}

	protected void onConnection(String clientId) {
		logger.info("On Connection: " + clientId);
		clientQueue.acceptId(clientId);
	}

	protected void onMessage(String clientId) {
		if (!RedisDispatcher.ID.equals(clientId)) {
			return;
		}
		String json = (String) redisTemplate.opsForList().leftPop(channel);
		if (StringUtils.isBlank(json)) {
			return;
		}
		final JobParameter parameter = JsonUtils.readObject(json, JobParameterImpl.class);
		final Object target = beanFactory.getBean(parameter.getJobClass());
		final JobListener listener = target instanceof JobListener ? (JobListener) target : JobListener.DEFAULT;
		listener.beforeExecution(parameter);
		Object result = null;
		try {
			result = MethodUtils.invokeMethod(target, JobDispatcher.DEFAULT_JOB_INVOCATION, parameter);
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			listener.onError(parameter, e);
		} finally {
			listener.afterExecution(parameter, result);
		}
	}

}
