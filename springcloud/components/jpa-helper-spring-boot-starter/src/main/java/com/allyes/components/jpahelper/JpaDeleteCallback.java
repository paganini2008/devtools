package com.allyes.components.jpahelper;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;

/**
 * 
 * JpaDeleteCallback
 * 
 * @author Fred Feng
 * @created 2019-02
 */
public interface JpaDeleteCallback<T> {

	CriteriaDelete<T> doInJpa(CriteriaBuilder builder);

}
