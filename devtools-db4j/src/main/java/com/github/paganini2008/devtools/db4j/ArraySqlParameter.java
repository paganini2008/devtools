package com.github.paganini2008.devtools.db4j;

/**
 * ArraySqlParameter
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class ArraySqlParameter extends AbstractSqlParameter implements SqlParameter {

	private final Object[] arguments;

	public ArraySqlParameter(Object[] arguments) {
		this.arguments = arguments;
	}

	public boolean hasValue(String name) {
		try {
			return Integer.parseInt(name) < arguments.length;
		} catch (RuntimeException e) {
			return false;
		}
	}

	public Object getValue(String name) {
		return arguments[Integer.parseInt(name)];
	}

}
