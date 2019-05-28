package com.github.paganini2008.devtools.beans.oq;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.github.paganini2008.devtools.beans.Getter;

/**
 * 
 * Aggregation
 * 
 * @author Fred Feng
 *
 */
public interface Aggregation<E> {

	<T extends Comparable<T>> T max(Getter<E, T> getter);

	<T extends Comparable<T>> T min(Getter<E, T> getter);

	<T extends Number> BigDecimal sum(Getter<E, T> getter);

	<T extends Number> BigDecimal avg(Getter<E, T> getter, int scale, RoundingMode roundingMode);
}
