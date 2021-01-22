package com.github.paganini2008.devtools.date;

import java.util.concurrent.TimeUnit;

import com.github.paganini2008.devtools.RandomUtils;
import com.github.paganini2008.devtools.collection.MetricUnit;
import com.github.paganini2008.devtools.collection.MetricUnits;
import com.github.paganini2008.devtools.collection.SimpleMetricsCollector;
import com.github.paganini2008.devtools.collection.SimpleSequentialMetricsCollector;
import com.github.paganini2008.devtools.multithreads.ThreadUtils;

public class TestMain {

	public static void test1(String[] args) {
		final String defaultMetric = "index-1";
		SimpleMetricsCollector metricsCollector = new SimpleMetricsCollector(128);
		for (int i = 0; i < 100000; i++) {
			metricsCollector.set(defaultMetric, MetricUnits.valueOf(RandomUtils.randomLong(1000, 1000000)));
		}
		MetricUnit metricUnit = metricsCollector.get(defaultMetric);
		System.out.println(metricUnit);
		System.out.println(metricUnit.getMiddleValue(4));
	}

	public static void main(String[] args) {
		final String defaultMetric = "index-1";
		SimpleSequentialMetricsCollector metricsCollector = new SimpleSequentialMetricsCollector(10, 7, SpanUnit.SECOND);
		for (int i = 0; i < 60; i++) {
			for (int j = 0; j < 100000; j++) {
				metricsCollector.set(defaultMetric, MetricUnits.valueOf(RandomUtils.randomLong(1000, 1000000)));
			}
			ThreadUtils.sleep(1, TimeUnit.SECONDS);
			System.out.println("Xie");
		}
		MetricUnit[] metricUnits = metricsCollector.values(defaultMetric);
		System.out.println(metricUnits.length);
		for(MetricUnit metricUnit: metricUnits) {
			System.out.println(metricUnit.toString() + "\t" + metricUnit.getMiddleValue() + "\t" + DateUtils.format(metricUnit.getTimestamp()));
		}
	}

}
