package com.github.paganini2008.springworld.cluster;

import org.springframework.context.ApplicationContext;

/**
 * 
 * ContextClusterStandbyEvent
 * 
 * @author Fred Feng
 * @created 2019-08
 * @revised 2019-08
 * @version 1.0
 */
public final class ContextClusterStandbyEvent extends ContextClusterEvent {

	private static final long serialVersionUID = -2932470508571995512L;

	public ContextClusterStandbyEvent(ApplicationContext context) {
		super(context);
	}

}
