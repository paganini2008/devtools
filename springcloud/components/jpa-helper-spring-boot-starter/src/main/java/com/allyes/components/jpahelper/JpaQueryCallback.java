package com.allyes.components.jpahelper;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

/**
 * 
 * JpaQueryCallback
 * 
 * @author Fred Feng
 * @created 2019-02
 */
public interface JpaQueryCallback<T> {

	CriteriaQuery<T> doInJpa(CriteriaBuilder builder);

}
