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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.github.paganini2008.devtools.MissingKeyException;

/**
 * DataConverterPropertiesTest
 * 
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class DataConverterPropertiesTest {

	@Test
	public void testConversions() {
		DataConverterProperties p = new DataConverterProperties();
		p.setProperty("bool", "true");
		p.setProperty("char", "xyz");
		p.setProperty("num", "12");
		p.setProperty("real", "1.5");
		assertTrue(p.getRequiredBoolean("bool"));
		assertEquals(Character.valueOf('x'), p.getRequiredCharacter("char"));
		assertEquals(Byte.valueOf((byte) 12), p.getRequiredByte("num"));
		assertEquals(Short.valueOf((short) 12), p.getRequiredShort("num"));
		assertEquals(Integer.valueOf(12), p.getRequiredInteger("num"));
		assertEquals(Long.valueOf(12), p.getRequiredLong("num"));
		assertEquals(Float.valueOf(1.5F), p.getRequiredFloat("real"));
		assertEquals(Double.valueOf(1.5D), p.getRequiredDouble("real"));
		assertThrows(MissingKeyException.class, () -> p.getRequiredInteger("missing"));

		assertEquals(Boolean.FALSE, p.getBoolean("missing", false));
		assertEquals(Character.valueOf('d'), p.getCharacter("missing", 'd'));
		assertEquals(Byte.valueOf((byte) 1), p.getByte("missing", (byte) 1));
		assertEquals(Short.valueOf((short) 1), p.getShort("missing", (short) 1));
		assertEquals(Integer.valueOf(1), p.getInteger("missing", 1));
		assertEquals(Long.valueOf(1), p.getLong("missing", 1L));
		assertEquals(Float.valueOf(1F), p.getFloat("missing", 1F));
		assertEquals(Double.valueOf(1D), p.getDouble("missing", 1D));
		assertEquals(Integer.valueOf(12), p.getInteger("num", 1));
		assertNull(p.getInteger("missing", null));
	}

}
