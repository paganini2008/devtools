package com.github.paganini2008.springworld.redis.pubsub;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;

import com.github.paganini2008.devtools.reflection.MethodUtils;

/**
 * 
 * RedisMessageHandlerBeanProcessor
 * 
 * @author Fred Feng
 * @created 2019-08
 * @revised 2019-08
 * @version 1.0
 */
public class RedisMessageHandlerBeanProcessor implements BeanPostProcessor {

	@Autowired
	private RedisMessageSender redisMessageSender;

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		if (bean instanceof RedisMessageHandler) {
			RedisMessageHandler messageHandler = (RedisMessageHandler) bean;
			redisMessageSender.subscribeChannel(beanName, messageHandler);
		} else if (bean.getClass().isAnnotationPresent(MessageHandler.class)) {
			MessageHandler annotation = bean.getClass().getAnnotation(MessageHandler.class);
			redisMessageSender.subscribeChannel(beanName, new ReflectiveRedisMessageHandler(annotation, bean));
		}
		return bean;
	}

	/**
	 * 
	 * ReflectiveRedisMessageHandler
	 *
	 * @author Fred Feng
	 * @created 2020-01
	 * @revised 2020-02
	 * @version 1.0
	 */
	private static class ReflectiveRedisMessageHandler implements RedisMessageHandler {

		private final MessageHandler annotation;
		private final Object targetBean;

		ReflectiveRedisMessageHandler(MessageHandler annotation, Object targetBean) {
			this.annotation = annotation;
			this.targetBean = targetBean;
		}

		@Override
		public String getChannel() {
			return annotation.value();
		}

		@Override
		public void onMessage(Object message) {
			MethodUtils.invokeMethodsWithAnnotation(targetBean, OnMessage.class, message);
		}

		@Override
		public boolean isEphemeral() {
			return annotation.ephemeral();
		}

	}

}
