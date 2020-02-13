package com.github.paganini2008.devtools;

public class CharValueOverflowException extends IllegalArgumentException {

	private static final long serialVersionUID = -5977208243857441008L;

	public CharValueOverflowException(String msg) {
		super(msg);
	}

	public static CharValueOverflowException causedByChar(int i) {
		return new CharValueOverflowException("Out of range [" + Character.MIN_VALUE + "," + Character.MAX_VALUE
				+ "] for input: " + i);
	}

	public static CharValueOverflowException causedByChar(char c) {
		return new CharValueOverflowException("Out of range [" + Character.MIN_VALUE + "," + Character.MAX_VALUE
				+ "] for input: " + c);
	}
}
