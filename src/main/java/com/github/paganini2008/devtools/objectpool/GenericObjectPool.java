package com.github.paganini2008.devtools.objectpool;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.github.paganini2008.devtools.collection.MapUtils;
import com.github.paganini2008.devtools.date.DateUtils;
import com.github.paganini2008.devtools.logging.Log;
import com.github.paganini2008.devtools.logging.LogFactory;
import com.github.paganini2008.devtools.multithreads.AtomicPositiveInteger;
import com.github.paganini2008.devtools.multithreads.Executable;
import com.github.paganini2008.devtools.multithreads.ExecutorUtils;
import com.github.paganini2008.devtools.multithreads.ThreadUtils;

/**
 * GenericObjectPool
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class GenericObjectPool implements ObjectPool {

	private static final Log logger = LogFactory.getLog(GenericObjectPool.class);

	private static final AtomicPositiveInteger serialNumber = new AtomicPositiveInteger(0);

	public GenericObjectPool(ObjectFactory objectFactory) {
		this.objectFactory = new DelegatedObjectFactory(objectFactory);
	}

	/**
	 * Card. Like a 'Library card'. To describe the borrowed object's status.
	 * 
	 * @author Fred Feng
	 * @version 1.0
	 */
	static class Card {

		Card(int id, String className) {
			this.id = id;
			this.className = className;
			this.createdTimestamp = System.currentTimeMillis();
		}

		final int id;
		final String className;
		final long createdTimestamp;
		long lastBorrowedTimestamp;
		long lastGivebackTimestamp;
		int age;
		boolean idle = true;

		public int getId() {
			return id;
		}

		public boolean isIdle() {
			return idle;
		}

		public long getCreatedTimestamp() {
			return createdTimestamp;
		}

		public long getLastBorrowedTimestamp() {
			return lastBorrowedTimestamp;
		}

		public long getLastGivebackTimestamp() {
			return lastGivebackTimestamp;
		}

		public int getAge() {
			return age;
		}

		/**
		 * Take a picture
		 * 
		 * @return
		 */
		public Map<String, Object> toSnapshot() {
			Map<String, Object> m = new HashMap<String, Object>();
			m.put("id", id);
			m.put("className", className);
			m.put("createdTimestamp", createdTimestamp);
			m.put("lastBorrowedTimestamp", lastBorrowedTimestamp);
			m.put("lastGivebackTimestamp", lastGivebackTimestamp);
			m.put("age", age);
			m.put("idle", idle);
			return m;
		}

		public String toString() {
			return MapUtils.toString(toSnapshot());
		}

	}

	/**
	 * Idle objects, wait for using.
	 */
	private final LinkedList<Object> idleList = new LinkedList<Object>();

	/**
	 * Active objects
	 */
	private final List<Object> activeList = new ArrayList<Object>();
	private final Map<Object, Card> cards = new IdentityHashMap<Object, Card>();
	private final DelegatedObjectFactory objectFactory;
	private final Lock lock = new ReentrantLock();

	private Semaphore semaphore;
	private int initSize;
	private int perNewSize = 2;
	private int maxIdleSize;
	private int maxSize = 1 << 5;
	private int maxAge;
	private boolean testOnCreate = false;
	private int maxTestTimesOnCreate = 3;

	private boolean testOnBorrow = true;
	private int maxTestTimesOnBorrow = 3;

	private boolean testOnGiveback = true;
	private int maxTestTimesOnGiveback = 3;

	private boolean testWhileIdle = false;
	private int maxTestTimesWhileIdle = 3;
	private int testWhileIdleInterval = 5;

	private int checkExpiredInterval = 5;
	private int maxWaitTimeForExpiration;

	private volatile State state = State.INITIALIZED;

	/**
	 * ObjectPool's State
	 * 
	 * @author Fred Feng
	 * @version 1.0
	 */
	static enum State {

		/**
		 * ObjectPool is running
		 */
		RUNNING,

		/**
		 * ObjectPool is paused
		 */
		SUSPENDED,

		/**
		 * ObjectPool is started and need to initialize
		 */
		INITIALIZED,

		/**
		 * ObjectPool is destroyed
		 */
		CLOSED;
	}

	public int getMaxTestTimesOnCreate() {
		return maxTestTimesOnCreate;
	}

	public void setMaxTestTimesOnCreate(int maxTestTimesOnCreate) {
		this.maxTestTimesOnCreate = maxTestTimesOnCreate;
	}

	public boolean isTestOnCreate() {
		return testOnCreate;
	}

	public void setTestOnCreate(boolean testOnCreate) {
		this.testOnCreate = testOnCreate;
	}

	public boolean isTestOnGiveback() {
		return testOnGiveback;
	}

	public void setTestOnGiveback(boolean testOnGiveback) {
		this.testOnGiveback = testOnGiveback;
	}

	public int getTestWhileIdleInterval() {
		return testWhileIdleInterval;
	}

	public void setTestWhileIdleInterval(int testWhileIdleInterval) {
		this.testWhileIdleInterval = testWhileIdleInterval;
	}

	public int getCheckExpiredInterval() {
		return checkExpiredInterval;
	}

	public void setCheckExpiredInterval(int checkExpiredInterval) {
		this.checkExpiredInterval = checkExpiredInterval;
	}

	public State getState() {
		return state;
	}

	public int getMaxTestTimesOnBorrow() {
		return maxTestTimesOnBorrow;
	}

	public void setMaxTestTimesOnBorrow(int maxTestTimesOnBorrow) {
		this.maxTestTimesOnBorrow = maxTestTimesOnBorrow;
	}

	public int getMaxTestTimesOnGiveback() {
		return maxTestTimesOnGiveback;
	}

	public void setMaxTestTimesOnGiveback(int maxTestTimesOnGiveback) {
		this.maxTestTimesOnGiveback = maxTestTimesOnGiveback;
	}

	public int getMaxTestTimesWhileIdle() {
		return maxTestTimesWhileIdle;
	}

	public void setMaxTestTimesWhileIdle(int maxTestTimesWhileIdle) {
		this.maxTestTimesWhileIdle = maxTestTimesWhileIdle;
	}

	public boolean isTestWhileIdle() {
		return testWhileIdle;
	}

	public void setTestWhileIdle(boolean testWhileIdle) {
		this.testWhileIdle = testWhileIdle;
	}

	public boolean isTestOnBorrow() {
		return testOnBorrow;
	}

	public void setTestOnBorrow(boolean testOnBorrow) {
		this.testOnBorrow = testOnBorrow;
	}

	public int getMaxAge() {
		return maxAge;
	}

	public void setMaxAge(int maxAge) {
		this.maxAge = maxAge;
	}

	public int getMaxWaitTimeForExpiration() {
		return maxWaitTimeForExpiration;
	}

	public void setMaxWaitTimeForExpiration(int maxWaitTimeForExpiration) {
		this.maxWaitTimeForExpiration = maxWaitTimeForExpiration;
	}

	public int getInitSize() {
		return initSize;
	}

	public void setInitSize(int initSize) {
		this.initSize = initSize;
	}

	public int getPerNewSize() {
		return perNewSize;
	}

	public void setPerNewSize(int perNewSize) {
		this.perNewSize = perNewSize;
	}

	public int getMaxSize() {
		return maxSize;
	}

	public void setMaxSize(int maxSize) {
		this.maxSize = maxSize;
	}

	public int getMaxIdleSize() {
		return maxIdleSize;
	}

	public void setMaxIdleSize(int maxIdleSize) {
		this.maxIdleSize = maxIdleSize;
	}

	public void setPhaseListener(LifeCycleListener phaseListener) {
		this.objectFactory.setPhaseListener(phaseListener);
	}

	private Scheduler scheduler;

	/**
	 * Initialize the object pool on the first time.
	 * 
	 * @throws Exception
	 */
	private void initialize() throws Exception {
		if (semaphore == null) {
			semaphore = new Semaphore(maxSize);
		}
		Object createdObject;
		for (int i = 0; i < initSize; i++) {
			createdObject = objectFactory.createObject();

			if (testOnCreate && !test(createdObject, maxTestTimesOnCreate)) {
				try {
					objectFactory.destroyObject(createdObject);
					if (logger.isDebugEnabled()) {
						logger.debug("Destroy the object: " + createdObject);
					}
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
				}

			} else {
				idleList.addLast(createdObject);
				cards.put(createdObject, new Card(serialNumber.incrementAndGet(), createdObject.getClass().getName()));
				if (logger.isDebugEnabled()) {
					logger.debug("Create the object: " + createdObject);
				}
			}
		}

		scheduler = new Scheduler();
		// Default enable DiscardExpiredObjectHandler. In order to avoid keeping
		// resource in pool for long time
		scheduler.schedule(new DiscardExpiredObjectHandler(), checkExpiredInterval);
		if (testWhileIdle) {
			scheduler.schedule(new DiscardUselessObjectHandler(), testWhileIdleInterval);
		}
		if (logger.isInfoEnabled()) {
			logger.info("ObjectPool is initialized on {}", DateUtils.format(System.currentTimeMillis()));
		}
	}

	// External threads operation
	public synchronized void suspend() {
		if (state != State.RUNNING) {
			throw new IllegalStateException("ObjectPool is not running.");
		}
		state = State.SUSPENDED;
	}

	// External threads operation
	public synchronized void resume() {
		if (state != State.SUSPENDED) {
			throw new IllegalStateException("ObjectPool is in a illegal state.");
		}
		state = State.RUNNING;
	}

	public Object borrowObject() throws Exception {
		return borrowObject(0);
	}

	public Object borrowObject(int timeout) throws Exception {
		if (state == State.CLOSED) {
			throw new IllegalStateException("ObjectPool is closed.");
		}
		if (state == State.SUSPENDED) {
			throw new IllegalStateException("ObjectPool has been suspended and it is not available at present.");
		}
		if (state == State.INITIALIZED) {
			lock.lock();
			try {
				if (state == State.INITIALIZED) {
					initialize();
					state = State.RUNNING;
				}
			} finally {
				lock.unlock();
			}
		}
		try {
			if (timeout > 0) {
				semaphore.tryAcquire(timeout, TimeUnit.SECONDS);
			} else {
				semaphore.acquire();
			}
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
		lock.lock();
		try {
			int idleSize = idleList.size();
			boolean testOnBorrow = this.testOnBorrow;
			if (idleSize == 0) {
				// No idle objects and create some.
				Object createdObject;
				for (int i = 0; i < perNewSize && (idleList.size() + activeList.size()) < maxSize; i++) {
					createdObject = objectFactory.createObject();

					if (testOnCreate && !test(createdObject, maxTestTimesOnCreate)) {
						try {
							objectFactory.destroyObject(createdObject);
							if (logger.isDebugEnabled()) {
								logger.debug("Destroy the object: " + createdObject);
							}
						} catch (Exception e) {
							logger.error(e.getMessage(), e);
						}

					} else {
						idleList.addLast(createdObject);
						cards.put(createdObject,
								new Card(serialNumber.incrementAndGet(), createdObject.getClass().getName()));
						if (logger.isDebugEnabled()) {
							logger.debug("Create the object: " + createdObject);
						}
					}
				}
				idleSize = idleList.size();
				testOnBorrow &= !testOnCreate;
			}
			if (idleSize > 0) {
				Object target = idleList.pollFirst();
				if (testOnBorrow && !test(target, maxTestTimesOnBorrow)) {
					try {
						objectFactory.destroyObject(target);
						if (logger.isDebugEnabled()) {
							logger.debug("Destroy the object: " + target);
						}
					} catch (Exception e) {
						logger.error(e.getMessage(), e);
					}
					cards.remove(target);

					throw new IllegalStateException("Can not borrow a valid object.");
				}
				activeList.add(target);
				Card card = cards.get(target);
				card.lastBorrowedTimestamp = System.currentTimeMillis();
				card.idle = false;
				return target;
			}
		} finally {
			lock.unlock();
		}
		throw new IllegalStateException("Can not borrow a extra object.");
	}

	public void givebackObject(Object other) throws Exception {
		if (state == State.INITIALIZED || state == State.CLOSED) {
			throw new IllegalStateException("ObjectPool is not running.");
		}
		lock.lock();
		try {
			Card card = cards.get(other);
			if (card == null) {
				throw new IllegalStateException("Unpooled object or deleted object.");
			}
			if (card.isIdle()) {
				throw new IllegalStateException("Don't giveback the object again.");
			} else {
				activeList.remove(other);
			}
			boolean destroy;
			if (testOnGiveback && !test(other, maxTestTimesOnGiveback)) {
				destroy = true;
			} else {
				card.lastGivebackTimestamp = System.currentTimeMillis();
				card.age += 1;
				card.idle = true;
				if (maxAge > 0 && card.getAge() > maxAge) {
					destroy = true;
				} else {
					destroy = false;
				}
			}
			if (destroy) {
				try {
					objectFactory.destroyObject(other);
					if (logger.isDebugEnabled()) {
						logger.debug("Destroy the object: " + other);
					}
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
				}
				cards.remove(other);
			} else {
				idleList.addLast(other);
			}
			ensureInMaxIdleSize();
		} finally {
			lock.unlock();
		}
		semaphore.release();
	}

	private final Comparator<Object> idleListSorter = new Comparator<Object>() {
		public int compare(Object left, Object right) {
			long now = System.currentTimeMillis();
			Card leftCard = cards.get(left);
			Card rightCard = cards.get(right);
			long result = (now - leftCard.getLastGivebackTimestamp()) - (now - rightCard.getLastGivebackTimestamp());
			if (result > 0) {
				return -1;
			}
			if (result < 0) {
				return 1;
			}
			return 0;
		}
	};

	/**
	 * Be sure to keep a standard with a defined size in pool
	 */
	private void ensureInMaxIdleSize() {
		if (maxIdleSize > 0 && idleList.size() > maxIdleSize) {
			Collections.sort(idleList, idleListSorter);
			Object idleObject;
			for (int i = 0, l = idleList.size() - maxIdleSize; i < l; i++) {
				idleObject = idleList.pollFirst();
				try {
					objectFactory.destroyObject(idleObject);
					if (logger.isDebugEnabled()) {
						logger.debug("Destroy the object: " + idleObject);
					}
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
				}
			}
		}
	}

	public void discardObject(Object other) throws Exception {
		if (state == State.INITIALIZED || state == State.CLOSED) {
			throw new IllegalStateException("ObjectPool is not running.");
		}
		lock.lock();
		try {
			Card card = cards.remove(other);
			if (card == null) {
				throw new IllegalStateException("Unpooled object or deleted object.");
			}
			if (!card.isIdle()) {
				activeList.remove(other);
			}
			try {
				objectFactory.destroyObject(other);
				if (logger.isDebugEnabled()) {
					logger.debug("Destroy the object: " + other);
				}
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		} finally {
			lock.unlock();
		}
		semaphore.release();
	}

	// Test
	private boolean test(Object other, int maxTestTimes) {
		boolean valid = false;
		int i = 0;
		do {
			try {
				valid = objectFactory.validateObject(other);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		} while (!valid && i++ < maxTestTimes);
		return valid;
	}

	public int getIdleSize() {
		return idleList.size();
	}

	public int getActiveSize() {
		return activeList.size();
	}

	public long getCreatedCount() {
		return objectFactory.getCreatedCount();
	}

	public long getDestroyedCount() {
		return objectFactory.getDestroyedCount();
	}

	public List<Map<String, Object>> getSnapshots() {
		List<Map<String, Object>> snapshots = new ArrayList<Map<String, Object>>();
		for (Card card : new ArrayList<Card>(cards.values())) {
			snapshots.add(card.toSnapshot());
		}
		return snapshots;
	}

	public void close() throws Exception {
		if (state == State.INITIALIZED || state == State.CLOSED) {
			throw new IllegalStateException("ObjectPool is not running.");
		}
		if (state == State.SUSPENDED) {
			throw new IllegalStateException("ObjectPool has been suspended until the end.");
		}
		state = State.SUSPENDED;
		while (semaphore.availablePermits() != maxSize) {
			ThreadUtils.sleep(1000L);
			if (logger.isInfoEnabled()) {
				logger.info("Closing the objectPool ...");
			}
		}
		lock.lock();
		try {
			if (scheduler != null) {
				scheduler.stop();
			}
			for (Object other : idleList) {
				objectFactory.destroyObject(other);
				if (logger.isDebugEnabled()) {
					logger.debug("Destroy the object: " + other);
				}
			}
			idleList.clear();
			cards.clear();
		} finally {
			lock.unlock();
			state = State.CLOSED;
			if (logger.isInfoEnabled()) {
				logger.info("ObjectPool is closed on {}", DateUtils.format(System.currentTimeMillis()));
			}
		}
	}

	/**
	 * Handle and discard some expired objects when user keep them for a long time.
	 * 
	 * @author Fred Feng
	 * @version 1.0
	 */
	private class DiscardExpiredObjectHandler implements Executable {

		public boolean execute() {
			if (state == State.RUNNING || state == State.SUSPENDED) {
				lock.lock();
				int n = 0;
				try {
					Map.Entry<Object, Card> entry;
					Object activeObject;
					Card card;
					for (Iterator<Map.Entry<Object, Card>> it = cards.entrySet().iterator(); it.hasNext();) {
						entry = it.next();
						activeObject = entry.getKey();
						card = entry.getValue();
						if (!card.isIdle() && (card.getLastBorrowedTimestamp() >= card.getLastGivebackTimestamp())
								&& (TimeUnit.SECONDS.convert(
										System.currentTimeMillis() - card.getLastBorrowedTimestamp(),
										TimeUnit.MILLISECONDS) > maxWaitTimeForExpiration)) {
							activeList.remove(activeObject);
							try {
								objectFactory.destroyObject(activeObject);
								if (logger.isInfoEnabled()) {
									logger.info("Long time no giveback and destroy the object(" + card.getId() + "): "
											+ activeObject);
								}
							} catch (Exception e) {
								logger.error(e.getMessage(), e);
							}
							it.remove();

							n++;
						}
					}
				} finally {
					lock.unlock();
				}
				if (n > 0) {
					semaphore.release(n);
				}
			}
			return true;
		}
	}

	/**
	 * Handle and discard some useless objects when db server is absent or break
	 * down.
	 * 
	 * @author Fred Feng
	 * @version 1.0
	 */
	private class DiscardUselessObjectHandler implements Executable {

		public boolean execute() {
			if (state == State.RUNNING || state == State.SUSPENDED) {
				lock.lock();
				try {
					Object idleObject;
					for (Iterator<Object> it = idleList.iterator(); it.hasNext();) {
						idleObject = it.next();
						if (!GenericObjectPool.this.test(idleObject, maxTestTimesWhileIdle)) {
							it.remove();
							try {
								objectFactory.destroyObject(idleObject);
								if (logger.isDebugEnabled()) {
									logger.debug("Destroy the object: " + idleObject);
								}
							} catch (Exception e) {
								logger.error(e.getMessage(), e);
							}
							cards.remove(idleObject);

						}
					}
				} finally {
					lock.unlock();
				}
			}
			return true;
		}
	}

	/**
	 * DelegatedObjectFactory
	 * 
	 * @author Fred Feng
	 * @version 1.0
	 */
	static class DelegatedObjectFactory implements ObjectFactory {

		private final ObjectFactory delegate;
		private long createdCount;
		private long destroyedCount;
		private LifeCycleListener phaseListener;

		DelegatedObjectFactory(ObjectFactory delegate) {
			this.delegate = delegate;
		}

		public void setPhaseListener(LifeCycleListener phaseListener) {
			this.phaseListener = phaseListener;
		}

		public Object createObject() throws Exception {
			try {
				Object o = delegate.createObject();
				if (phaseListener != null) {
					phaseListener.onCreateObject(o);
				}
				return o;
			} finally {
				createdCount++;
			}

		}

		public boolean validateObject(Object o) throws Exception {
			return delegate.validateObject(o);
		}

		public void destroyObject(Object o) throws Exception {
			if (phaseListener != null) {
				phaseListener.onDestroyObject(o);
			}
			try {
				delegate.destroyObject(o);
			} finally {
				destroyedCount++;
			}
		}

		public long getCreatedCount() {
			return createdCount;
		}

		public long getDestroyedCount() {
			return destroyedCount;
		}

	}

	static class Scheduler {

		final ScheduledExecutorService threadPool;
		final Map<String, ScheduledFuture<?>> futures = new HashMap<String, ScheduledFuture<?>>();

		Scheduler() {
			this.threadPool = Executors.newScheduledThreadPool(2);
		}

		void schedule(final Executable executable, int interval) {
			final String uuid = UUID.randomUUID().toString();
			futures.put(uuid, threadPool.scheduleAtFixedRate(new Runnable() {
				public void run() {
					if (!executable.execute()) {
						futures.remove(uuid).cancel(false);
					}
				}
			}, interval, interval, TimeUnit.SECONDS));
		}

		void stop() {
			for (String uuid : new HashSet<String>(futures.keySet())) {
				ScheduledFuture<?> future = futures.remove(uuid);
				future.cancel(false);
			}
			ExecutorUtils.gracefulShutdown(threadPool, 60000);
		}

	}

}
