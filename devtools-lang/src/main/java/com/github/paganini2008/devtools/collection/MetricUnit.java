package com.github.paganini2008.devtools.collection;

/**
 * 
 * MetricUnit
 *
 * @author Jimmy Hoff
 * @version 1.0
 */
public interface MetricUnit {

	Number getHighestValue();

	Number getLowestValue();

	Number getTotalValue();

	int getCount();

	default Number getMiddleValue() {
		return getMiddleValue(4);
	}

	Number getMiddleValue(int scale);

	long getTimestamp();

	void update(MetricUnit anotherUnit);

}
