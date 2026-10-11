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
package com.github.paganini2008.devtools.multithreads;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.concurrent.atomic.AtomicLong;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

/**
 * ThreadLocalNumberTest
 * 
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
@Timeout(10)
public class ThreadLocalNumberTest {

	@Test
	public void threadLocalInteger_operations() {
		ThreadLocalInteger i = new ThreadLocalInteger(5);
		assertEquals(5, i.getAndIncrement());
		assertEquals(7, i.incrementAndGet());
		assertEquals(7, i.getAndDecrement());
		assertEquals(5, i.decrementAndGet());
		assertEquals(5, i.getAndAdd(10));
		assertEquals(20, i.addAndGet(5));
		i.set(3);
		assertEquals(3, i.intValue());
		assertEquals(3L, i.longValue());
		assertEquals(3f, i.floatValue());
		assertEquals(3d, i.doubleValue());
		assertEquals("3", i.toString());
		i.reset();
		assertEquals(5, i.get());
		assertEquals(0, new ThreadLocalInteger().get());
	}

	@Test
	public void threadLocalLong_operations() {
		ThreadLocalLong l = new ThreadLocalLong(5);
		assertEquals(5, l.getAndIncrement());
		assertEquals(7, l.incrementAndGet());
		assertEquals(7, l.getAndDecrement());
		assertEquals(5, l.decrementAndGet());
		assertEquals(5, l.getAndAdd(10));
		assertEquals(20, l.addAndGet(5));
		l.set(3);
		assertEquals(3, l.intValue());
		assertEquals(3L, l.longValue());
		assertEquals(3f, l.floatValue());
		assertEquals(3d, l.doubleValue());
		assertEquals("3", l.toString());
		l.reset();
		assertEquals(5, l.get());
		assertEquals(0, new ThreadLocalLong().get());
	}

	@Test
	public void valuesAreIsolatedPerThread() throws Exception {
		ThreadLocalInteger i = new ThreadLocalInteger(0);
		ThreadLocalLong l = new ThreadLocalLong(0);
		i.set(42);
		l.set(42);
		AtomicLong seen = new AtomicLong(-1);
		Thread t = new Thread(() -> {
			i.incrementAndGet();
			seen.set(i.get() * 100 + l.incrementAndGet());
		});
		t.start();
		t.join();
		assertEquals(101, seen.get());
		assertEquals(42, i.get());
		assertEquals(42, l.get());
	}

}
