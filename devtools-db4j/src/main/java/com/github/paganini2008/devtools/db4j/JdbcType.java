/**
* Copyright 2017-2021 Fred Feng (paganini.fy@gmail.com)

* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.github.paganini2008.devtools.db4j;

import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

/**
 * Jdbc data type
 * 
 * @author Fred Feng
 * @since 2.0.1
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
	ENUM(10001), 
	OBJECT(10002),
	
	AUTO(99999);

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
