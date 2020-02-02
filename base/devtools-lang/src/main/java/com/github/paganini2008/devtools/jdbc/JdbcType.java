package com.github.paganini2008.devtools.jdbc;

import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

/**
 * Jdbc data type
 * 
 * @author Fred Feng
 * @version 1.0
 */
public enum JdbcType {

	ARRAY(Types.ARRAY), 
	BIT(Types.BIT), 
	TINYINT(Types.TINYINT), 
	SMALLINT(Types.SMALLINT), 
	INTEGER(Types.INTEGER), 
	BIGINT(Types.BIGINT), 
	FLOAT(Types.FLOAT), 
	REAL(Types.REAL), 
	DOUBLE(Types.DOUBLE),
	NUMERIC(Types.NUMERIC), 
	DECIMAL(Types.DECIMAL),
	CHAR(Types.CHAR), 
	VARCHAR(Types.VARCHAR), 
	LONGVARCHAR(Types.LONGVARCHAR), 
	DATE(Types.DATE), 
	TIME(Types.TIME), 
	TIMESTAMP(Types.TIMESTAMP), 
	BINARY(Types.BINARY), 
	VARBINARY(Types.VARBINARY), 
	LONGVARBINARY(Types.LONGVARBINARY), 
	NULL(Types.NULL), 
	OTHER(Types.OTHER), 
	BLOB(Types.BLOB), 
	CLOB(Types.CLOB), 
	BOOLEAN(Types.BOOLEAN), 
	NVARCHAR(Types.NVARCHAR), 
	NCHAR(Types.NCHAR), 
	NCLOB(Types.NCLOB), 
	STRUCT(Types.STRUCT), 
	ENUM(Integer.MIN_VALUE + 1001), 
	OBJECT(Integer.MIN_VALUE + 1002);

	private final int typeCode;

	private JdbcType(int typeCode) {
		this.typeCode = typeCode;
	}

	public int getTypeCode() {
		return typeCode;
	}

	private static Map<Integer, JdbcType> jdbcTypes = new HashMap<Integer, JdbcType>();

	static {
		for (JdbcType type : JdbcType.values()) {
			jdbcTypes.put(type.typeCode, type);
		}
	}

	/**
	 * Find by typeCode
	 * 
	 * @param typeCode
	 * @return
	 */
	public static JdbcType find(int typeCode) {
		return jdbcTypes.get(typeCode);
	}

}
