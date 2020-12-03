package com.github.paganini2008.devtools;

import java.math.BigInteger;

/**
 * NumberRangeAssert
 * 
 * @author Jimmy Hoff
 * @version 1.0
 */
public abstract class NumberRangeAssert {

	private static final BigInteger MIN_BYTE = BigInteger.valueOf(Byte.MIN_VALUE);
	private static final BigInteger MAX_BYTE = BigInteger.valueOf(Byte.MAX_VALUE);
	private static final BigInteger MIN_SHORT = BigInteger.valueOf(Short.MIN_VALUE);
	private static final BigInteger MAX_SHORT = BigInteger.valueOf(Short.MAX_VALUE);
	private static final BigInteger MIN_INTEGER = BigInteger.valueOf(Integer.MIN_VALUE);
	private static final BigInteger MAX_INTEGER = BigInteger.valueOf(Integer.MAX_VALUE);
	private static final BigInteger MIN_LONG = BigInteger.valueOf(Long.MIN_VALUE);
	private static final BigInteger MAX_LONG = BigInteger.valueOf(Long.MAX_VALUE);

	public static boolean checkByte(BigInteger value) {
		return !Comparables.between(value, MIN_BYTE, MAX_BYTE);
	}

	public static boolean checkShort(BigInteger value) {
		return !Comparables.between(value, MIN_SHORT, MAX_SHORT);
	}

	public static boolean checkInteger(BigInteger value) {
		return !Comparables.between(value, MIN_INTEGER, MAX_INTEGER);
	}

	public static boolean checkLong(BigInteger value) {
		return !Comparables.between(value, MIN_LONG, MAX_LONG);
	}

}
