package com.github.paganini2008.devtools.io.filter;

import java.io.File;

/**
 * LastModifiedFileFilter
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class LastModifiedFileFilter extends LogicalFileFilter {

	private final long lastModified;
	private final Operator operator;

	public LastModifiedFileFilter(long lastModified, Operator operator) {
		this.lastModified = lastModified;
		this.operator = operator;
	}

	public boolean accept(File file) {
		long l = file.lastModified();
		switch (operator) {
		case LT:
			return l < lastModified;
		case GT:
			return l > lastModified;
		case LTE:
			return l <= lastModified;
		case GTE:
			return l >= lastModified;
		case EQ:
			return l == lastModified;
		case NE:
			return l!= lastModified;
		}
		throw new UnsupportedOperationException();
	}

}
