package com.github.paganini2008.springworld.fastjpa;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;

/**
 * 
 * PredicateBuilder
 * 
 * @author Fred Feng
 * @revised 2019-07
 * @created 2019-02
 * @version 1.0
 */
public interface PredicateBuilder<T> {

	default String getDefaultAlias() {
		return Model.ROOT;
	}

	Predicate toPredicate(Model<?> model, Expression<T> expression, CriteriaBuilder builder);

}
