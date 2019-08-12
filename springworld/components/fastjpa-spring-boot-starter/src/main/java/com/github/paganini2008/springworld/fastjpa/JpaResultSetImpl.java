package com.github.paganini2008.springworld.fastjpa;

import java.util.List;

import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Selection;

import com.github.paganini2008.devtools.converter.ConvertUtils;
import com.github.paganini2008.devtools.jdbc.ResultSetSlice;

/**
 * 
 * JpaResultSetImpl
 * 
 * @author Fred Feng
 * @created 2019-02
 */
public class JpaResultSetImpl<E> implements JpaResultSet<E> {

	private final Model<E> model;
	private final CriteriaQuery<Tuple> query;
	private final JpaCustomQuery<?> customQuery;

	JpaResultSetImpl(Model<E> model, CriteriaQuery<Tuple> query, JpaCustomQuery<?> customQuery) {
		this.model = model;
		this.query = query;
		this.customQuery = customQuery;
	}

	public <T> T getResult(Class<T> requiredType) {
		Tuple tuple = customQuery.getSingleResult(builder -> {
			return query;
		});
		Object result = tuple.get(0);
		if (result != null) {
			try {
				return requiredType.cast(result);
			} catch (RuntimeException e) {
				result = ConvertUtils.convertValue(result, requiredType);
				return requiredType.cast(result);
			}
		}
		return null;
	}

	public int totalCount() {
		final List<Selection<?>> selectionList = query.getSelection().getCompoundSelectionItems();
		Tuple tuple = customQuery.getSingleResult(builder -> {
			query.multiselect(Fields.count(1).toExpression(model, builder));
			return query;
		});
		query.multiselect(selectionList);
		Object result = tuple.get(0);
		return result instanceof Number ? ((Number) result).intValue() : 0;
	}

	public List<E> list(int maxResults, int firstResult) {
		return setTransformer(Transformers.asBean(model.getType())).list(maxResults, firstResult);
	}

	public <T> ResultSetSlice<T> setTransformer(Transformer<E, T> transformer) {
		return new JpaResultSetSlice<E, T>(model, query, customQuery, transformer);
	}
}
