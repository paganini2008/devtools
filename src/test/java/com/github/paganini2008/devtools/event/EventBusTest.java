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
package com.github.paganini2008.devtools.event;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import com.github.paganini2008.devtools.multithreads.ExecutorUtils;

/**
 * EventBusTest
 * 
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
@Timeout(10)
public class EventBusTest {

	private final List<EventBus<?, ?>> buses = new ArrayList<EventBus<?, ?>>();

	private EventBus<TestEvent, String> newBus(int nThreads, boolean multicast) {
		EventBus<TestEvent, String> bus = new EventBus<TestEvent, String>(nThreads, multicast);
		buses.add(bus);
		return bus;
	}

	@AfterEach
	@Timeout(10)
	public void tearDown() {
		for (EventBus<?, ?> bus : buses) {
			bus.close();
		}
	}

	@Test
	public void event_exposesSourceArgumentAndTimestamp() {
		long before = System.currentTimeMillis();
		TestEvent event = new TestEvent(this, "hello");
		assertSame(this, event.getSource());
		assertEquals("hello", event.getArgument());
		event.setArgument("bye");
		assertEquals("bye", event.getArgument());
		assertTrue(event.getTimestamp() >= before);
	}

	@Test
	public void pubSubMulticast_deliversEveryEventToEverySubscriber() throws Exception {
		EventBus<TestEvent, String> bus = newBus(4, true);
		CountDownLatch latch = new CountDownLatch(6);
		Map<String, List<String>> received = new ConcurrentHashMap<String, List<String>>();
		for (String name : new String[] { "a", "b", "c" }) {
			received.put(name, Collections.synchronizedList(new ArrayList<String>()));
			bus.subscribe(new Subscriber() {
				public void onEventFired(TestEvent event) {
					received.get(name).add(event.getArgument());
					latch.countDown();
				}
			});
		}
		bus.publish(new TestEvent(this, "e1"));
		bus.publish(new TestEvent(this, "e2"));
		assertTrue(latch.await(5, TimeUnit.SECONDS));
		for (List<String> list : received.values()) {
			synchronized (list) {
				Collections.sort(list);
				assertEquals("[e1, e2]", list.toString());
			}
		}
	}

	@Test
	public void pubSubWithoutMulticast_deliversOnlyFirstEventThenDropsSubscribers() throws Exception {
		EventBus<TestEvent, String> bus = new EventBus<TestEvent, String>(ExecutorUtils.directExecutor(), false, false);
		buses.add(bus);
		List<String> received = Collections.synchronizedList(new ArrayList<String>());
		bus.subscribe(new Subscriber() {
			public void onEventFired(TestEvent event) {
				received.add("a:" + event.getArgument());
			}
		});
		bus.subscribe(new Subscriber() {
			public void onEventFired(TestEvent event) {
				received.add("b:" + event.getArgument());
			}
		});
		bus.publish(new TestEvent(this, "1"));
		bus.publish(new TestEvent(this, "2"));
		Collections.sort(received);
		assertEquals("[a:1, b:1]", received.toString());
	}

	@Test
	public void queueMulticast_distributesEventsRoundRobin() {
		EventBus<TestEvent, String> bus = new EventBus<TestEvent, String>(ExecutorUtils.directExecutor(), true, false);
		buses.add(bus);
		List<String> received = new ArrayList<String>();
		QueueSubscriber a = new QueueSubscriber("a", received);
		QueueSubscriber b = new QueueSubscriber("b", received);
		bus.subscribe(a);
		bus.subscribe(b);
		bus.subscribe(a);
		for (int i = 0; i < 4; i++) {
			bus.publish(new TestEvent(this, String.valueOf(i)));
		}
		assertEquals("[a:0, b:1, a:2, b:3]", received.toString());
		bus.unsubscribe(a);
		bus.publish(new TestEvent(this, "4"));
		assertEquals("b:4", received.get(4));
		bus.unsubscribe(b);
		bus.publish(new TestEvent(this, "5"));
		assertEquals(5, received.size());
	}

	@Test
	public void queueWithoutMulticast_consumesSubscribersInFifoOrder() {
		EventBus<TestEvent, String> bus = new EventBus<TestEvent, String>(ExecutorUtils.directExecutor(), false, false);
		buses.add(bus);
		List<String> received = new ArrayList<String>();
		bus.subscribe(new QueueSubscriber("a", received));
		bus.subscribe(new QueueSubscriber("b", received));
		bus.subscribe(new QueueSubscriber("c", received));
		for (int i = 0; i < 5; i++) {
			bus.publish(new TestEvent(this, String.valueOf(i)));
		}
		assertEquals("[a:0, b:1, c:2]", received.toString());
	}

	@Test
	public void unsubscribe_stopsDelivery() {
		EventBus<TestEvent, String> bus = new EventBus<TestEvent, String>(ExecutorUtils.directExecutor(), true, false);
		buses.add(bus);
		AtomicInteger counter = new AtomicInteger();
		Subscriber subscriber = new Subscriber() {
			public void onEventFired(TestEvent event) {
				counter.incrementAndGet();
			}
		};
		bus.publish(new TestEvent(this, "no group yet"));
		bus.unsubscribe(subscriber);
		bus.subscribe(subscriber);
		bus.subscribe(subscriber);
		bus.publish(new TestEvent(this, "x"));
		assertEquals(1, counter.get());
		bus.unsubscribe(subscriber);
		bus.publish(new TestEvent(this, "y"));
		assertEquals(1, counter.get());
	}

	@Test
	public void eventsAreRoutedByExactEventClass() {
		EventBus<Event<String>, String> bus = new EventBus<Event<String>, String>(ExecutorUtils.directExecutor(), true, false);
		buses.add(bus);
		List<String> received = new ArrayList<String>();
		bus.subscribe(new EventSubscriber<Event<String>, String>() {
			public void onEventFired(Event<String> event) {
				received.add("raw:" + event.getArgument());
			}
		});
		bus.publish(new TestEvent(this, "typed"));
		assertTrue(received.isEmpty());
	}

	@Test
	public void subscribe_rejectsNullAndUntypedSubscribers() {
		EventBus<TestEvent, String> bus = newBus(1, true);
		IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> bus.subscribe(null));
		assertEquals("Nullable subscriber", e.getMessage());
		assertThrows(IllegalArgumentException.class, () -> bus.unsubscribe(null));
		IllegalStateException ise = assertThrows(IllegalStateException.class, () -> bus.subscribe(event -> {
		}));
		assertTrue(ise.getMessage().contains("EventBusTest"));
	}

	@Test
	public void subscriberDefaults() {
		Subscriber a = new Subscriber() {
			public void onEventFired(TestEvent event) {
			}
		};
		assertTrue(a.isPubSub());
		assertEquals(0, a.compareTo(a));
	}

	@Test
	public void close_shutsDownOwnedExecutorAfterPendingEvents() throws Exception {
		ExecutorService executor = Executors.newFixedThreadPool(2);
		EventBus<TestEvent, String> bus = new EventBus<TestEvent, String>(executor, true, true);
		AtomicInteger counter = new AtomicInteger();
		bus.subscribe(new Subscriber() {
			public void onEventFired(TestEvent event) {
				counter.incrementAndGet();
			}
		});
		for (int i = 0; i < 20; i++) {
			bus.publish(new TestEvent(this, "e"));
		}
		bus.close();
		assertEquals(20, counter.get());
		assertTrue(executor.awaitTermination(5, TimeUnit.SECONDS));
	}

	@Test
	public void stress_concurrentPublishersDeliverEveryEvent() throws Exception {
		EventBus<TestEvent, String> bus = newBus(4, true);
		AtomicInteger a = new AtomicInteger();
		AtomicInteger b = new AtomicInteger();
		bus.subscribe(new Subscriber() {
			public void onEventFired(TestEvent event) {
				a.incrementAndGet();
			}
		});
		bus.subscribe(new Subscriber() {
			public void onEventFired(TestEvent event) {
				b.incrementAndGet();
			}
		});
		final int publishers = 6;
		final int perPublisher = 200;
		CountDownLatch start = new CountDownLatch(1);
		CountDownLatch done = new CountDownLatch(publishers);
		for (int p = 0; p < publishers; p++) {
			new Thread(() -> {
				try {
					start.await();
					for (int i = 0; i < perPublisher; i++) {
						bus.publish(new TestEvent(this, "x"));
					}
				} catch (InterruptedException ignored) {
				} finally {
					done.countDown();
				}
			}).start();
		}
		start.countDown();
		assertTrue(done.await(8, TimeUnit.SECONDS));
		bus.close();
		assertEquals(publishers * perPublisher, a.get());
		assertEquals(publishers * perPublisher, b.get());
	}

	@Test
	public void stress_subscribersUnsubscribingDuringPublishDoNotDeadlock() throws Exception {
		EventBus<TestEvent, String> bus = newBus(2, true);
		final int n = 50;
		CountDownLatch latch = new CountDownLatch(n);
		for (int i = 0; i < n; i++) {
			bus.subscribe(new Subscriber() {
				public void onEventFired(TestEvent event) {
					bus.unsubscribe(this);
					latch.countDown();
				}
			});
		}
		bus.publish(new TestEvent(this, "once"));
		assertTrue(latch.await(5, TimeUnit.SECONDS));
	}

	@Test
	public void stress_queueGroupSurvivesConcurrentSubscriptionChanges() throws Exception {
		EventBus<TestEvent, String> bus = new EventBus<TestEvent, String>(ExecutorUtils.directExecutor(), true, false);
		buses.add(bus);
		List<String> received = Collections.synchronizedList(new ArrayList<String>());
		QueueSubscriber stable = new QueueSubscriber("stable", received);
		bus.subscribe(stable);
		AtomicInteger errors = new AtomicInteger();
		CountDownLatch start = new CountDownLatch(1);
		Thread churn = new Thread(() -> {
			try {
				start.await();
				for (int i = 0; i < 2000; i++) {
					QueueSubscriber s = new QueueSubscriber("tmp", received);
					bus.subscribe(s);
					bus.unsubscribe(s);
				}
			} catch (Throwable e) {
				errors.incrementAndGet();
			}
		});
		Thread publisher = new Thread(() -> {
			try {
				start.await();
				for (int i = 0; i < 2000; i++) {
					bus.publish(new TestEvent(this, "x"));
				}
			} catch (Throwable e) {
				errors.incrementAndGet();
			}
		});
		churn.start();
		publisher.start();
		start.countDown();
		churn.join();
		publisher.join();
		assertEquals(0, errors.get());
		assertEquals(2000, received.size());
	}

	@Test
	public void publish_nullEventIsIgnored() {
		EventBus<TestEvent, String> bus = new EventBus<TestEvent, String>(ExecutorUtils.directExecutor(), true, false);
		buses.add(bus);
		AtomicInteger counter = new AtomicInteger();
		bus.subscribe(new Subscriber() {
			public void onEventFired(TestEvent event) {
				counter.incrementAndGet();
			}
		});
		bus.publish(null);
		assertEquals(0, counter.get());
	}

	@Test
	public void subscriberPublishingNestedEventsOnSaturatedBusDoesNotDeadlock() throws Exception {
		// 1 thread => 2 permits: every subscriber republishes, which used to block forever
		EventBus<TestEvent, String> bus = newBus(1, true);
		final int depth = 8;
		CountDownLatch latch = new CountDownLatch(2 * (depth + 1));
		AtomicInteger delivered = new AtomicInteger();
		for (int i = 0; i < 2; i++) {
			final boolean republisher = i == 0;
			bus.subscribe(new Subscriber() {
				public void onEventFired(TestEvent event) {
					delivered.incrementAndGet();
					int level = Integer.parseInt(event.getArgument());
					latch.countDown();
					if (republisher && level < depth) {
						bus.publish(new TestEvent(this, String.valueOf(level + 1)));
					}
				}
			});
		}
		bus.publish(new TestEvent(this, "0"));
		assertTrue(latch.await(5, TimeUnit.SECONDS));
		bus.close();
		assertEquals(2 * (depth + 1), delivered.get());
	}

	@Test
	public void closeFromSubscriberDoesNotDeadlockAndShutsDownAsynchronously() throws Exception {
		ExecutorService executor = Executors.newFixedThreadPool(2);
		EventBus<TestEvent, String> bus = new EventBus<TestEvent, String>(executor, true, true);
		CountDownLatch returned = new CountDownLatch(1);
		bus.subscribe(new Subscriber() {
			public void onEventFired(TestEvent event) {
				bus.close();
				returned.countDown();
			}
		});
		bus.publish(new TestEvent(this, "close"));
		assertTrue(returned.await(5, TimeUnit.SECONDS));
		assertTrue(executor.awaitTermination(5, TimeUnit.SECONDS));
	}

	@Test
	public void stress_manyRoundsOfConcurrentPublishAndClose() throws Exception {
		for (int round = 0; round < 50; round++) {
			EventBus<TestEvent, String> bus = new EventBus<TestEvent, String>(3, true);
			AtomicInteger a = new AtomicInteger();
			AtomicInteger nested = new AtomicInteger();
			bus.subscribe(new Subscriber() {
				public void onEventFired(TestEvent event) {
					a.incrementAndGet();
				}
			});
			bus.subscribe(new Subscriber() {
				public void onEventFired(TestEvent event) {
					if ("x".equals(event.getArgument())) {
						bus.publish(new TestEvent(this, "y"));
					} else {
						nested.incrementAndGet();
					}
				}
			});
			final int publishers = 4;
			final int perPublisher = 50;
			CountDownLatch start = new CountDownLatch(1);
			CountDownLatch done = new CountDownLatch(publishers);
			for (int p = 0; p < publishers; p++) {
				new Thread(() -> {
					try {
						start.await();
						for (int i = 0; i < perPublisher; i++) {
							bus.publish(new TestEvent(this, "x"));
						}
					} catch (InterruptedException ignored) {
					} finally {
						done.countDown();
					}
				}).start();
			}
			start.countDown();
			assertTrue(done.await(5, TimeUnit.SECONDS), "round " + round);
			bus.close();
			// every "x" reaches both subscribers; every nested "y" reaches both as well
			assertEquals(2 * publishers * perPublisher, a.get(), "round " + round);
			assertEquals(publishers * perPublisher, nested.get(), "round " + round);
		}
	}

	static class TestEvent extends Event<String> {

		private static final long serialVersionUID = 1L;

		TestEvent(Object source, String argument) {
			super(source, argument);
		}
	}

	abstract static class Subscriber implements EventSubscriber<TestEvent, String> {
	}

	static class QueueSubscriber implements EventSubscriber<TestEvent, String> {

		final String name;
		final List<String> received;

		QueueSubscriber(String name, List<String> received) {
			this.name = name;
			this.received = received;
		}

		public void onEventFired(TestEvent event) {
			received.add(name + ":" + event.getArgument());
		}

		public boolean isPubSub() {
			return false;
		}
	}

}
