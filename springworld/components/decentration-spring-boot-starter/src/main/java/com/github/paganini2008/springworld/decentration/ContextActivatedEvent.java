package com.github.paganini2008.springworld.decentration;

import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ApplicationContextEvent;

/**
 * 
 * ContextActivatedEvent
 * 
 * @author Fred Feng
 * @created 2019-08
 * @revised 2019-08
 * @version 1.0
 */
public class ContextActivatedEvent extends ApplicationContextEvent {

	private static final long serialVersionUID = -2932470508571995512L;
	
	public ContextActivatedEvent(ApplicationContext context) {
		super(context);
	}

	



}
