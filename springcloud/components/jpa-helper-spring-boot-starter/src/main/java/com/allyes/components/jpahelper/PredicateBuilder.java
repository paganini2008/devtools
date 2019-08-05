package com.allyes.components.jpahelper;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;

/**
 * 
 * PredicateBuilder
 * 
 * @author Fred Feng
 * @created 2019-02
 */
public interface PredicateBuilder<T> {

	default String getDefaultAlias() {
		return Model.ROOT;
	}

	Predicate toPredicate(Model<?> model, Expression<T> expression, CriteriaBuilder builder);

}
