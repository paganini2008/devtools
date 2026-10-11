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
package com.github.paganini2008.devtools.comparator;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * ComparatorHelperTest
 * 
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class ComparatorHelperTest {

	@Test
	public void testValueOf() {
		assertEquals(-1, ComparatorHelper.valueOf(-0.5D));
		assertEquals(1, ComparatorHelper.valueOf(2.5D));
		assertEquals(0, ComparatorHelper.valueOf(0D));
		assertEquals(-1, ComparatorHelper.valueOf(-5L));
		assertEquals(1, ComparatorHelper.valueOf(5L));
		assertEquals(0, ComparatorHelper.valueOf(0L));
	}

	@Test
	public void testCompareTo() {
		assertEquals(-1, ComparatorHelper.compareTo(1, 5));
		assertEquals(1, ComparatorHelper.compareTo("b", "a"));
		assertEquals(0, ComparatorHelper.compareTo(3, 3));
		assertEquals(1, ComparatorHelper.compareTo(3, null));
		assertEquals(-1, ComparatorHelper.compareTo(null, 3));
		assertEquals(0, ComparatorHelper.compareTo((Integer) null, null));
		assertEquals(0, ComparatorHelper.compareTo("A", "a", true));
		assertEquals(-1, ComparatorHelper.compareTo("A", "a", false));
		assertEquals(1, ComparatorHelper.compareTo("a", null, true));
		assertEquals(-1, ComparatorHelper.compareTo(null, "a", true));
		assertEquals(0, ComparatorHelper.compareTo(null, null, false));
		assertEquals(0, ComparatorHelper.<String>identity().compare("a", "b"));
	}

}
