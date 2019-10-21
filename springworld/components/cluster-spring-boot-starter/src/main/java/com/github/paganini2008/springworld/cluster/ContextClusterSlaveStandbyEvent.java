package com.github.paganini2008.springworld.cluster;

import org.springframework.context.ApplicationContext;

/**
 * 
 * ContextClusterSlaveStandbyEvent
 * 
 * @author Fred Feng
 * @created 2019-10
 * @revised 2019-10
 * @version 1.0
 */
public class ContextClusterSlaveStandbyEvent extends ContextClusterEvent {

	private static final long serialVersionUID = 9109166626001674260L;

	public ContextClusterSlaveStandbyEvent(ApplicationContext context) {
		super(context);
	}

}
