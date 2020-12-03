package com.github.paganini2008.devtools.jdbc;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

/**
 * TransactionIsolationLevel
 * 
 * @author Jimmy Hoff
 * @version 1.0
 */
public enum TransactionIsolationLevel {
	
	NONE(Connection.TRANSACTION_NONE), 
	READ_COMMITTED(Connection.TRANSACTION_READ_COMMITTED), 
	READ_UNCOMMITTED(Connection.TRANSACTION_READ_UNCOMMITTED), 
	REPEATABLE_READ(Connection.TRANSACTION_REPEATABLE_READ), 
	SERIALIZABLE(Connection.TRANSACTION_SERIALIZABLE);

	private final int level;

	private TransactionIsolationLevel(int level) {
		this.level = level;
	}

	public int getLevel() {
		return level;
	}

	private static final Map<Integer, TransactionIsolationLevel> lookup = new HashMap<Integer, TransactionIsolationLevel>();

	static {
		for (TransactionIsolationLevel level : TransactionIsolationLevel.values()) {
			lookup.put(level.getLevel(), level);
		}
	}

	public static TransactionIsolationLevel get(int level) {
		if (!lookup.containsKey(level)) {
			throw new IllegalArgumentException("Unknown transaction isolation level: " + level);
		}
		return lookup.get(level);
	}
}
