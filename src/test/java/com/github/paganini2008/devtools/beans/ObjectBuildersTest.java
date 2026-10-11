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
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.github.paganini2008.devtools.beans.TestBeans.Address;
import com.github.paganini2008.devtools.beans.TestBeans.Employee;
import com.github.paganini2008.devtools.beans.TestBeans.Failing;
import com.github.paganini2008.devtools.beans.TestBeans.NoProperty;
import com.github.paganini2008.devtools.beans.TestBeans.Person;
import com.github.paganini2008.devtools.beans.ToStringBuilder.PrintStyle;

/**
 * ObjectBuildersTest
 * 
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class ObjectBuildersTest {

	public static class Simple {
		private String name;
		private int age;
		private String secret;

		public Simple(String name, int age) {
			this.name = name;
			this.age = age;
		}

		public String getName() {
			return name;
		}

		public int getAge() {
			return age;
		}

		public void setSecret(String secret) {
			this.secret = secret;
		}

		public synchronized String getSynced() {
			return "s" + (secret == null ? "" : secret);
		}
	}

	@Test
	public void reflectionEquals_comparesReadableProperties() {
		Person a = new Person("Tom", 30);
		Person b = new Person("Tom", 30);
		Person c = new Person("Tom", 31);
		assertTrue(EqualsBuilder.reflectionEquals(a, a));
		assertTrue(EqualsBuilder.reflectionEquals(a, b));
		assertFalse(EqualsBuilder.reflectionEquals(a, c));
		assertFalse(EqualsBuilder.reflectionEquals(a, null));
		assertFalse(EqualsBuilder.reflectionEquals(null, a));
		assertFalse(EqualsBuilder.reflectionEquals(a, new Employee()));
		assertTrue(EqualsBuilder.reflectionEquals(a, c, PropertyFilters.excludedProperties(new String[] { "age" })));
		Employee e1 = new Employee();
		Employee e2 = new Employee();
		e1.setSalary(1);
		e1.setName("x");
		e2.setSalary(1);
		assertTrue(EqualsBuilder.reflectionEquals(e1, e2, Person.class, null));
		assertFalse(EqualsBuilder.reflectionEquals(e1, e2));
		// no properties: falls back to equals()
		assertTrue(EqualsBuilder.reflectionEquals(new NoProperty(1), new NoProperty(1)));
		assertFalse(EqualsBuilder.reflectionEquals(new NoProperty(1), new NoProperty(2)));
		assertThrows(BeanPropertyAccessException.class, () -> EqualsBuilder.reflectionEquals(new Failing(), new Failing()));
	}

	@Test
	public void reflectionHashCode_isStableForEqualBeans() {
		Person a = new Person("Tom", 30);
		Person b = new Person("Tom", 30);
		assertEquals(HashCodeBuilder.reflectionHashCode(a), HashCodeBuilder.reflectionHashCode(b));
		assertNotEquals(HashCodeBuilder.reflectionHashCode(a), HashCodeBuilder.reflectionHashCode(new Person("Tom", 31)));
		int expected = 17;
		expected += 31 * expected + Integer.valueOf(30).hashCode();
		expected += 31 * expected + "Tom".hashCode();
		assertEquals(expected,
				HashCodeBuilder.reflectionHashCode(a, PropertyFilters.includedProperties(new String[] { "name", "age" })));
		assertEquals(0, HashCodeBuilder.reflectionHashCode(new NoProperty(3)));
		assertEquals(0, HashCodeBuilder.reflectionHashCode(new Employee(), Person.class,
				PropertyFilters.includedProperties(new String[] { "none" })));
		assertThrows(IllegalArgumentException.class, () -> HashCodeBuilder.reflectionHashCode(null));
		assertThrows(BeanPropertyAccessException.class, () -> HashCodeBuilder.reflectionHashCode(new Failing()));
	}

	@Test
	public void reflectionToString_rendersAllPrintStyles() {
		Simple s = new Simple("Tom", 30);
		assertEquals("{age: 30, name: Tom, synced: s}", ToStringBuilder.reflectionToString(s));
		assertEquals("{age: 30, name: Tom, synced: s}", ToStringBuilder.reflectionToString(s, PrintStyle.DEFAULT));
		assertEquals("{30,Tom,s}", ToStringBuilder.reflectionToString(s, PrintStyle.PLAIN));
		assertEquals("{name: Tom}", ToStringBuilder.reflectionToString(s, new String[] { "age", "synced" }));
		assertEquals("{age: 30}", ToStringBuilder.reflectionToString(s, PropertyFilters.includedProperties(new String[] { "age" })));
		String prefix = Simple.class.getName() + "@" + s.hashCode();
		assertEquals(prefix + "{age: 30, name: Tom, synced: s}", ToStringBuilder.reflectionToString(s, PrintStyle.SINGLE_LINE));
		String nl = System.getProperty("line.separator");
		assertEquals(prefix + " {" + nl + "    age: 30" + nl + "    name: Tom" + nl + "    synced: s" + nl + "}",
				ToStringBuilder.reflectionToString(s, PrintStyle.MULTI_LINE));
		assertEquals("", ToStringBuilder.reflectionToString(s, PropertyFilters.includedProperties(new String[] { "none" })));
		assertEquals("{age: 30}", ToStringBuilder.reflectionToString(s, PropertyFilters.includedProperties(new String[] { "age" }),
				(ToStringStyle) null));
		Employee e = new Employee();
		e.setSalary(2.5d);
		assertEquals("{salary: 2.5}", ToStringBuilder.reflectionToString(e, Person.class, null, (ToStringStyle) null));
		assertEquals("[address]", ToStringBuilder.reflectionToString(new Person(), PropertyFilters.includedProperties(new String[] { "address" }),
				(bean, descriptors) -> "[" + descriptors.iterator().next().getName() + "]"));
		assertThrows(IllegalArgumentException.class, () -> ToStringBuilder.reflectionToString(null));
	}

	@Test
	public void toStringStyles_renderEmptyPropertyList() {
		Address a = new Address("x");
		assertEquals("{}", new ToStringStyles.PlainToStringStyle().toString(a, java.util.Collections.emptyList()));
		assertEquals("{}", new ToStringStyles.DefaultToSringStyle().toString(a, null));
		assertEquals(Address.class.getName() + "@" + a.hashCode() + "{}", new ToStringStyles.SingleLineToSrtingStyle().toString(a, null));
		assertEquals(Address.class.getName() + "@" + a.hashCode() + " {}", new ToStringStyles.MultiLineToStringStyle().toString(a, null));
	}

	@Test
	public void printStyle_lookupIsCaseInsensitive() {
		assertSame(PrintStyle.PLAIN, PrintStyle.get("plain"));
		assertSame(PrintStyle.MULTI_LINE, PrintStyle.get("MULTI_LINE"));
		assertNull(PrintStyle.get("unknown"));
		assertTrue(PrintStyle.DEFAULT.getStyle() instanceof ToStringStyles.DefaultToSringStyle);
	}

}
