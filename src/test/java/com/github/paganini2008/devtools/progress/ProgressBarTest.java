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
package com.github.paganini2008.devtools.progress;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

/**
 * ProgressBarTest
 *
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class ProgressBarTest {

	private final AtomicLong now = new AtomicLong(0);
	private final RecordingRenderer renderer = new RecordingRenderer();

	private ProgressBar.Builder builder() {
		return ProgressBar.builder().taskName("Job").renderer(renderer).clock(now::get);
	}

	@Test
	public void defaults_unknownTotalAndNewState() {
		ProgressBar bar = builder().build();
		assertEquals("Job", bar.getTaskName());
		assertEquals("", bar.getUnit());
		assertEquals(-1L, bar.getTotal());
		assertEquals(-1d, bar.getPercent());
		assertEquals(0L, bar.getElapsedMillis());
		assertEquals(0d, bar.getRatePerSecond());
		assertEquals(-1L, bar.getEtaMillis());
		assertEquals(ProgressState.NEW, bar.getState());
		assertFalse(bar.isFinished());
		assertTrue(renderer.rendered.isEmpty());
		ProgressBar unnamed = ProgressBar.builder().taskName(null).unit(null).total(0).build();
		assertEquals("", unnamed.getTaskName());
		assertTrue(unnamed.toString().startsWith("0  0.0/s  Elapsed 00:00:00"));
	}

	@Test
	public void create_usesConsoleRendererByDefault() {
		ProgressBar bar = ProgressBar.create("Import", 10);
		assertEquals(10L, bar.getTotal());
		assertEquals("Import", bar.getTaskName());
	}

	@Test
	public void start_rendersImmediatelyAndOnlyOnce() {
		ProgressBar bar = builder().total(10).build();
		bar.start();
		bar.start();
		assertEquals(ProgressState.RUNNING, bar.getState());
		assertEquals(1, renderer.rendered.size());
		assertEquals(0L, renderer.last().getCompleted());
	}

	@Test
	public void step_startsAutomatically() {
		ProgressBar bar = builder().total(10).build();
		bar.step();
		assertEquals(ProgressState.RUNNING, bar.getState());
		assertEquals(1L, bar.getCompleted());
		assertEquals(1, renderer.rendered.size(), "start render only, the step is throttled");
	}

	@Test
	public void stepTo_startsAutomaticallyAndNeverDecreases() {
		ProgressBar bar = builder().total(10).build();
		bar.stepTo(5);
		assertEquals(ProgressState.RUNNING, bar.getState());
		bar.stepTo(3);
		assertEquals(5L, bar.getCompleted());
		bar.stepTo(7);
		assertEquals(7L, bar.getCompleted());
	}

	@Test
	public void stepBy_negativeIsRejected() {
		ProgressBar bar = builder().build();
		IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> bar.stepBy(-1));
		assertEquals("Negative step: -1", e.getMessage());
	}

	@Test
	public void throttle_rendersAtMostOncePerInterval() {
		ProgressBar bar = builder().total(100).refreshInterval(200, TimeUnit.MILLISECONDS).build();
		now.set(1000);
		bar.start();
		bar.step();
		now.set(1199);
		bar.step();
		assertEquals(1, renderer.rendered.size());
		now.set(1200);
		bar.step();
		assertEquals(2, renderer.rendered.size());
		assertEquals(3L, renderer.last().getCompleted());
		now.set(1300);
		bar.stepBy(10);
		assertEquals(2, renderer.rendered.size());
		now.set(1350);
		bar.complete();
		assertEquals(3, renderer.rendered.size(), "completion is always rendered");
		assertEquals(ProgressState.COMPLETED, renderer.last().getState());
		assertEquals(13L, renderer.last().getCompleted());
		assertEquals(1, renderer.finished.size());
		assertSame(renderer.last(), renderer.finished.get(0));
	}

	@Test
	public void zeroRefreshInterval_rendersEveryStep() {
		ProgressBar bar = builder().refreshInterval(0, TimeUnit.SECONDS).build();
		bar.step();
		bar.step();
		bar.step();
		assertEquals(4, renderer.rendered.size());
	}

	@Test
	public void rateEtaAndPercent_areExact() {
		ProgressBar bar = builder().total(1000).unit("rows").build();
		bar.start();
		now.set(5000);
		bar.stepBy(250);
		assertEquals(25d, bar.getPercent());
		assertEquals(5000L, bar.getElapsedMillis());
		assertEquals(50d, bar.getRatePerSecond());
		assertEquals(15000L, bar.getEtaMillis());
		ProgressSnapshot s = bar.snapshot();
		assertEquals("Job", s.getTaskName());
		assertEquals(250L, s.getCompleted());
		assertEquals(1000L, s.getTotal());
		assertEquals("rows", s.getUnit());
		assertEquals(40, s.getWidth());
		assertEquals(25d, s.getPercent());
		assertEquals(5000L, s.getElapsedMillis());
		assertEquals(50d, s.getRatePerSecond());
		assertEquals(15000L, s.getEtaMillis());
		assertNull(s.getExtraMessage());
		assertEquals(ProgressState.RUNNING, s.getState());
		assertNull(s.getFailure());
		bar.setExtraMessage("table=t_user");
		assertEquals("table=t_user", bar.snapshot().getExtraMessage());
	}

	@Test
	public void percentIsCappedAndEtaZeroWhenOvershooting() {
		ProgressBar bar = builder().total(10).build();
		bar.start();
		now.set(1000);
		bar.stepBy(15);
		assertEquals(100d, bar.getPercent());
		assertEquals(0L, bar.getEtaMillis());
	}

	@Test
	public void setTotal_nonPositiveMeansUnknown() {
		ProgressBar bar = builder().total(10).build();
		bar.setTotal(0);
		assertEquals(-1L, bar.getTotal());
		bar.setTotal(20);
		assertEquals(20L, bar.getTotal());
	}

	@Test
	public void elapsedFreezesAfterFinish_andStepsAreIgnored() {
		ProgressBar bar = builder().total(10).build();
		now.set(100);
		bar.step();
		now.set(600);
		bar.complete();
		now.set(5000);
		assertEquals(500L, bar.getElapsedMillis());
		bar.step();
		bar.stepTo(9);
		bar.stepBy(3);
		assertEquals(1L, bar.getCompleted());
		bar.complete();
		bar.fail(new IllegalStateException());
		assertEquals(ProgressState.COMPLETED, bar.getState());
		assertEquals(1, renderer.finished.size());
		bar.start();
		assertEquals(ProgressState.COMPLETED, bar.getState());
	}

	@Test
	public void completeWithoutStart_hasZeroElapsed() {
		ProgressBar bar = builder().build();
		now.set(700);
		bar.complete();
		assertTrue(bar.isFinished());
		assertEquals(0L, bar.getElapsedMillis());
		assertEquals(1, renderer.rendered.size());
	}

	@Test
	public void fail_recordsCauseAndRenders() {
		ProgressBar bar = builder().total(10).build();
		bar.stepBy(3);
		IllegalStateException cause = new IllegalStateException("boom");
		now.set(3000);
		bar.fail(cause);
		assertEquals(ProgressState.FAILED, bar.getState());
		assertTrue(bar.isFinished());
		assertSame(cause, renderer.last().getFailure());
		assertEquals(ProgressState.FAILED, renderer.finished.get(0).getState());
		bar.close();
		assertEquals(ProgressState.FAILED, bar.getState(), "close after fail keeps FAILED");
	}

	@Test
	public void close_completesAndIsIdempotent() {
		RecordingRenderer r = renderer;
		try (ProgressBar bar = builder().total(3).build()) {
			for (int i = 0; i < 3; i++) {
				bar.step();
			}
		}
		assertEquals(ProgressState.COMPLETED, r.last().getState());
		assertEquals(3L, r.last().getCompleted());
		assertEquals(1, r.finished.size());
	}

	@Test
	public void rendererExceptions_areIgnored() {
		ProgressBar bar = ProgressBar.builder().refreshInterval(0, TimeUnit.MILLISECONDS).renderer(s -> {
			throw new IllegalStateException("broken");
		}).build();
		bar.step();
		bar.step();
		bar.complete();
		assertEquals(2L, bar.getCompleted());
		assertEquals(ProgressState.COMPLETED, bar.getState());
	}

	@Test
	public void protectedClockHook_canBeOverridden() {
		final AtomicLong time = new AtomicLong(10);
		ProgressBar.Builder b = ProgressBar.builder().renderer(ProgressRenderers.noop()).total(4);
		ProgressBar bar = new ProgressBar(b) {
			@Override
			protected long currentTimeMillis() {
				return time.get();
			}
		};
		bar.start();
		time.set(2010);
		bar.stepBy(2);
		assertEquals(2000L, bar.getElapsedMillis());
		assertEquals(1d, bar.getRatePerSecond());
		assertEquals(2000L, bar.getEtaMillis());
	}

	@Test
	public void builder_rejectsInvalidValues() {
		assertThrows(IllegalArgumentException.class, () -> ProgressBar.builder().width(-1));
		assertThrows(IllegalArgumentException.class, () -> ProgressBar.builder().refreshInterval(-1, TimeUnit.SECONDS));
		assertThrows(IllegalArgumentException.class, () -> ProgressBar.builder().refreshInterval(1, null));
		assertEquals(0, ProgressBar.builder().width(0).renderer(ProgressRenderers.noop()).build().snapshot().getWidth());
	}

	@Test
	public void toString_describesCurrentProgress() {
		ProgressBar bar = builder().total(4).width(4).build();
		bar.start();
		now.set(1000);
		bar.stepBy(2);
		assertEquals("Job [==> ]  50% 2/4  2.0/s  ETA 00:00:01", bar.toString());
	}

	@Test
	@Timeout(10)
	public void concurrentSteps_areCountedExactly() throws Exception {
		final ProgressBar bar = ProgressBar.builder().total(8 * 10000 * 3).renderer(renderer)
				.refreshInterval(0, TimeUnit.MILLISECONDS).build();
		final CountDownLatch go = new CountDownLatch(1);
		final CountDownLatch done = new CountDownLatch(8);
		List<Thread> threads = new ArrayList<Thread>();
		for (int t = 0; t < 8; t++) {
			Thread thread = new Thread(() -> {
				try {
					go.await();
					for (int i = 0; i < 10000; i++) {
						bar.stepBy(3);
					}
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				} finally {
					done.countDown();
				}
			});
			threads.add(thread);
			thread.start();
		}
		go.countDown();
		assertTrue(done.await(8, TimeUnit.SECONDS));
		bar.complete();
		assertEquals(240000L, bar.getCompleted());
		assertEquals(100d, bar.getPercent());
		assertEquals(0, renderer.overlaps.get(), "renderings must never overlap");
		assertEquals(240000L, renderer.last().getCompleted());
		assertEquals(ProgressState.COMPLETED, renderer.last().getState());
		for (ProgressSnapshot s : renderer.rendered.subList(0, renderer.rendered.size() - 1)) {
			assertEquals(ProgressState.RUNNING, s.getState(), "no running snapshot after the final one");
		}
	}

	@Test
	public void stateIsFinished() {
		assertFalse(ProgressState.NEW.isFinished());
		assertFalse(ProgressState.RUNNING.isFinished());
		assertTrue(ProgressState.COMPLETED.isFinished());
		assertTrue(ProgressState.FAILED.isFinished());
	}

	@Test
	public void snapshotConstructor_normalizesNulls() {
		ProgressSnapshot s = new ProgressSnapshot(null, 0, -5, null, 10, -3, null, null, null);
		assertEquals("", s.getTaskName());
		assertEquals("", s.getUnit());
		assertEquals(-1L, s.getTotal());
		assertFalse(s.isTotalKnown());
		assertEquals(0L, s.getElapsedMillis());
		assertEquals(ProgressState.NEW, s.getState());
	}

}
