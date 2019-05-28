package com.github.paganini2008.devtools.beans.oq;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.github.paganini2008.devtools.Comparables;
import com.github.paganini2008.devtools.beans.Getter;
import com.github.paganini2008.devtools.math.BigDecimals;

/**
 * 
 * Unit
 * 
 * @author Fred Feng
 *
 */
public class Unit<E> implements Unitable<E> {

	private final List<E> content;

	Unit(List<E> content) {
		this.content = content;
	}

	public int count() {
		return content.size();
	}

	public List<E> list() {
		return content;
	}

	public List<E> distinctList() {
		return list().stream().distinct().collect(Collectors.toCollection(new Supplier<List<E>>() {
			public List<E> get() {
				return new ArrayList<E>();
			}
		}));
	}

	public int distinctCount() {
		return distinctList().size();
	}

	public <T extends Comparable<T>> T max(final Getter<E, T> getter) {
		Optional<E> optional = list().stream().max(new Comparator<E>() {
			public int compare(E a, E b) {
				T left = getter.apply(a);
				T right = getter.apply(b);
				return Comparables.compareTo(left, right);
			}
		});
		return optional.isPresent() ? getter.apply(optional.get()) : null;
	}

	public <T extends Comparable<T>> T min(final Getter<E, T> getter) {
		Optional<E> optional = list().stream().min(new Comparator<E>() {
			public int compare(E a, E b) {
				T left = getter.apply(a);
				T right = getter.apply(b);
				return Comparables.compareTo(left, right);
			}
		});
		return optional.isPresent() ? getter.apply(optional.get()) : null;
	}

	public <T extends Number> BigDecimal sum(final Getter<E, T> getter) {
		return list().stream().map(new Function<E, BigDecimal>() {
			public BigDecimal apply(E entity) {
				T t = getter.apply(entity);
				return t != null ? BigDecimals.valueOf(t) : BigDecimal.ZERO;
			}
		}).reduce(BigDecimal.ZERO, new BinaryOperator<BigDecimal>() {
			public BigDecimal apply(BigDecimal left, BigDecimal right) {
				return left.add(right);
			}
		});
	}

	public <T extends Number> BigDecimal avg(final Getter<E, T> getter, int scale, RoundingMode roundingMode) {
		BigDecimal sum = sum(getter);
		return sum.divide(BigDecimal.valueOf(count()), scale, roundingMode);
	}

}
