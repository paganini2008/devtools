package com.github.paganini2008.springworld.fastjpa;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;

/**
 * 
 * AndFilter
 * 
 * @author Fred Feng
 * @revised 2019-07
 * @created 2019-02
 */
public class AndFilter extends LogicalFilter {

	private final Filter filter;

	private final Filter otherFilter;

	public AndFilter(Filter filter, Filter otherFilter) {
		this.filter = filter;
		this.otherFilter = otherFilter;
	}

	public Predicate toPredicate(Model<?> model, CriteriaBuilder builder) {
		Predicate left = filter.toPredicate(model, builder);
		Predicate right = otherFilter.toPredicate(model, builder);
		return builder.and(new Predicate[] { left, right });
	}

}
