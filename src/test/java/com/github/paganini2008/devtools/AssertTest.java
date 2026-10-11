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

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;

import org.junit.jupiter.api.Test;

/**
 * AssertTest
 * 
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class AssertTest {

	@Test
	public void testText() {
		assertThrows(IllegalArgumentException.class, () -> Assert.hasNoLength(null));
		assertThrows(IllegalArgumentException.class, () -> Assert.hasNoLength(""));
		assertDoesNotThrow(() -> Assert.hasNoLength(" "));
		assertThrows(IllegalArgumentException.class, () -> Assert.hasNoText(" "));
		assertDoesNotThrow(() -> Assert.hasNoText("a"));
		IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> Assert.hasNoText(null, "Bad %s", "name"));
		assertEquals("Bad name", e.getMessage());
	}

	@Test
	public void testNullAndArray() {
		assertThrows(IllegalArgumentException.class, () -> Assert.isNull(null));
		assertDoesNotThrow(() -> Assert.isNull("a"));
		IllegalStateException ise = new IllegalStateException();
		assertSame(ise, assertThrows(IllegalStateException.class, () -> Assert.isNull(null, ise)));
		assertDoesNotThrow(() -> Assert.isNull("a", ise));
		assertThrows(IllegalArgumentException.class, () -> Assert.isNotArray("a"));
		assertThrows(IllegalArgumentException.class, () -> Assert.isNotArray(null));
		assertDoesNotThrow(() -> Assert.isNotArray(new int[0]));
	}

	@Test
	public void testCompare() {
		assertThrows(IllegalArgumentException.class, () -> Assert.lt(1, 2, "lt"));
		assertDoesNotThrow(() -> Assert.lt(2, 2, "lt"));
		assertThrows(IllegalArgumentException.class, () -> Assert.lte(2, 2, "lte"));
		assertDoesNotThrow(() -> Assert.lte(3, 2, "lte"));
		assertThrows(IllegalArgumentException.class, () -> Assert.gt(3, 2, "gt"));
		assertDoesNotThrow(() -> Assert.gt(2, 2, "gt"));
		assertThrows(IllegalArgumentException.class, () -> Assert.gte(2, 2, "gte"));
		assertDoesNotThrow(() -> Assert.gte(1, 2, "gte"));
		assertThrows(IllegalArgumentException.class, () -> Assert.ne(1, 2, "ne"));
		assertDoesNotThrow(() -> Assert.ne(2, 2, "ne"));
		assertThrows(IllegalArgumentException.class, () -> Assert.eq(2, 2, "eq"));
		assertDoesNotThrow(() -> Assert.eq(1, 2, "eq"));
		assertDoesNotThrow(() -> Assert.eq(null, 2, "eq"));
		assertThrows(IllegalArgumentException.class, () -> Assert.range(2, 1, 3, "range"));
		assertDoesNotThrow(() -> Assert.range(4, 1, 3, "range"));
		assertThrows(IllegalArgumentException.class, () -> Assert.outOfRange(0, 1, 3, "out"));
		assertThrows(IllegalArgumentException.class, () -> Assert.outOfRange(4, 1, 3, "out"));
		assertDoesNotThrow(() -> Assert.outOfRange(2, 1, 3, "out"));
		assertDoesNotThrow(() -> Assert.outOfRange(null, 1, 3, "out"));
	}

	@Test
	public void testTrueFalse() {
		assertThrows(IllegalArgumentException.class, () -> Assert.isTrue(true, "msg"));
		assertDoesNotThrow(() -> Assert.isTrue(false, "msg"));
		IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> Assert.isFalse(false, "v=%d", 1));
		assertEquals("v=1", e.getMessage());
		assertDoesNotThrow(() -> Assert.isFalse(true, "msg"));
		assertEquals("100%", assertThrows(IllegalArgumentException.class, () -> Assert.isTrue(true, "100%", (Object[]) null)).getMessage());

		assertThrows(IllegalStateException.class, () -> Assert.isTrue(true, new IllegalStateException()));
		assertDoesNotThrow(() -> Assert.isTrue(false, new IllegalStateException()));
		assertThrows(IllegalStateException.class, () -> Assert.isFalse(false, new IllegalStateException()));
		assertDoesNotThrow(() -> Assert.isFalse(true, new IllegalStateException()));

		assertThrows(IOException.class, () -> Assert.isTrue(true, new IOException()));
		assertDoesNotThrow(() -> Assert.isTrue(false, new IOException()));
		assertThrows(IOException.class, () -> Assert.isFalse(false, new IOException()));
		assertDoesNotThrow(() -> Assert.isFalse(true, new IOException()));

		assertThrows(AssertionError.class, () -> Assert.isTrue(true, new AssertionError()));
		assertDoesNotThrow(() -> Assert.isTrue(false, new AssertionError()));
		assertThrows(AssertionError.class, () -> Assert.isFalse(false, new AssertionError()));
		assertDoesNotThrow(() -> Assert.isFalse(true, new AssertionError()));
	}

	@Test
	public void testNumberRangeAssert() {
		assertTrue(NumberRangeAssert.checkByte(BigInteger.valueOf(128)));
		assertFalse(NumberRangeAssert.checkByte(BigInteger.valueOf(-128)));
		assertTrue(NumberRangeAssert.checkShort(BigInteger.valueOf(Short.MIN_VALUE - 1)));
		assertFalse(NumberRangeAssert.checkShort(BigInteger.valueOf(Short.MAX_VALUE)));
		assertTrue(NumberRangeAssert.checkInteger(BigInteger.valueOf(Integer.MAX_VALUE + 1L)));
		assertFalse(NumberRangeAssert.checkInteger(BigInteger.ZERO));
		assertTrue(NumberRangeAssert.checkLong(BigInteger.valueOf(Long.MAX_VALUE).add(BigInteger.ONE)));
		assertFalse(NumberRangeAssert.checkLong(BigInteger.valueOf(Long.MIN_VALUE)));
	}

	@Test
	public void testNumeric() {
		Numeric d = new Numeric(1.5d);
		assertEquals(1, d.intValue());
		assertEquals(1L, d.longValue());
		assertEquals(1.5f, d.floatValue());
		assertEquals(1.5d, d.doubleValue());
		assertEquals((byte) 1, d.byteValue());
		assertEquals((short) 1, d.shortValue());
		assertEquals(7L, new Numeric(7L).longValue());
		assertEquals(10, new Numeric(BigInteger.TEN).intValue());
		assertEquals(2.25d, new Numeric(new BigDecimal("2.25")).doubleValue());
		assertEquals(3.5d, new Numeric("3.5").doubleValue());
	}

}
