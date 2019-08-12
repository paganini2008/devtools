package com.github.paganini2008.springworld.fastjpa;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;
import javax.persistence.metamodel.SingularAttribute;

/**
 * 
 * RootModel
 * 
 * @author Fred Feng
 * @created 2019-02
 */
public class RootModel<X> implements Model<X> {

	private final Root<X> root;
	private final String alias;
	private final Metamodel metamodel;

	RootModel(Root<X> root, String alias, Metamodel metamodel) {
		this.root = root;
		this.alias = alias;
		this.metamodel = metamodel;
	}

	public <T> Path<T> getAttribute(String attributeName) {
		return getAttribute(ROOT, attributeName);
	}

	public <T> Path<T> getAttribute(String name, String attributeName) {
		if (this.alias.equals(name)) {
			return PathUtils.createPath(root, attributeName);
		}
		throw new PathMismatchedException(name, attributeName);
	}

	public Class<?> getRootType() {
		return getType();
	}

	public Class<X> getType() {
		return root.getModel().getBindableJavaType();
	}

	public EntityType<X> getEntityType() {
		return root != null ? root.getModel() : null;
	}

	public boolean isManaged(Class<?> type) {
		return getType().equals(type);
	}

	public <Y> Model<Y> join(String attributeName, String alias, Predicate on) {
		Join<X, Y> join = root.join(attributeName, JoinType.INNER);
		if (on != null) {
			join.on(on);
		}
		return new JoinModel<X, Y>(join, alias, metamodel, this);
	}

	public <Y> Model<Y> leftJoin(String attributeName, String alias, Predicate on) {
		Join<X, Y> join = root.join(attributeName, JoinType.LEFT);
		if (on != null) {
			join.on(on);
		}
		return new JoinModel<X, Y>(join, alias, metamodel, this);
	}

	public <Y> Model<Y> rightJoin(String attributeName, String alias, Predicate on) {
		Join<X, Y> join = root.join(attributeName, JoinType.RIGHT);
		if (on != null) {
			join.on(on);
		}
		return new JoinModel<X, Y>(join, alias, metamodel, this);
	}

	public Root<X> getRoot() {
		return root;
	}

	public List<Selection<?>> getSelections(String name) {
		if (!this.alias.equals(name)) {
			throw new AliasMismatchedException(name);
		}
		List<Selection<?>> selections = new ArrayList<Selection<?>>();
		selections.add(root.alias(alias));
		return selections;
	}

	public List<JpaAttributeDetail> getAttributeDetails(String name) {
		if (!this.alias.equals(name)) {
			throw new AliasMismatchedException(name);
		}
		List<JpaAttributeDetail> details = new ArrayList<JpaAttributeDetail>();
		for (SingularAttribute<? super X, ?> attribute : root.getModel().getSingularAttributes()) {
			details.add(new JpaAttributeDetailImpl<>(attribute));
		}
		return details;
	}

	public <S> Model<S> sibling(Model<S> sibling) {
		return new SiblingModel<X, S>(sibling, this);
	}

}
