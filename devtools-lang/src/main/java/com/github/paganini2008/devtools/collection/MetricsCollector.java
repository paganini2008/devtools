package com.github.paganini2008.devtools.collection;

import java.util.Map;

/**
 * 
 * MetricsCollector
 *
 * @author Jimmy Hoff
 * @version 1.0
 */
public interface MetricsCollector {

	MetricUnit set(String metric, MetricUnit metricUnit);

	MetricUnit get(String metric);
	
	String[] metrics();
	
	Map<String, MetricUnit> fetch();

}