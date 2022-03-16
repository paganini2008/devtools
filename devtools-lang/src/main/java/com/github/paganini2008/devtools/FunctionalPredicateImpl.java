package com.github.paganini2008.devtools;

import java.util.Collection;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * 
 * FunctionalPredicateImpl
 *
 * @author Fred Feng
 *
 * @version 2.0.5
 */
public class FunctionalPredicateImpl<T> implements FunctionalPredicate<T> {

	public FunctionalPredicateImpl() {
		this(t -> true);
	}

	protected FunctionalPredicateImpl(Predicate<T> junction) {
		this.junction = junction;
	}

	protected Predicate<T> junction;

	@Override
	public <R extends Comparable<R>> FunctionalPredicate<T> nonNull(Function<T, R> supplier) {
		junction = junction.and(t -> supplier.apply(t) != null);
		return this;
	}

	@Override
	public <R extends Comparable<R>> FunctionalPredicate<T> isNull(Function<T, R> supplier) {
		junction = junction.and(t -> supplier.apply(t) == null);
		return this;
	}

	@Override
	public <R extends Comparable<R>> FunctionalPredicate<T> between(Function<T, R> supplier, R minValue, R maxValue, boolean exclusive) {
		junction = junction.and(t -> Comparables.between(supplier.apply(t), minValue, maxValue, exclusive));
		return this;
	}

	@Override
	public <R extends Comparable<R>> FunctionalPredicate<T> eq(Function<T, R> supplier, R value) {
		junction = junction.and(t -> Comparables.eq(supplier.apply(t), value));
		return this;
	}

	@Override
	public <R extends Comparable<R>> FunctionalPredicate<T> ne(Function<T, R> supplier, R value) {
		junction = junction.and(t -> Comparables.ne(supplier.apply(t), value));
		return this;
	}

	@Override
	public <R extends Comparable<R>> FunctionalPredicate<T> lt(Function<T, R> supplier, R value) {
		junction = junction.and(t -> Comparables.lt(supplier.apply(t), value));
		return this;
	}

	@Override
	public <R extends Comparable<R>> FunctionalPredicate<T> lte(Function<T, R> supplier, R value) {
		junction = junction.and(t -> Comparables.lte(supplier.apply(t), value));
		return this;
	}

	@Override
	public <R extends Comparable<R>> FunctionalPredicate<T> gt(Function<T, R> supplier, R value) {
		junction = junction.and(t -> Comparables.gt(supplier.apply(t), value));
		return this;
	}

	@Override
	public <R extends Comparable<R>> FunctionalPredicate<T> gte(Function<T, R> supplier, R value) {
		junction = junction.and(t -> Comparables.gte(supplier.apply(t), value));
		return this;
	}

	@Override
	public <R extends Comparable<R>> FunctionalPredicate<T> eqAny(Function<T, R> supplier, Collection<R> values) {
		junction = junction.and(t -> Comparables.eqAny(supplier.apply(t), values));
		return this;
	}

	@Override
	public <R extends Comparable<R>> FunctionalPredicate<T> eqAll(Function<T, R> supplier, Collection<R> values) {
		junction = junction.and(t -> Comparables.eqAll(supplier.apply(t), values));
		return this;
	}

	@Override
	public <R extends Comparable<R>> FunctionalPredicate<T> ltAny(Function<T, R> supplier, Collection<R> values) {
		junction = junction.and(t -> Comparables.ltAny(supplier.apply(t), values));
		return this;
	}

	@Override
	public <R extends Comparable<R>> FunctionalPredicate<T> ltAll(Function<T, R> supplier, Collection<R> values) {
		junction = junction.and(t -> Comparables.ltAll(supplier.apply(t), values));
		return this;
	}

	@Override
	public <R extends Comparable<R>> FunctionalPredicate<T> lteAny(Function<T, R> supplier, Collection<R> values) {
		junction = junction.and(t -> Comparables.lteAny(supplier.apply(t), values));
		return this;
	}

	@Override
	public <R extends Comparable<R>> FunctionalPredicate<T> lteAll(Function<T, R> supplier, Collection<R> values) {
		junction = junction.and(t -> Comparables.lteAll(supplier.apply(t), values));
		return this;
	}

	@Override
	public <R extends Comparable<R>> FunctionalPredicate<T> gtAny(Function<T, R> supplier, Collection<R> values) {
		junction = junction.and(t -> Comparables.gtAny(supplier.apply(t), values));
		return this;
	}

	@Override
	public <R extends Comparable<R>> FunctionalPredicate<T> gtAll(Function<T, R> supplier, Collection<R> values) {
		junction = junction.and(t -> Comparables.gtAll(supplier.apply(t), values));
		return this;
	}

	@Override
	public <R extends Comparable<R>> FunctionalPredicate<T> gteAny(Function<T, R> supplier, Collection<R> values) {
		junction = junction.and(t -> Comparables.gteAny(supplier.apply(t), values));
		return this;
	}

	@Override
	public <R extends Comparable<R>> FunctionalPredicate<T> gteAll(Function<T, R> supplier, Collection<R> values) {
		junction = junction.and(t -> Comparables.gteAll(supplier.apply(t), values));
		return this;
	}

	@Override
	public FunctionalPredicate<T> matches(Function<T, String> supplier, String substr, MatchMode matchMode) {
		junction = junction.and(t -> matchMode.matches(supplier.apply(t), substr));
		return this;
	}

	@Override
	public boolean test(T t) {
		return junction.test(t);
	}

}
