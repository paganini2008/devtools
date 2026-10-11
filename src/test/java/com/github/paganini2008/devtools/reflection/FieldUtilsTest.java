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
package com.github.paganini2008.devtools.reflection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import com.github.paganini2008.devtools.MatchMode;
import com.github.paganini2008.devtools.reflection.ReflectionFixtures.Base;
import com.github.paganini2008.devtools.reflection.ReflectionFixtures.Child;
import com.github.paganini2008.devtools.reflection.ReflectionFixtures.Constants;
import com.github.paganini2008.devtools.reflection.ReflectionFixtures.Marker;
import com.github.paganini2008.devtools.reflection.ReflectionFixtures.Other;

/**
 * FieldUtilsTest
 * 
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class FieldUtilsTest {

	public static class Holder {
		public int primitive = 1;
		public Integer wrapper = 2;
		public int[] array = { 1, 2 };
		public List<String> generic = new ArrayList<String>();
	}

	public interface NoFields {
	}

	public interface WithField {
		int VALUE = 42;
	}

	public static class EmptyThenField implements NoFields, WithField {
	}

	@AfterEach
	public void reset() {
		Base.staticValue = "base-static";
	}

	private static List<String> names(List<Field> fields) {
		List<String> names = new ArrayList<String>();
		for (Field f : fields) {
			if (f.isSynthetic()) {
				// e.g. fields injected by coverage tools
				continue;
			}
			names.add(f.getName());
		}
		return names;
	}

	@Test
	public void readField_readsPrivateInheritedAndInterfaceFields() {
		Child child = new Child();
		assertEquals("child", FieldUtils.readField(child, "title"));
		assertEquals(7L, FieldUtils.readField(child, "number"));
		assertEquals("fixed", FieldUtils.readField(child, "fixed"));
		assertEquals(1, FieldUtils.readField(child, "visible"));
		assertEquals("hello", FieldUtils.readField(child, "GREETING"));
		assertEquals("child", FieldUtils.readDeclaredField(child, "title"));
		assertThrows(ReflectionException.class, () -> FieldUtils.readDeclaredField(child, "visible"));
		// private field of the super class is not reachable through getField
		assertThrows(ReflectionException.class, () -> FieldUtils.readField(child, "secret"));
		assertEquals("base-secret", FieldUtils.readField(child, FieldUtils.getDeclaredField(Base.class, "secret")));
	}

	@Test
	public void readField_withInvalidArguments_throws() {
		Child child = new Child();
		assertThrows(IllegalArgumentException.class, () -> FieldUtils.readField(null, "title"));
		assertThrows(IllegalArgumentException.class, () -> FieldUtils.readField(child, (Field) null));
		assertThrows(IllegalArgumentException.class, () -> FieldUtils.readField(child, ""));
		assertThrows(IllegalArgumentException.class, () -> FieldUtils.readDeclaredField(null, "title"));
		ReflectionException e = assertThrows(ReflectionException.class,
				() -> FieldUtils.readField(new Holder(), FieldUtils.getField(Child.class, "title")));
		assertTrue(e.getMessage().contains("title"));
		ReflectionException missing = assertThrows(ReflectionException.class, () -> FieldUtils.getField(Child.class, "missing"));
		assertTrue(missing.getMessage().contains("missing"));
	}

	@Test
	public void readStaticField_readsStaticAndRejectsInstanceFields() {
		assertEquals(5, FieldUtils.readStaticField(Child.class, "counter"));
		assertEquals(5, FieldUtils.readDeclaredStaticField(Child.class, "counter"));
		assertEquals("base-static", FieldUtils.readStaticField(Child.class, "staticValue"));
		assertEquals("hello", FieldUtils.readStaticField(FieldUtils.getField(Constants.class, "GREETING")));
		IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
				() -> FieldUtils.readStaticField(Child.class, "title"));
		assertTrue(e.getMessage().contains("not static"));
		assertThrows(IllegalArgumentException.class, () -> FieldUtils.readStaticField((Field) null));
	}

	@Test
	public void writeField_writesPrimitiveWrapperArrayAndGenericFields() {
		Holder h = new Holder();
		FieldUtils.writeField(h, "primitive", 10);
		FieldUtils.writeField(h, "wrapper", null);
		FieldUtils.writeDeclaredField(h, "array", new int[] { 9 });
		List<String> list = new ArrayList<String>();
		list.add("x");
		FieldUtils.writeField(h, FieldUtils.getField(Holder.class, "generic"), list);
		assertEquals(10, h.primitive);
		assertNull(h.wrapper);
		assertEquals(9, h.array[0]);
		assertEquals("x", h.generic.get(0));
		// primitive field cannot be set to null
		ReflectionException e = assertThrows(ReflectionException.class, () -> FieldUtils.writeField(h, "primitive", null));
		assertTrue(e.getMessage().startsWith("Cannot write field"));
		assertThrows(ReflectionException.class, () -> FieldUtils.writeField(h, "primitive", "text"));
		assertThrows(IllegalArgumentException.class, () -> FieldUtils.writeField(null, "primitive", 1));
		assertThrows(IllegalArgumentException.class, () -> FieldUtils.writeField(h, (Field) null, 1));
		assertThrows(IllegalArgumentException.class, () -> FieldUtils.writeDeclaredField(null, "primitive", 1));

		Child child = new Child();
		FieldUtils.writeField(child, "title", "changed");
		assertEquals("changed", child.getTitle());
	}

	@Test
	public void writeStaticField_writesStaticAndRejectsInstanceOrFinalFields() {
		FieldUtils.writeStaticField(Base.class, "staticValue", "changed");
		assertEquals("changed", Base.staticValue);
		FieldUtils.writeDeclaredStaticField(Base.class, "staticValue", "again");
		assertEquals("again", Base.staticValue);
		FieldUtils.writeStaticField(FieldUtils.getField(Base.class, "staticValue"), "third");
		assertEquals("third", Base.staticValue);
		assertThrows(IllegalArgumentException.class, () -> FieldUtils.writeStaticField(Child.class, "title", "x"));
		ReflectionException e = assertThrows(ReflectionException.class,
				() -> FieldUtils.writeStaticField(Constants.class, "GREETING", "x"));
		assertTrue(e.getMessage().startsWith("Cannot write static field"));
		assertThrows(IllegalArgumentException.class, () -> FieldUtils.writeStaticField((Field) null, "x"));
	}

	@Test
	public void getField_searchesDeclaredPublicAndInterfaces() {
		assertEquals(Child.class, FieldUtils.getField(Child.class, "title").getDeclaringClass());
		assertEquals(Base.class, FieldUtils.getField(Child.class, "visible").getDeclaringClass());
		assertEquals(Constants.class, FieldUtils.getField(Child.class, "GREETING").getDeclaringClass());
		assertNotNull(FieldUtils.getFieldIfAbsent(Child.class, "title"));
		assertNull(FieldUtils.getFieldIfAbsent(Child.class, "missing"));
		assertNull(FieldUtils.getFieldIfAbsent(null, "title"));
		assertThrows(IllegalArgumentException.class, () -> FieldUtils.getField(null, "x"));
		assertThrows(IllegalArgumentException.class, () -> FieldUtils.getDeclaredField(null, "x"));
		assertThrows(IllegalArgumentException.class, () -> FieldUtils.getDeclaredField(Child.class, null));
		assertThrows(ReflectionException.class, () -> FieldUtils.getDeclaredField(Child.class, "visible"));
	}

	@Test
	public void getDeclaredFields_includesDirectInterfacesAndAppliesFilter() {
		List<String> all = names(FieldUtils.getDeclaredFields(Child.class, null));
		assertTrue(all.containsAll(java.util.Arrays.asList("counter", "title", "number", "fixed", "GREETING")));
		assertFalse(all.contains("visible"));
		assertEquals(java.util.Arrays.asList("title"),
				names(FieldUtils.getDeclaredFields(Child.class, FieldFilters.isAnnotationPresent(Marker.class))));
	}

	@Test
	public void getFields_walksWholeHierarchyWithoutDuplicates() {
		List<String> all = names(FieldUtils.getFields(Child.class, null));
		assertTrue(all.containsAll(java.util.Arrays.asList("title", "visible", "secret", "staticValue", "GREETING")));
		Set<String> unique = new HashSet<String>(all);
		assertEquals(unique.size(), all.size(), all.toString());
		assertEquals(java.util.Arrays.asList("GREETING"),
				names(FieldUtils.getFields(Child.class, FieldFilters.include(new String[] { "GREETING" }))));
	}

	@Test
	public void fieldIterators_skipInterfacesWithoutFields() {
		Iterator<Field> it = new FieldUtils.DeclaredFieldIterator(EmptyThenField.class);
		assertEquals(java.util.Arrays.asList("VALUE"), drain(it));
		assertFalse(it.hasNext());
		assertThrows(NoSuchElementException.class, it::next);
		Iterator<Field> it2 = new FieldUtils.FieldIterator(EmptyThenField.class);
		assertEquals(java.util.Arrays.asList("VALUE"), drain(it2));
		assertThrows(NoSuchElementException.class, it2::next);
	}

	private static List<String> drain(Iterator<Field> it) {
		List<Field> fields = new ArrayList<Field>();
		while (it.hasNext()) {
			fields.add(it.next());
		}
		return names(fields);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void fieldFilters_matchByAnnotationTypeAndName() throws Exception {
		Field title = Child.class.getDeclaredField("title");
		Field number = Child.class.getDeclaredField("number");
		assertTrue(FieldFilters.isAnnotationPresent(new Class[] { Other.class, Marker.class }).accept("title", title));
		assertFalse(FieldFilters.isAnnotationPresent(new Class[] { Other.class }).accept("title", title));
		assertTrue(FieldFilters.isAnnotationPresent(Other.class).accept("number", number));
		assertTrue(FieldFilters.isAssignable(CharSequence.class).accept("title", title));
		assertTrue(FieldFilters.isAssignable(new Class<?>[] { Long.class }).accept("number", number));
		assertFalse(FieldFilters.isAssignable(new Class<?>[] { Integer.class }).accept("number", number));
		assertTrue(FieldFilters.exclude(new String[] { "x" }).accept("title", title));
		assertFalse(FieldFilters.exclude(new String[] { "title" }).accept("title", title));
		assertTrue(FieldFilters.matches("it", MatchMode.ANY_WHERE).accept("title", title));
		assertTrue(FieldFilters.typeContains(new Class<?>[] { long.class }).accept("number", number));
		assertFalse(FieldFilters.typeContains(new Class<?>[] { Long.class }).accept("number", number));

		FieldFilter yes = (n, f) -> true;
		FieldFilter no = (n, f) -> false;
		assertTrue(yes.and(yes).accept("a", title));
		assertFalse(yes.and(no).accept("a", title));
		assertTrue(no.or(yes).accept("a", title));
		assertFalse(no.or(no).accept("a", title));
		// not(filter) only negates the given filter
		assertTrue(no.not(no).accept("a", title));
		assertFalse(yes.not(yes).accept("a", title));
	}

}
