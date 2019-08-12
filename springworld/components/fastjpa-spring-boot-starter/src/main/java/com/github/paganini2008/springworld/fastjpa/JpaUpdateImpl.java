package com.github.paganini2008.springworld.fastjpa;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

/**
 * 
 * JpaUpdateImpl
 * 
 * @author Fred Feng
 * @created 2019-02
 */
public class JpaUpdateImpl<E> implements JpaUpdate<E> {

	private final Model<E> model;
	private final CriteriaUpdate<E> update;
	private final CriteriaBuilder builder;
	private final JpaCustomUpdate<E> customUpdate;

	JpaUpdateImpl(Model<E> model, CriteriaUpdate<E> update, CriteriaBuilder builder, JpaCustomUpdate<E> customUpdate) {
		this.model = model;
		this.update = update;
		this.builder = builder;
		this.customUpdate = customUpdate;
	}

	public JpaUpdate<E> filter(Filter filter) {
		if (filter != null) {
			update.where(filter.toPredicate(model, builder));
		}
		return this;
	}

	public <T> JpaUpdate<E> set(String attributeName, T value) {
		Path<T> path = model.getAttribute(attributeName);
		update.set(path, value);
		return this;
	}

	public JpaUpdate<E> set(String attributeName, String anotherAttributeName) {
		return set(attributeName, Property.forName(anotherAttributeName));
	}

	public <T> JpaUpdate<E> set(String attributeName, Field<T> value) {
		Path<T> path = model.getAttribute(attributeName);
		update.set(path, value.toExpression(model, builder));
		return this;
	}

	public <X> JpaSubQuery<X, X> subQuery(Class<X> entityClass) {
		Subquery<X> subquery = update.subquery(entityClass);
		Root<X> root = subquery.from(entityClass);
		return new JpaSubQueryImpl<X, X>(Model.forRoot(root), subquery, builder);
	}

	public int execute() {
		return customUpdate.executeUpdate((CriteriaBuilder builder) -> {
			return update;
		});
	}
}
