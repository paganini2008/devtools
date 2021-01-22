package com.github.paganini2008.devtools.collection;

import com.github.paganini2008.devtools.ArrayUtils;

/**
 * 
 * SequentialMetricsCollector
 *
 * @author Jimmy Hoff
 * @version 1.0
 */
public interface SequentialMetricsCollector extends MetricsCollector {

	default MetricUnit set(String metric, MetricUnit metricUnit) {
		return set(metric, Long.min(System.currentTimeMillis(), metricUnit.getTimestamp()), metricUnit);
	}

	MetricUnit set(String metric, long timestamp, MetricUnit metricUnit);

	default MetricUnit get(String metrics) {
		return (MetricUnit) ArrayUtils.getLast(values(metrics));
	}

	default MetricUnit[] values() {
		String[] metrics = metrics();
		MetricUnit[] metricUnits = new MetricUnit[metrics.length];
		for (int i = 0; i < metricUnits.length; i++) {
			MetricUnit[] units = values(metrics[i]);
			metricUnits[i] = (MetricUnit) ArrayUtils.getLast(units);
		}
		return metricUnits;
	}

	MetricUnit[] values(String metric);

}
