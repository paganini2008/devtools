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
package com.github.paganini2008.devtools.beans;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import com.github.paganini2008.devtools.reflection.ReflectionException;

/**
 * PropertyUtilsGetterTest
 * <p>
 * The getter path of {@link PropertyUtils#getProperty(Object, String)} and
 * {@link BeanUtils#getProperty(Object, String, Class)}: messages and
 * exception chains, accessibility, conversion, and its cost (it is called for
 * every element and every comparison of an in-memory query).
 * 
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class PropertyUtilsGetterTest {

	public static class Bean {

		private Integer value = 7;

		public Integer getValue() {
			return value;
		}

		public void setValue(Integer value) {
			this.value = value;
		}

		public String getBroken() {
			throw new IllegalStateException("broken getter");
		}

		public void setWriteOnly(String s) {
		}
	}

	private static class Hidden {

		@SuppressWarnings("unused")
		public String getSecret() {
			return "secret";
		}
	}

	@Test
	public void throwingGetterIsWrappedLikeBefore() {
		BeanPropertyAccessException e = assertThrows(BeanPropertyAccessException.class, () -> PropertyUtils.getProperty(new Bean(), "broken"));
		assertTrue(e.getMessage().contains("getBroken"));
		assertTrue(e.getMessage().contains(Bean.class.getName()));
		// the same chain as MethodUtils#invokeMethod produced
		assertTrue(e.getCause() instanceof ReflectionException);
		assertEquals("Failed to invoke method: getBroken", e.getCause().getMessage());
		assertTrue(e.getCause().getCause() instanceof InvocationTargetException);
		assertEquals("broken getter", e.getCause().getCause().getCause().getMessage());
	}

	@Test
	public void getterOfNonPublicClassIsAccessible() {
		assertEquals("secret", PropertyUtils.getProperty(new Hidden(), "secret"));
		assertEquals("secret", PropertyUtils.getProperty(new Hidden(), "secret"));
	}

	@Test
	public void argumentChecksKeepTheirMessages() {
		assertEquals("Source instance must not be null.",
				assertThrows(IllegalArgumentException.class, () -> PropertyUtils.getProperty(null, "value")).getMessage());
		assertEquals("PropertyName must not be null or empty.",
				assertThrows(IllegalArgumentException.class, () -> PropertyUtils.getProperty(new Bean(), " ")).getMessage());
		assertEquals("PropertyName must not be null or empty.",
				assertThrows(IllegalArgumentException.class, () -> PropertyUtils.getProperty(new Bean(), (String) null)).getMessage());
		assertEquals("Property 'nothing' is not found in class '" + Bean.class.getName() + "'.",
				assertThrows(IllegalArgumentException.class, () -> PropertyUtils.getProperty(new Bean(), "nothing")).getMessage());
		assertEquals("Property descriptor must not be null.",
				assertThrows(IllegalArgumentException.class, () -> PropertyUtils.getProperty(new Bean(), (PropertyDescriptor) null)).getMessage());
		assertEquals("Source instance must not be null.", assertThrows(IllegalArgumentException.class,
				() -> PropertyUtils.getProperty(null, PropertyUtils.getPropertyDescriptor(Bean.class, "value"))).getMessage());
		assertEquals("Cannot find the getter of 'writeOnly'.", assertThrows(IllegalArgumentException.class,
				() -> PropertyUtils.getProperty(new Bean(), PropertyUtils.getPropertyDescriptor(Bean.class, "writeOnly"))).getMessage());
		assertEquals("Bean class must not be null.",
				assertThrows(IllegalArgumentException.class, () -> PropertyUtils.getPropertyDescriptors(null, null)).getMessage());
	}

	@Test
	public void requiredTypeConversion() {
		Bean bean = new Bean();
		Integer same = bean.getValue();
		assertSame(same, BeanUtils.getProperty(bean, "value", Integer.class));
		assertSame(same, BeanUtils.getProperty(bean, "value", Number.class));
		assertSame(same, BeanUtils.getProperty(bean, "value", Object.class));
		assertEquals(Long.valueOf(7L), BeanUtils.getProperty(bean, "value", Long.class));
		assertEquals(new BigDecimal("7"), BeanUtils.getProperty(bean, "value", BigDecimal.class));
		assertEquals("7", BeanUtils.getProperty(bean, "value", String.class));
		// a primitive type is converted to its wrapper
		assertEquals(Integer.valueOf(7), BeanUtils.getProperty(bean, "value", int.class));
		// no required type: the raw value
		assertSame(same, BeanUtils.getProperty(bean, "value", null));
		// null value: the default value
		bean.setValue(null);
		assertNull(BeanUtils.getProperty(bean, "value", Long.class));
		assertEquals(Long.valueOf(3L), BeanUtils.getProperty(bean, "value", Long.class, 3L));
	}

	@Test
	@Timeout(value = 30, unit = TimeUnit.SECONDS)
	public void readingAPropertyIsCheap() {
		Bean bean = new Bean();
		for (int i = 0; i < 20000; i++) {
			PropertyUtils.getProperty(bean, "value");
		}
		long begin = System.nanoTime();
		for (int i = 0; i < 500000; i++) {
			PropertyUtils.getProperty(bean, "value");
		}
		long elapsed = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - begin);
		// about 6 microseconds per read when every check created an exception, well below one now
		assertTrue(elapsed < 2500, "500000 reads took " + elapsed + " ms");
	}

}
