package com.github.paganini2008.springworld.webcrawler.core;

import com.github.paganini2008.springworld.socketbird.Tuple;

/**
 * 
 * CountLimitedCondition
 * 
 * @author Fred Feng
 * @created 2019-10
 * @revised 2019-12
 * @version 1.0
 */
public class CountLimitedCondition implements BatchCondition {

	private final long countLimit;
	private final ResourceCounter resourceCounter;

	public CountLimitedCondition(ResourceCounter resourceCounter) {
		this(10000, resourceCounter);
	}

	public CountLimitedCondition(long countLimit, ResourceCounter resourceCounter) {
		this.countLimit = countLimit;
		this.resourceCounter = resourceCounter;
	}

	@Override
	public boolean finish(Tuple tuple) {
		Long sourceId = (Long) tuple.getField("sourceId");
		long count = resourceCounter.get(sourceId);
		return countLimit > count;
	}

}
