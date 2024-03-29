/**
* Copyright 2017-2022 Fred Feng (paganini.fy@gmail.com)

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

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import com.github.paganini2008.devtools.db4j.type.BigDecimalTypeHandler;
import com.github.paganini2008.devtools.db4j.type.BigIntegerTypeHandler;
import com.github.paganini2008.devtools.db4j.type.BlobTypeHandler;
import com.github.paganini2008.devtools.db4j.type.BooleanTypeHandler;
import com.github.paganini2008.devtools.db4j.type.ByteTypeHandler;
import com.github.paganini2008.devtools.db4j.type.BytesTypeHandler;
import com.github.paganini2008.devtools.db4j.type.CharacterTypeHandler;
import com.github.paganini2008.devtools.db4j.type.ClobTypeHandler;
import com.github.paganini2008.devtools.db4j.type.DateTypeHandler;
import com.github.paganini2008.devtools.db4j.type.DoubleTypeHandler;
import com.github.paganini2008.devtools.db4j.type.EnumTypeHandler;
import com.github.paganini2008.devtools.db4j.type.FloatTypeHandler;
import com.github.paganini2008.devtools.db4j.type.IntegerTypeHandler;
import com.github.paganini2008.devtools.db4j.type.LongTypeHandler;
import com.github.paganini2008.devtools.db4j.type.ObjectTypeHandler;
import com.github.paganini2008.devtools.db4j.type.ShortTypeHandler;
import com.github.paganini2008.devtools.db4j.type.SqlDateTypeHandler;
import com.github.paganini2008.devtools.db4j.type.SqlTimeTypeHandler;
import com.github.paganini2008.devtools.db4j.type.StringTypeHandler;
import com.github.paganini2008.devtools.db4j.type.TimestampTypeHandler;
import com.github.paganini2008.devtools.db4j.type.TypeHandler;
import com.github.paganini2008.devtools.db4j.type.UndefinedTypeHandler;

/**
 * TypeHandlerRegistryImpl
 * 
 * @author Fred Feng
 * @since 2.0.1
 */
@SuppressWarnings("all")
public class TypeHandlerRegistryImpl implements TypeHandlerRegistry {

	private static final Map<Type, Type> wrapperPrimitiveMap = new HashMap<Type, Type>() {
		private static final long serialVersionUID = 1L;
		{
			put(Byte.class, byte.class);
			put(Short.class, short.class);
			put(Integer.class, int.class);
			put(Long.class, long.class);
			put(Float.class, float.class);
			put(Double.class, double.class);
			put(Boolean.class, boolean.class);
			put(Character.class, char.class);
		}
	};

	private static final ObjectTypeHandler OBJECT_TYPE_HANDLER = new ObjectTypeHandler();
	private final Map<JdbcType, TypeHandler> jdbcTypeHandlers = new EnumMap<JdbcType, TypeHandler>(JdbcType.class);
	private final Map<Type, Map<JdbcType, TypeHandler>> javaTypeHandlers = new HashMap<Type, Map<JdbcType, TypeHandler>>();

	public static TypeHandler getDefault() {
		return OBJECT_TYPE_HANDLER;
	}

