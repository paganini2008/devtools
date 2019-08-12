package com.github.paganini2008.springworld.fastjpa;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaBuilder.Case;
import javax.persistence.criteria.Expression;

/**
 * 
 * CaseExpression
 * 
 * @author Fred Feng
 * @revised 2019-07
 * @created 2019-02
 * @version 1.0
 */
public class CaseExpression<R> implements Field<R> {

	private final List<Field<Boolean>> conditions = new ArrayList<Field<Boolean>>();
	private final List<R> results = new ArrayList<R>();
	private final List<Field<R>> resultFields = new ArrayList<Field<R>>();
	private R defaultResult;
	private Field<R> defaultFieldResult;

	CaseExpression() {
	}

	public CaseExpression<R> when(Field<Boolean> condition, R result) {
		conditions.add(condition);
		results.add(result);
		resultFields.add(null);
		return this;
	}

	public CaseExpression<R> when(Field<Boolean> condition, Field<R> result) {
		conditions.add(condition);
		results.add(null);
		resultFields.add(result);
		return this;
	}

	public void otherwise(R result) {
		this.defaultResult = result;
	}

	public void otherwise(Field<R> otherwise) {
		this.defaultFieldResult = otherwise;
	}

	public Expression<R> toExpression(Model<?> model, CriteriaBuilder builder) {
		Case<R> theCase = builder.selectCase();
		for (int i = 0, l = conditions.size(); i < l; i++) {
			R result = results.get(i);
			if (result != null) {
				theCase = theCase.when(conditions.get(i).toExpression(model, builder), result);
			} else if (resultFields.get(i) != null) {
				theCase = theCase.when(conditions.get(i).toExpression(model, builder),
						resultFields.get(i).toExpression(model, builder));
			}
		}
		if (defaultResult != null) {
			return theCase.otherwise(defaultResult);
		} else if (defaultFieldResult != null) {
			return theCase.otherwise(defaultFieldResult.toExpression(model, builder));
		}
		throw new UnsupportedOperationException(toString());
	}

}
