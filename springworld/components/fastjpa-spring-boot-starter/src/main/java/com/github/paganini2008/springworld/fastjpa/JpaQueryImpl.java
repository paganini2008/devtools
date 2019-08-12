package com.github.paganini2008.springworld.fastjpa;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;
import javax.persistence.criteria.Subquery;

/**
 * 
 * JpaQueryImpl
 * 
 * @author Fred Feng
 * @revised 2019-07
 * @created 2019-02
 */
public class JpaQueryImpl<E> implements JpaQuery<E> {

	JpaQueryImpl(Model<E> model, CriteriaQuery<Tuple> query, CriteriaBuilder builder, JpaCustomQuery<?> customQuery) {
		this.model = model;
		this.query = query;
		this.builder = builder;
		this.customQuery = customQuery;
		this.rowSet = new JpaResultSetImpl<E>(model, query, customQuery);
	}

	private final Model<E> model;
	private final CriteriaQuery<Tuple> query;
	private final CriteriaBuilder builder;
	private final JpaCustomQuery<?> customQuery;
	private final JpaResultSet<E> rowSet;

	public JpaQuery<E> filter(Filter filter) {
		if (filter != null) {
			query.where(filter.toPredicate(model, builder));
		}
		return this;
	}

	public JpaGroupBy<E> groupBy(FieldList fieldList) {
		List<Expression<?>> paths = new ArrayList<Expression<?>>();
		for (Field<?> field : fieldList) {
			paths.add(field.toExpression(model, builder));
		}
		query.groupBy(paths);
		return new JpaGroupByImpl<E>(model, query, builder, rowSet);
	}

	public <X> JpaSubQuery<X, X> subQuery(Class<X> entityClass, String alias) {
		Subquery<X> subquery = query.subquery(entityClass);
		Root<X> root = subquery.from(entityClass);
		Model<X> model = this.model.sibling(Model.forRoot(root, alias));
		return new JpaSubQueryImpl<X, X>(model, subquery.select(root), builder);
	}

	public <X, T> JpaSubQuery<X, T> subQuery(Class<X> entityClass, String alias, Class<T> resultClass) {
		Subquery<T> subquery = query.subquery(resultClass);
		Root<X> root = subquery.from(entityClass);
		Model<X> model = this.model.sibling(Model.forRoot(root, alias));
		return new JpaSubQueryImpl<X, T>(model, subquery, builder);
	}

	public JpaResultSet<E> selectAlias(String... tableAlias) {
		if (tableAlias != null) {
			List<Selection<?>> selections = new ArrayList<Selection<?>>();
			for (String alias : tableAlias) {
				selections.addAll(model.getSelections(alias));
			}
			query.multiselect(selections);
		}
		return rowSet;
	}

	public JpaResultSet<E> select(ColumnList columnList) {
		if (columnList != null) {
			List<Selection<?>> selections = new ArrayList<Selection<?>>();
			for (Column column : columnList) {
				selections.add(column.toSelection(model, builder));
			}
			query.multiselect(selections);
		}
		return rowSet;
	}

	public JpaQuery<E> sort(JpaSort... sorts) {
		if (sorts != null) {
			List<Order> orders = new ArrayList<Order>();
			for (JpaSort sort : sorts) {
				orders.add(sort.toOrder(model, builder));
			}
			query.orderBy(orders);
		}
		return this;
	}

	public JpaQuery<E> distinct(boolean distinct) {
		query.distinct(distinct);
		return this;
	}

	public <X> JpaQuery<X> join(String attributeName, String alias, Filter on) {
		Model<X> join = model.join(attributeName, alias, on != null ? on.toPredicate(model, builder) : null);
		return new JpaQueryImpl<X>(join, query, builder, customQuery);
	}

	public <X> JpaQuery<X> leftJoin(String attributeName, String alias, Filter on) {
		Model<X> join = model.leftJoin(attributeName, alias, on != null ? on.toPredicate(model, builder) : null);
		return new JpaQueryImpl<X>(join, query, builder, customQuery);
	}

	public <X> JpaQuery<X> rightJoin(String attributeName, String alias, Filter on) {
		Model<X> join = model.rightJoin(attributeName, alias, on != null ? on.toPredicate(model, builder) : null);
		return new JpaQueryImpl<X>(join, query, builder, customQuery);
	}

}
