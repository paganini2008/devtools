package com.allyes.components.jpahelper.support;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.allyes.developer.utils.Cases;
import com.allyes.developer.utils.collection.Tuple;

/**
 * 
 * RowMapperPageSlice
 * 
 * @author Fred Feng
 * @created 2019-04
 */
public abstract class RowMapperResultSetSlice<T> extends GeneralResultSetSlice<T> {

	protected RowMapperResultSetSlice(String sql, Object[] arguments, EntityManager em, RowMapper<T> mapper) {
		super(sql, arguments, em);
		this.mapper = mapper;
	}

	private final RowMapper<T> mapper;

	protected final List<T> getResultList(Query query) {
		List<T> results = new ArrayList<T>();
		List<Map<String, Object>> resultList = queryForMap(query);
		for (Map<String, Object> data : resultList) {
			T mapped = mapper.mapRow(Tuple.createBy(Cases.LOWER_UNDER_SCORE, data));
			results.add(mapped);
		}
		return results;
	}

	protected abstract List<Map<String, Object>> queryForMap(Query query);

}
