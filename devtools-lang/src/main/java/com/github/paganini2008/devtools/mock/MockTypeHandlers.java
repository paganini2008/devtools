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
package com.github.paganini2008.devtools.mock;

import java.lang.reflect.Field;
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
		public Object apply(Field field, RandomOperations operations, MockContext context) {
			if (field.getType() == String.class) {
				return operations.randomString(field, context);
			}
			return context.mock(field, operations);
		}

	}

	public static class BigIntegerTypeHandler implements MockTypeHandler {

		@Override
		public Object apply(Field field, RandomOperations operations, MockContext context) {
			if (field.getType() == BigInteger.class) {
				return operations.randomBigInteger(field, context);
			}
			return context.mock(field, operations);
		}

	}

	public static class BigDecimalTypeHandler implements MockTypeHandler {

		@Override
		public Object apply(Field field, RandomOperations operations, MockContext context) {
			if (field.getType() == BigDecimal.class) {
				return operations.randomBigDecimal(field, context);
			}
			return context.mock(field, operations);
		}

	}

	public static class DateTypeHandler implements MockTypeHandler {

		@Override
		public Object apply(Field field, RandomOperations operations, MockContext context) {
			if (field.getType() == Date.class) {
				return operations.randomDate(field, context);
			}
			return context.mock(field, operations);
		}

	}

	public static class LocalDateTypeHandler implements MockTypeHandler {

		@Override
		public Object apply(Field field, RandomOperations operations, MockContext context) {
			if (field.getType() == LocalDate.class) {
				return operations.randomLocalDate(field, context);
			}
			return context.mock(field, operations);
		}

	}

	public static class LocalDateTimeTypeHandler implements MockTypeHandler {

		@Override
		public Object apply(Field field, RandomOperations operations, MockContext context) {
			if (field.getType() == LocalDateTime.class) {
				return operations.randomLocalDateTime(field, context);
			}
			return context.mock(field, operations);
		}

	}

	public static class LocalTimeTypeHandler implements MockTypeHandler {

		@Override
		public Object apply(Field field, RandomOperations operations, MockContext context) {
			if (field.getType() == LocalTime.class) {
				return operations.randomLocalTime(field, context);
			}
			return context.mock(field, operations);
		}

	}

	public static class BooleanTypeHandler implements MockTypeHandler {

		@Override
		public Object apply(Field field, RandomOperations operations, MockContext context) {
			if (field.getType() == Boolean.class || field.getType() == boolean.class) {
				return operations.randomBoolean(field, context);
			}
			return context.mock(field, operations);
		}

	}

	public static class CharacterTypeHandler implements MockTypeHandler {

		@Override
		public Object apply(Field field, RandomOperations operations, MockContext context) {
			if (field.getType() == Character.class || field.getType() == char.class) {
				return operations.randomChar(field, context);
			}
			return context.mock(field, operations);
		}

	}

	public static class ByteTypeHandler implements MockTypeHandler {

		@Override
		public Object apply(Field field, RandomOperations operations, MockContext context) {
			if (field.getType() == Byte.class || field.getType() == byte.class) {
				return operations.randomByte(field, context);
			}
			return context.mock(field, operations);
		}

	}

	public static class ShortTypeHandler implements MockTypeHandler {

		@Override
		public Object apply(Field field, RandomOperations operations, MockContext context) {
			if (field.getType() == Short.class || field.getType() == short.class) {
				return operations.randomShort(field, context);
			}
			return context.mock(field, operations);
		}

	}

	public static class IntegerTypeHandler implements MockTypeHandler {

		@Override
		public Object apply(Field field, RandomOperations operations, MockContext context) {
			if (field.getType() == Integer.class || field.getType() == int.class) {
				return operations.randomInt(field, context);
			}
			return context.mock(field, operations);
		}

	}

	public static class LongTypeHandler implements MockTypeHandler {

		@Override
		public Object apply(Field field, RandomOperations operations, MockContext context) {
			if (field.getType() == Long.class || field.getType() == long.class) {
				return operations.randomLong(field, context);
			}
			return context.mock(field, operations);
		}

	}

	public static class FloatTypeHandler implements MockTypeHandler {

		@Override
		public Object apply(Field field, RandomOperations operations, MockContext context) {
			if (field.getType() == Float.class || field.getType() == float.class) {
				return operations.randomFloat(field, context);
			}
			return context.mock(field, operations);
		}

	}

	public static class DoubleTypeHandler implements MockTypeHandler {

		@Override
		public Object apply(Field field, RandomOperations operations, MockContext context) {
			if (field.getType() == Double.class || field.getType() == double.class) {
				return operations.randomDouble(field, context);
			}
			return context.mock(field, operations);
		}

	}

	public static class EnumTypeHandler implements MockTypeHandler {

		@Override
		public Object apply(Field field, RandomOperations operations, MockContext context) {
			if (Enum.class.isAssignableFrom(field.getType())) {
				return operations.randomEnum(field, context);
			}
			return context.mock(field, operations);
		}

	}

}
