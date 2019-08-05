package com.allyes.components.amber.rabbitmq;

import org.apache.commons.lang3.reflect.MethodUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;

import com.allyes.components.amber.config.JobBeanFactory;
import com.allyes.components.amber.config.JobDispatcher;
import com.allyes.components.amber.config.JobListener;
import com.allyes.components.amber.config.JobParameter;

/**
 * 
 * RabbitmqJobProcessor
 * 
 * @author Fred Feng
 * @create 2018-03
 */
public class RabbitmqJobProcessor {

	private static final Logger logger = LoggerFactory.getLogger(RabbitmqJobProcessor.class);

	@Autowired
	private JobBeanFactory beanFactory;

	@RabbitListener(queues = "${amber.rabbitmq.queue}")
	@RabbitHandler
	public void onMessage(JobParameter parameter) {
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
