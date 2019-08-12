package com.github.paganini2008.springworld.fastjpa;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Order;

/**
 * 
 * JpaSort
 *
 * @author Fred Feng
 * @revised 2019-07
 * @created 2019-02
 * @version 1.0
 */
public interface JpaSort {

	Order toOrder(Model<?> model, CriteriaBuilder builder);

	static JpaSort asc(String attributeName) {
		return asc(Property.forName(attributeName));
	}

	static JpaSort asc(String name, String attributeName) {
		return asc(Property.forName(name, attributeName));
	}

	static JpaSort asc(Field<?> field) {
		return new JpaSort() {
			public Order toOrder(Model<?> model, CriteriaBuilder builder) {
				return builder.asc(field.toExpression(model, builder));
			}
		};
	}

	static JpaSort desc(String attributeName) {
		return desc(Property.forName(attributeName));
	}

	static JpaSort desc(String name, String attributeName) {
		return desc(Property.forName(name, attributeName));
	}

	static JpaSort desc(Field<?> field) {
		return new JpaSort() {
			public Order toOrder(Model<?> model, CriteriaBuilder builder) {
				return builder.desc(field.toExpression(model, builder));
			}
		};
	}

}
