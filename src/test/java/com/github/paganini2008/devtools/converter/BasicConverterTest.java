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
package com.github.paganini2008.devtools.converter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * BasicConverterTest
 * 
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class BasicConverterTest {

	static class LabelConverter extends BasicConverter<String> {
		LabelConverter() {
			registerType(Object.class, (Object source, String defaultValue) -> "object");
			registerType(Number.class, (Number source, String defaultValue) -> "number");
			registerType(Integer.class, (Integer source, String defaultValue) -> "integer:" + source);
			registerType(Collection.class, (Collection<?> source, String defaultValue) -> "collection");
			registerType(List.class, (List<?> source, String defaultValue) -> "list");
		}
	}

	@Test
	public void testExactMatchWins() {
		LabelConverter converter = new LabelConverter();
		assertEquals("integer:5", converter.convertValue(5, null));
	}

	@Test
	public void testMostSpecificAssignableWins() {
		LabelConverter converter = new LabelConverter();
		assertEquals("number", converter.convertValue(5L, null));
		assertEquals("list", converter.convertValue(new ArrayList<String>(), null));
		assertEquals("object", converter.convertValue("abc", null));
		// repeated to make sure the result does not depend on hash iteration order
		for (int i = 0; i < 20; i++) {
			assertEquals("list", converter.convertValue(new ArrayList<String>(), null));
		}
	}

	@Test
	public void testNullAndUnmatched() {
		BasicConverter<String> converter = new BasicConverter<String>() {
		};
		assertEquals("dft", converter.convertValue(null, "dft"));
		assertEquals("dft", converter.convertValue(1, "dft"));
		converter.registerType(Integer.class, (Integer source, String defaultValue) -> String.valueOf(source));
		assertEquals("dft", converter.convertValue(1L, "dft"));
		assertEquals("1", converter.convertValue(1, "dft"));
	}

	@Test
	public void testRegistry() {
		LabelConverter converter = new LabelConverter();
		assertTrue(converter.hasType(Number.class));
		assertNotNull(converter.lookupType(Number.class));
		converter.removeType(Number.class);
		assertFalse(converter.hasType(Number.class));
		assertNull(converter.lookupType(Number.class));
		assertEquals("object", converter.convertValue(5L, null));
		Converter<Object, String> c = (s, d) -> "x";
		converter.registerType(Long.class, c);
		assertSame(c, converter.lookupType(Long.class));
		assertEquals("x", converter.convertValue(5L, null));
	}

}
