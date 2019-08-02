package com.github.paganini2008.devtools.jdbc;

/**
 * ArrayParameterSource
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class ArrayParameterSource extends ParameterSourceSupport implements ParameterSource {

	private final Object[] arguments;

	public ArrayParameterSource(Object[] arguments) {
		this.arguments = arguments;
	}

	public boolean hasValue(String paramName) {
		try {
			return Integer.parseInt(paramName) < arguments.length;
		} catch (RuntimeException e) {
			return false;
		}
	}

	public Object getValue(String paramName) {
		return arguments[Integer.parseInt(paramName)];
	}

}
