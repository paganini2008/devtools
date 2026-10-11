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
package com.github.paganini2008.devtools.time;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * DurationTest
 * 
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class DurationTest {

	private static final long MS = ((((1L * 24 + 2) * 60 + 3) * 60) + 4) * 1000 + 5; // 1d 2h 3m 4s 5ms

	@Test
	public void testToArray() {
		assertArrayEquals(new Long[] { 1L, 2L, 3L, 4L, 5L }, Duration.DAY.toArray(MS));
		assertArrayEquals(new Long[] { 26L, 3L, 4L, 5L }, Duration.HOUR.toArray(MS));
		assertArrayEquals(new Long[] { 26L * 60 + 3, 4L, 5L }, Duration.MINUTE.toArray(MS));
		assertArrayEquals(new Long[] { 0L, 0L, 0L, 0L, 0L }, Duration.DAY.toArray(0));
	}

	@Test
	public void testFormat() {
		assertEquals("#d:#h:#m:#s:#ms", Duration.DAY.getPattern());
		assertEquals("1d:2h:3m:4s:5ms", Duration.DAY.format(MS));
		assertEquals("26h:3m:4s:5ms", Duration.HOUR.format(MS));
		assertEquals("1563m:4s:5ms", Duration.MINUTE.format(MS));
		assertEquals("1 days 2 hours", Duration.DAY.format(MS, "# days # hours"));
		assertEquals("1d:2h:3m:4s:5ms", Duration.DAY.format(MS, " "));
	}

}
