package com.github.paganini2008.devtools.objectpool.dbpool;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicLong;

import com.github.paganini2008.devtools.collection.LruSet;

/**
 * 
 * QuerySpanImpl
 *
 * @author Fred Feng
 * 
 * 
 */
public class QuerySpanImpl implements QuerySpan, Serializable {

	private static final long serialVersionUID = 3318269140121500179L;

	public QuerySpanImpl(int maxSize, long acceptableExecutionTime) {
		this.slowQueries = new LruSet<QueryTrace>(new TreeSet<QueryTrace>(), maxSize);
		this.acceptableExecutionTime = acceptableExecutionTime;
	}

	private final LruSet<QueryTrace> slowQueries;
	private final long acceptableExecutionTime;

	private final AtomicLong executionTotalCount = new AtomicLong(0);
	private final AtomicLong executionTotalTime = new AtomicLong(0);
	private final AtomicLong executionMaxTime = new AtomicLong(0);
	private final AtomicLong executionMinTime = new AtomicLong(Long.MAX_VALUE);

	public void record(QueryTrace queryTrace) {
		final long time = (queryTrace.getEndTime() - queryTrace.getStartTime());

		executionTotalCount.incrementAndGet();
		executionTotalTime.addAndGet(time);

		long minTime = executionMinTime.get();
		executionMinTime.getAndSet(Math.min(minTime, time));
		long maxTime = executionMaxTime.get();
		executionMaxTime.getAndSet(Math.max(maxTime, time));

		if (time > acceptableExecutionTime) {
			slowQueries.add(queryTrace);
		}
	}

	public List<QueryTrace> getQueryTraces() {
		return new ArrayList<QueryTrace>(slowQueries);
	}

	public long getExecutionCount() {
		return executionTotalCount.get();
	}

	public long getExecutionAvgTime() {
		return executionTotalTime.get() / getExecutionCount();
	}

	public long getExecutionMaxTime() {
		return executionMaxTime.get();
	}

	public long getExecutionMinTime() {
		return executionMinTime.get();
	}

	public String toString() {
		return "QuerySpan [executionCount=" + getExecutionCount() + ", executionAvgTime=" + getExecutionAvgTime() + ", executionMaxTime="
				+ getExecutionMaxTime() + ", executionMinTime=" + getExecutionMinTime() + ", slowQueries: " + slowQueries.size() + "]";
	}

}
