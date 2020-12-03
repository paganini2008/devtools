package com.github.paganini2008.devtools.io.filter;

import java.io.File;

/**
 * LastModifiedFileFilter
 * 
 * @author Jimmy Hoff
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
		final long l = file.lastModified();
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
	
	public static LastModifiedFileFilter eq(long lastModified) {
		return new LastModifiedFileFilter(lastModified, Operator.EQ);
	}

	public static LastModifiedFileFilter ne(long lastModified) {
		return new LastModifiedFileFilter(lastModified, Operator.NE);
	}

	public static LastModifiedFileFilter gte(long lastModified) {
		return new LastModifiedFileFilter(lastModified, Operator.GTE);
	}

	public static LastModifiedFileFilter gt(long lastModified) {
		return new LastModifiedFileFilter(lastModified, Operator.GT);
	}

	public static LastModifiedFileFilter lte(long lastModified) {
		return new LastModifiedFileFilter(lastModified, Operator.LTE);
	}

	public static LastModifiedFileFilter lt(long lastModified) {
		return new LastModifiedFileFilter(lastModified, Operator.LT);
	}

}
