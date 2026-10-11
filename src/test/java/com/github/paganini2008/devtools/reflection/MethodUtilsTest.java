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

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import org.junit.jupiter.api.Test;

import com.github.paganini2008.devtools.MatchMode;
import com.github.paganini2008.devtools.reflection.ReflectionFixtures.Base;
import com.github.paganini2008.devtools.reflection.ReflectionFixtures.Child;
import com.github.paganini2008.devtools.reflection.ReflectionFixtures.Marker;
import com.github.paganini2008.devtools.reflection.ReflectionFixtures.Other;

/**
 * MethodUtilsTest
 * 
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class MethodUtilsTest {

	@Test
	public void invokeMethod_resolvesOverloadsNullArgumentsAndPrivateMethods() {
		Child child = new Child();
		assertEquals("string:a", MethodUtils.invokeMethod(child, "echo", "a"));
		assertEquals("object:1", MethodUtils.invokeMethod(child, "echo", 1));
		assertEquals("chars:sb", MethodUtils.invokeMethod(child, "echo", new StringBuilder("sb")));
		// null argument picks the most specific overload
		assertEquals("string:null", MethodUtils.invokeMethod(child, "echo", (Object) null));
		assertEquals(5L, MethodUtils.invokeMethod(child, "add", 2, 3L));
		assertEquals("private", MethodUtils.invokeMethod(child, "privateMethod"));
		assertEquals("private", MethodUtils.invokeMethod(child, "privateMethod", (Object[]) null));
		assertEquals("child", MethodUtils.invokeMethod(child, "name", null, (Object[]) null));
		assertEquals("base-marked", MethodUtils.invokeMethod(child, "marked"));
		assertEquals("object:x", MethodUtils.invokeMethod(child, "echo", new Class<?>[] { Object.class }, "x"));
		ReflectionException e = assertThrows(ReflectionException.class, () -> MethodUtils.invokeMethod(child, "fail"));
		assertTrue(e.getCause().getCause() instanceof IllegalStateException);
		assertThrows(ReflectionException.class, () -> MethodUtils.invokeMethod(child, "missing"));
		assertThrows(IllegalArgumentException.class, () -> MethodUtils.invokeMethod(null, "echo", "a"));
		assertThrows(IllegalArgumentException.class, () -> MethodUtils.invokeMethod(child, (Method) null));
		Method m = MethodUtils.getMethod(Child.class, "privateMethod");
		assertEquals("private", MethodUtils.invokeMethod(child, m, (Object[]) null));
	}

	@Test
	public void invokeStaticMethod_invokesStaticAndRejectsInstanceMethods() {
		assertEquals("static:a", MethodUtils.invokeStaticMethod(Child.class, "staticEcho", "a"));
		assertEquals(5, MethodUtils.invokeStaticMethod(Child.class, "count", (Object[]) null));
		assertEquals(5, MethodUtils.invokeStaticMethod(Child.class, "count", null, (Object[]) null));
		Method m = MethodUtils.getMethod(Child.class, "count");
		assertEquals(5, MethodUtils.invokeStaticMethod(m, (Object[]) null));
		assertThrows(ReflectionException.class, () -> MethodUtils.invokeStaticMethod(MethodUtils.getMethod(Child.class, "getTitle")));
		assertThrows(IllegalArgumentException.class, () -> MethodUtils.invokeStaticMethod((Method) null));
	}

	@Test
	public void getMethod_findsDeclaredInheritedAndAssignableMethods() {
		assertEquals(String.class, MethodUtils.getMethod(Child.class, "echo", String.class).getParameterTypes()[0]);
		assertEquals(CharSequence.class, MethodUtils.getMethod(Child.class, "echo", StringBuilder.class).getParameterTypes()[0]);
		assertEquals(Object.class, MethodUtils.getMethod(Child.class, "echo", Integer.class).getParameterTypes()[0]);
		assertNotNull(MethodUtils.getMethod(Child.class, "add", Integer.class, Long.class));
		// inherited public method
		assertEquals(Object.class, MethodUtils.getMethod(Child.class, "hashCode").getDeclaringClass());
		assertEquals(Object.class, MethodUtils.getMethod(Child.class, "equals", String.class).getDeclaringClass());
		assertNull(MethodUtils.getMethodIfAbsent(Child.class, "missing"));
		assertNull(MethodUtils.getMethodIfAbsent(null, "echo"));
		assertNull(MethodUtils.getMethodIfAbsent(Child.class, "echo", int.class, int.class));
		assertNotNull(MethodUtils.getMethodIfAbsent(Child.class, "count", (Class<?>[]) null));
	}

	@Test
	public void getMethods_walkHierarchyWithoutDuplicates() {
		List<Method> declared = MethodUtils.getDeclaredMethods(Child.class, MethodFilters.include(new String[] { "echo" }));
		assertEquals(3, declared.size());
		// declared methods of Child include methods of its directly implemented interfaces
		List<Method> all = MethodUtils.getDeclaredMethods(Child.class, null);
		assertTrue(all.size() >= 12);

		List<Method> named = MethodUtils.getMethods(Child.class, MethodFilters.include(new String[] { "name" }));
		Set<Class<?>> owners = new HashSet<Class<?>>();
		for (Method m : named) {
			owners.add(m.getDeclaringClass());
		}
		// Child, Base and the Named interface each declare name() exactly once
		assertEquals(3, named.size());
		assertEquals(3, owners.size());
		List<Method> withMarker = MethodUtils.getMethodsWithAnnotation(Child.class, Marker.class);
		assertEquals(1, withMarker.size());
		assertEquals(Base.class, withMarker.get(0).getDeclaringClass());
		assertTrue(MethodUtils.getDeclaredMethodsWithAnnotation(Child.class, Marker.class).isEmpty());
		assertEquals(1, MethodUtils.getDeclaredMethodsWithAnnotation(Child.class, Other.class).size());
	}

	@Test
	public void invokeMethodWithAnnotation_invokesAnnotatedMethods() {
		Child child = new Child();
		assertEquals("base-marked", MethodUtils.invokeMethodWithAnnotation(child, Marker.class));
		assertArrayEquals(new Object[] { "other" }, MethodUtils.invokeMethodsWithAnnotation(child, Other.class));
		assertNull(MethodUtils.invokeMethodWithAnnotation(child, Deprecated.class));
		assertNull(MethodUtils.invokeMethodsWithAnnotation(child, Deprecated.class));
	}

	@Test
	public void methodIterators_skipInterfacesWithoutMethods() {
		Iterator<Method> it = new MethodUtils.DeclaredMethodIterator(ReflectionFixtures.Empty.class);
		assertFalse(it.hasNext());
		assertThrows(NoSuchElementException.class, it::next);
		// an interface without methods followed by one with methods must not stop the iteration
		Iterator<Method> it2 = new MethodUtils.DeclaredMethodIterator(EmptyThenNamed.class);
		Set<String> names = new HashSet<String>();
		while (it2.hasNext()) {
			names.add(it2.next().getName());
		}
		assertTrue(names.contains("name"));
		Iterator<Method> it3 = new MethodUtils.MethodIterator(EmptyThenNamed.class);
		names.clear();
		while (it3.hasNext()) {
			names.add(it3.next().getName());
		}
		assertTrue(names.contains("name"));
		assertThrows(NoSuchElementException.class, it3::next);
	}

	@Test
	public void methodFilters_matchByAnnotationTypesAndName() {
		Method echo = MethodUtils.getMethod(Child.class, "echo", String.class);
		Method other = MethodUtils.getMethod(Child.class, "other");
		Method marked = MethodUtils.getMethod(Base.class, "marked");
		assertTrue(MethodFilters.isAnnotationPresent(Other.class).accept("other", other));
		assertFalse(MethodFilters.isAnnotationPresent(Other.class).accept("echo", echo));
		@SuppressWarnings("unchecked")
		MethodFilter any = MethodFilters.isAnnotationPresent(new Class[] { Marker.class, Other.class });
		assertTrue(any.accept("marked", marked));
		assertFalse(any.accept("echo", echo));
		assertTrue(MethodFilters.isAssignable(CharSequence.class).accept("echo", echo));
		assertFalse(MethodFilters.isAssignable(Integer.class).accept("echo", echo));
		assertTrue(MethodFilters.isAssignable(new Class<?>[] { Integer.class, String.class }).accept("echo", echo));
		assertTrue(MethodFilters.isMatched(new Class<?>[] { String.class }).accept("echo", echo));
		assertFalse(MethodFilters.isMatched(new Class<?>[] { Integer.class }).accept("echo", echo));
		assertTrue(MethodFilters.exclude(new String[] { "x" }).accept("echo", echo));
		assertFalse(MethodFilters.exclude(new String[] { "echo" }).accept("echo", echo));
		assertTrue(MethodFilters.matches("ec", MatchMode.START).accept("echo", echo));
		assertTrue(MethodFilters.returnTypeContains(new Class<?>[] { String.class }).accept("echo", echo));
		assertFalse(MethodFilters.returnTypeContains(new Class<?>[] { Object.class }).accept("echo", echo));

		MethodFilter yes = (n, m) -> true;
		MethodFilter no = (n, m) -> false;
		assertTrue(yes.and(yes).accept("a", echo));
		assertFalse(yes.and(no).accept("a", echo));
		assertTrue(no.or(yes).accept("a", echo));
		assertFalse(no.or(no).accept("a", echo));
		// not(filter) only negates the given filter
		assertTrue(no.not(no).accept("a", echo));
		assertFalse(yes.not(yes).accept("a", echo));
	}

	@Test
	public void reflectionException_keepsMessageAndCause() {
		Throwable cause = new RuntimeException();
		assertEquals("m", new ReflectionException("m").getMessage());
		assertEquals(cause, new ReflectionException("m", cause).getCause());
		assertEquals(cause, new ReflectionException(cause).getCause());
	}

	public interface NoMethods {
	}

	public interface Greeter {
		default String greet(String who) {
			return "hi " + who;
		}
	}

	public static class DefaultGreeter implements Greeter {
	}

	@Test
	public void invokeMethod_supportsInterfaceDefaultMethods() {
		assertEquals("hi bob", MethodUtils.invokeMethod(new DefaultGreeter(), "greet", "bob"));
		assertEquals(Greeter.class, MethodUtils.getMethod(DefaultGreeter.class, "greet", String.class).getDeclaringClass());
		assertEquals(1, MethodUtils.getMethods(DefaultGreeter.class, MethodFilters.include(new String[] { "greet" })).size());
	}

	@Test
	public void invokeMethod_supportsArrayParameters() {
		assertEquals("[1, 2]", MethodUtils.invokeStaticMethod(java.util.Arrays.class, "toString", new Class<?>[] { int[].class },
				new Object[] { new int[] { 1, 2 } }));
	}

	public static class EmptyThenNamed implements NoMethods, ReflectionFixtures.Named {
		@Override
		public String name() {
			return "x";
		}
	}


	public static class GenericParent<T> {
		public String take(T value) {
			return "parent:" + value;
		}

		public Object make() {
			return "parent";
		}
	}

	public static class StringChild extends GenericParent<String> {
		@Override
		public String take(String value) {
			return "child:" + value;
		}

		@Override
		public String make() {
			return "child";
		}

		public String pick(int value) {
			return "int";
		}

		public String pick(Integer value) {
			return "Integer";
		}

		public String pick(long value) {
			return "long";
		}
	}

	@Test
	public void getMethod_prefersRealMethodOverBridgeOfGenericOrCovariantOverride() {
		Method take = MethodUtils.getMethod(StringChild.class, "take", String.class);
		assertFalse(take.isBridge());
		assertEquals(String.class, take.getParameterTypes()[0]);
		assertEquals("child:x", MethodUtils.invokeMethod(new StringChild(), "take", "x"));
		// an Object argument only matches the erased (bridge) signature, which still dispatches to the override
		assertEquals("child:y", MethodUtils.invokeMethod(new StringChild(), "take", new Class<?>[] { Object.class }, "y"));
		Method make = MethodUtils.getMethod(StringChild.class, "make");
		assertFalse(make.isBridge());
		assertEquals(String.class, make.getReturnType());
		assertEquals("child", MethodUtils.invokeMethod(new StringChild(), "make"));
	}

	@Test
	public void getMethod_resolvesPrimitiveAndWrapperOverloadsExactly() {
		assertEquals(int.class, MethodUtils.getMethod(StringChild.class, "pick", int.class).getParameterTypes()[0]);
		assertEquals(Integer.class, MethodUtils.getMethod(StringChild.class, "pick", Integer.class).getParameterTypes()[0]);
		assertEquals(long.class, MethodUtils.getMethod(StringChild.class, "pick", long.class).getParameterTypes()[0]);
		// runtime classes of arguments are wrappers, so the wrapper overload is chosen
		assertEquals("Integer", MethodUtils.invokeMethod(new StringChild(), "pick", 1));
		assertEquals("long", MethodUtils.invokeMethod(new StringChild(), "pick", 1L));
		// null cannot be passed to a primitive parameter
		assertEquals("Integer", MethodUtils.invokeMethod(new StringChild(), "pick", (Object) null));
	}
}
