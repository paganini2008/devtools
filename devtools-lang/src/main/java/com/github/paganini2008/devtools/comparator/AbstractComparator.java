package com.github.paganini2008.devtools.comparator;

import java.util.Comparator;
import java.util.List;

import com.github.paganini2008.devtools.ArrayUtils;
import com.github.paganini2008.devtools.collection.ListUtils;

/**
 * AbstractComparator
 * 
 * @author Jimmy Hoff
 * @version 1.0
 */
public abstract class AbstractComparator<T> implements Comparator<T> {

	public T[] sort(T[] args) {
		if (ArrayUtils.isNotEmpty(args)) {
			ArrayUtils.sort(args, this);
		}
		return args;
	}

	public List<T> sort(List<T> args) {
		if (ListUtils.isNotEmpty(args)) {
			ListUtils.sort(args, this);
		}
		return args;
	}

	public Comparator<T> reverse() {
		return new ReverseComparator<T>(this);
	}

	public String toString() {
		return "[Comparator] " + getClass().getName();
	}

}
