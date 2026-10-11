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

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

import org.junit.jupiter.api.Test;

/**
 * TimeSlotTest
 * 
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class TimeSlotTest {

	private static final ZoneId UTC = ZoneOffset.UTC;
	private static final Instant INS = Instant.parse("2021-01-31T10:47:38Z");

	@Test
	public void testLocate() {
		assertEquals(LocalDateTime.of(2021, 1, 31, 0, 0), TimeSlot.DAY.locate(INS, 1, UTC));
		assertEquals(LocalDateTime.of(2021, 1, 31, 9, 0), TimeSlot.HOUR.locate(INS, 3, UTC));
		assertEquals(LocalDateTime.of(2021, 1, 31, 10, 45), TimeSlot.MINUTE.locate(INS, 15, UTC));
		assertEquals(LocalDateTime.of(2021, 1, 31, 10, 47, 30), TimeSlot.SECOND.locate(INS, 10, UTC));
		assertEquals(LocalDateTime.of(2021, 1, 31, 18, 0), TimeSlot.HOUR.locate(INS, 1, ZoneId.of("Asia/Shanghai")));
		for (TimeSlot slot : TimeSlot.values()) {
			assertEquals(slot.locate(INS, 5, ZoneId.systemDefault()), slot.locate(INS, 5));
		}
	}

	@Test
	public void testSizeOf() {
		assertEquals(3, TimeSlot.DAY.sizeOf(1, 3));
		assertEquals(8, TimeSlot.HOUR.sizeOf(3, 1));
		assertEquals(5 * 2, TimeSlot.HOUR.sizeOf(5, 2));
		assertEquals(4 * 24, TimeSlot.MINUTE.sizeOf(15, 1));
		assertEquals(9 * 24, TimeSlot.MINUTE.sizeOf(7, 1));
		assertEquals(6 * 60 * 24, TimeSlot.SECOND.sizeOf(10, 1));
		assertEquals(9 * 60 * 24, TimeSlot.SECOND.sizeOf(7, 1));
	}

}
