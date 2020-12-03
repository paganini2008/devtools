package com.github.paganini2008.devtools.db4j;

import java.util.List;

/**
 * 
 * ArraySqlParameters
 *
 * @author Jimmy Hoff
 * @version 1.0
 */
public class ArraySqlParameters extends AbstractSqlParameter implements SqlParameters {

	private final List<Object[]> argsList;

	public ArraySqlParameters(List<Object[]> argsList) {
		this.argsList = argsList;
	}

	@Override
	public int getSize() {
		return argsList.size();
	}

	@Override
	public boolean hasValue(int index, String name) {
		try {
			return Integer.parseInt(name) < argsList.get(index).length;
		} catch (RuntimeException e) {
			return false;
		}
	}

	@Override
	public Object getValue(int index, String name) {
		return argsList.get(index)[Integer.parseInt(name)];
	}

}
