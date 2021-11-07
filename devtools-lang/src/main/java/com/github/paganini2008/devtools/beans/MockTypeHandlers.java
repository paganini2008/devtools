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
package com.github.paganini2008.devtools.beans;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;

/**
 * 
 * MockTypeHandlers
 *
 * @author Fred Feng
 *
 * @since 2.0.4
 */
public abstract class MockTypeHandlers {

	public static class StringTypeHandler implements MockTypeHandler {

		@Override
		public Object apply(Type type, RandomOperations operations, MockContext context) {
			if (type == String.class) {
				return operations.randomString(type, context);
			}
			return context.mock(type, operations);
		}

	}

	public static class BigIntegerTypeHandler implements MockTypeHandler {

		@Override
		public Object apply(Type type, RandomOperations operations, MockContext context) {
			if (type == BigInteger.class) {
				return operations.randomBigInteger(type, context);
			}
			return context.mock(type, operations);
		}

	}

	public static class BigDecimalTypeHandler implements MockTypeHandler {

		@Override
		public Object apply(Type type, RandomOperations operations, MockContext context) {
			if (type == BigDecimal.class) {
				return operations.randomBigDecimal(type, context);
			}
			return context.mock(type, operations);
		}

	}

	public static class DateTypeHandler implements MockTypeHandler {

		@Override
		public Object apply(Type type, RandomOperations operations, MockContext context) {
			if (type == Date.class) {
				return operations.randomDate(type, context);
			}
			return context.mock(type, operations);
		}

	}

	public static class LocalDateTypeHandler implements MockTypeHandler {

		@Override
		public Object apply(Type type, RandomOperations operations, MockContext context) {
			if (type == LocalDate.class) {
				return operations.randomLocalDate(type, context);
			}
			return context.mock(type, operations);
		}

	}

	public static class LocalDateTimeTypeHandler implements MockTypeHandler {

		@Override
		public Object apply(Type type, RandomOperations operations, MockContext context) {
			if (type == LocalDateTime.class) {
				return operations.randomLocalDateTime(type, context);
			}
			return context.mock(type, operations);
		}

	}

	public static class LocalTimeTypeHandler implements MockTypeHandler {

		@Override
		public Object apply(Type type, RandomOperations operations, MockContext context) {
			if (type == LocalTime.class) {
				return operations.randomLocalTime(type, context);
			}
			return context.mock(type, operations);
		}

	}

	public static class BooleanTypeHandler implements MockTypeHandler {

		@Override
		public Object apply(Type type, RandomOperations operations, MockContext context) {
			if (type == Boolean.class || type == boolean.class) {
				return operations.randomBoolean(type, context);
			}
			return context.mock(type, operations);
		}

	}

	public static class CharacterTypeHandler implements MockTypeHandler {

		@Override
		public Object apply(Type type, RandomOperations operations, MockContext context) {
			if (type == Character.class || type == char.class) {
				return operations.randomChar(type, context);
			}
			return context.mock(type, operations);
		}

	}

	public static class ByteTypeHandler implements MockTypeHandler {

		@Override
		public Object apply(Type type, RandomOperations operations, MockContext context) {
			if (type == Byte.class || type == byte.class) {
				return operations.randomByte(type, context);
			}
			return context.mock(type, operations);
		}

	}

	public static class ShortTypeHandler implements MockTypeHandler {

		@Override
		public Object apply(Type type, RandomOperations operations, MockContext context) {
			if (type == Short.class || type == short.class) {
				return operations.randomShort(type, context);
			}
			return context.mock(type, operations);
		}

	}

	public static class IntegerTypeHandler implements MockTypeHandler {

		@Override
		public Object apply(Type type, RandomOperations operations, MockContext context) {
			if (type == Integer.class || type == int.class) {
				return operations.randomInt(type, context);
			}
			return context.mock(type, operations);
		}

	}

	public static class LongTypeHandler implements MockTypeHandler {

		@Override
		public Object apply(Type type, RandomOperations operations, MockContext context) {
			if (type == Long.class || type == long.class) {
				return operations.randomLong(type, context);
			}
			return context.mock(type, operations);
		}

	}

	public static class FloatTypeHandler implements MockTypeHandler {

		@Override
		public Object apply(Type type, RandomOperations operations, MockContext context) {
			if (type == Float.class || type == float.class) {
				return operations.randomFloat(type, context);
			}
			return context.mock(type, operations);
		}

	}

	public static class DoubleTypeHandler implements MockTypeHandler {

		@Override
		public Object apply(Type type, RandomOperations operations, MockContext context) {
			if (type == Double.class || type == double.class) {
				return operations.randomDouble(type, context);
			}
			return context.mock(type, operations);
		}

	}

	public static class EnumTypeHandler implements MockTypeHandler {

		@SuppressWarnings("all")
		@Override
		public Object apply(Type type, RandomOperations operations, MockContext context) {
			if (Enum.class.isAssignableFrom((Class<?>) type)) {
				return operations.randomEnum((Class<Enum>) type, context);
			}
			return context.mock(type, operations);
		}

	}

}
