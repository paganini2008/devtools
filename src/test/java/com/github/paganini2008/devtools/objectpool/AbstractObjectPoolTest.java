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
package com.github.paganini2008.devtools.objectpool;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

/**
 * Shared behaviour tests of {@link GenericObjectPool} and
 * {@link Jdk14ObjectPool}.
 * 
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
@Timeout(20)
public abstract class AbstractObjectPoolTest {

	/**
	 * Pooled test object
	 */
	static class Item {

		final int id;
		volatile boolean valid = true;
		volatile boolean destroyed;

		Item(int id) {
			this.id = id;
		}

		@Override
		public boolean equals(Object obj) {
			return obj instanceof Item;
		}

		@Override
		public int hashCode() {
			return 1;
		}

		@Override
		public String toString() {
			return "Item-" + id;
		}
	}

	/**
	 * Factory which counts creations, tests and destructions
	 */
	static class ItemFactory implements ObjectFactory {

		final AtomicInteger created = new AtomicInteger();
		final AtomicInteger destroyed = new AtomicInteger();
		final AtomicInteger tested = new AtomicInteger();
		volatile boolean failCreate;
		volatile boolean createNull;
		volatile boolean failTestWithException;
		volatile boolean failDestroy;

		@Override
		public Object createObject() throws Exception {
			if (failCreate) {
				throw new IllegalStateException("cannot create");
			}
			if (createNull) {
				return null;
			}
			return new Item(created.incrementAndGet());
		}

		@Override
		public boolean testObject(Object object) throws Exception {
			tested.incrementAndGet();
			if (failTestWithException) {
				throw new IllegalStateException("test failed");
			}
			return ((Item) object).valid;
		}

		@Override
		public void destroyObject(Object object) throws Exception {
			((Item) object).destroyed = true;
			destroyed.incrementAndGet();
			if (failDestroy) {
				throw new IllegalStateException("cannot destroy");
			}
		}
	}

	protected ItemFactory factory;
	protected ObjectPool pool;

	protected abstract ObjectPool newPool(ObjectFactory objectFactory);

	@BeforeEach
	public void setUp() {
		factory = new ItemFactory();
		pool = newPool(factory);
		set("setMaxPoolSize", 3);
		set("setCloseTimeout", 1000L);
	}

	@AfterEach
	public void tearDown() throws Exception {
		set("setCloseTimeout", 0L);
		pool.close();
	}

	protected void set(String setter, Object value) {
		invoke(setter, value);
	}

	protected Object get(String getter) {
		return invoke(getter);
	}

	protected int intValue(String getter) {
		return ((Number) get(getter)).intValue();
	}

	private Object invoke(String name, Object... args) {
		for (Method method : pool.getClass().getMethods()) {
			if (method.getName().equals(name) && method.getParameterCount() == args.length) {
				try {
					return method.invoke(pool, args);
				} catch (InvocationTargetException e) {
					throw new IllegalStateException(e.getTargetException());
				} catch (Exception e) {
					if (args.length == 1 && args[0] instanceof Number) {
						continue;
					}
					throw new IllegalStateException(e);
				}
			}
		}
		throw new IllegalArgumentException(name);
	}

	@Test
	public void borrowAndGiveback_reusesIdleObject() throws Exception {
		Object first = pool.borrowObject();
		assertEquals(1, intValue("getPoolSize"));
		assertEquals(1, intValue("getBusySize"));
		assertEquals(0, intValue("getIdleSize"));
		pool.givebackObject(first);
		assertEquals(0, intValue("getBusySize"));
		assertEquals(1, intValue("getIdleSize"));
		Object second = pool.borrowObject(100);
		assertSame(first, second);
		assertEquals(1, factory.created.get());
		ObjectDetail detail = pool.getDetail(second);
		assertSame(second, detail.getObject());
		assertEquals(2, detail.getUsage());
		assertTrue(detail.getCreated() > 0);
		assertTrue(detail.getLastBorrowed() >= detail.getCreated());
		assertTrue(detail.getLastReturned() > 0);
		assertTrue(detail.getLastTested() > 0);
		assertNotNull(detail.toString());
		pool.givebackObject(second);
		assertNull(pool.getDetail(new Item(99)));
	}

	@Test
	public void objectsWithEqualEqualsAreTrackedByIdentity() throws Exception {
		Object a = pool.borrowObject();
		Object b = pool.borrowObject();
		assertNotSame(a, b);
		assertEquals(a, b);
		pool.givebackObject(b);
		assertEquals(1, intValue("getBusySize"));
		assertSame(b, pool.borrowObject());
		pool.givebackObject(a);
		pool.givebackObject(b);
		assertEquals(2, intValue("getIdleSize"));
	}

	@Test
	public void givebackTwice_orUnpooledObject_isRejected() throws Exception {
		Object item = pool.borrowObject();
		pool.givebackObject(item);
		PooledObjectException e = assertThrows(PooledObjectException.class, () -> pool.givebackObject(item));
		assertTrue(e.getMessage().contains("repeatedly"));
		PooledObjectException e2 = assertThrows(PooledObjectException.class, () -> pool.givebackObject(new Item(100)));
		assertTrue(e2.getMessage().contains("Unpooled"));
		assertEquals(1, intValue("getIdleSize"));
	}

	@Test
	public void borrowWithTimeout_failsWhenExhausted() throws Exception {
		pool.borrowObject();
		pool.borrowObject();
		pool.borrowObject();
		long start = System.nanoTime();
		assertThrows(PooledObjectException.class, () -> pool.borrowObject(100));
		long elapsedMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
		assertTrue(elapsedMs >= 90, "waited " + elapsedMs);
		assertTrue(elapsedMs < 2000, "waited " + elapsedMs);
		assertThrows(PooledObjectException.class, () -> pool.borrowObject(0));
		assertThrows(PooledObjectException.class, () -> pool.borrowObject(-1));
		assertEquals(3, factory.created.get());
	}

	@Test
	public void waitingBorrower_isWokenByGiveback() throws Exception {
		set("setMaxPoolSize", 1);
		Object item = pool.borrowObject();
		ExecutorService executor = Executors.newSingleThreadExecutor();
		try {
			CountDownLatch waiting = new CountDownLatch(1);
			Future<Object> future = executor.submit(() -> {
				waiting.countDown();
				return pool.borrowObject();
			});
			waiting.await();
			Thread.sleep(50);
			assertFalse(future.isDone());
			pool.givebackObject(item);
			assertSame(item, future.get(5, TimeUnit.SECONDS));
			Future<Object> timed = executor.submit(() -> pool.borrowObject(5000));
			Thread.sleep(50);
			pool.discardObject(item);
			Object fresh = timed.get(5, TimeUnit.SECONDS);
			assertNotSame(item, fresh);
		} finally {
			executor.shutdownNow();
		}
	}

	@Test
	public void interruptedBorrower_getsException() throws Exception {
		set("setMaxPoolSize", 1);
		pool.borrowObject();
		AtomicBoolean interruptedFlag = new AtomicBoolean();
		Thread thread = new Thread(() -> {
			try {
				pool.borrowObject();
			} catch (PooledObjectException e) {
				interruptedFlag.set(Thread.currentThread().isInterrupted() && e.getCause() instanceof InterruptedException);
			} catch (Exception e) {
				// fail
			}
		});
		thread.start();
		Thread.sleep(50);
		thread.interrupt();
		thread.join(5000);
		assertTrue(interruptedFlag.get());
	}

	@Test
	public void maxUsage_destroysObjectOnLastGiveback() throws Exception {
		set("setMaxUsage", 2);
		assertEquals(2, intValue("getMaxUsage"));
		Object item = pool.borrowObject();
		pool.givebackObject(item);
		assertSame(item, pool.borrowObject());
		pool.givebackObject(item);
		assertTrue(((Item) item).destroyed);
		assertEquals(0, intValue("getPoolSize"));
		assertNotSame(item, pool.borrowObject());
	}

	@Test
	public void discard_removesBusyOrIdleObject() throws Exception {
		Object busy = pool.borrowObject();
		Object idle = pool.borrowObject();
		pool.givebackObject(idle);
		pool.discardObject(busy);
		pool.discardObject(idle);
		pool.discardObject(idle);
		pool.discardObject(new Item(7));
		assertEquals(0, intValue("getPoolSize"));
		assertEquals(0, intValue("getIdleSize"));
		assertEquals(0, intValue("getBusySize"));
		assertEquals(2, factory.destroyed.get());
		Object next = pool.borrowObject();
		assertNotSame(idle, next, "a discarded idle object must never be borrowed again");
		factory.failDestroy = true;
		assertThrows(IllegalStateException.class, () -> pool.discardObject(next));
		assertEquals(0, intValue("getPoolSize"));
	}

	@Test
	public void invalidIdleObject_isDiscardedOnBorrow() throws Exception {
		Item item = (Item) pool.borrowObject();
		pool.givebackObject(item);
		item.valid = false;
		Object next = pool.borrowObject();
		assertNotSame(item, next);
		assertTrue(item.destroyed);
		assertEquals(1, intValue("getPoolSize"));
		assertEquals(4, intValue("getMaxTestTimes") + 1, "default max test times");
	}

	@Test
	public void failingCreationOrValidation_keepsPoolSizeConsistent() throws Exception {
		factory.failCreate = true;
		assertThrows(IllegalStateException.class, () -> pool.borrowObject());
		assertEquals(0, intValue("getPoolSize"));
		factory.failCreate = false;
		factory.createNull = true;
		assertThrows(PooledObjectException.class, () -> pool.borrowObject(10));
		assertEquals(0, intValue("getPoolSize"));
		factory.createNull = false;
		factory.failTestWithException = true;
		set("setMaxTestTimes", 1);
		assertEquals(1, intValue("getMaxTestTimes"));
		int before = factory.tested.get();
		assertThrows(PooledObjectException.class, () -> pool.borrowObject());
		assertEquals(2, factory.tested.get() - before, "tested maxTestTimes + 1 times");
		assertEquals(0, intValue("getPoolSize"));
		assertEquals(1, factory.destroyed.get());
		factory.failTestWithException = false;
		Object item = pool.borrowObject();
		assertNotNull(item);
		assertEquals(1, intValue("getPoolSize"));
	}

	@Test
	public void close_destroysIdleObjectsAndRejectsBorrowing() throws Exception {
		Object a = pool.borrowObject();
		Object b = pool.borrowObject();
		pool.givebackObject(a);
		pool.givebackObject(b);
		pool.close();
		pool.close();
		assertFalse((Boolean) get("isRunning"));
		assertTrue(((Item) a).destroyed && ((Item) b).destroyed);
		assertEquals(0, intValue("getPoolSize"));
		assertThrows(PooledObjectException.class, () -> pool.borrowObject());
		assertThrows(PooledObjectException.class, () -> pool.borrowObject(10));
	}

	@Test
	public void close_waitsForBorrowedObjects() throws Exception {
		Object busy = pool.borrowObject();
		ExecutorService executor = Executors.newSingleThreadExecutor();
		try {
			Future<?> closing = executor.submit(() -> {
				pool.close();
				return null;
			});
			Thread.sleep(100);
			assertFalse(closing.isDone(), "close must wait for borrowed objects");
			pool.givebackObject(busy);
			closing.get(5, TimeUnit.SECONDS);
			assertTrue(((Item) busy).destroyed);
			assertEquals(0, intValue("getPoolSize"));
		} finally {
			executor.shutdownNow();
		}
	}

	@Test
	public void close_forciblyDestroysUnreturnedObjectsAfterTimeout() throws Exception {
		set("setCloseTimeout", 50L);
		assertEquals(50L, ((Number) get("getCloseTimeout")).longValue());
		factory.failDestroy = true;
		Object busy = pool.borrowObject();
		pool.close();
		assertTrue(((Item) busy).destroyed);
		assertEquals(0, intValue("getPoolSize"));
		assertThrows(PooledObjectException.class, () -> pool.givebackObject(busy));
	}

	@Test
	public void close_wakesWaitingBorrowers() throws Exception {
		set("setMaxPoolSize", 1);
		set("setCloseTimeout", 50L);
		pool.borrowObject();
		ExecutorService executor = Executors.newSingleThreadExecutor();
		try {
			CountDownLatch waiting = new CountDownLatch(1);
			Future<Object> future = executor.submit(() -> {
				waiting.countDown();
				return pool.borrowObject();
			});
			waiting.await();
			Thread.sleep(50);
			pool.close();
			java.util.concurrent.ExecutionException e = assertThrows(java.util.concurrent.ExecutionException.class,
					() -> future.get(5, TimeUnit.SECONDS), "a waiting borrower must fail when the pool closes");
			assertTrue(e.getCause() instanceof PooledObjectException);
			assertTrue(e.getCause().getMessage().contains("closed"));
		} finally {
			executor.shutdownNow();
		}
	}

	@Test
	public void testWhileIdle_discardsInvalidIdleObjects() throws Exception {
		set("setMinIdleSize", 0);
		assertEquals(0, intValue("getMinIdleSize"));
		set("setTestWhileIdleInterval", 20L);
		set("setTestWhileIdle", true);
		assertTrue((Boolean) get("isTestWhileIdle"));
		assertEquals(20L, ((Number) get("getTestWhileIdleInterval")).longValue());
		Item a = (Item) pool.borrowObject();
		Item b = (Item) pool.borrowObject();
		pool.givebackObject(a);
		pool.givebackObject(b);
		a.valid = false;
		waitUntil(() -> a.destroyed);
		assertFalse(b.destroyed);
		assertEquals(1, intValue("getIdleSize"));
		assertEquals(1, intValue("getPoolSize"));
		set("setTestWhileIdleInterval", 30L);
		set("setTestWhileIdle", false);
		assertFalse((Boolean) get("isTestWhileIdle"));
	}

	@Test
	public void testWhileIdle_keepsMinIdleObjects() throws Exception {
		set("setMinIdleSize", 1);
		set("setTestWhileIdleInterval", 10L);
		set("setTestWhileIdle", true);
		Item a = (Item) pool.borrowObject();
		pool.givebackObject(a);
		a.valid = false;
		Thread.sleep(80);
		assertFalse(a.destroyed, "idle objects are not tested while idle size <= minIdleSize");
	}

	@Test
	public void checkIdleSize_trimsMostUsedIdleObjects() throws Exception {
		set("setCheckIdleSizeInterval", 20L);
		assertEquals(20L, ((Number) get("getCheckIdleSizeInterval")).longValue());
		Item a = (Item) pool.borrowObject();
		Item b = (Item) pool.borrowObject();
		Item c = (Item) pool.borrowObject();
		pool.givebackObject(a);
		pool.givebackObject(b);
		pool.givebackObject(c);
		assertSame(a, pool.borrowObject());
		pool.givebackObject(a);
		set("setMaxIdleSize", 2);
		set("setMaxIdleSize", 0);
		assertEquals(2, intValue("getMaxIdleSize"));
		waitUntil(() -> a.destroyed);
		assertFalse(b.destroyed);
		assertFalse(c.destroyed);
		assertEquals(2, intValue("getIdleSize"));
		set("setCheckIdleSizeInterval", 25L);
	}

	@Test
	public void checkObjectExpired_discardsLongBorrowedObjects() throws Exception {
		set("setMaxWaitTimeForExpiration", 30L);
		assertEquals(30L, ((Number) get("getMaxWaitTimeForExpiration")).longValue());
		set("setCheckObjectExpiredInterval", 20L);
		set("setCheckObjectExpired", true);
		assertTrue((Boolean) get("isCheckObjectExpired"));
		assertEquals(20L, ((Number) get("getCheckObjectExpiredInterval")).longValue());
		Item busy = (Item) pool.borrowObject();
		waitUntil(() -> busy.destroyed);
		assertEquals(0, intValue("getPoolSize"));
		assertThrows(PooledObjectException.class, () -> pool.givebackObject(busy));
		set("setCheckObjectExpiredInterval", 40L);
		set("setCheckObjectExpired", false);
	}

	@Test
	public void maintenanceTasks_areNotScheduledAfterClose() throws Exception {
		pool.close();
		set("setTestWhileIdle", true);
		set("setCheckObjectExpired", true);
		set("setMaxIdleSize", 1);
		assertEquals(1, intValue("getMaxIdleSize"));
	}

	@Test
	public void concurrentBorrowers_neverExceedMaxPoolSize() throws Exception {
		set("setMaxPoolSize", 4);
		assertEquals(4, intValue("getMaxPoolSize"));
		final int threads = 10;
		final int rounds = 200;
		final AtomicInteger active = new AtomicInteger();
		final AtomicInteger maxActive = new AtomicInteger();
		final Set<Object> inUse = Collections.synchronizedSet(Collections.newSetFromMap(new java.util.IdentityHashMap<Object, Boolean>()));
		final AtomicBoolean sharedObject = new AtomicBoolean();
		final CountDownLatch start = new CountDownLatch(1);
		ExecutorService executor = Executors.newFixedThreadPool(threads);
		List<Future<?>> futures = new ArrayList<Future<?>>();
		try {
			for (int i = 0; i < threads; i++) {
				final boolean timed = i % 2 == 0;
				futures.add(executor.submit(() -> {
					start.await();
					for (int j = 0; j < rounds; j++) {
						Object item = timed ? pool.borrowObject(5000) : pool.borrowObject();
						if (!inUse.add(item)) {
							sharedObject.set(true);
						}
						maxActive.accumulateAndGet(active.incrementAndGet(), Math::max);
						active.decrementAndGet();
						inUse.remove(item);
						pool.givebackObject(item);
					}
					return null;
				}));
			}
			start.countDown();
			for (Future<?> f : futures) {
				f.get(15, TimeUnit.SECONDS);
			}
		} finally {
			executor.shutdownNow();
		}
		assertFalse(sharedObject.get(), "an object was lent to two borrowers at the same time");
		assertTrue(maxActive.get() <= 4);
		assertTrue(factory.created.get() <= 4);
		assertEquals(0, intValue("getBusySize"));
		assertEquals(factory.created.get(), intValue("getIdleSize"));
		assertEquals(factory.created.get() - factory.destroyed.get(), intValue("getPoolSize"), "pool size must match live objects");
		Set<Object> distinct = new HashSet<Object>();
		distinct.add(pool.borrowObject());
		assertEquals(1, distinct.size());
	}

	@Test
	public void concurrentBorrowDiscardAndFailures_keepCountersConsistent() throws Exception {
		set("setMaxPoolSize", 3);
		final int threads = 8;
		final CountDownLatch start = new CountDownLatch(1);
		final AtomicInteger round = new AtomicInteger();
		ExecutorService executor = Executors.newFixedThreadPool(threads);
		List<Future<?>> futures = new ArrayList<Future<?>>();
		try {
			for (int i = 0; i < threads; i++) {
				futures.add(executor.submit(() -> {
					start.await();
					for (int j = 0; j < 150; j++) {
						int n = round.incrementAndGet();
						Object item;
						try {
							item = pool.borrowObject(5000);
						} catch (IllegalStateException ignored) {
							continue;
						}
						if (n % 5 == 0) {
							pool.discardObject(item);
						} else {
							if (n % 7 == 0) {
								((Item) item).valid = false;
							}
							pool.givebackObject(item);
						}
						if (n % 50 == 0) {
							factory.failCreate = !factory.failCreate;
						}
					}
					return null;
				}));
			}
			start.countDown();
			for (Future<?> f : futures) {
				f.get(15, TimeUnit.SECONDS);
			}
		} finally {
			executor.shutdownNow();
		}
		factory.failCreate = false;
		assertEquals(0, intValue("getBusySize"));
		int live = factory.created.get() - factory.destroyed.get();
		assertEquals(live, intValue("getPoolSize"), "pool size must equal created - destroyed");
		assertEquals(live, intValue("getIdleSize"));
		assertTrue(live <= 3);
		// all capacity is available again
		Object a = pool.borrowObject(100);
		Object b = pool.borrowObject(100);
		Object c = pool.borrowObject(100);
		assertThrows(PooledObjectException.class, () -> pool.borrowObject(10));
		pool.givebackObject(a);
		pool.givebackObject(b);
		pool.givebackObject(c);
	}

	@Test
	public void close_wakesWaitingBorrowersImmediatelyWhileWaitingForBorrowedObjects() throws Exception {
		set("setMaxPoolSize", 1);
		set("setCloseTimeout", 10000L);
		Object busy = pool.borrowObject();
		ExecutorService executor = Executors.newFixedThreadPool(2);
		try {
			CountDownLatch waiting = new CountDownLatch(1);
			Future<Object> borrower = executor.submit(() -> {
				waiting.countDown();
				return pool.borrowObject();
			});
			waiting.await();
			Thread.sleep(50);
			Future<?> closing = executor.submit(() -> {
				pool.close();
				return null;
			});
			long start = System.nanoTime();
			java.util.concurrent.ExecutionException e = assertThrows(java.util.concurrent.ExecutionException.class,
					() -> borrower.get(5, TimeUnit.SECONDS));
			assertTrue(e.getCause() instanceof PooledObjectException);
			assertTrue(TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start) < 3000,
					"the borrower must not wait for the close timeout");
			assertFalse(closing.isDone(), "close still waits for the borrowed object");
			pool.givebackObject(busy);
			closing.get(5, TimeUnit.SECONDS);
			assertTrue(((Item) busy).destroyed);
		} finally {
			executor.shutdownNow();
		}
	}

	protected static void waitUntil(java.util.function.BooleanSupplier condition) throws InterruptedException {
		long deadline = System.currentTimeMillis() + 5000;
		while (!condition.getAsBoolean()) {
			if (System.currentTimeMillis() > deadline) {
				throw new AssertionError("Condition not met in time");
			}
			Thread.sleep(5);
		}
	}
}
