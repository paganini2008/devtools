/**
* Copyright 2017-2022 Fred Feng (paganini.fy@gmail.com)

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
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicLong;

import com.github.paganini2008.devtools.collection.LruSet;
import com.github.paganini2008.devtools.primitives.Floats;

/**
 * 
 * QuerySpanImpl
 *
 * @author Fred Feng
 * @since 2.0.1
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
	private final AtomicLong executionMinTime = new AtomicLong(0);

	public void record(QueryTrace queryTrace) {
		final long time = (queryTrace.getEndTime() - queryTrace.getStartTime());

		executionTotalCount.incrementAndGet();
		executionTotalTime.addAndGet(time);

		long minTime = executionMinTime.get();
		executionMinTime.getAndSet(minTime == 0 ? minTime : Math.min(minTime, time));
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

	public float getExecutionAvgTime() {
		return Floats.toFixed((float) executionTotalTime.get() / getExecutionCount(), 1);
	}

	public long getExecutionMaxTime() {
		return executionMaxTime.get();
	}

	public long getExecutionMinTime() {
		return executionMinTime.get();
	}

	public String toString() {
		return "QuerySpan [executionCount=" + getExecutionCount() + ", executionAvgTime=" + getExecutionAvgTime() + "(ms), executionMaxTime="
				+ getExecutionMaxTime() + "(ms), executionMinTime=" + getExecutionMinTime() + "(ms), slowQueries: " + slowQueries.size() + "]";
	}

}
