package com.github.paganini2008.springworld.fastjpa;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Selection;

import com.github.paganini2008.devtools.jdbc.PageRequest;
import com.github.paganini2008.devtools.jdbc.PageResponse;
import com.github.paganini2008.devtools.jdbc.ResultSetSlice;

/**
 * 
 * JpaGroupByImpl
 * 
 * @author Fred Feng
 * @revised 2019-07
 * @created 2019-02
 */
public class JpaGroupByImpl<E> implements JpaGroupBy<E>, JpaResultSet<E> {

	private final Model<E> model;
	private final CriteriaQuery<Tuple> query;
	private final CriteriaBuilder builder;
	private final JpaResultSet<E> rowSet;

	JpaGroupByImpl(Model<E> model, CriteriaQuery<Tuple> query, CriteriaBuilder builder, JpaResultSet<E> rowSet) {
		this.model = model;
		this.query = query;
		this.builder = builder;
		this.rowSet = rowSet;
	}

	public JpaGroupBy<E> having(Filter filter) {
		query.having(filter.toPredicate(model, builder));
		return this;
	}

	public JpaResultSet<E> select(ColumnList columnList) {
		if (columnList != null) {
			List<Selection<?>> selections = new ArrayList<Selection<?>>();
			for (Column column : columnList) {
				selections.add(column.toSelection(model, builder));
			}
			query.multiselect(selections);
		}
		return this;
	}

	public JpaGroupBy<E> sort(JpaSort... sorts) {
		List<Order> orders = new ArrayList<Order>();
		for (JpaSort sort : sorts) {
			orders.add(sort.toOrder(model, builder));
		}
		if (!orders.isEmpty()) {
			query.orderBy(orders);
		}
		return this;
	}

	public <T> T getResult(Class<T> requiredType) {
		return rowSet.getResult(requiredType);
	}

	public int totalCount() {
		return rowSet.totalCount();
	}

	public List<E> list(int maxResults, int firstResult) {
		return rowSet.list(maxResults, firstResult);
	}

	public PageResponse<E> list(PageRequest pageRequest) {
		return rowSet.list(pageRequest);
	}

	public <T> ResultSetSlice<T> setTransformer(Transformer<E, T> transformer) {
		return rowSet.setTransformer(transformer);
	}

}
