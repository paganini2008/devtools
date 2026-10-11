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
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

/**
 * ForEachTest
 * 
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
@Timeout(10)
public class ForEachTest {

	@Test
	public void run_processesEveryElementAndShutsDown() {
		Set<Integer> seen = ConcurrentHashMap.newKeySet();
		List<Integer> input = new ArrayList<Integer>();
		for (int i = 0; i < 200; i++) {
			input.add(i);
		}
		ForEach.run(input, seen::add);
		assertEquals(200, seen.size());
	}

	@Test
	public void accept_ignoresNullElementsAndNullIterable() {
		AtomicInteger counter = new AtomicInteger();
		ExecutorService executor = Executors.newFixedThreadPool(2);
		ForEach<String> forEach = new ForEach<String>(executor, 0) {
			protected void process(String element) {
				counter.incrementAndGet();
			}
		};
		forEach.accept((String) null);
		forEach.accept((Iterable<String>) null);
		forEach.accept(Arrays.asList("a", null, "b"));
		forEach.join(true);
		assertEquals(2, counter.get());
		assertTrue(executor.isShutdown());
	}

	@Test
	public void accept_blocksWhenMaxPermitsReached() throws Exception {
		ExecutorService executor = Executors.newFixedThreadPool(1);
		CountDownLatch blocker = new CountDownLatch(1);
		ForEach<Integer> forEach = new ForEach<Integer>(executor, 1) {
			protected void process(Integer element) {
				GenericThreadPoolTest.await(blocker);
			}
		};
		try {
			forEach.accept(1);
			Thread producer = new Thread(() -> forEach.accept(2));
			producer.start();
			Thread.sleep(30);
			assertTrue(producer.isAlive());
			blocker.countDown();
			producer.join();
			forEach.join(false);
		} finally {
			executor.shutdownNow();
		}
	}

	@Test
	public void customQueue_determinesProcessingOrder() {
		List<Integer> order = Collections.synchronizedList(new ArrayList<Integer>());
		ForEach<Integer> forEach = new ForEach<Integer>(ExecutorUtils.directExecutor(), new PriorityBlockingQueue<Integer>(), 10) {
			protected void process(Integer element) {
				order.add(element);
			}
		};
		forEach.accept(Arrays.asList(3, 1, 2));
		forEach.join(false);
		assertEquals(Arrays.asList(3, 1, 2), order);
	}

	@Test
	public void processFailure_stillReleasesPermit() {
		ForEach<Integer> forEach = new ForEach<Integer>(ExecutorUtils.directExecutor(), 1) {
			protected void process(Integer element) {
				throw new IllegalStateException("fail " + element);
			}
		};
		IllegalStateException e = assertThrows(IllegalStateException.class, () -> forEach.accept(1));
		assertEquals("fail 1", e.getMessage());
		assertThrows(IllegalStateException.class, () -> forEach.accept(2));
		forEach.join(true);
	}

	@Test
	public void rejectedExecution_releasesPermitAndRethrows() {
		ForEach<Integer> forEach = new ForEach<Integer>(r -> {
			throw new RejectedExecutionException("full");
		}, 1) {
			protected void process(Integer element) {
			}
		};
		assertThrows(RejectedExecutionException.class, () -> forEach.accept(1));
		assertThrows(RejectedExecutionException.class, () -> forEach.accept(2));
		forEach.join(true);
	}

	@Test
	public void accept_interruptedWhileWaitingDropsElement() throws Exception {
		ExecutorService executor = Executors.newFixedThreadPool(1);
		CountDownLatch blocker = new CountDownLatch(1);
		AtomicInteger counter = new AtomicInteger();
		ForEach<Integer> forEach = new ForEach<Integer>(executor, 1) {
			protected void process(Integer element) {
				counter.incrementAndGet();
				GenericThreadPoolTest.await(blocker);
			}
		};
		try {
			forEach.accept(1);
			Thread producer = new Thread(() -> {
				Thread.currentThread().interrupt();
				forEach.accept(2);
			});
			producer.start();
			producer.join();
			blocker.countDown();
			forEach.join(false);
			assertEquals(1, counter.get());
		} finally {
			executor.shutdownNow();
		}
	}

	@Test
	public void join_shutsDownThreadPoolExecutor() {
		ThreadPool pool = ThreadUtils.commonPool(2);
		AtomicInteger counter = new AtomicInteger();
		ForEach<Integer> forEach = new ForEach<Integer>(pool, 4) {
			protected void process(Integer element) {
				counter.addAndGet(element);
			}
		};
		forEach.accept(Arrays.asList(1, 2, 3, 4));
		forEach.join(true);
		assertEquals(10, counter.get());
		assertTrue(pool.isShutdown());
	}

	@Test
	public void constructorWithThreadCount_createsOwnExecutor() {
		AtomicInteger counter = new AtomicInteger();
		ForEach<Integer> forEach = new ForEach<Integer>(2) {
			protected void process(Integer element) {
				counter.incrementAndGet();
			}
		};
		forEach.accept(Arrays.asList(1, 2, 3));
		forEach.join(true);
		assertEquals(3, counter.get());
	}

	@Test
	public void stress_concurrentProducersProcessEverything() throws Exception {
		ExecutorService executor = Executors.newFixedThreadPool(4);
		Map<Integer, AtomicInteger> seen = new ConcurrentHashMap<Integer, AtomicInteger>();
		ForEach<Integer> forEach = new ForEach<Integer>(executor, 8) {
			protected void process(Integer element) {
				seen.computeIfAbsent(element, k -> new AtomicInteger()).incrementAndGet();
			}
		};
		final int producers = 6;
		final int perProducer = 300;
		CountDownLatch start = new CountDownLatch(1);
		CountDownLatch done = new CountDownLatch(producers);
		for (int p = 0; p < producers; p++) {
			final int base = p * perProducer;
			new Thread(() -> {
				try {
					start.await();
					for (int i = 0; i < perProducer; i++) {
						forEach.accept(base + i);
					}
				} catch (InterruptedException ignored) {
				} finally {
					done.countDown();
				}
			}).start();
		}
		start.countDown();
		assertTrue(done.await(8, TimeUnit.SECONDS));
		forEach.join(true);
		assertEquals(producers * perProducer, seen.size());
		assertTrue(seen.values().stream().allMatch(c -> c.get() == 1));
	}

	@Test
	public void reactor_runsReactionChainThenFinalOnSuccess() {
		ExecutorService executor = Executors.newFixedThreadPool(2);
		List<String> events = Collections.synchronizedList(new ArrayList<String>());
		Reactor<Integer, Integer> reactor = new Reactor<Integer, Integer>(executor, 2, new Reactor.Consumer<Integer, Integer>() {

			public Integer apply(Integer action) {
				return action;
			}

			public boolean shouldReact(Integer result) {
				return result != null && result < 3;
			}

			public Integer onSuccess(Integer result, Integer action) {
				events.add(action + ":" + result);
				return result + 1;
			}
		});
		reactor.accept(1);
		reactor.join(true);
		assertEquals(Arrays.asList("1:1", "1:2", "1:3"), events);
	}

	@Test
	public void reactor_defaultConsumerCallsOnSuccessOnceWithNullResult() {
		AtomicInteger finals = new AtomicInteger();
		AtomicReference<Integer> last = new AtomicReference<Integer>(-1);
		Reactor<Integer, Integer> reactor = new Reactor<Integer, Integer>(2, new Reactor.Consumer<Integer, Integer>() {

			public Integer apply(Integer action) {
				return null;
			}

			public Integer onSuccess(Integer result, Integer action) {
				finals.incrementAndGet();
				last.set(result);
				return null;
			}
		});
		reactor.accept(7);
		reactor.join(true);
		assertEquals(1, finals.get());
		assertEquals(null, last.get());
	}

	@Test
	public void reactor_failureInvokesOnFailure() {
		AtomicReference<Exception> failure = new AtomicReference<Exception>();
		AtomicReference<Integer> failedAction = new AtomicReference<Integer>();
		Reactor<Integer, Integer> reactor = new Reactor<Integer, Integer>(ExecutorUtils.directExecutor(), 0,
				new Reactor.Consumer<Integer, Integer>() {

					public Integer apply(Integer action) throws Exception {
						throw new IllegalStateException("bad " + action);
					}

					public void onFailure(Integer action, Exception cause) {
						failedAction.set(action);
						failure.set(cause);
					}
				});
		reactor.accept(5);
		reactor.join(false);
		assertSame(5, failedAction.get());
		assertEquals("bad 5", failure.get().getMessage());
	}

	@Test
	public void reactor_manyActionsWithSmallPermitsDoNotDeadlock() {
		ExecutorService executor = Executors.newFixedThreadPool(2);
		AtomicInteger reactions = new AtomicInteger();
		Reactor<Integer, Integer> reactor = new Reactor<Integer, Integer>(executor, 2, new Reactor.Consumer<Integer, Integer>() {

			public Integer apply(Integer action) {
				return 0;
			}

			public boolean shouldReact(Integer result) {
				return result < 5;
			}

			public Integer onSuccess(Integer result, Integer action) {
				reactions.incrementAndGet();
				return result + 1;
			}
		});
		for (int i = 0; i < 50; i++) {
			reactor.accept(i);
		}
		reactor.join(true);
		assertEquals(50 * 6, reactions.get());
	}

}
