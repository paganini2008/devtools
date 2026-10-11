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

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * ComparableComparatorTest
 * 
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class ComparableComparatorTest {

	@Test
	public void testSortArrayAndList() {
		Integer[] array = { 3, null, 1, 2 };
		assertSame(array, ComparableComparator.INTEGER.sort(array));
		assertArrayEquals(new Integer[] { null, 1, 2, 3 }, array);
		assertNull(ComparableComparator.INTEGER.sort((Integer[]) null));
		List<Long> list = new ArrayList<Long>(Arrays.asList(5L, 1L, 3L));
		assertSame(list, ComparableComparator.LONG.sort(list));
		assertEquals("[1, 3, 5]", list.toString());
		assertTrue(ComparableComparator.LONG.sort(new ArrayList<Long>()).isEmpty());
		List<BigDecimal> decimals = new ArrayList<BigDecimal>(Arrays.asList(new BigDecimal("2.0"), new BigDecimal("1.5")));
		ComparableComparator.DECIMAL.reverse();
		ComparableComparator.DECIMAL.sort(decimals);
		assertEquals("[1.5, 2.0]", decimals.toString());
		assertEquals(1, ComparableComparator.DECIMAL.reverse().compare(new BigDecimal("1"), new BigDecimal("2")));
		assertEquals(-1, ComparableComparator.BYTE.compare((byte) 1, (byte) 2));
		assertEquals(1, ComparableComparator.SHORT.compare((short) 3, (short) 2));
		assertEquals(0, ComparableComparator.FLOAT.compare(1F, 1F));
		assertEquals(-1, ComparableComparator.DOUBLE.compare(1D, 2D));
		assertEquals(1, ComparableComparator.BIGINT.compare(java.math.BigInteger.TEN, java.math.BigInteger.ONE));
		assertEquals("[Comparator] " + ComparableComparator.class.getName(), ComparableComparator.INTEGER.toString());
		assertEquals(0, new ComparableComparator<String>().compare("a", "a"));
	}

}
