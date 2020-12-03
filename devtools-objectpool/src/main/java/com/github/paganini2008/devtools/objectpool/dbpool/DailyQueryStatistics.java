package com.github.paganini2008.devtools.objectpool.dbpool;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.github.paganini2008.devtools.collection.LruMap;
import com.github.paganini2008.devtools.date.DateUtils;

/**
 * Query statistics recent seven-day
 * 
 * @author Jimmy Hoff
 * @version 1.0
 */
public class DailyQueryStatistics {

	private final static String dateFormatPattern = "dd/MM/yyyy";
	private final LruMap<String, ConcurrentMap<String, QuerySpan>> dailyStatistics;

	public DailyQueryStatistics() {
		dailyStatistics = new LruMap<String, ConcurrentMap<String, QuerySpan>>(7);
	}

	private int statisticalSampleCount = 60;
	private long acceptableExecutionTime = 1000L;

	public Map<String, QuerySpan> getStatisticsResult(String daily) {
		return dailyStatistics.containsKey(daily) ? new HashMap<String, QuerySpan>(dailyStatistics.get(daily)) : null;
	}

	public void setStatisticalSampleCount(int statisticalSampleCount) {
		this.statisticalSampleCount = statisticalSampleCount;
	}

	public void setAcceptableExecutionTime(long acceptableExecutionTime) {
		this.acceptableExecutionTime = acceptableExecutionTime;
	}

	public QuerySpan executed(String sql, Object[] parameters, long startTime, long endTime) {
		final String daily = DateUtils.format(endTime, dateFormatPattern);
		ConcurrentMap<String, QuerySpan> data = dailyStatistics.get(daily);
		if (data == null) {
			dailyStatistics.put(daily, new ConcurrentHashMap<String, QuerySpan>());
			data = dailyStatistics.get(daily);
		}
		QuerySpan qs = data.get(sql);
		if (qs == null) {
			data.putIfAbsent(sql, new QuerySpanImpl(statisticalSampleCount, acceptableExecutionTime));
			qs = data.get(sql);
		}
		qs.record(new QueryTraceImpl(sql, parameters, startTime, endTime));
		return qs;
	}
}
