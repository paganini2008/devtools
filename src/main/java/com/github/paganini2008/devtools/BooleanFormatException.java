package com.github.paganini2008.devtools;

/**
 * BooleanFormatException
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class BooleanFormatException extends IllegalArgumentException {

	private static final long serialVersionUID = 503818756488609077L;

	public BooleanFormatException(String input) {
		super("Invalid format for input: '" + input + "'.");
	}

	public BooleanFormatException(char input) {
		this(String.valueOf(input));
	}

}
