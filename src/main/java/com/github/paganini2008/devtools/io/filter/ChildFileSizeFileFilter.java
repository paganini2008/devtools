package com.github.paganini2008.devtools.io.filter;

import java.io.File;

/**
 * ChildFileSizeFileFilter
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class ChildFileSizeFileFilter extends LogicalFileFilter {

	private final int size;
	private final Operator operator;

	public ChildFileSizeFileFilter(int size, Operator operator) {
		this.size = size;
		this.operator = operator;
	}

	public boolean accept(File file) {
		String[] names = file.list();
		int l = names != null ? names.length : 0;
		switch (operator) {
		case LT:
			return l < size;
		case GT:
			return l > size;
		case LTE:
			return l <= size;
		case GTE:
			return l >= size;
		case EQ:
			return l == size;
		case NE:
			return l != size;
		}
		throw new UnsupportedOperationException();
	}

}
