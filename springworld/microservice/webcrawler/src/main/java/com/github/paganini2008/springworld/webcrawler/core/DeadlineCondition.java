package com.github.paganini2008.springworld.webcrawler.core;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

import com.github.paganini2008.transport.Tuple;

/**
 * 
 * DeadlineCondition
 * 
 * @author Fred Feng
 * @created 2019-10
 * @revised 2019-12
 * @version 1.0
 */
public class DeadlineCondition implements FinishCondition {

	private final long time;
	private final TimeUnit timeUnit;
	private final ResourceCounter resourceCounter;

	public DeadlineCondition(long time, TimeUnit timeUnit, ResourceCounter resourceCounter) {
		this.time = time;
		this.timeUnit = timeUnit;
		this.resourceCounter = resourceCounter;
	}

	private final ConcurrentMap<Long, Long> endTimes = new ConcurrentHashMap<Long, Long>();

	@Override
	public boolean shouldFinish(Tuple tuple) {
		Long sourceId = (Long) tuple.getField("sourceId");
		if (resourceCounter.getStartTime(sourceId) == 0) {
			return false;
		}
		return getEndTime(sourceId) > System.currentTimeMillis();
	}

	private Long getEndTime(Long sourceId) {
		Long endTime = endTimes.get(sourceId);
		if (endTime == null) {
			endTimes.putIfAbsent(sourceId, resourceCounter.getStartTime(sourceId) + TimeUnit.MILLISECONDS.convert(time, timeUnit));
			endTime = endTimes.get(sourceId);
		}
		return endTime;
	}

}