	public TypeHandlerRegistryImpl() {
		TypeHandler typeHandler = new ByteTypeHandler();
		register(Byte.class, typeHandler);
		register(JdbcType.TINYINT, typeHandler);

		typeHandler = new ShortTypeHandler();
		register(Short.class, typeHandler);
		register(JdbcType.SMALLINT, typeHandler);

		typeHandler = new IntegerTypeHandler();
		register(Integer.class, typeHandler);
		register(JdbcType.INTEGER, typeHandler);

		typeHandler = new LongTypeHandler();
		register(Long.class, typeHandler);
		register(JdbcType.BIGINT, typeHandler);

		typeHandler = new FloatTypeHandler();
		register(Float.class, typeHandler);
		register(JdbcType.FLOAT, typeHandler);

		typeHandler = new DoubleTypeHandler();
		register(Double.class, typeHandler);
		register(JdbcType.DOUBLE, typeHandler);

		typeHandler = new CharacterTypeHandler();
		register(Character.class, typeHandler);
		register(JdbcType.CHAR, typeHandler);

		typeHandler = new BooleanTypeHandler();
		register(Boolean.class, typeHandler);
		register(JdbcType.BOOLEAN, typeHandler);
		register(JdbcType.BIT, typeHandler);

		typeHandler = new BigDecimalTypeHandler();
		register(BigDecimal.class, typeHandler);
		register(JdbcType.REAL, typeHandler);
		register(JdbcType.DECIMAL, typeHandler);
		register(JdbcType.NUMERIC, typeHandler);

		register(BigInteger.class, new BigIntegerTypeHandler());

		typeHandler = new StringTypeHandler();
		register(String.class, typeHandler);
		register(String.class, JdbcType.CHAR, typeHandler);
		register(String.class, JdbcType.VARCHAR, typeHandler);
		register(String.class, JdbcType.NVARCHAR, typeHandler);
		register(String.class, JdbcType.NCHAR, typeHandler);
		register(String.class, JdbcType.LONGVARCHAR, typeHandler);
		register(JdbcType.CHAR, typeHandler);
		register(JdbcType.VARCHAR, typeHandler);
		register(JdbcType.NVARCHAR, typeHandler);
		register(JdbcType.NCHAR, typeHandler);
		register(JdbcType.LONGVARCHAR, typeHandler);

		typeHandler = new ClobTypeHandler();
		register(String.class, JdbcType.CLOB, typeHandler);
		register(String.class, JdbcType.NCLOB, typeHandler);
		register(JdbcType.CLOB, typeHandler);
		register(JdbcType.NCLOB, typeHandler);

		typeHandler = new BlobTypeHandler();
		register(byte[].class, JdbcType.BLOB, typeHandler);
		register(byte[].class, JdbcType.LONGVARBINARY, typeHandler);
		register(JdbcType.BLOB, new BlobTypeHandler());
		register(JdbcType.LONGVARBINARY, new BlobTypeHandler());

		register(byte[].class, new BytesTypeHandler());

		register(Object.class, OBJECT_TYPE_HANDLER);
		register(JdbcType.OBJECT, OBJECT_TYPE_HANDLER);
		register((Type) null, OBJECT_TYPE_HANDLER);
		register(JdbcType.NULL, OBJECT_TYPE_HANDLER);

		typeHandler = new UndefinedTypeHandler(this);
		register(Object.class, JdbcType.OTHER, typeHandler);
		register(JdbcType.OTHER, typeHandler);

		typeHandler = new DateTypeHandler();
		register(Date.class, typeHandler);
		register(Date.class, JdbcType.TIMESTAMP, typeHandler);

		typeHandler = new SqlDateTypeHandler();
		register(Date.class, JdbcType.DATE, typeHandler);
		register(JdbcType.DATE, typeHandler);
		register(java.sql.Date.class, typeHandler);

		typeHandler = new SqlTimeTypeHandler();
		register(Date.class, JdbcType.TIME, typeHandler);
		register(JdbcType.TIME, typeHandler);
		register(java.sql.Time.class, typeHandler);

		typeHandler = new TimestampTypeHandler();
		register(java.sql.Timestamp.class, typeHandler);
		register(JdbcType.TIMESTAMP, typeHandler);

	}

	public TypeHandler getTypeHandler(Type type) {
		return getTypeHandler(type, JdbcType.OTHER);
	}

	public TypeHandler getTypeHandler(Type javaType, JdbcType jdbcType) {
		TypeHandler find = null;
		Map<JdbcType, TypeHandler> map = javaTypeHandlers.get(javaType);
		if (map == null) {
			find = jdbcTypeHandlers.get(jdbcType);
		} else {
			find = map.get(jdbcType);
		}
		if (find == null && jdbcType == JdbcType.ENUM && Enum.class.isAssignableFrom((Class<?>) javaType)) {
			return new EnumTypeHandler((Class<?>) javaType);
		}
		if (find == null) {
			find = OBJECT_TYPE_HANDLER;
		}
		return find;
	}

	public void register(JdbcType jdbcType, TypeHandler typeHandler) {
		jdbcTypeHandlers.put(jdbcType, typeHandler);
	}

	public void register(Type javaType, TypeHandler typeHandler) {
		register(javaType, JdbcType.OTHER, typeHandler);
	}

	public void register(Type javaType, JdbcType jdbcType, TypeHandler typeHandler) {
		Map<JdbcType, TypeHandler> map = javaTypeHandlers.get(javaType);
		if (map == null) {
			javaTypeHandlers.put(javaType, new HashMap<JdbcType, TypeHandler>());
			map = javaTypeHandlers.get(javaType);
		}
		map.put(jdbcType, typeHandler);
		if (wrapperPrimitiveMap.containsKey(javaType)) {
			register(wrapperPrimitiveMap.get(javaType), jdbcType, typeHandler);
		}
	}

}
