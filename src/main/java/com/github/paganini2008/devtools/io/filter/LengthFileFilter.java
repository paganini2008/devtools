package com.github.paganini2008.devtools.io.filter;

import java.io.File;

import com.github.paganini2008.devtools.io.SizeUnit;

/**
 * LengthFileFilter
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class LengthFileFilter extends LogicalFileFilter {

	private final long length;
	private final SizeUnit sizeUnit;
	private final Operator operator;

	public LengthFileFilter(long length, SizeUnit sizeUnit, Operator operator) {
		this.length = length;
		this.sizeUnit = sizeUnit;
		this.operator = operator;
	}

	public boolean accept(File file) {
		long dest = SizeUnit.BYTES.convert(this.length, sizeUnit).longValue();
		long src = file.length();
		switch (operator) {
		case LT:
			return src < dest;
		case GT:
			return src > dest;
		case LTE:
			return src <= dest;
		case GTE:
			return src >= dest;
		case EQ:
			return src == dest;
		case NE:
			return src != dest;
		}
		throw new UnsupportedOperationException();
	}

	public static LengthFileFilter eq(long length, SizeUnit sizeUnit) {
		return new LengthFileFilter(length, sizeUnit, Operator.EQ);
	}

	public static LengthFileFilter ne(long length, SizeUnit sizeUnit) {
		return new LengthFileFilter(length, sizeUnit, Operator.NE);
	}

	public static LengthFileFilter gte(long length, SizeUnit sizeUnit) {
		return new LengthFileFilter(length, sizeUnit, Operator.GTE);
	}

	public static LengthFileFilter gt(long length, SizeUnit sizeUnit) {
		return new LengthFileFilter(length, sizeUnit, Operator.GT);
	}

	public static LengthFileFilter lte(long length, SizeUnit sizeUnit) {
		return new LengthFileFilter(length, sizeUnit, Operator.LTE);
	}

	public static LengthFileFilter lt(long length, SizeUnit sizeUnit) {
		return new LengthFileFilter(length, sizeUnit, Operator.LT);
	}
}
