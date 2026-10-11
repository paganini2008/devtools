/**
* Copyright 2017-2026 Fred Feng (paganini.fy@gmail.com)

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
package com.github.paganini2008.devtools;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.RandomAccess;
import java.util.function.Supplier;

import org.junit.jupiter.api.Test;

/**
 * ClassUtilsTest
 * 
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class ClassUtilsTest {

	interface Named<T> {
	}

	interface SubNamed extends Named<String>, Serializable {
	}

	static class Base implements Comparator<String>, Runnable {

		public int compare(String a, String b) {
			return 0;
		}

		public void run() {
		}
	}

	static class Child extends Base implements SubNamed, Supplier<Integer> {

		private static final long serialVersionUID = 1L;

		public Integer get() {
			return 1;
		}
	}

	@Test
	public void testArrayClass() {
		assertSame(int[].class, ClassUtils.getPrimitiveArrayClass("int[]"));
		assertNull(ClassUtils.getPrimitiveArrayClass("Integer[]"));
		assertSame(Integer[].class, ClassUtils.getWrapperArrayClass("Integer[]"));
	}

	@Test
	public void testPrimitiveAndWrapper() {
		assertTrue(ClassUtils.isPrimitiveOrWrapper(int.class));
		assertTrue(ClassUtils.isPrimitiveOrWrapper(Integer.class));
		assertFalse(ClassUtils.isPrimitiveOrWrapper(String.class));
		assertFalse(ClassUtils.isPrimitiveOrWrapper(null));
		assertSame(Integer.class, ClassUtils.toWrapper(int.class));
		assertSame(String.class, ClassUtils.toWrapper(String.class));
		assertNull(ClassUtils.toWrapper(null));
		assertSame(long.class, ClassUtils.toPrimitive(Long.class));
		assertSame(String.class, ClassUtils.toPrimitive(String.class));
		assertEquals("I", ClassUtils.getAbbreviation("INT"));
		assertEquals("", ClassUtils.getAbbreviation(null));
		assertEquals("long", ClassUtils.getName("j"));
		assertEquals("", ClassUtils.getName(null));
	}

	@Test
	public void testTypeChecks() {
		assertTrue(ClassUtils.isArray(int[].class));
		assertFalse(ClassUtils.isArray(null));
		assertTrue(ClassUtils.isNotArray(String.class));
		assertTrue(ClassUtils.isBoolean(boolean.class));
		assertTrue(ClassUtils.isBoolean(Boolean.class));
		assertTrue(ClassUtils.isNotBoolean(null));
		assertTrue(ClassUtils.isCharacter(char.class));
		assertTrue(ClassUtils.isNotCharacter(int.class));
		assertTrue(ClassUtils.isByte(Byte.class));
		assertTrue(ClassUtils.isNotByte(null));
		assertTrue(ClassUtils.isShort(short.class));
		assertTrue(ClassUtils.isNotShort(int.class));
		assertTrue(ClassUtils.isInteger(Integer.class));
		assertTrue(ClassUtils.isNotInteger(null));
		assertTrue(ClassUtils.isFloat(float.class));
		assertTrue(ClassUtils.isNotFloat(double.class));
		assertTrue(ClassUtils.isDouble(Double.class));
		assertTrue(ClassUtils.isNotDouble(null));
		assertTrue(ClassUtils.isLong(long.class));
		assertTrue(ClassUtils.isNotLong(int.class));
		assertTrue(ClassUtils.isAssignableFrom(Number.class, Integer.class));
		assertFalse(ClassUtils.isAssignableFrom(null, Integer.class));
		assertTrue(ClassUtils.isNotAssignableFrom(Integer.class, Number.class));
		assertSame(String.class, ClassUtils.getType(String[].class));
		assertSame(String.class, ClassUtils.getType(String.class));
		assertNull(ClassUtils.getType(null));
	}

	@Test
	public void testHierarchy() {
		List<ParameterizedType> types = ClassUtils.getAllParameterizedTypes(Child.class);
		assertEquals(3, types.size());
		assertTrue(types.stream().anyMatch(t -> t.getRawType() == Named.class));
		assertTrue(types.stream().anyMatch(t -> t.getRawType() == Supplier.class));
		assertTrue(types.stream().anyMatch(t -> t.getRawType() == Comparator.class));
		assertThrows(IllegalArgumentException.class, () -> ClassUtils.getAllParameterizedTypes(null));

		List<Class<?>> interfaces = ClassUtils.getAllInterfaces(Child.class);
		assertTrue(interfaces.contains(SubNamed.class));
		assertTrue(interfaces.contains(Named.class));
		assertTrue(interfaces.contains(Serializable.class));
		assertTrue(interfaces.contains(Runnable.class));
		assertTrue(interfaces.contains(Comparator.class));
		assertEquals(interfaces.size(), interfaces.stream().distinct().count());

		List<Class<?>> supers = ClassUtils.getAllSuperClasses(ArrayList.class);
		assertEquals(2, supers.size());
		assertSame(AbstractList.class, supers.get(0));

		List<Class<?>> all = ClassUtils.getAllSuperClassesAndInterfaces(ArrayList.class);
		assertTrue(all.contains(AbstractList.class));
		assertTrue(all.contains(List.class));
		assertTrue(all.contains(Collection.class));
		assertTrue(all.contains(RandomAccess.class));
		assertFalse(all.contains(Object.class));
		assertEquals(all.size(), all.stream().distinct().count());
	}

	@Test
	public void testAssignable() {
		assertTrue(ClassUtils.isAssignable(Number.class, int.class));
		assertTrue(ClassUtils.isAssignable(int.class, Integer.class));
		assertTrue(ClassUtils.isAssignable(Object.class, String.class));
		assertFalse(ClassUtils.isAssignable(String.class, Object.class));
		assertFalse(ClassUtils.isAssignable((Class<?>) null, String.class));
		assertTrue(ClassUtils.isNotAssignable(long.class, String.class));
		assertTrue(ClassUtils.isAssignable(new Class<?>[] { String.class, Number.class }, Integer.class));
		assertFalse(ClassUtils.isAssignable(new Class<?>[] { String.class }, Integer.class));
		assertFalse(ClassUtils.isAssignable((Class<?>[]) null, Integer.class));
		assertTrue(ClassUtils.isAssignable(new Class<?>[] { Number.class, Object.class }, new Class<?>[] { Integer.class, String.class }));
		assertTrue(ClassUtils.isNotAssignable(new Class<?>[] { Number.class }, new Class<?>[] { String.class }));
		assertFalse(ClassUtils.isAssignable(new Class<?>[] { Number.class }, new Class<?>[0]));
		assertTrue(ClassUtils.isAssignable((Class<?>[]) null, (Class<?>[]) null));
		assertTrue(ClassUtils.isAssignable((Class<?>[]) null, new Class<?>[0]));
		assertTrue(ClassUtils.equals(new Class<?>[] { String.class }, new Class<?>[] { String.class }));
		assertFalse(ClassUtils.equals(new Class<?>[] { String.class }, new Class<?>[] { Object.class }));
		assertFalse(ClassUtils.equals(new Class<?>[] { String.class }, null));
		assertTrue(ClassUtils.equals(null, new Class<?>[0]));
	}

	@Test
	public void testForName() {
		assertTrue(ClassUtils.isPresent("java.lang.String"));
		assertFalse(ClassUtils.isPresent("no.such.Clazz"));
		assertThrows(IllegalArgumentException.class, () -> ClassUtils.isPresent(" "));
		assertSame(String.class, ClassUtils.forName("java.lang.String"));
		assertThrows(IllegalArgumentException.class, () -> ClassUtils.forName("no.such.Clazz"));
		assertSame(ArrayList.class, ClassUtils.forName("java.util.ArrayList", List.class));
		assertThrows(IllegalArgumentException.class, () -> ClassUtils.forName("java.lang.String", Number.class));
	}

	@Test
	public void testNullableValue() {
		assertEquals(Byte.valueOf((byte) 0), ClassUtils.getNullableValue(byte.class));
		assertEquals(Short.valueOf((short) 0), ClassUtils.getNullableValue(short.class));
		assertEquals(Integer.valueOf(0), ClassUtils.getNullableValue(int.class));
		assertEquals(Long.valueOf(0), ClassUtils.getNullableValue(long.class));
		assertEquals(Boolean.FALSE, ClassUtils.getNullableValue(boolean.class));
		assertEquals(Character.valueOf('\0'), ClassUtils.getNullableValue(char.class));
		assertNull(ClassUtils.getNullableValue(Integer.class));
		assertNull(ClassUtils.getNullableValue(null));
	}

	@Test
	public void testContainsAndJoin() {
		Class<?>[] types = { Number.class, String.class };
		assertTrue(ClassUtils.contains(types, Integer.class));
		assertFalse(ClassUtils.contains(types, Integer.class, true));
		assertTrue(ClassUtils.contains(types, String.class, true));
		assertTrue(ClassUtils.notContains(types, Thread.class));
		assertTrue(ClassUtils.notContains(types, Integer.class, true));
		assertFalse(ClassUtils.contains(null, String.class));
		assertEquals("java.lang.Number,java.lang.String", ClassUtils.join(types, ","));
	}

}
