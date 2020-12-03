package com.github.paganini2008.devtools.multithreads;

import java.util.concurrent.atomic.AtomicInteger;

import com.github.paganini2008.devtools.StringUtils;

/**
 * PooledThreadFactory
 * 
 * @author Jimmy Hoff
 * @version 1.0
 */
public class PooledThreadFactory extends DefaultThreadFactory {

	private static final AtomicInteger poolNumber = new AtomicInteger(1);
	
	public PooledThreadFactory() {
		this(null);
	}

	public PooledThreadFactory(String prefix) {
		super(getPrefix(prefix));
	}

	private static String getPrefix(String prefix) {
		int poolNum = poolNumber.getAndIncrement();
		return StringUtils.isNotBlank(prefix) ? prefix + poolNum : "pool-" + poolNum;
	}

}
