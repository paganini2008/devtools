package com.github.paganini2008.springworld.fastjpa;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Subquery;

/**
 * 
 * JpaSubQueryImpl
 * 
 * @author Fred Feng
 * @revised 2019-07
 * @created 2019-02
 */
public class JpaSubQueryImpl<E, T> implements JpaSubQuery<E, T> {

	private final Model<E> model;
	private final Subquery<T> query;
	private final CriteriaBuilder builder;

	JpaSubQueryImpl(Model<E> model, Subquery<T> query, CriteriaBuilder builder) {
		this.model = model;
		this.query = query;
		this.builder = builder;
	}

	public JpaSubQuery<E, T> filter(Filter filter) {
		query.where(filter.toPredicate(model, builder));
		return this;
	}

	public JpaSubGroupBy<E, T> groupBy(FieldList fieldList) {
		List<Expression<?>> paths = new ArrayList<Expression<?>>();
		for (Field<?> field : fieldList) {
			paths.add(field.toExpression(model, builder));
		}
		query.groupBy(paths);
		return new JpaSubGroupBy<E, T>() {

			public JpaSubGroupBy<E, T> having(Filter filter) {
				query.having(filter.toPredicate(model, builder));
				return this;
			}

			public JpaSubGroupBy<E, T> select(String alias, String attributeName) {
				return select(Property.forName(alias, attributeName));
			}

			public JpaSubGroupBy<E, T> select(Field<T> field) {
				Expression<T> expression = field.toExpression(model, builder);
				query.select(expression);
				return this;
			}

		};
	}

	public JpaSubQuery<E, T> select(String alias, String attributeName) {
		return select(Property.forName(alias, attributeName));
	}

	public JpaSubQuery<E, T> select(Field<T> field) {
		Expression<T> expression = field.toExpression(model, builder);
		query.select(expression);
		return this;
	}

	public <X> JpaSubQuery<X, T> join(String attributeName, String alias, Filter on) {
		Model<X> join = model.join(attributeName, alias, on != null ? on.toPredicate(model, builder) : null);
		return new JpaSubQueryImpl<X, T>(join, query, builder);
	}

	public <X> JpaSubQuery<X, T> leftJoin(String attributeName, String alias, Filter on) {
		Model<X> join = model.leftJoin(attributeName, alias, on != null ? on.toPredicate(model, builder) : null);
		return new JpaSubQueryImpl<X, T>(join, query, builder);
	}

	public <X> JpaSubQuery<X, T> rightJoin(String attributeName, String alias, Filter on) {
		Model<X> join = model.rightJoin(attributeName, alias, on != null ? on.toPredicate(model, builder) : null);
		return new JpaSubQueryImpl<X, T>(join, query, builder);
	}

	public Subquery<T> toSubquery(CriteriaBuilder builder) {
		return query;
	}

}
