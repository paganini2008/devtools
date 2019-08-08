package com.github.paganini2008.springworld.amber.rabbitmq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.github.paganini2008.springworld.amber.config.JobDispatcher;
import com.github.paganini2008.springworld.amber.config.JobParameter;

/**
 * 
 * RabbitmqDispatcher
 * 
 * @author Fred Feng
 * @create 2018-03
 */
public class RabbitmqDispatcher implements JobDispatcher {

	private static final Logger logger = LoggerFactory.getLogger(JobDispatcher.class);

	@Value("${amber.rabbitmq.queue}")
	private String routingKey;

	@Autowired
	private RabbitTemplate rabbitTemplate;

	public void dispatch(JobParameter parameter) {
		try {
			rabbitTemplate.convertAndSend(routingKey, parameter);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

}
