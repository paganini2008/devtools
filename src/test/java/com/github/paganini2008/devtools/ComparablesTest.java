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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * ComparablesTest
 * 
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class ComparablesTest {

	private static final List<Integer> NUMS = Arrays.asList(1, 2, 3);
	private static final List<Integer> EMPTY = Collections.emptyList();

	@Test
	public void testNullOr() {
		assertEquals(2, Comparables.nullOrMax(null, 2));
		assertEquals(1, Comparables.nullOrMax(1, null));
		// null is regarded as the minimum value
		assertNull(Comparables.nullOrMin(null, 2));
		assertNull(Comparables.nullOrMin(1, null));
	}

	@Test
	public void testOrDefault() {
		assertNull(Comparables.getOrDefault(1, 1));
		assertEquals(1, Comparables.getOrDefault(1, 2));
		assertEquals(9, Comparables.getOrDefault(1, 1, 9));
		assertEquals(0, Comparables.minOrDefault(1, 1, 0));
		assertEquals(1, Comparables.minOrDefault(1, 2, 5));
		assertEquals(0, Comparables.maxOrDefault(2, 2, 0));
		assertEquals(3, Comparables.maxOrDefault(3, 2, 1));
	}

	@Test
	public void testMaxMin() {
		assertEquals(5, Comparables.max(new Integer[] { 3, 5, 1 }));
		assertEquals(1, Comparables.min(new Integer[] { 3, 5, 1 }));
		assertNull(Comparables.max((Integer[]) null));
		assertNull(Comparables.min(new Integer[0]));
		assertEquals("b", Comparables.max("a", "b"));
		assertEquals("a", Comparables.min("a", "b"));
		assertEquals("a", Comparables.max("a", null));
		assertNull(Comparables.min("a", null));
	}

	@Test
	public void testAnyAll() {
		assertTrue(Comparables.eqAny(2, NUMS));
		assertFalse(Comparables.eqAny(4, NUMS));
		assertFalse(Comparables.eqAny(1, EMPTY));
		assertTrue(Comparables.eqAll(1, Arrays.asList(1, 1)));
		assertFalse(Comparables.eqAll(1, NUMS));
		assertFalse(Comparables.eqAll(1, null));
		assertTrue(Comparables.ltAny(2, NUMS));
		assertFalse(Comparables.ltAny(3, NUMS));
		assertFalse(Comparables.ltAny(3, EMPTY));
		assertTrue(Comparables.ltAll(0, NUMS));
		assertFalse(Comparables.ltAll(1, NUMS));
		assertFalse(Comparables.ltAll(0, EMPTY));
		assertTrue(Comparables.gtAny(2, NUMS));
		assertFalse(Comparables.gtAny(1, NUMS));
		assertFalse(Comparables.gtAny(1, EMPTY));
		assertTrue(Comparables.gtAll(4, NUMS));
		assertFalse(Comparables.gtAll(3, NUMS));
		assertFalse(Comparables.gtAll(3, EMPTY));
		assertTrue(Comparables.lteAny(1, NUMS));
		assertFalse(Comparables.lteAny(4, NUMS));
		assertFalse(Comparables.lteAny(4, EMPTY));
		assertTrue(Comparables.lteAll(1, NUMS));
		assertFalse(Comparables.lteAll(2, NUMS));
		assertFalse(Comparables.lteAll(2, EMPTY));
		assertTrue(Comparables.gteAny(1, NUMS));
		assertFalse(Comparables.gteAny(0, NUMS));
		assertFalse(Comparables.gteAny(0, EMPTY));
		assertTrue(Comparables.gteAll(3, NUMS));
		assertFalse(Comparables.gteAll(2, NUMS));
		assertFalse(Comparables.gteAll(2, EMPTY));
	}

	@Test
	public void testCompare() {
		assertTrue(Comparables.between(2, 1, 3));
		assertTrue(Comparables.between(1, 1, 3));
		assertFalse(Comparables.between(1, 1, 3, true));
		assertTrue(Comparables.between(2, 1, 3, true));
		assertTrue(Comparables.eq(null, null));
		assertTrue(Comparables.ne(null, 1));
		assertEquals(-1, Comparables.compareTo(null, 1));
		assertEquals(1, Comparables.compareTo(1, null));
		assertEquals(0, Comparables.compareTo((Integer) null, null));
		assertEquals(-1, Comparables.compareTo("a", "z"));
		assertEquals(1, Comparables.compareTo("z", "a"));
		assertEquals(0, Comparables.compareTo("a", "a"));
	}

}
