package com.github.paganini2008.devtools.beans.streaming;

import com.github.paganini2008.devtools.beans.TupleImpl;

/**
 * 
 * View
 * 
 * @author Fred Feng
 * @revised 2019-07
 * @created 2019-06
 * @version 1.0
 */
public abstract class View<E> implements Transformer<Group<E>, TupleImpl> {

	public TupleImpl transfer(Group<E> group) {
		TupleImpl tuple = new TupleImpl();
		tuple.putAll(group.getAttributes());
		setAttributes(tuple, group);
		return tuple;
	}

	protected void setAttributes(TupleImpl tuple, Group<E> group) {
	}
}
