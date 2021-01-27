package com.github.paganini2008.devtools.collection;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

/**
 * 
 * SimpleMetricsCollector
 *
 * @author Jimmy Hoff
 * @version 1.0
 */
public class SimpleMetricsCollector implements MetricsCollector {

	private final Map<String, MetricUnit> store;

	public SimpleMetricsCollector() {
		this(-1, null);
	}

	public SimpleMetricsCollector(int bufferSize, HistoricalMetricsHandler historicalMetricsHandler) {
		this(bufferSize, true, historicalMetricsHandler);
	}

	public SimpleMetricsCollector(int bufferSize, boolean ordered, HistoricalMetricsHandler historicalMetricsHandler) {
		this.store = bufferSize > 0 ? new BufferedMetricsCollectorMap(ordered, bufferSize, historicalMetricsHandler)
				: new MetricsCollectorMap(ordered);
	}

	@Override
	public MetricUnit set(String metric, MetricUnit metricUnit) {
		return store.put(metric, metricUnit);
	}

	@Override
	public MetricUnit get(String metric) {
		return store.get(metric);
	}

	@Override
	public String[] metrics() {
		return store.keySet().toArray(new String[0]);
	}

	@Override
	public Map<String, MetricUnit> fetch() {
		return Collections.unmodifiableMap(new TreeMap<String, MetricUnit>(store));
	}

	/**
	 * 
	 * BufferedMetricsCollectorMap
	 *
	 * @author Jimmy Hoff
	 * @version 1.0
	 */
	private static class BufferedMetricsCollectorMap extends LruMap<String, MetricUnit> {

		private static final long serialVersionUID = 1L;

		private final HistoricalMetricsHandler historicalMetricsHandler;

		BufferedMetricsCollectorMap(boolean ordered, int bufferSize, HistoricalMetricsHandler historicalMetricsHandler) {
			super(new MetricsCollectorMap(ordered), bufferSize);
			this.historicalMetricsHandler = historicalMetricsHandler;
		}

		@Override
		public void onEviction(String metric, MetricUnit metricUnit) {
			if (historicalMetricsHandler != null) {
				historicalMetricsHandler.handleHistoricalMetrics(metric, metricUnit);
			}
		}

	}

	/**
	 * 
	 * MetricsCollectorMap
	 *
	 * @author Jimmy Hoff
	 * @version 1.0
	 */
	private static class MetricsCollectorMap extends AtomicReferenceMap<String, MetricUnit> {

		private static final long serialVersionUID = 1L;

		MetricsCollectorMap(boolean ordered) {
			super(ordered);
		}

		@Override
		protected MetricUnit merge(String metric, MetricUnit current, MetricUnit value) {
			if (current != null) {
				return current.merge(value);
			} else {
				return value;
			}
		}
	}

}
