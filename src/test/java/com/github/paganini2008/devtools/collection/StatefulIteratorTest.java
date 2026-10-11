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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

/**
 * StatefulIteratorTest
 * 
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class StatefulIteratorTest {

	@Test
	public void testConvertFrom() throws Exception {
		StatefulIterator<String> it = StatefulIterator.convertFrom(Arrays.asList("a").iterator());
		it.prepare();
		assertTrue(it.hasNext());
		assertEquals("a", it.next());
		assertFalse(it.hasNext());
		assertThrows(UnsupportedOperationException.class, it::remove);
		it.close();
		assertSame(it, StatefulIterator.convertFrom(it));
	}

}
