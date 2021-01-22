package com.github.paganini2008.devtools.collection;

import java.util.Calendar;
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

	public SimpleSequentialMetricsCollector(int maxSize, int span, SpanUnit spanUnit) {
		Assert.lt(maxSize, 1, "MetricsCollector's maxSize must greater than zero");
		Assert.lt(span, 1, "MetricsCollector's sequential span must greater than zero");
		this.store = new ConcurrentHashMap<String, MetricsCollector>();
		this.supplier = () -> new SimpleMetricsCollector(maxSize, true);
		this.span = span;
		this.spanUnit = spanUnit;
	}

	private final Map<String, MetricsCollector> store;
	private final Supplier<MetricsCollector> supplier;
	private final SpanUnit spanUnit;
	private final int span;
	private final ThreadLocal<Calendar> calendarLocal = ThreadUtils.newThreadLocal(() -> Calendar.getInstance());
	private String datePattern = "HH:mm:ss";

	public void setDatePattern(String datePattern) {
		this.datePattern = datePattern;
	}

	@Override
	public MetricUnit set(String metric, long timestamp, MetricUnit metricUnit) {
		Assert.hasNoText(metric, "No metric defined");
		Assert.isNull(metricUnit, "No metricUnit inputted");
		Calendar calendar = calendarLocal.get();
		long time = spanUnit.startsInMsWith(calendar, timestamp, span);
		MetricsCollector metricsCollector = MapUtils.get(store, metric, supplier);
		return metricsCollector.set(DateUtils.format(time, datePattern), metricUnit);
	}

	@Override
	public String[] metrics() {
		return store.keySet().toArray(new String[0]);
	}

	@Override
	public MetricUnit[] values(String metric) {
		MetricsCollector metricsCollector = store.get(metric);
		return metricsCollector != null ? metricsCollector.values() : new MetricUnit[0];
	}

}
