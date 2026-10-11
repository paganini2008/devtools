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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.Test;

/**
 * SequenceTest
 *
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class SequenceTest {

	@Test
	public void testIntRange() {
		assertEquals(Arrays.asList(0, 1, 2), Sequence.range(3));
		assertEquals(Arrays.asList(2, 3), Sequence.range(2, 4));
		assertEquals(Arrays.asList(0, 3, 6, 9), Sequence.range(0, 10, 3));
		assertEquals(Collections.emptyList(), Sequence.range(5, 5));
		assertEquals(Collections.emptyList(), Sequence.range(5, 1));
		// a non-positive interval yields nothing when from >= to (backward compatible)
		assertEquals(Collections.emptyList(), Sequence.range(5, 1, -2));
	}

	@Test
	public void testLongRange() {
		assertEquals(Arrays.asList(0L, 1L), Sequence.range(2L));
		assertEquals(Arrays.asList(3L, 4L), Sequence.range(3L, 5L));
		assertEquals(Arrays.asList(1L, 3L), Sequence.range(1L, 5L, 2));
		assertEquals(Collections.emptyList(), Sequence.range(3L, 1L, -1));
	}

	@Test
	public void testForEach() {
		int sum = 0;
		for (int i : Sequence.forEach(5)) {
			sum += i;
		}
		assertEquals(10, sum);
		long total = 0;
		for (long l : Sequence.forEach(5L)) {
			total += l;
		}
		assertEquals(10L, total);
		Iterator<Integer> it = Sequence.forEach(1, 2).iterator();
		assertTrue(it.hasNext());
		assertEquals(1, it.next().intValue());
		assertFalse(it.hasNext());
		assertThrows(NoSuchElementException.class, it::next);
		Iterator<Long> lit = Sequence.forEach(1L, 2L).iterator();
		assertEquals(1L, lit.next().longValue());
		assertThrows(NoSuchElementException.class, lit::next);
	}

	@Test
	public void testNonPositiveIntervalIsRejected() {
		// these used to loop forever
		assertThrows(IllegalArgumentException.class, () -> Sequence.range(0, 10, 0));
		assertThrows(IllegalArgumentException.class, () -> Sequence.range(0, 10, -1));
		assertThrows(IllegalArgumentException.class, () -> Sequence.range(0L, 10L, 0));
	}

	@Test
	public void testNoOverflowNearMaxValue() {
		assertEquals(Arrays.asList(Integer.MAX_VALUE - 1), Sequence.range(Integer.MAX_VALUE - 1, Integer.MAX_VALUE, 5));
		assertEquals(Arrays.asList(Long.MAX_VALUE - 1), Sequence.range(Long.MAX_VALUE - 1, Long.MAX_VALUE, 5));
	}

}
