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

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.github.paganini2008.devtools.collection.LruMap;
import com.github.paganini2008.devtools.time.DateUtils;

/**
 * DailyQueryStatistics keeps the query statistics of the recent seven days,
 * grouped by day and sql.
 * 
 * @author Fred Feng
 * @date 2019-07-29
 * @since 2.0.1
 */
public class DailyQueryStatistics {

	private final static String dateFormatPattern = "dd/MM/yyyy";
	private final LruMap<String, ConcurrentMap<String, QuerySpan>> dailyStatistics;

	public DailyQueryStatistics() {
		dailyStatistics = new LruMap<String, ConcurrentMap<String, QuerySpan>>(7);
	}

	private int statisticalSampleCount = 60;
	private long acceptableExecutionTime = 1000L;

	/**
	 * Get the statistics of the given day.
	 * 
	 * @param daily
	 *            the day formatted as {@code dd/MM/yyyy}
	 * @return a copy of the statistics keyed by sql, or null if nothing was
	 *         recorded on that day
	 */
	public Map<String, QuerySpan> getStatisticsResult(String daily) {
		if (daily == null) {
			return null;
		}
		ConcurrentMap<String, QuerySpan> data;
		synchronized (dailyStatistics) {
			data = dailyStatistics.get(daily);
		}
		return data != null ? new HashMap<String, QuerySpan>(data) : null;
	}

	public void setStatisticalSampleCount(int statisticalSampleCount) {
		this.statisticalSampleCount = statisticalSampleCount;
	}

	public void setAcceptableExecutionTime(long acceptableExecutionTime) {
		this.acceptableExecutionTime = acceptableExecutionTime;
	}

	/**
	 * Record an execution of the sql.
	 * 
	 * @param sql
	 * @param parameters
	 * @param startTime
	 *            start time in milliseconds
	 * @param endTime
	 *            end time in milliseconds, which also decides the day
	 * @return the statistics of the sql on that day
	 */
	public QuerySpan executed(String sql, Object[] parameters, long startTime, long endTime) {
		final String daily = DateUtils.format(endTime, dateFormatPattern);
		ConcurrentMap<String, QuerySpan> data;
		synchronized (dailyStatistics) {
			data = dailyStatistics.get(daily);
			if (data == null) {
				data = new ConcurrentHashMap<String, QuerySpan>();
				dailyStatistics.put(daily, data);
			}
		}
		QuerySpan qs = data.get(sql);
		if (qs == null) {
			QuerySpan newSpan = new QuerySpanImpl(statisticalSampleCount, acceptableExecutionTime);
			qs = data.putIfAbsent(sql, newSpan);
			if (qs == null) {
				qs = newSpan;
			}
		}
		qs.record(new QueryTraceImpl(sql, parameters, startTime, endTime));
		return qs;
	}
}
