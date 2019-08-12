package com.github.paganini2008.springworld.fastjpa;

import java.util.List;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;
import javax.persistence.metamodel.EntityType;

/**
 * 
 * SiblingModel
 * 
 * @author Fred Feng
 * @revised 2019-07
 * @created 2019-04
 */
public class SiblingModel<X, Y> implements Model<Y> {

	private final Model<X> model;
	private final Model<Y> sibling;

	SiblingModel(Model<Y> sibling, Model<X> model) {
		this.sibling = sibling;
		this.model = model;
	}

	public EntityType<Y> getEntityType() {
		return sibling.getEntityType();
	}

	public Class<?> getRootType() {
		return sibling.getRootType();
	}

	public Class<Y> getType() {
		return sibling.getType();
	}

	public boolean isManaged(Class<?> type) {
		return getType().equals(type);
	}

	public <T> Path<T> getAttribute(String attributeName) {
		return sibling.getAttribute(attributeName);
	}

	public <T> Path<T> getAttribute(String name, String attributeName) {
		if (sibling.hasAttribute(name, attributeName)) {
			return sibling.getAttribute(name, attributeName);
		}
		return model.getAttribute(name, attributeName);
	}

	public Root<?> getRoot() {
		return model.getRoot();
	}

	public List<Selection<?>> getSelections(String name) {
		return sibling.getSelections(name);
	}

	public List<JpaAttributeDetail> getAttributeDetails(String name) {
		return sibling.getAttributeDetails(name);
	}

	public <Z> Model<Z> join(String attributeName, String alias, Predicate on) {
		Join<Y, Z> join = this.sibling.getRoot().join(attributeName, JoinType.INNER);
		if (on != null) {
			join.on(on);
		}
		return new JoinModel<Y, Z>(join, alias, null, this);
	}

	public <Z> Model<Z> leftJoin(String attributeName, String alias, Predicate on) {
		Join<Y, Z> join = this.sibling.getRoot().join(attributeName, JoinType.LEFT);
		if (on != null) {
			join.on(on);
		}
		return new JoinModel<Y, Z>(join, alias, null, this);
	}

	public <Z> Model<Z> rightJoin(String attributeName, String alias, Predicate on) {
		Join<Y, Z> join = this.sibling.getRoot().join(attributeName, JoinType.RIGHT);
		if (on != null) {
			join.on(on);
		}
		return new JoinModel<Y, Z>(join, alias, null, this);
	}

	public <S> Model<S> sibling(Model<S> sibling) {
		return new SiblingModel<Y, S>(sibling, this);
	}

}
