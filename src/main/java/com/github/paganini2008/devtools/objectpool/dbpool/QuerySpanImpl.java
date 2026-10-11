/**
* Copyright 2017-2026 Fred Feng (paganini.fy@gmail.com)

* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.github.paganini2008.devtools.objectpool.dbpool;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicLong;

import com.github.paganini2008.devtools.primitives.Floats;

/**
 * 
 * QuerySpanImpl is the thread-safe {@link QuerySpan} which keeps the latest
 * slow queries in a bounded list.
 *
 * @author Fred Feng
 * @date 2019-07-29
 * @since 2.0.1
 */
public class QuerySpanImpl implements QuerySpan, Serializable {

	private static final long serialVersionUID = 3318269140121500179L;

	public QuerySpanImpl(int maxSize, long acceptableExecutionTime) {
		this.maxSize = maxSize;
		this.acceptableExecutionTime = acceptableExecutionTime;
	}

	private final int maxSize;
	private final LinkedList<QueryTrace> slowQueries = new LinkedList<QueryTrace>();
	private final long acceptableExecutionTime;

	private final AtomicLong executionTotalCount = new AtomicLong(0);
	private final AtomicLong executionTotalTime = new AtomicLong(0);
	private final AtomicLong executionMaxTime = new AtomicLong(0);
	private final AtomicLong executionMinTime = new AtomicLong(Long.MAX_VALUE);

	/**
	 * Record an execution. It is kept as a slow query if its elapsed time is
	 * greater than the acceptable execution time; only the latest
	 * {@code maxSize} slow queries are kept.
	 */
	public void record(QueryTrace queryTrace) {
		final long time = (queryTrace.getEndTime() - queryTrace.getStartTime());

		executionTotalTime.addAndGet(time);
		executionMinTime.accumulateAndGet(time, Math::min);
		executionMaxTime.accumulateAndGet(time, Math::max);
		executionTotalCount.incrementAndGet();

		if (time > acceptableExecutionTime && maxSize > 0) {
			synchronized (slowQueries) {
				slowQueries.addLast(queryTrace);
				while (slowQueries.size() > maxSize) {
					slowQueries.removeFirst();
				}
			}
		}
	}

	/**
	 * Get the kept slow queries, the slowest first.
	 */
	public List<QueryTrace> getQueryTraces() {
		List<QueryTrace> list;
		synchronized (slowQueries) {
			list = new ArrayList<QueryTrace>(slowQueries);
		}
		Collections.sort(list);
		return list;
	}

	public long getExecutionCount() {
		return executionTotalCount.get();
	}

	public float getExecutionAvgTime() {
		long count = getExecutionCount();
		return count > 0 ? Floats.toFixed((float) executionTotalTime.get() / count, 1) : 0F;
	}

	public long getExecutionMaxTime() {
		return executionMaxTime.get();
	}

	public long getExecutionMinTime() {
		long min = executionMinTime.get();
		return min == Long.MAX_VALUE ? 0L : min;
	}

	public String toString() {
		return "QuerySpan [executionCount=" + getExecutionCount() + ", executionAvgTime=" + getExecutionAvgTime() + "(ms), executionMaxTime="
				+ getExecutionMaxTime() + "(ms), executionMinTime=" + getExecutionMinTime() + "(ms), slowQueries: " + getQueryTraces().size() + "]";
	}

}
