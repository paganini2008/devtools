package com.github.paganini2008.springworld.fastjpa;

import java.util.List;

import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;
import javax.persistence.metamodel.EntityType;

/**
 * 
 * Model
 * 
 * @author Fred Feng
 * @revised 2019-07
 * @created 2019-02
 */
public interface Model<X> {

	static final String ROOT = "this";

	default String getDefaultAlias() {
		return ROOT;
	}

	EntityType<X> getEntityType();

	Class<?> getRootType();

	Class<X> getType();

	boolean isManaged(Class<?> type);

	Root<?> getRoot();

	<T> Path<T> getAttribute(String attributeName);

	<T> Path<T> getAttribute(String name, String attributeName);

	default boolean hasAttribute(String name, String attributeName) {
		try {
			getAttribute(name, attributeName);
			return true;
		} catch (RuntimeException ignored) {
			return false;
		}
	}

	List<Selection<?>> getSelections(String name);

	List<JpaAttributeDetail> getAttributeDetails(String name);

	<Y> Model<Y> join(String attributeName, String alias, Predicate on);

	<Y> Model<Y> leftJoin(String attributeName, String alias, Predicate on);

	<Y> Model<Y> rightJoin(String attributeName, String alias, Predicate on);

	<S> Model<S> sibling(Model<S> sibling);

	static <X> Model<X> forRoot(Root<X> root) {
		return forRoot(root, ROOT);
	}

	static <X> Model<X> forRoot(Root<X> root, String alias) {
		return new RootModel<X>(root, alias, null);
	}

}
