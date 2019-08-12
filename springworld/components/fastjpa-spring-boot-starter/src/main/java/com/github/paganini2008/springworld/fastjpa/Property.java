package com.github.paganini2008.springworld.fastjpa;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;

import org.apache.commons.lang3.StringUtils;

/**
 * 
 * Property
 * 
 * @author Fred Feng
 * @revised 2019-07
 * @created 2019-02
 */
public final class Property<T> implements Field<T> {

	private final String alias;
	private final String attributeName;
	private final Class<T> requiredType;

	Property(String alias, String attributeName, Class<T> requiredType) {
		this.alias = alias;
		this.attributeName = attributeName;
		this.requiredType = requiredType;
	}

	public Expression<T> toExpression(Model<?> model, CriteriaBuilder builder) {
		Expression<T> expression = StringUtils.isNotBlank(alias) ? model.getAttribute(alias, attributeName)
				: model.getAttribute(model.getDefaultAlias(), attributeName);
		if (requiredType != null) {
			return expression.as(requiredType);
		}
		return expression;
	}

	public String toString() {
		return String.format("%s.%s", alias, attributeName);
	}

	public static <T> Property<T> forName(String attributeName) {
		return forName(null, attributeName);
	}

	public static <T> Property<T> forName(String alias, String attributeName) {
		return forName(alias, attributeName, null);
	}

	public static <T> Property<T> forName(String attributeName, Class<T> requiredType) {
		return forName(null, attributeName, requiredType);
	}

	public static <T> Property<T> forName(String alias, String attributeName, Class<T> requiredType) {
		return new Property<T>(alias, attributeName, requiredType);
	}

}
