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
package com.github.paganini2008.devtools.collection;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.github.paganini2008.devtools.CaseFormats;
import com.github.paganini2008.devtools.MissingKeyException;

/**
 * TupleImplTest
 * 
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class TupleImplTest {

	public static class User {

		private String userName;
		private Integer age;

		public String getUserName() {
			return userName;
		}

		public void setUserName(String userName) {
			this.userName = userName;
		}

		public Integer getAge() {
			return age;
		}

		public void setAge(Integer age) {
			this.age = age;
		}
	}

	@Test
	public void testTuple() {
		Tuple tuple = Tuple.newTuple();
		assertTrue(tuple.isEmpty());
		tuple.set("name", "fred");
		tuple.set("age", "18");
		assertEquals(2, tuple.size());
		assertEquals("fred", tuple.get("name"));
		assertEquals("fred", tuple.getProperty("name"));
		assertEquals("x", tuple.getProperty("missing", "x"));
		assertEquals(Integer.valueOf(18), tuple.getProperty("age", Integer.class));
		assertEquals(Integer.valueOf(1), tuple.getProperty("missing", Integer.class, 1));
		assertNull(tuple.getProperty("missing", Integer.class));
		assertEquals(Integer.valueOf(18), tuple.getRequiredProperty("age", Integer.class));
		assertThrows(MissingKeyException.class, () -> tuple.getRequiredProperty("missing", Integer.class));
		assertArrayEquals(new String[] { "name", "age" }, tuple.keys());
		assertArrayEquals(new Object[] { "fred", "18" }, tuple.toValues());
		assertEquals("{name=fred, age=18}", tuple.toMap().toString());
	}

	@Test
	public void testCaseFormatAndBean() {
		Map<String, Object> kwargs = new LinkedHashMap<String, Object>();
		kwargs.put("user_name", "fred");
		kwargs.put("age", 18);
		Tuple tuple = Tuple.wrap(kwargs, CaseFormats.LOWER_CAMEL);
		assertEquals("fred", tuple.get("userName"));
		User user = tuple.toBean(User.class);
		assertEquals("fred", user.getUserName());
		assertEquals(Integer.valueOf(18), user.getAge());
		tuple.append(null);
		assertEquals(2, tuple.size());
		Tuple wrapped = Tuple.wrap(kwargs);
		assertEquals("fred", wrapped.get("user_name"));
		assertNull(wrapped.get("userName"));
	}


	public static class Person {
		private Integer age;
		private String userName;

		public Integer getAge() {
			return age;
		}

		public void setAge(Integer age) {
			this.age = age;
		}

		public String getUserName() {
			return userName;
		}

		public void setUserName(String userName) {
			this.userName = userName;
		}
	}

	@Test
	public void defaultTuple_ignoresCase() {
		Tuple tuple = Tuple.newTuple();
		tuple.set("Name", "x");
		assertEquals("x", tuple.get("NAME"));
		assertEquals("x", tuple.get("name"));
		assertNull(tuple.get("na_me"));
	}

	@Test
	public void upperCaseColumnLabels_canBeReadInAnyCase_andOriginalKeysAreKept() {
		// JdbcUtils builds tuples with LOWER_CAMEL
		Tuple tuple = Tuple.newTuple(CaseFormats.LOWER_CAMEL);
		tuple.set("AGE", 30);
		tuple.set("USER_NAME", "alice");
		assertEquals(30, tuple.get("AGE"));
		assertEquals(30, tuple.get("age"));
		assertEquals(30, tuple.get("Age"));
		assertEquals("alice", tuple.get("USER_NAME"));
		assertEquals("alice", tuple.get("userName"));
		assertEquals("alice", tuple.get("username"));
		assertArrayEquals(new String[] { "AGE", "USER_NAME" }, tuple.keys());
		assertEquals(2, tuple.size());

		tuple.set("age", 31);
		assertEquals(2, tuple.size());
		assertEquals(31, tuple.get("AGE"));

		Person person = tuple.toBean(Person.class);
		assertEquals(Integer.valueOf(31), person.getAge());
		assertEquals("alice", person.getUserName());
	}
}
