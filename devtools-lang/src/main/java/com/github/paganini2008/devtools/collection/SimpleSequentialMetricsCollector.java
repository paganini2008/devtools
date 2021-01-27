package com.github.paganini2008.devtools.collection;

import java.util.Calendar;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

import com.github.paganini2008.devtools.Assert;
import com.github.paganini2008.devtools.date.DateUtils;
import com.github.paganini2008.devtools.date.SpanUnit;
import com.github.paganini2008.devtools.multithreads.ThreadUtils;

/**
 * 
 * SimpleSequentialMetricsCollector
 *
 * @author Jimmy Hoff
 * @version 1.0
 */
public class SimpleSequentialMetricsCollector implements SequentialMetricsCollector {

	public static final String DEFAULT_DATETIME_PATTERN = "HH:mm:ss";

	public SimpleSequentialMetricsCollector(int bufferSize, int span, SpanUnit spanUnit,
			HistoricalMetricsHandler historicalMetricsHandler) {
		Assert.lt(bufferSize, 1, "MetricsCollector's bufferSize must greater than zero");
		Assert.lt(span, 1, "MetricsCollector's sequential span must greater than zero");
		this.store = new ConcurrentHashMap<String, MetricsCollector>();
		this.supplier = () -> new SimpleMetricsCollector(bufferSize, true, historicalMetricsHandler);
		this.span = span;
		this.spanUnit = spanUnit;
	}

	private final Map<String, MetricsCollector> store;
	private final Supplier<MetricsCollector> supplier;
	private final SpanUnit spanUnit;
	private final int span;
	private final ThreadLocal<Calendar> calendarLocal = ThreadUtils.newThreadLocal(() -> Calendar.getInstance());
	private String datetimePattern = DEFAULT_DATETIME_PATTERN;

	public void setDatetimePattern(String datetimePattern) {
		this.datetimePattern = datetimePattern;
	}

	@Override
	public MetricUnit set(String metric, long timestamp, MetricUnit metricUnit) {
		Assert.hasNoText(metric, "No metric defined");
		Assert.isNull(metricUnit, "No metricUnit inputted");
		Calendar calendar = calendarLocal.get();
		long time = spanUnit.startsInMsWith(calendar, timestamp, span);
		MetricsCollector metricsCollector = MapUtils.get(store, metric, supplier);
		return metricsCollector.set(DateUtils.format(time, datetimePattern), metricUnit);
	}

	@Override
	public String[] metrics() {
		return store.keySet().toArray(new String[0]);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, MetricUnit> sequence(String metric) {
		if (store.containsKey(metric)) {
			return store.get(metric).fetch();
		}
		return Collections.EMPTY_MAP;
	}

}
