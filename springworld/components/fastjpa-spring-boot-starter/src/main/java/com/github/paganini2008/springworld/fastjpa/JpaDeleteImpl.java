package com.github.paganini2008.springworld.fastjpa;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

/**
 * 
 * JpaDeleteImpl
 * 
 * @author Fred Feng
 * @created 2019-02
 */
public class JpaDeleteImpl<E> implements JpaDelete<E> {

	private final Model<E> model;
	private final CriteriaDelete<E> delete;
	private final CriteriaBuilder builder;
	private final JpaCustomUpdate<E> customUpdate;

	JpaDeleteImpl(Model<E> model, CriteriaDelete<E> delete, CriteriaBuilder builder, JpaCustomUpdate<E> customUpdate) {
		this.model = model;
		this.delete = delete;
		this.builder = builder;
		this.customUpdate = customUpdate;
	}

	public JpaDelete<E> filter(Filter filter) {
		if (filter != null) {
			delete.where(filter.toPredicate(model, builder));
		}
		return this;
	}

	public <X> JpaSubQuery<X, X> subQuery(Class<X> entityClass) {
		Subquery<X> subquery = delete.subquery(entityClass);
		Root<X> root = subquery.from(entityClass);
		return new JpaSubQueryImpl<X, X>(Model.forRoot(root), subquery, builder);
	}

	public int execute() {
		return customUpdate.executeUpdate((CriteriaBuilder builder) -> {
			return delete;
		});
	}

}
