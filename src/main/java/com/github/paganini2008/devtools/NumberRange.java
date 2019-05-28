package com.github.paganini2008.devtools;

import java.math.BigInteger;

/**
 * NumberRange
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class NumberRange {

	private static final BigInteger BYTE_MIN = BigInteger.valueOf(Byte.MIN_VALUE);
	private static final BigInteger BYTE_MAX = BigInteger.valueOf(Byte.MAX_VALUE);
	private static final BigInteger SHORT_MIN = BigInteger.valueOf(Short.MIN_VALUE);
	private static final BigInteger SHORT_MAX = BigInteger.valueOf(Short.MAX_VALUE);
	private static final BigInteger INTEGER_MIN = BigInteger.valueOf(Integer.MIN_VALUE);
	private static final BigInteger INTEGER_MAX = BigInteger.valueOf(Integer.MAX_VALUE);
	private static final BigInteger LONG_MIN = BigInteger.valueOf(Long.MIN_VALUE);
	private static final BigInteger LONG_MAX = BigInteger.valueOf(Long.MAX_VALUE);

	public static int checkByte(BigInteger value) {
		if (value.compareTo(BYTE_MIN) < 0) {
			return -1;
		}
		if (value.compareTo(BYTE_MAX) > 0) {
			return 1;
		}
		return 0;
	}

	public static int checkShort(BigInteger value) {
		if (value.compareTo(SHORT_MIN) < 0) {
			return -1;
		}
		if (value.compareTo(SHORT_MAX) > 0) {
			return 1;
		}
		return 0;
	}

	public static int checkInteger(BigInteger value) {
		if (value.compareTo(INTEGER_MIN) < 0) {
			return -1;
		}
		if (value.compareTo(INTEGER_MAX) > 0) {
			return 1;
		}
		return 0;
	}

	public static int checkLong(BigInteger value) {
		if (value.compareTo(LONG_MIN) < 0) {
			return -1;
		}
		if (value.compareTo(LONG_MAX) > 0) {
			return 1;
		}
		return 0;
	}

	private NumberRange() {
	}

}
