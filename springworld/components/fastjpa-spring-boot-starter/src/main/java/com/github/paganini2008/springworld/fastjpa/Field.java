package com.github.paganini2008.springworld.fastjpa;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Selection;

/**
 * 
 * Field
 * 
 * @author Fred Feng
 * @revised 2019-07
 * @created 2019-02
 */
public interface Field<T> {

	Expression<T> toExpression(Model<?> model, CriteriaBuilder builder);

	default Column as(final String alias) {
		return new Column() {

			public Selection<?> toSelection(Model<?> model, CriteriaBuilder builder) {
				return Field.this.toExpression(model, builder).alias(alias);
			}

			public String toString() {
				return Field.this.toString();
			}
		};
	}
}
