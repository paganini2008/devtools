package com.github.paganini2008.devtools.collection;

import java.util.Map;

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
		this(-1);
	}

	public SimpleMetricsCollector(int maxSize) {
		this(maxSize, true);
	}

	public SimpleMetricsCollector(int maxSize, boolean ordered) {
		this.store = maxSize > 0 ? new LruMap<String, MetricUnit>(new MetricsCollectorMap(ordered), maxSize)
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
	public MetricUnit[] values() {
		return store.values().toArray(new MetricUnit[0]);
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
		protected MetricUnit merge(String metric, MetricUnit current, MetricUnit update) {
			if (current != null) {
				update.update(current);
			}
			return update;
		}
	}

}
