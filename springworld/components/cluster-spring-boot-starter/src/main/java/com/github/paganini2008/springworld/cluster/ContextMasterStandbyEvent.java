package com.github.paganini2008.springworld.cluster;

import org.springframework.context.ApplicationContext;

/**
 * 
 * ContextMasterStandbyEvent
 * 
 * @author Fred Feng
 * @created 2019-08
 * @revised 2019-08
 * @version 1.0
 */
public final class ContextMasterStandbyEvent extends ContextClusterEvent {

	private static final long serialVersionUID = -2932470508571995512L;

	public ContextMasterStandbyEvent(ApplicationContext context) {
		super(context);
	}

}
