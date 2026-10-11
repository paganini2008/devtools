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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Constructor;

import org.junit.jupiter.api.Test;

import com.github.paganini2008.devtools.reflection.ReflectionFixtures.Child;

/**
 * ConstructorUtilsTest
 * 
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class ConstructorUtilsTest {

	public static abstract class Abstract {
		public Abstract() {
		}
	}

	public static class Failing {
		public Failing() {
			throw new IllegalStateException("boom");
		}
	}

	@Test
	public void invokeConstructor_choosesBestMatchingConstructor() {
		assertEquals("child", ConstructorUtils.invokeConstructor(Child.class).getTitle());
		assertEquals("child", ConstructorUtils.invokeConstructor(Child.class, (Object[]) null).getTitle());
		assertEquals("a", ConstructorUtils.invokeConstructor(Child.class, "a").getTitle());
		assertEquals("object:1.5", ConstructorUtils.invokeConstructor(Child.class, 1.5d).getTitle());
		// private constructor with primitive parameter receives a wrapper argument
		assertEquals(9L, ConstructorUtils.invokeConstructor(Child.class, 9).getNumber());
		Child c = ConstructorUtils.invokeConstructor(Child.class, "b", 3L);
		assertEquals("b", c.getTitle());
		assertEquals(3L, c.getNumber());
		// widening is not supported between wrapper types: Integer is not assignable to long
		assertThrows(ReflectionException.class, () -> ConstructorUtils.invokeConstructor(Child.class, "b", 3));
	}

	@Test
	public void invokeConstructor_acceptsNullArguments() {
		// null matches the most specific reference type
		assertNull(ConstructorUtils.invokeConstructor(Child.class, (Object) null).getTitle());
		Child c = ConstructorUtils.invokeConstructor(Child.class, new Object[] { null, 4L });
		assertNull(c.getTitle());
		assertEquals(4L, c.getNumber());
	}

	@Test
	public void invokeConstructor_wrapsFailures() {
		ReflectionException e = assertThrows(ReflectionException.class, () -> ConstructorUtils.invokeConstructor(Failing.class));
		assertTrue(e.getMessage().contains("Failing"));
		assertTrue(e.getCause().getCause() instanceof IllegalStateException);
		assertThrows(ReflectionException.class, () -> ConstructorUtils.invokeConstructor(Abstract.class));
		Constructor<Child> ctor = ConstructorUtils.getConstructor(Child.class, String.class);
		assertThrows(ReflectionException.class, () -> ConstructorUtils.invokeConstructor(ctor, 1));
		assertEquals("child", ConstructorUtils.invokeConstructor(ConstructorUtils.getConstructor(Child.class), (Object[]) null).getTitle());
	}

	@Test
	public void getConstructor_resolvesExactAssignableAndMissing() {
		assertEquals(String.class, ConstructorUtils.getConstructor(Child.class, String.class).getParameterTypes()[0]);
		assertNotNull(ConstructorUtils.getConstructor(Child.class, (Class<?>[]) null));
		assertEquals(int.class, ConstructorUtils.getConstructor(Child.class, Integer.class).getParameterTypes()[0]);
		assertEquals(Object.class, ConstructorUtils.getConstructor(Child.class, Double.class).getParameterTypes()[0]);
		assertNull(ConstructorUtils.getConstructorIfAbsent(Child.class, Thread.class, Thread.class));
		assertNotNull(ConstructorUtils.getConstructorIfAbsent(Child.class));
		assertThrows(IllegalArgumentException.class, () -> ConstructorUtils.getConstructor(null));
		ReflectionException e = assertThrows(ReflectionException.class, () -> ConstructorUtils.getConstructor(Child.class, Thread.class, Thread.class));
		assertTrue(e.getCause() instanceof NoSuchMethodException);
	}

}
