package com.github.paganini2008.devtools.beans.oq;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.github.paganini2008.devtools.Comparables;
import com.github.paganini2008.devtools.beans.Getter;
import com.github.paganini2008.devtools.math.BigDecimals;

/**
 * 
 * Select
 * 
 * @author Fred Feng
 *
 */
public class Select<E> implements Selectable<E> {

	private final Collection<E> content;

	Select(Collection<E> content) {
		this.content = content;
	}

	public static <E> Select<E> from(Collection<E> content) {
		return new Select<E>(content);
	}

	public Selectable<E> filter(final Expression<E> expression) {
		List<E> results = content.stream().filter(new Predicate<E>() {
			public boolean test(E entity) {
				return expression.accept(entity);
			}
		}).collect(Collectors.toCollection(new Supplier<List<E>>() {
			public List<E> get() {
				return new ArrayList<E>();
			}
		}));
		return new Select<E>(results);
	}

	public List<E> list() {
		return new ArrayList<E>(content);
	}

	public List<E> distinctList() {
		return list().stream().distinct().collect(Collectors.toCollection(new Supplier<List<E>>() {
			public List<E> get() {
				return new ArrayList<E>();
			}
		}));
	}

	public Sortable<E> sort() {
		return new Sort<E>(list());
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
		return sum.divide(BigDecimal.valueOf(list().size()), scale, roundingMode);
	}

	public Groupable<E> group() {
		return new Group<E>(list());
	}

	public int count() {
		return content.size();
	}

	public int distinctCount() {
		return (int) content.stream().distinct().count();
	}

}
