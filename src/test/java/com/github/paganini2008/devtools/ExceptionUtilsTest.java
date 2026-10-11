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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import org.junit.jupiter.api.Test;

/**
 * ExceptionUtilsTest
 *
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class ExceptionUtilsTest {

	@Test
	public void testToArray() {
		assertEquals(0, ExceptionUtils.toArray(null).length);
		String[] lines = ExceptionUtils.toArray(new IllegalStateException("boom", new IOException("io")));
		assertEquals("java.lang.IllegalStateException: boom", lines[0]);
		assertTrue(lines[1].startsWith("    at "), lines[1]);
		boolean causedBy = false;
		for (String line : lines) {
			assertFalse(line.contains("\t"));
			assertFalse(line.equals(System.getProperty("line.separator")));
			causedBy |= line.startsWith("Caused by: java.io.IOException: io");
		}
		assertTrue(causedBy);
	}

	@Test
	public void testToString() {
		assertEquals("", ExceptionUtils.toString(null));
		String text = ExceptionUtils.toString(new IllegalStateException("boom"));
		assertTrue(text.startsWith("java.lang.IllegalStateException: boom"));
		assertTrue(text.contains("ExceptionUtilsTest"));
	}

	@Test
	public void testIgnoreException() {
		Class<?>[] classes = new Class<?>[] { IOException.class, IllegalArgumentException.class };
		assertTrue(ExceptionUtils.ignoreException(new NumberFormatException(), classes));
		assertTrue(ExceptionUtils.ignoreException(new IOException(), classes));
		assertFalse(ExceptionUtils.ignoreException(new IllegalStateException(), classes));
		assertFalse(ExceptionUtils.ignoreException(new IllegalStateException(), null));
		assertFalse(ExceptionUtils.ignoreException(null, classes));
	}

}
