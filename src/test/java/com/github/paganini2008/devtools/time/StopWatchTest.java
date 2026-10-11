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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;

/**
 * StopWatchTest
 * 
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class StopWatchTest {

	private static final long MS = 1000000L;

	static class FakeStopWatch extends StopWatch {

		long now = 1000L;

		FakeStopWatch(String id) {
			super(id);
		}

		void advanceMillis(long millis) {
			now += millis * MS;
		}

		@Override
		protected long nanoTime() {
			return now;
		}
	}

	@Test
	void multipleTasks_areRecordedInOrderAndSummed() {
		FakeStopWatch watch = new FakeStopWatch("import");
		assertEquals(0, watch.getNanoTime());
		watch.start("load");
		assertTrue(watch.isRunning());
		watch.advanceMillis(100);
		assertEquals(100 * MS, watch.getNanoTime());
		assertEquals(100 * MS, watch.stop());
		assertFalse(watch.isRunning());
		watch.advanceMillis(50); // idle time between tasks is not counted
		watch.start("parse");
		watch.advanceMillis(300);
		watch.stop();
		assertEquals(400, watch.getTime());
		assertEquals(400 * MS, watch.getNanoTime());
		assertEquals(400000, watch.getTime(TimeUnit.MICROSECONDS));
		assertEquals(0, watch.getTime(TimeUnit.SECONDS));
		List<StopWatch.TaskInfo> infos = watch.getTaskInfos();
		assertEquals(2, infos.size());
		assertEquals(2, watch.getTaskCount());
		assertEquals("load", infos.get(0).getTaskName());
		assertEquals(100 * MS, infos.get(0).getTimeNanos());
		assertEquals(100, infos.get(0).getTimeMillis());
		assertEquals(300, infos.get(1).getTime(TimeUnit.MILLISECONDS));
		assertEquals("load: 100000000 ns", infos.get(0).toString());
		assertThrows(UnsupportedOperationException.class, () -> infos.clear());
		assertEquals("import", watch.getId());
	}

	@Test
	void prettyPrint_showsTimeAndPercentage() {
		FakeStopWatch watch = new FakeStopWatch("import");
		watch.start("load");
		watch.advanceMillis(100);
		watch.stop();
		watch.start("parse");
		watch.advanceMillis(300);
		watch.stop();
		String expected = "StopWatch 'import': running time = 400000000 ns (400 ms)\n"
				+ "---------------------------------------------\n"
				+ "               ns        %  Task name\n"
				+ "---------------------------------------------\n"
				+ "        100000000   25.00%  load\n"
				+ "        300000000   75.00%  parse\n";
		assertEquals(expected, watch.prettyPrint());
		assertEquals("StopWatch 'import': state=STOPPED, tasks=2, total=400 ms", watch.toString());
	}

	@Test
	void prettyPrint_emptyAndZeroDuration() {
		FakeStopWatch watch = new FakeStopWatch(null);
		assertEquals("", watch.getId());
		assertTrue(watch.prettyPrint().contains("No task info kept"));
		assertEquals("StopWatch '': state=UNSTARTED, tasks=0, total=0 ms", watch.toString());
		watch.start("noop");
		watch.stop();
		assertTrue(watch.prettyPrint().contains("                0    0.00%  noop"), watch.prettyPrint());
	}

	@Test
	void split_recordsLapsWithoutStopping() {
		FakeStopWatch watch = new FakeStopWatch("w");
		watch.start("job");
		watch.advanceMillis(10);
		assertEquals(10 * MS, watch.split());
		assertTrue(watch.isRunning());
		watch.advanceMillis(20);
		assertEquals(20 * MS, watch.split());
		watch.advanceMillis(5);
		assertEquals(35 * MS, watch.getNanoTime());
		assertEquals(5 * MS, watch.stop());
		List<StopWatch.TaskInfo> infos = watch.getTaskInfos();
		assertEquals(3, infos.size());
		assertEquals("job#1", infos.get(0).getTaskName());
		assertEquals("job#2", infos.get(1).getTaskName());
		assertEquals("job#3", infos.get(2).getTaskName());
		assertEquals(35, watch.getTime());
		// next task without splits keeps its plain name
		watch.start("next");
		watch.advanceMillis(1);
		watch.stop();
		assertEquals("next", watch.getTaskInfos().get(3).getTaskName());
	}

	@Test
	void suspendAndResume_excludeSuspendedTime() {
		FakeStopWatch watch = new FakeStopWatch("w");
		watch.start("job");
		watch.advanceMillis(10);
		watch.suspend();
		assertTrue(watch.isSuspended());
		assertFalse(watch.isRunning());
		watch.advanceMillis(1000);
		assertEquals(10 * MS, watch.getNanoTime());
		watch.resume();
		assertTrue(watch.isRunning());
		watch.advanceMillis(5);
		assertEquals(15 * MS, watch.getNanoTime());
		watch.suspend();
		watch.advanceMillis(7);
		assertEquals(15 * MS, watch.stop()); // stop while suspended
		assertEquals(15, watch.getTime());
	}

	@Test
	void defaultTaskNamesAndCreateStarted() {
		StopWatch started = StopWatch.createStarted();
		assertTrue(started.isRunning());
		assertTrue(started.getNanoTime() >= 0);
		started.stop();
		assertEquals("task-1", started.getTaskInfos().get(0).getTaskName());
		started.start();
		started.stop();
		assertEquals("task-2", started.getTaskInfos().get(1).getTaskName());
		StopWatch plain = new StopWatch();
		assertEquals("", plain.getId());
	}

	@Test
	void reset_clearsEverything() {
		FakeStopWatch watch = new FakeStopWatch("w");
		watch.start();
		watch.advanceMillis(3);
		watch.reset();
		assertFalse(watch.isRunning());
		assertEquals(0, watch.getNanoTime());
		assertEquals(0, watch.getTaskCount());
		assertThrows(IllegalStateException.class, watch::stop);
		watch.start();
		watch.advanceMillis(2);
		watch.stop();
		assertEquals("task-1", watch.getTaskInfos().get(0).getTaskName());
		assertEquals(2, watch.getTime());
	}

	@Test
	void illegalStateTransitions_throw() {
		FakeStopWatch watch = new FakeStopWatch("w");
		assertThrows(IllegalStateException.class, watch::stop);
		assertThrows(IllegalStateException.class, watch::split);
		assertThrows(IllegalStateException.class, watch::suspend);
		assertThrows(IllegalStateException.class, watch::resume);
		watch.start("a");
		IllegalStateException e = assertThrows(IllegalStateException.class, () -> watch.start("b"));
		assertTrue(e.getMessage().contains("'a'"));
		assertThrows(IllegalStateException.class, watch::resume);
		watch.suspend();
		assertThrows(IllegalStateException.class, watch::start);
		assertThrows(IllegalStateException.class, watch::split);
		assertThrows(IllegalStateException.class, watch::suspend);
		watch.resume();
		watch.stop();
		assertThrows(IllegalStateException.class, watch::stop);
		assertThrows(IllegalStateException.class, watch::split);
		assertThrows(IllegalArgumentException.class, () -> watch.getTime(null));
	}

}
