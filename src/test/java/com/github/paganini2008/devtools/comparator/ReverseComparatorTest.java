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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * ReverseComparatorTest
 * 
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class ReverseComparatorTest {

	@Test
	public void testReverse() {
		ReverseComparator<Integer> c = new ReverseComparator<Integer>(ComparableComparator.INTEGER);
		assertEquals(1, c.compare(1, 2));
		List<Integer> list = new ArrayList<Integer>(Arrays.asList(1, 3, 2));
		c.sort(list);
		assertEquals("[3, 2, 1]", list.toString());
		assertTrue(c.toString().startsWith("[Comparator] " + ReverseComparator.class.getName() + "["));
		assertThrows(IllegalArgumentException.class, () -> new ReverseComparator<Integer>(null));
	}

}
