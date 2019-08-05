package com.allyes.components.jpahelper;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Subquery;

/**
 * 
 * SubQueryBuilder
 * 
 * @author Fred Feng
 * @revised 2019-07
 * @created 2019-02
 */
public interface SubQueryBuilder<T> {

	Subquery<T> toSubquery(CriteriaBuilder builder);

}
