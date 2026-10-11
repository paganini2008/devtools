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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.Calendar;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.Test;

/**
 * ThreadLocalCalendarTest
 * 
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class ThreadLocalCalendarTest {

	@Test
	public void testCurrent() throws Exception {
		ThreadLocalCalendar tlc = ThreadLocalCalendar.current();
		assertSame(tlc, ThreadLocalCalendar.current());
		Calendar c = tlc.get();
		assertNotNull(c);
		assertSame(c, tlc.get());
		AtomicReference<Calendar> other = new AtomicReference<>();
		Thread t = new Thread(() -> other.set(tlc.get()));
		t.start();
		t.join();
		assertNotNull(other.get());
		assertNotSame(c, other.get());
	}

	@Test
	public void testSupplier() {
		ThreadLocalCalendar tlc = ThreadLocalCalendar.get(() -> Calendar.getInstance(TimeZone.getTimeZone("UTC")));
		assertEquals("UTC", tlc.get().getTimeZone().getID());
	}

}
