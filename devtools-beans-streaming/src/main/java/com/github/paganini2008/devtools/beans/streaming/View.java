package com.github.paganini2008.devtools.beans.streaming;

import com.github.paganini2008.devtools.collection.Tuple;

/**
 * 
 * View
 * 
 * @author Jimmy Hoff
 * @version 1.0
 */
public abstract class View<E> implements Transformer<Group<E>, Tuple> {

	public Tuple transfer(Group<E> group) {
		Tuple tuple = Tuple.newTuple();
		tuple.append(group.getAttributes());
		setAttributes(tuple, group);
		return tuple;
	}

	protected void setAttributes(Tuple tuple, Group<E> group) {
	}
}
