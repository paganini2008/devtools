package com.github.paganini2008.springworld.fastjpa.support;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;
import org.springframework.data.repository.NoRepositoryBean;

import com.github.paganini2008.springworld.fastjpa.Filter;
import com.github.paganini2008.springworld.fastjpa.JpaDelete;
import com.github.paganini2008.springworld.fastjpa.JpaQuery;
import com.github.paganini2008.springworld.fastjpa.JpaUpdate;

/**
 * 
 * EntityDao
 * 
 * @author Fred Feng
 * @created 2019-02
 */
@NoRepositoryBean
public interface EntityDao<E, ID> extends JpaRepositoryImplementation<E, ID>, NativeSqlOperations<E> {

	Class<E> getEntityClass();

	boolean exists(Filter filter);

	long count(Filter filter);

	List<E> findAll(Filter filter);

	List<E> findAll(Filter filter, Sort sort);

	Page<E> findAll(Filter filter, Pageable pageable);

	Optional<E> findOne(Filter filter);

	<T extends Comparable<T>> T max(String property, Filter filter, Class<T> requiredType);

	<T extends Comparable<T>> T min(String property, Filter filter, Class<T> requiredType);

	<T extends Number> T avg(String property, Filter filter, Class<T> requiredType);

	<T extends Number> T sum(String property, Filter filter, Class<T> requiredType);

	JpaUpdate<E> update();

	JpaDelete<E> delete();

	JpaQuery<E> select();

}
