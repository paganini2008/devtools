package com.github.paganini2008.devtools.objectpool.dbpool;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.github.paganini2008.devtools.collection.LruMap;
import com.github.paganini2008.devtools.date.DateUtils;

/**
 * Query statistics in one day
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class DailyQueryStatistics {

	private final LruMap<String, ConcurrentMap<String, QuerySpan>> dailyStatistics;

	public DailyQueryStatistics() {
		dailyStatistics = new LruMap<String, ConcurrentMap<String, QuerySpan>>(7);
	}

	private int statisticalSqlSampleCount = 60;
	private long acceptableExecutionTime = 1000L;

	public void setStatisticalSqlSampleCount(int statisticalSqlSampleCount) {
		this.statisticalSqlSampleCount = statisticalSqlSampleCount;
	}

	public void setAcceptableExecutionTime(long acceptableExecutionTime) {
		this.acceptableExecutionTime = acceptableExecutionTime;
	}

	public void executed(String sql, Object[] parameters, long startTime, long endTime) {
		final String daily = DateUtils.format(endTime, "yyyy-MM-dd");
		ConcurrentMap<String, QuerySpan> data = dailyStatistics.get(daily);
		if (data == null) {
			dailyStatistics.put(daily, new ConcurrentHashMap<String, QuerySpan>());
			data = dailyStatistics.get(daily);
		}
		QuerySpan qs = data.get(sql);
		if (qs == null) {
			data.putIfAbsent(sql, new QuerySpanImpl(statisticalSqlSampleCount, acceptableExecutionTime));
			qs = data.get(sql);
		}
		qs.record(new QueryTraceImpl(sql, parameters, startTime, endTime));
	}

}
