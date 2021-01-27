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

	Number getMiddleValue(int scale);

	long getTimestamp();

	MetricUnit merge(MetricUnit anotherUnit);

}
