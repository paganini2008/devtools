package com.github.paganini2008.devtools.beans.streaming;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 
 * Summary
 * 
 * @author Jimmy Hoff
 *
 * @version 1.0
 */
public interface Summary<E> {

	<T> T summarize(Calculation<E, T> calculation);

	default <T extends Comparable<T>> T min(String attributeName, Class<T> requiredType) {
		return summarize(Functions.min(attributeName, requiredType));
	}

	default <T extends Comparable<T>> T max(String attributeName, Class<T> requiredType) {
		return summarize(Functions.max(attributeName, requiredType));
	}

	default BigDecimal sum(String attributeName) {
		return summarize(Functions.sum(attributeName));
	}

	default BigDecimal avg(String attributeName) {
		return avg(attributeName, 2, RoundingMode.HALF_UP);
	}

	default BigDecimal avg(String attributeName, int scale, RoundingMode roundingMode) {
		return summarize(Functions.avg(attributeName, scale, roundingMode));
	}

}
