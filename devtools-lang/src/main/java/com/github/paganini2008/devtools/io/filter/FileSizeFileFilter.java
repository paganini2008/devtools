package com.github.paganini2008.devtools.io.filter;

import java.io.File;

/**
 * FileSizeFileFilter
 * 
 * @author Jimmy Hoff
 * @version 1.0
 */
public class FileSizeFileFilter extends LogicalFileFilter {

	private final int size;
	private final Operator operator;

	public FileSizeFileFilter(int size, Operator operator) {
		this.size = size;
		this.operator = operator;
	}

	public boolean accept(File file) {
		String[] names = file.list();
		final int l = names != null ? names.length : 0;
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
	
	public static FileSizeFileFilter eq(int size) {
		return new FileSizeFileFilter(size, Operator.EQ);
	}

	public static FileSizeFileFilter ne(int size) {
		return new FileSizeFileFilter(size, Operator.NE);
	}

	public static FileSizeFileFilter gte(int size) {
		return new FileSizeFileFilter(size, Operator.GTE);
	}

	public static FileSizeFileFilter gt(int size) {
		return new FileSizeFileFilter(size, Operator.GT);
	}

	public static FileSizeFileFilter lte(int size) {
		return new FileSizeFileFilter(size, Operator.LTE);
	}

	public static FileSizeFileFilter lt(int size) {
		return new FileSizeFileFilter(size, Operator.LT);
	}

}
