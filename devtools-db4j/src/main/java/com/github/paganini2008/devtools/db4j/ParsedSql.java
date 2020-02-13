package com.github.paganini2008.devtools.db4j;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * ParsedSql
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class ParsedSql implements Serializable {

	private static final long serialVersionUID = -7823481840053509465L;
	private final StringBuilder originalSql = new StringBuilder();
	private final List<String> parameterNames = new ArrayList<String>();

	public List<String> getParameterNames() {
		return parameterNames;
	}

	public StringBuilder getOriginalSql() {
		return originalSql;
	}

	public String toString() {
		return getOriginalSql().toString();
	}

}
