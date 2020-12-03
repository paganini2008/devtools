package com.github.paganini2008.devtools.comparator;

import java.util.Comparator;

import com.github.paganini2008.devtools.Assert;

/**
 * ReverseComparator
 * 
 * @author Jimmy Hoff
 * @version 1.0
 */
public class ReverseComparator<T> extends AbstractComparator<T> {

	public ReverseComparator(Comparator<T> delegate) {
		Assert.isNull(delegate, "Delegate comparator is missing.");
		this.delegate = delegate;
	}

	private final Comparator<T> delegate;

	public int compare(T left, T right) {
		return delegate.compare(right, left);
	}

	public String toString() {
		return super.toString() + "[" + delegate.toString() + "]";
	}

}
