package com.github.paganini2008.springworld.cluster;

import org.springframework.context.ApplicationContext;

/**
 * 
 * ContextMulticastEvent
 *
 * @author Fred Feng
 * @created 2019-08
 * @revised 2019-08
 * @version 1.0
 */
public final class ContextMulticastEvent extends ContextClusterEvent {

	private static final long serialVersionUID = -7066246274529484854L;

	public ContextMulticastEvent(ApplicationContext source) {
		super(source);
	}

}
