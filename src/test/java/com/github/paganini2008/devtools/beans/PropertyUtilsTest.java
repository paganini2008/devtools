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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.beans.PropertyDescriptor;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.github.paganini2008.devtools.beans.TestBeans.Address;
import com.github.paganini2008.devtools.beans.TestBeans.Employee;
import com.github.paganini2008.devtools.beans.TestBeans.Failing;
import com.github.paganini2008.devtools.beans.TestBeans.Person;
import com.github.paganini2008.devtools.beans.TestBeans.PersonDto;
import com.github.paganini2008.devtools.reflection.ReflectionException;

/**
 * PropertyUtilsTest
 * 
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class PropertyUtilsTest {

	public interface Labeled {
		String getLabel();
	}

	public static class Box<T> {
		private T content;
		private int[] sizes;
		private Integer count;

		public T getContent() {
			return content;
		}

		public void setContent(T content) {
			this.content = content;
		}

		public int[] getSizes() {
			return sizes;
		}

		public void setSizes(int[] sizes) {
			this.sizes = sizes;
		}

		public Integer getCount() {
			return count;
		}

		public void setCount(Integer count) {
			this.count = count;
		}
	}

	public static class StringBox extends Box<String> {
	}

	private static Person tom() {
		Person p = new Person("Tom", 30);
		p.setEmail("tom@x.com");
		p.setAddress(new Address("Paris"));
		return p;
	}

	@Test
	public void getPropertyDescriptors_returnsCachedSortedPropertiesExcludingClass() {
		Map<String, PropertyDescriptor> map = PropertyUtils.getPropertyDescriptors(Person.class);
		assertEquals(Arrays.asList("address", "age", "email", "id", "level", "name"), Arrays.asList(map.keySet().toArray()));
		assertFalse(map.containsKey("class"));
		assertSame(map, PropertyUtils.getPropertyDescriptors(Person.class));
		assertThrows(UnsupportedOperationException.class, () -> map.put("x", null));

		Map<String, PropertyDescriptor> own = PropertyUtils.getPropertyDescriptors(Employee.class, Person.class);
		assertEquals(1, own.size());
		assertTrue(own.containsKey("salary"));
		assertEquals(7, PropertyUtils.getPropertyDescriptors(Employee.class).size());

		Map<String, PropertyDescriptor> filtered = PropertyUtils.getPropertyDescriptors(Person.class, null,
				PropertyFilters.includedProperties(new String[] { "name", "age" }));
		assertEquals(2, filtered.size());
		assertThrows(IllegalArgumentException.class, () -> PropertyUtils.getPropertyDescriptors(null));
		assertThrows(IllegalArgumentException.class, () -> PropertyUtils.getPropertyDescriptors(Person.class, Address.class));
		assertTrue(PropertyUtils.getPropertyDescriptors(Object.class).isEmpty());
		assertEquals(Arrays.asList("label"), Arrays.asList(PropertyUtils.getPropertyDescriptors(Labeled.class).keySet().toArray()));
	}

	@Test
	public void getPropertyDescriptor_returnsNullForUnknownProperty() {
		assertNotNull(PropertyUtils.getPropertyDescriptor(Person.class, "name"));
		assertNull(PropertyUtils.getPropertyDescriptor(Person.class, "missing"));
		assertTrue(PropertyUtils.hasProperty(Person.class, "age"));
		assertFalse(PropertyUtils.hasProperty(Person.class, "class"));
	}

	@Test
	public void getProperty_readsSimpleNestedAndFailsOnMissingOrBrokenGetter() {
		Person p = tom();
		assertEquals("Tom", PropertyUtils.getProperty(p, "name"));
		assertEquals(30, PropertyUtils.getProperty(p, "age"));
		assertEquals("Paris", PropertyUtils.getProperty(p, "address.city"));
		assertEquals("ID-Tom", PropertyUtils.getProperty(p, PropertyUtils.getPropertyDescriptor(Person.class, "id")));
		assertThrows(IllegalArgumentException.class, () -> PropertyUtils.getProperty(p, "missing"));
		assertThrows(IllegalArgumentException.class, () -> PropertyUtils.getProperty(null, "name"));
		assertThrows(IllegalArgumentException.class, () -> PropertyUtils.getProperty(p, ""));
		assertThrows(IllegalArgumentException.class, () -> PropertyUtils.getProperty(new Person(), "address.city"));
		BeanPropertyAccessException e = assertThrows(BeanPropertyAccessException.class,
				() -> PropertyUtils.getProperty(new Failing(), "value"));
		assertNotNull(e.getCause());
	}

	@Test
	public void setProperty_convertsValuesAndRejectsReadOnlyOrMissingProperties() {
		Person p = tom();
		assertTrue(PropertyUtils.setProperty(p, "name", "Jerry"));
		assertEquals("Jerry", p.getName());
		assertTrue(PropertyUtils.setProperty(p, "age", "41"));
		assertEquals(41, p.getAge());
		assertFalse(PropertyUtils.setProperty(p, "age", null));
		assertFalse(PropertyUtils.setProperty(p, "age", "not-a-number"));
		assertEquals(41, p.getAge());
		assertTrue(PropertyUtils.setProperty(p, "address.city", "Rome"));
		assertEquals("Rome", p.getAddress().getCity());
		assertTrue(PropertyUtils.setProperty(p, "email", null));
		assertNull(p.getEmail());
		// fallback to a setter whose parameter type does not match the getter
		assertTrue(PropertyUtils.setProperty(p, "level", "3"));
		assertEquals(30, p.getLevel());
		// read only property
		assertThrows(ReflectionException.class, () -> PropertyUtils.setProperty(p, "id", "x"));
		assertThrows(IllegalArgumentException.class, () -> PropertyUtils.setProperty(p, "id", null));
		assertThrows(IllegalArgumentException.class, () -> PropertyUtils.setProperty(p, "missing", "x"));
		assertThrows(IllegalArgumentException.class, () -> PropertyUtils.setProperty(null, "name", "x"));
		assertThrows(BeanPropertyAccessException.class, () -> PropertyUtils.setProperty(new Failing(), "value", "x"));
	}

	@Test
	public void populate_copiesMapEntriesHonoringOverwriteFlagAndFilter() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("name", "Jerry");
		map.put("age", "20");
		map.put("unknown", "ignored");
		map.put("email", "jerry@x.com");
		Person p = new Person();
		PropertyUtils.populate(p, map);
		assertEquals("Jerry", p.getName());
		assertEquals(20, p.getAge());
		assertEquals("jerry@x.com", p.getEmail());

		Person q = tom();
		PropertyUtils.populate(q, map, null, false);
		assertEquals("Tom", q.getName());
		assertEquals("tom@x.com", q.getEmail());

		Person r = new Person();
		PropertyUtils.populate(r, map, PropertyFilters.excludedProperties(new String[] { "email" }));
		assertEquals("Jerry", r.getName());
		assertNull(r.getEmail());
	}

	@Test
	public void copyProperties_copiesOnlyMatchingWritablePropertiesHonoringFilter() {
		Person source = tom();
		Person target = new Person();
		PropertyUtils.copyProperties(source, target);
		assertEquals("Tom", target.getName());
		assertEquals(30, target.getAge());
		assertSame(source.getAddress(), target.getAddress());

		// filter on source properties is honored
		Person filtered = new Person();
		filtered.setEmail("keep@x.com");
		PropertyUtils.copyProperties(source, filtered, PropertyFilters.includedProperties(new String[] { "name" }));
		assertEquals("Tom", filtered.getName());
		assertEquals(0, filtered.getAge());
		assertEquals("keep@x.com", filtered.getEmail());

		// not overwrite
		Person existing = new Person();
		existing.setName("Old");
		PropertyUtils.copyProperties(source, existing, null, false);
		assertEquals("Old", existing.getName());
		assertEquals("tom@x.com", existing.getEmail());

		// different types: only common properties are copied, read-only and missing ones are skipped
		PersonDto dto = new PersonDto();
		PropertyUtils.copyProperties(source, dto);
		assertEquals("Tom", dto.getName());
		assertEquals("30", dto.getAge());
		assertEquals("keep", dto.getNickname());
		assertEquals("tom@x.com", dto.getEmail());
		assertNull(dto.getCity());

		// subclass -> parent
		Employee emp = new Employee();
		emp.setName("Emp");
		emp.setSalary(1.5d);
		Person parent = new Person();
		PropertyUtils.copyProperties(emp, parent);
		assertEquals("Emp", parent.getName());

		// stop class
		Employee emp2 = new Employee();
		PropertyUtils.copyProperties(emp, Person.class, emp2, null, true);
		assertEquals(1.5d, emp2.getSalary());
		assertNull(emp2.getName());

		// getter failure on source is ignored
		Failing failing = new Failing();
		PropertyUtils.copyProperties(failing, new Failing());
		assertThrows(IllegalArgumentException.class, () -> PropertyUtils.copyProperties(null, new Person()));
		assertThrows(IllegalArgumentException.class, () -> PropertyUtils.copyProperties(source, null));
	}

	@Test
	public void copyProperties_withMapping_usesPropertyMapperAndExcludedProperty() {
		Person source = tom();
		PersonDto dto = new PersonDto();
		PropertyUtils.copyProperties(source, null, dto, null, true, true);
		assertEquals("Tom", dto.getName());
		assertEquals("Paris", dto.getCity());
		assertEquals("untouched", dto.getEmail());
		assertEquals("tom@x.com", dto.getPhone());

		Person noAddress = new Person("Ann", 1);
		PersonDto dto2 = new PersonDto();
		PropertyUtils.copyProperties(noAddress, null, dto2, null, true, true);
		assertEquals("Ann", dto2.getName());
		assertNull(dto2.getCity());
	}

	@Test
	public void convertToMap_readsAllPropertiesHonoringStopClassAndFilter() {
		Map<String, Object> map = PropertyUtils.convertToMap(tom());
		assertEquals("Tom", map.get("name"));
		assertEquals(30, map.get("age"));
		assertEquals("ID-Tom", map.get("id"));
		assertEquals(6, map.size());
		Employee emp = new Employee();
		emp.setSalary(2d);
		Map<String, Object> own = PropertyUtils.convertToMap(emp, Person.class);
		assertEquals(1, own.size());
		assertEquals(2d, own.get("salary"));
		Map<String, Object> filtered = PropertyUtils.convertToMap(tom(), null, PropertyFilters.matches("a", com.github.paganini2008.devtools.MatchMode.START));
		assertEquals(Arrays.asList("address", "age"), Arrays.asList(filtered.keySet().toArray()));
	}

	@Test
	public void setProperty_handlesGenericArrayAndWrapperProperties() {
		StringBox box = new StringBox();
		assertTrue(PropertyUtils.setProperty(box, "content", "text"));
		assertEquals("text", box.getContent());
		assertTrue(PropertyUtils.setProperty(box, "sizes", "1,2,3"));
		assertEquals(3, box.getSizes().length);
		assertEquals(3, box.getSizes()[2]);
		assertTrue(PropertyUtils.setProperty(box, "count", "5"));
		assertEquals(Integer.valueOf(5), box.getCount());
		// wrapper property accepts null, primitive property does not
		assertTrue(PropertyUtils.setProperty(box, "count", null));
		assertNull(box.getCount());
		Map<String, Object> map = PropertyUtils.convertToMap(box);
		assertEquals("text", map.get("content"));
		StringBox copy = new StringBox();
		PropertyUtils.populate(copy, map);
		assertEquals("text", copy.getContent());
		assertSame(box.getSizes(), copy.getSizes());
	}

}
