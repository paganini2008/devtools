package com.github.paganini2008.springworld.decentration;

import org.springframework.context.ApplicationEvent;

/**
 * 
 * CentralizingEvent
 * 
 * @author Fred Feng
 * @created 2019-08
 * @revised 2019-08
 * @version 1.0
 */
public class CentralizingEvent extends ApplicationEvent {

	private static final long serialVersionUID = -2932470508571995512L;

	public CentralizingEvent(Object source) {
		super(source);
	}

}
