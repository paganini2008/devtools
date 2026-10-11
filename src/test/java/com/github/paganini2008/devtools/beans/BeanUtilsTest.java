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
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.github.paganini2008.devtools.beans.TestBeans.Address;
import com.github.paganini2008.devtools.beans.TestBeans.Exploding;
import com.github.paganini2008.devtools.beans.TestBeans.Person;
import com.github.paganini2008.devtools.beans.TestBeans.PersonDto;

/**
 * BeanUtilsTest
 * 
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class BeanUtilsTest {

	private static Person tom() {
		Person p = new Person("Tom", 30);
		p.setAddress(new Address("Paris"));
		return p;
	}

	@Test
	public void copy_createsNewInstanceWithCopiedProperties() {
		Person source = tom();
		Person copy = BeanUtils.copy(source);
		assertNotSame(source, copy);
		assertEquals("Tom", copy.getName());
		assertEquals(30, copy.getAge());

		Person partial = BeanUtils.copy(source, PropertyFilters.includedProperties(new String[] { "age" }));
		assertNull(partial.getName());
		assertEquals(30, partial.getAge());

		PersonDto dto = BeanUtils.copy(source, PersonDto.class, null);
		assertEquals("30", dto.getAge());

		Person target = new Person();
		BeanUtils.copyProperties(source, target);
		assertEquals("Tom", target.getName());
	}

	@Test
	public void getProperty_convertsToRequiredTypeAndFallsBackToDefault() {
		Person p = tom();
		BeanUtils.setProperty(p, "age", "12");
		assertEquals(12, p.getAge());
		// errors are swallowed
		BeanUtils.setProperty(p, "missing", "x");
		BeanUtils.setProperty(p, "id", "x");

		assertEquals("Tom", BeanUtils.getProperty(p, "name", String.class));
		assertEquals("12", BeanUtils.getProperty(p, "age", String.class));
		assertEquals(Long.valueOf(12L), BeanUtils.getProperty(p, "age", Long.class));
		assertEquals(Integer.valueOf(12), BeanUtils.getProperty(p, "age", int.class));
		assertEquals(Integer.valueOf(12), BeanUtils.getProperty(p, "age", null));
		assertEquals("none", BeanUtils.getProperty(p, "email", String.class, "none"));
		assertEquals(Integer.valueOf(-1), BeanUtils.getProperty(p, "name", Integer.class, -1));
		assertNull(BeanUtils.getProperty(p, "email", String.class));
	}

	@Test
	public void convertAsBean_setsWritablePropertiesFromMap() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("name", "Jerry");
		map.put("age", "18");
		map.put("id", "ignored");
		map.put("unknown", 1);
		Person p = BeanUtils.convertAsBean(map, Person.class);
		assertEquals("Jerry", p.getName());
		assertEquals(18, p.getAge());
		assertNull(p.getEmail());
	}

	@Test
	public void instantiate_resolvesConstructorsAndWrapsFailures() {
		Person p = BeanUtils.instantiate(Person.class.getName());
		assertNull(p.getName());
		Person q = BeanUtils.instantiate(Person.class.getName(), "Ann", 3);
		assertEquals("Ann", q.getName());
		assertEquals(3, q.getAge());
		Person r = BeanUtils.instantiate(Person.class.getName(), getClass().getClassLoader(), "Bob", 4);
		assertEquals("Bob", r.getName());
		Person s = BeanUtils.instantiate(Person.class, (Object[]) null);
		assertNull(s.getName());
		Person t = BeanUtils.instantiate(Person.class, null, 5);
		assertNull(t.getName());
		assertEquals(5, t.getAge());

		BeanInstantiationException e = assertThrows(BeanInstantiationException.class, () -> BeanUtils.instantiate("no.such.Clazz"));
		assertTrue(e.getCause() instanceof ClassNotFoundException);
		assertThrows(BeanInstantiationException.class, () -> BeanUtils.instantiate(Exploding.class));
		assertThrows(BeanInstantiationException.class, () -> BeanUtils.instantiate(Person.class, "a", "b", "c"));
	}

	@Test
	public void exceptions_keepMessageAndCause() {
		Throwable cause = new RuntimeException("c");
		assertEquals("m", new BeanInstantiationException("m").getMessage());
		assertSame(cause, new BeanInstantiationException("m", cause).getCause());
		assertSame(cause, new BeanInstantiationException(cause).getCause());
		assertEquals("m", new BeanPropertyAccessException("m").getMessage());
		assertSame(cause, new BeanPropertyAccessException("m", cause).getCause());
		assertSame(cause, new BeanPropertyAccessException(cause).getCause());
	}

}
