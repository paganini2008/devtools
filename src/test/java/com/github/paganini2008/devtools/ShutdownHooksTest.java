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
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

/**
 * ShutdownHooksTest
 *
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class ShutdownHooksTest {

	static class RecordingHook extends ShutdownHook {

		final List<String> log;
		final String name;
		final int priority;
		final boolean fail;
		Throwable ignored;

		RecordingHook(List<String> log, String name, int priority, boolean fail) {
			this.log = log;
			this.name = name;
			this.priority = priority;
			this.fail = fail;
		}

		@Override
		protected void process() {
			log.add(name);
			if (fail) {
				throw new IllegalStateException(name);
			}
		}

		@Override
		protected void ignoreException(Throwable e) {
			ignored = e;
		}

		@Override
		protected int getPriority() {
			return priority;
		}
	}

	@AfterEach
	public void cleanup() {
		ShutdownHooks.cleanHooks();
	}

	@Test
	public void testRegistry() {
		List<String> log = new ArrayList<String>();
		RecordingHook hook = new RecordingHook(log, "a", 0, false);
		int before = ShutdownHooks.countOfHooks();
		ShutdownHooks.addHook(hook);
		ShutdownHooks.addHook(null);
		assertEquals(before + 1, ShutdownHooks.countOfHooks());
		ShutdownHooks.removeHook(hook);
		ShutdownHooks.removeHook(null);
		assertEquals(before, ShutdownHooks.countOfHooks());
		ShutdownHooks.addHook(hook);
		ShutdownHooks.cleanHooks();
		assertEquals(0, ShutdownHooks.countOfHooks());
	}

	@Test
	public void testHookThreadRunsInPriorityOrderAndSwallowsErrors() {
		List<String> log = new ArrayList<String>();
		ShutdownHooks.HookThread thread = new ShutdownHooks.HookThread();
		RecordingHook failing = new RecordingHook(log, "b", 2, true);
		thread.queue.add(new RecordingHook(log, "c", 3, false));
		thread.queue.add(failing);
		thread.queue.add(new RecordingHook(log, "a", 1, false));
		thread.run();
		assertEquals(Arrays.asList("a", "b", "c"), log);
		assertTrue(failing.ignored instanceof IllegalStateException);
		assertTrue(thread.queue.isEmpty());
	}

	@Test
	public void testCompareToDoesNotOverflow() {
		RecordingHook low = new RecordingHook(null, "low", Integer.MIN_VALUE, false);
		RecordingHook high = new RecordingHook(null, "high", Integer.MAX_VALUE, false);
		assertTrue(low.compareTo(high) < 0);
		assertTrue(high.compareTo(low) > 0);
		ShutdownHook plain = new ShutdownHook() {
			@Override
			protected void process() {
			}
		};
		assertEquals(0, plain.getPriority());
		plain.ignoreException(new RuntimeException());
		assertEquals(0, plain.compareTo(plain));
	}

}
