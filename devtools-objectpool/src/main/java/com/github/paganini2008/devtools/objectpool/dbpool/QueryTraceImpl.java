package com.github.paganini2008.devtools.objectpool.dbpool;

import java.io.Serializable;
import java.util.Arrays;

import com.github.paganini2008.devtools.date.DateUtils;

/**
 * QueryTraceImpl
 * 
 * @author Fred Feng
 * 
 * 
 * @version 1.0
 */
public class QueryTraceImpl implements QueryTrace, Serializable {

	private static final long serialVersionUID = 5264579507686000051L;
	private final String sql;
	private final Object[] parameters;
	private final long startTime;
	private final long endTime;

	public QueryTraceImpl(String sql, Object[] parameters, long startTime, long endTime) {
		this.sql = sql;
		this.parameters = parameters;
		this.startTime = startTime;
		this.endTime = endTime;
	}

	public Object[] getParameters() {
		return parameters;
	}

	public String getSql() {
		return sql;
	}

	public long getStartTime() {
		return startTime;
	}

	public long getEndTime() {
		return endTime;
	}

	public String toString() {
		return "[QueryTrace] sql: " + sql + ", parameters: " + Arrays.toString(parameters) + ", elapsed: "
				+ DateUtils.formatDurationAsHour(endTime - startTime);
	}

}
