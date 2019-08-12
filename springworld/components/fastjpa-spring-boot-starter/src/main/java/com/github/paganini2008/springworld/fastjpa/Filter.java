package com.github.paganini2008.springworld.fastjpa;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;

/**
 * 
 * Filter
 * 
 * @author Fred Feng
 * @revised 2019-07
 * @created 2019-02
 */
public interface Filter {

	public Predicate toPredicate(Model<?> model, CriteriaBuilder builder);

}
