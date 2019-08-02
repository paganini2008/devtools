package com.github.paganini2008.devtools.beans.streaming;

import com.github.paganini2008.devtools.beans.Tuple;

/**
 * 
 * View
 * 
 * @author Fred Feng
 * @revised 2019-07
 * @created 2019-06
 * @version 1.0
 */
public abstract class View<E> implements Transformer<Group<E>, Tuple> {

	public Tuple transfer(Group<E> group) {
		Tuple tuple = new Tuple();
		tuple.putAll(group.getAttributes());
		setAttributes(tuple, group);
		return tuple;
	}

	protected void setAttributes(Tuple tuple, Group<E> group) {
	}
}
