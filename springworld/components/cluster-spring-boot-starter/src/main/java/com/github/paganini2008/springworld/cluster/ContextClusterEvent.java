package com.github.paganini2008.springworld.cluster;

import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ApplicationContextEvent;

/**
 * 
 * ContextClusterEvent
 *
 * @author Fred Feng
 * @revised 2019-08
 * @created 2019-08
 * @version 1.0
 */
public abstract class ContextClusterEvent extends ApplicationContextEvent {

	private static final long serialVersionUID = -9030425105386583374L;

	public ContextClusterEvent(ApplicationContext source) {
		super(source);
	}

}
