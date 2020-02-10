package com.github.paganini2008.springworld.transport;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import com.github.paganini2008.devtools.Observable;

/**
 * 
 * HandlerBeanPostProcessor
 * 
 * @author Fred Feng
 * @created 2019-10
 * @revised 2019-10
 * @version 1.0
 */
public class HandlerBeanPostProcessor implements BeanPostProcessor, ApplicationListener<ContextRefreshedEvent> {

	private final Observable handlerObservable = Observable.unrepeatable();

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		if (bean instanceof Handler) {
			final Handler handler = (Handler) bean;
			handlerObservable.addObserver((ob, arg) -> {
				((LoopProcessor) arg).addHandler(handler);
			});
		}
		return bean;
	}

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		LoopProcessor loopProcessor = event.getApplicationContext().getBean(LoopProcessor.class);
		handlerObservable.notifyObservers(loopProcessor);
		loopProcessor.startDaemon();
	}

}
