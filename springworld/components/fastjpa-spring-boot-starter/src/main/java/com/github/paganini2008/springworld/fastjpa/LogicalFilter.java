package com.github.paganini2008.springworld.fastjpa;

/**
 * 
 * LogicalFilter
 * 
 * @author Fred Feng
 * @revised 2019-07
 * @created 2019-02
 */
public abstract class LogicalFilter implements Filter {

	public LogicalFilter and(Filter filter) {
		return new AndFilter(this, filter);
	}

	public LogicalFilter or(Filter filter) {
		return new OrFilter(this, filter);
	}

	public LogicalFilter not() {
		return new NotFilter(this);
	}

}
