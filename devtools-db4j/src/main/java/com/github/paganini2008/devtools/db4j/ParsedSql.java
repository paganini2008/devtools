package com.github.paganini2008.devtools.db4j;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.github.paganini2008.devtools.PlaceholderTokenParser;
import com.github.paganini2008.devtools.collection.LruMap;

/**
 * ParsedSql
 * 
 * @author Fred Feng
 * @version 1.0
 */
public final class ParsedSql implements Serializable {

	private static final long serialVersionUID = -7823481840053509465L;
	private static final LruMap<String, ParsedSql> parsedSqlCache = new LruMap<String, ParsedSql>(1024);
	private static final String DEFAULT_PLACEHOLDER_PREFIX = "{";
	private static final String DEFAULT_PLACEHOLDER_SUFFEX = "}";
	private final StringBuilder rawSql = new StringBuilder();
	private final List<String> parameterNames = new ArrayList<String>();

	ParsedSql() {
	}

	public List<String> getParameterNames() {
		return parameterNames;
	}

	public StringBuilder getRawSql() {
		return rawSql;
	}

	public String toString() {
		return rawSql.toString();
	}

	public static ParsedSql parse(String sql) {
		return parsedSqlCache.getOrDefault(sql, doParse(sql));
	}

	private static ParsedSql doParse(String sql) {
		ParsedSql parsedSql = new ParsedSql();
		PlaceholderTokenParser placeholderTokenizer = new PlaceholderTokenParser(DEFAULT_PLACEHOLDER_PREFIX, DEFAULT_PLACEHOLDER_SUFFEX);
		parsedSql.getRawSql().append(placeholderTokenizer.parse(sql, parameterName -> {
			parsedSql.getParameterNames().add(parameterName);
			return "?";
		}));
		return parsedSql;
	}

}
