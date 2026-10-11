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
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

/**
 * SystemPropertyUtilsTest
 *
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class SystemPropertyUtilsTest {

	private static final String KEY = "devtools.test.SystemPropertyUtilsTest";
	private static final String MISSING = "devtools.test.SystemPropertyUtilsTest.missing";

	@AfterEach
	public void cleanup() {
		System.clearProperty(KEY);
	}

	@Test
	public void testString() {
		assertNull(SystemPropertyUtils.getString(MISSING));
		assertEquals("def", SystemPropertyUtils.getString(MISSING, "def"));
		System.setProperty(KEY, "abc");
		assertEquals("abc", SystemPropertyUtils.getString(KEY));
		System.setProperty(KEY, "  ");
		assertEquals("def", SystemPropertyUtils.getString(KEY, "def"));
	}

	@Test
	public void testNumbers() {
		System.setProperty(KEY, "12");
		assertEquals(Byte.valueOf((byte) 12), SystemPropertyUtils.getByte(KEY));
		assertEquals(Short.valueOf((short) 12), SystemPropertyUtils.getShort(KEY));
		assertEquals(Integer.valueOf(12), SystemPropertyUtils.getInteger(KEY));
		assertEquals(Long.valueOf(12L), SystemPropertyUtils.getLong(KEY));
		assertEquals(Float.valueOf(12F), SystemPropertyUtils.getFloat(KEY));
		assertEquals(Double.valueOf(12D), SystemPropertyUtils.getDouble(KEY));
		assertNull(SystemPropertyUtils.getByte(MISSING));
		assertNull(SystemPropertyUtils.getShort(MISSING));
		assertNull(SystemPropertyUtils.getInteger(MISSING));
		assertNull(SystemPropertyUtils.getLong(MISSING));
		assertNull(SystemPropertyUtils.getFloat(MISSING));
		assertNull(SystemPropertyUtils.getDouble(MISSING));
		assertEquals(Integer.valueOf(7), SystemPropertyUtils.getInteger(MISSING, 7));
	}

	@Test
	public void testBooleanAndCharacter() {
		System.setProperty(KEY, "true");
		assertEquals(Boolean.TRUE, SystemPropertyUtils.getBoolean(KEY));
		assertNull(SystemPropertyUtils.getBoolean(MISSING));
		assertEquals(Boolean.FALSE, SystemPropertyUtils.getBoolean(MISSING, false));
		assertEquals(Character.valueOf('t'), SystemPropertyUtils.getCharacter(KEY));
		assertNull(SystemPropertyUtils.getCharacter(MISSING));
		assertEquals(Character.valueOf('x'), SystemPropertyUtils.getCharacter(MISSING, 'x'));
	}

}
