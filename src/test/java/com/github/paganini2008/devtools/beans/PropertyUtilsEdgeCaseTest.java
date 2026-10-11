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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import com.github.paganini2008.devtools.beans.TestBeans.Person;

/**
 * Edge cases of {@link PropertyUtils} and {@link BeanUtils}: bridge methods of generic sub classes, mapping annotations
 * inherited from super classes, primitive/wrapper conversions, null maps and concurrent descriptor lookups.
 *
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class PropertyUtilsEdgeCaseTest {

	public static class Holder<T> {
		private T value;

		public T getValue() {
			return value;
		}

		public void setValue(T value) {
			this.value = value;
		}
	}

	/**
	 * Overrides the generic accessors, so the class also has synthetic bridge methods taking/returning Object.
	 */
	public static class IntHolder extends Holder<Integer> {

		@Override
		public Integer getValue() {
			return super.getValue();
		}

		@Override
		public void setValue(Integer value) {
			super.setValue(value);
		}
	}

	public static class Base {
		@ExcludedProperty
		private String secret = "base-secret";
		@PropertyMapper("name")
		private String alias;

		public String getSecret() {
			return secret;
		}

		public void setSecret(String secret) {
			this.secret = secret;
		}

		public String getAlias() {
			return alias;
		}

		public void setAlias(String alias) {
			this.alias = alias;
		}
	}

	public static class Derived extends Base {
		private int age;

		public int getAge() {
			return age;
		}

		public void setAge(int age) {
			this.age = age;
		}
	}

	public static class Source {
		private String name = "Tom";
		private String secret = "leaked";
		private Integer age;

		public String getName() {
			return name;
		}

		public String getSecret() {
			return secret;
		}

		public Integer getAge() {
			return age;
		}

		public void setAge(Integer age) {
			this.age = age;
		}
	}

	@Test
	public void setProperty_onGenericSubclassWithBridgeMethods_convertsToTheRealType() {
		IntHolder holder = new IntHolder();
		PropertyDescriptor pd = PropertyUtils.getPropertyDescriptor(IntHolder.class, "value");
		assertFalse(pd.getWriteMethod().isBridge(), "the bridge setter must not be chosen");
		assertTrue(PropertyUtils.setProperty(holder, "value", "42"));
		assertEquals(Integer.valueOf(42), holder.getValue());
		assertTrue(PropertyUtils.setProperty(holder, "value", 7L));
		assertEquals(Integer.valueOf(7), holder.getValue());
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("value", "9");
		PropertyUtils.populate(holder, map);
		assertEquals(Integer.valueOf(9), holder.getValue());
		assertEquals(Integer.valueOf(9), PropertyUtils.convertToMap(holder).get("value"));
	}

	@Test
	public void copyProperties_withMapping_honorsAnnotationsOnSuperClassFields() {
		Source source = new Source();
		source.setAge(5);
		Derived target = new Derived();
		PropertyUtils.copyProperties(source, null, target, null, true, true);
		assertEquals("base-secret", target.getSecret(), "@ExcludedProperty declared in the super class must be honored");
		assertEquals("Tom", target.getAlias(), "@PropertyMapper declared in the super class must be honored");
		assertEquals(5, target.getAge());

		// without mapping the annotations are ignored
		Derived plain = new Derived();
		PropertyUtils.copyProperties(source, plain);
		assertEquals("leaked", plain.getSecret());
		assertNull(plain.getAlias());
	}

	@Test
	public void copyProperties_nullWrapperToPrimitive_keepsDefaultValue() {
		Source source = new Source();
		Derived target = new Derived();
		target.setAge(3);
		PropertyUtils.copyProperties(source, target);
		assertEquals(3, target.getAge(), "a null wrapper value must not reset or break a primitive property");

		Person person = new Person("Ann", 3);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("age", null);
		PropertyUtils.populate(person, map);
		assertEquals(3, person.getAge());
	}

	@Test
	public void populateAndConvertAsBean_nullMap_isNoOp() {
		Person person = new Person("Ann", 3);
		PropertyUtils.populate(person, null);
		PropertyUtils.populate(person, null, null, false);
		assertEquals("Ann", person.getName());
		Person created = BeanUtils.convertAsBean(null, Person.class);
		assertNull(created.getName());
		assertEquals(0, created.getAge());
	}

	@Test
	@Timeout(10)
	public void getPropertyDescriptors_concurrentLookupsReturnOneCachedInstance() throws Exception {
		final int threads = 8;
		final CountDownLatch start = new CountDownLatch(1);
		final Set<Object> seen = Collections.synchronizedSet(Collections.newSetFromMap(new IdentityHashMap<Object, Boolean>()));
		ExecutorService executor = Executors.newFixedThreadPool(threads);
		try {
			List<Future<?>> futures = new ArrayList<Future<?>>();
			for (int i = 0; i < threads; i++) {
				futures.add(executor.submit(() -> {
					start.await();
					for (int j = 0; j < 200; j++) {
						Map<String, PropertyDescriptor> map = PropertyUtils.getPropertyDescriptors(Derived.class);
						assertEquals(3, map.size());
						if (j == 199) {
							seen.add(map);
						}
						PropertyUtils.getPropertyDescriptors(Derived.class, Base.class);
					}
					return null;
				}));
			}
			start.countDown();
			for (Future<?> f : futures) {
				f.get(5, TimeUnit.SECONDS);
			}
		} finally {
			executor.shutdownNow();
		}
		assertEquals(1, seen.size(), "after warm-up all threads must share the cached descriptors");
		assertSame(seen.iterator().next(), PropertyUtils.getPropertyDescriptors(Derived.class));
	}
}
