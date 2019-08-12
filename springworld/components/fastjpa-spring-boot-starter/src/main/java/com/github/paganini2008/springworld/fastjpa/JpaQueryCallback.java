package com.github.paganini2008.springworld.fastjpa;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

/**
 * 
 * JpaQueryCallback
 *
 * @author Fred Feng
 * @revised 2019-07
 * @created 2019-02
 * @version 1.0
 */
public interface JpaQueryCallback<T> {

	CriteriaQuery<T> doInJpa(CriteriaBuilder builder);

}
