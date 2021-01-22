package com.github.paganini2008.devtools.collection;

import java.io.Serializable;

/**
 * 
 * LongStatisticalUnit
 *
 * @author Jimmy Hoff
 * @version 1.0
 */
public class LongUnit implements Serializable {

	private static final long serialVersionUID = -2260288923022788088L;
	private long minimum;
	private long maximum;
	private long total;
	private long count;
	private final long timestamp;

	public LongUnit(long value) {
		this.minimum = value;
		this.maximum = value;
		this.total = value;
		this.count = 1;
		this.timestamp = System.currentTimeMillis();
	}

	public long getMinimum() {
		return minimum;
	}

	public void setMinimum(long minimum) {
		this.minimum = minimum;
	}

	public long getMaximum() {
		return maximum;
	}

	public void setMaximum(long maximum) {
		this.maximum = maximum;
	}

	public double getAverage() {
		return count > 0 ? (double) (total / count) : 0;
	}

	public long getTotal() {
		return total;
	}

	public long getCount() {
		return count;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTotal(long total) {
		this.total = total;
	}

	public void setCount(long count) {
		this.count = count;
	}

}
