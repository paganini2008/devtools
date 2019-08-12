package com.github.paganini2008.springworld.support;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import com.github.paganini2008.devtools.collection.CollectionUtils;
import com.github.paganini2008.devtools.jdbc.PageResponse;
import com.github.paganini2008.springworld.fastjpa.Filter;
import com.github.paganini2008.springworld.fastjpa.JpaDelete;
import com.github.paganini2008.springworld.fastjpa.JpaQuery;
import com.github.paganini2008.springworld.fastjpa.JpaSort;
import com.github.paganini2008.springworld.fastjpa.JpaUpdate;
import com.github.paganini2008.springworld.fastjpa.support.EntityDao;

/**
 * 
 * BaseService
 * 
 * @author Fred Feng
 * @created 2019-07
 * @revised 2018-10
 * @version 1.0
 */
public abstract class BaseService<E extends BaseEntity, ID extends Serializable, DAO extends EntityDao<E, ID>> {

	protected BaseService(DAO dao) {
		this.dao = dao;
	}
	
	protected final DAO dao;

	public E save(E entity) {
		return dao.save(entity);
	}

	public E saveAndFlush(E entity) {
		return dao.saveAndFlush(entity);
	}

	public void saveAll(Collection<E> entities) {
		dao.saveAll(entities);
	}

	public void delete(E entity) {
		if (entity != null) {
			dao.delete(entity);
		}
	}

	public void flush() {
		dao.flush();
	}

	public void delete(Filter filter) {
		Optional<E> optional = dao.findOne(filter);
		if (optional.isPresent()) {
			dao.delete(optional.get());
		}
	}

	public void deleteAll(Filter filter) {
		List<E> entities = find(filter);
		if (CollectionUtils.isNotEmpty(entities)) {
			deleteAll(entities);
		}
	}

	public void deleteAll(Collection<E> entities) {
		dao.deleteAll(entities);
	}

	public E get(ID id) {
		return id != null ? dao.getOne(id) : null;
	}

	public E findOne(ID id) {
		return id != null ? dao.findById(id).orElse(null) : null;
	}

	public E delete(ID id) {
		E entity = get(id);
		delete(entity);
		return entity;
	}

	public E findOne(E example) {
		Optional<E> optional = dao.findOne(Example.of(example));
		return optional.orElse(null);
	}

	public E findOne(Filter filter) {
		Optional<E> optional = dao.findOne(filter);
		return optional.orElse(null);
	}

	public E findFirst(Filter filter) {
		return dao.select().filter(filter).selectThis().first();
	}

	public E findFirst(Filter filter, JpaSort sort) {
		return dao.select().filter(filter).sort(sort).selectThis().first();
	}

	public List<E> find(E example) {
		return dao.findAll(Example.of(example));
	}

	public Page<E> find(E example, Pageable pageable) {
		return dao.findAll(Example.of(example), pageable);
	}

	public Page<E> find(E example, int page, int size) {
		PageRequest pageable = PageRequest.of(page, size);
		return dao.findAll(Example.of(example), pageable);
	}

	public Page<E> find(E example, int page, int size, Sort sort) {
		PageRequest pageable = PageRequest.of(page, size, sort);
		return dao.findAll(Example.of(example), pageable);
	}

	public E findOne(Specification<E> specification) {
		Optional<E> optional = dao.findOne(specification);
		return optional.isPresent() ? optional.get() : null;
	}

	public List<E> find(Filter filter) {
		return dao.findAll(filter);
	}

	public PageResponse<E> find(Filter filter, int page, int size) {
		return dao.select().filter(filter).selectThis().list(com.github.paganini2008.devtools.jdbc.PageRequest.of(page, size));
	}

	public List<E> find(Specification<E> specification) {
		return dao.findAll(specification);
	}

	public Page<E> find(Specification<E> specification, Pageable pageable) {
		return dao.findAll(specification, pageable);
	}

	public Page<E> find(Specification<E> specification, int page, int size) {
		PageRequest pageable = PageRequest.of(page, size);
		return dao.findAll(specification, pageable);
	}

	public Page<E> find(Specification<E> specification, int page, int size, Sort sort) {
		PageRequest pageable = PageRequest.of(page, size, sort);
		return dao.findAll(specification, pageable);
	}

	public int rowCount(E example) {
		return (int) dao.count(Example.of(example));
	}

	public int rowCount(Specification<E> specification) {
		return (int) dao.count(specification);
	}

	public boolean exists(Filter filter) {
		return dao.exists(filter);
	}

	public JpaQuery<E> criteria() {
		return dao.select();
	}

	public JpaDelete<E> delete() {
		return dao.delete();
	}

	public JpaUpdate<E> update() {
		return dao.update();
	}

}
