package com.github.paganini2008.springworld.fastjpa;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaUpdate;

/**
 * 
 * JpaUpdateCallback
 * 
 * @author Fred Feng
 * @created 2019-02
 */
public interface JpaUpdateCallback<T> {

	CriteriaUpdate<T> doInJpa(CriteriaBuilder builder);

}
