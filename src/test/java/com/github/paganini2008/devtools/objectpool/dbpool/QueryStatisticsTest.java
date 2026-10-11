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
package com.github.paganini2008.devtools.objectpool.dbpool;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.UndeclaredThrowableException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

/**
 * QueryStatisticsTest
 * 
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
@Timeout(20)
public class QueryStatisticsTest {

	@Test
	public void querySpan_tracksCountMinMaxAvg() {
		QuerySpanImpl span = new QuerySpanImpl(10, 1000L);
		assertEquals(0, span.getExecutionCount());
		assertEquals(0F, span.getExecutionAvgTime());
		assertEquals(0L, span.getExecutionMinTime());
		assertEquals(0L, span.getExecutionMaxTime());
		span.record(new QueryTraceImpl("q", null, 100, 130));
		span.record(new QueryTraceImpl("q", null, 200, 210));
		span.record(new QueryTraceImpl("q", null, 300, 350));
		assertEquals(3, span.getExecutionCount());
		assertEquals(10L, span.getExecutionMinTime(), "min time must be tracked, not stuck at 0");
		assertEquals(50L, span.getExecutionMaxTime());
		assertEquals(30.0F, span.getExecutionAvgTime());
		assertTrue(span.getQueryTraces().isEmpty());
		assertTrue(span.toString().contains("executionCount=3"));
	}

	@Test
	public void querySpan_keepsLatestSlowQueriesSortedSlowestFirst() {
		QuerySpanImpl span = new QuerySpanImpl(3, 10L);
		long[] durations = { 20, 50, 5, 20, 30, 40 };
		long start = 0;
		for (long d : durations) {
			span.record(new QueryTraceImpl("q", new Object[] { d }, start, start + d));
			start += 100;
		}
		List<QueryTrace> traces = span.getQueryTraces();
		assertEquals(3, traces.size());
		assertEquals(40L, traces.get(0).getEndTime() - traces.get(0).getStartTime());
		assertEquals(30L, traces.get(1).getEndTime() - traces.get(1).getStartTime());
		assertEquals(20L, traces.get(2).getEndTime() - traces.get(2).getStartTime());
		assertEquals(0, new QuerySpanImpl(0, 0L).getQueryTraces().size());
		QuerySpanImpl disabled = new QuerySpanImpl(0, 0L);
		disabled.record(new QueryTraceImpl("q", null, 0, 10));
		assertTrue(disabled.getQueryTraces().isEmpty());
	}

	@Test
	public void queryTrace_comparesByElapsedTimeDescending() {
		QueryTrace slow = new QueryTraceImpl("a", null, 0, 100);
		QueryTrace fast = new QueryTraceImpl("b", new Object[] { 1, "x" }, 0, 10);
		QueryTrace sameAsFast = new QueryTraceImpl("c", null, 50, 60);
		assertTrue(slow.compareTo(fast) < 0);
		assertTrue(fast.compareTo(slow) > 0);
		assertEquals(0, fast.compareTo(sameAsFast));
		List<QueryTrace> list = new ArrayList<QueryTrace>();
		list.add(fast);
		list.add(slow);
		Collections.sort(list);
		assertSame(slow, list.get(0));
		assertEquals("b", fast.getSql());
		assertEquals(2, fast.getParameters().length);
		assertTrue(fast.toString().contains("sql: b"));
		assertTrue(fast.toString().contains("[1, x]"));
	}

	@Test
	public void dailyStatistics_groupsByDayAndSql() {
		DailyQueryStatistics statistics = new DailyQueryStatistics();
		statistics.setAcceptableExecutionTime(5);
		statistics.setStatisticalSampleCount(2);
		long now = System.currentTimeMillis();
		QuerySpan a = statistics.executed("select a", null, now - 10, now);
		QuerySpan a2 = statistics.executed("select a", null, now - 1, now);
		statistics.executed("select b", null, now - 1, now);
		assertSame(a, a2);
		String today = new SimpleDateFormat("dd/MM/yyyy").format(new Date(now));
		Map<String, QuerySpan> result = statistics.getStatisticsResult(today);
		assertEquals(2, result.size());
		assertEquals(2, result.get("select a").getExecutionCount());
		assertEquals(1, result.get("select a").getQueryTraces().size());
		result.clear();
		assertEquals(2, statistics.getStatisticsResult(today).size(), "a copy is returned");
		assertNull(statistics.getStatisticsResult(null));
		assertNull(statistics.getStatisticsResult("31/12/1999"));
	}

	@Test
	public void dailyStatistics_keepsSevenDays() {
		DailyQueryStatistics statistics = new DailyQueryStatistics();
		long day = TimeUnit.DAYS.toMillis(1);
		long base = System.currentTimeMillis() - 10 * day;
		for (int i = 0; i < 9; i++) {
			statistics.executed("q", null, base + i * day, base + i * day + 1);
		}
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		assertNull(statistics.getStatisticsResult(format.format(new Date(base + 1))));
		assertEquals(1, statistics.getStatisticsResult(format.format(new Date(base + 8 * day + 1))).size());
	}

	@Test
	public void dailyStatistics_isThreadSafe() throws Exception {
		final DailyQueryStatistics statistics = new DailyQueryStatistics();
		final long now = System.currentTimeMillis();
		final CountDownLatch start = new CountDownLatch(1);
		ExecutorService executor = Executors.newFixedThreadPool(8);
		List<Future<?>> futures = new ArrayList<Future<?>>();
		for (int i = 0; i < 8; i++) {
			futures.add(executor.submit(() -> {
				start.await();
				for (int j = 0; j < 500; j++) {
					statistics.executed("q" + (j % 3), null, now - j % 7, now);
				}
				return null;
			}));
		}
		start.countDown();
		for (Future<?> f : futures) {
			f.get(10, TimeUnit.SECONDS);
		}
		executor.shutdown();
		Map<String, QuerySpan> result = statistics.getStatisticsResult(new SimpleDateFormat("dd/MM/yyyy").format(new Date(now)));
		long total = 0;
		for (QuerySpan span : result.values()) {
			total += span.getExecutionCount();
		}
		assertEquals(8 * 500L, total, "no execution may be lost");
		assertEquals(0L, result.get("q0").getExecutionMinTime());
		assertEquals(6L, result.get("q0").getExecutionMaxTime());
	}

	@Test
	public void exceptionUtils_unwrapsReflectionWrappers() {
		Exception root = new IllegalStateException("root");
		assertSame(root, ExceptionUtils.unwrapThrowable(new UndeclaredThrowableException(new InvocationTargetException(root))));
		assertSame(root, ExceptionUtils.unwrapThrowable(root));
		assertNull(ExceptionUtils.unwrapThrowable(null));
	}
}
