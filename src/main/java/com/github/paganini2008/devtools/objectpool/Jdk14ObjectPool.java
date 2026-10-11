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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import com.github.paganini2008.devtools.logging.Log;
import com.github.paganini2008.devtools.logging.LogFactory;

/**
 * 
 * Jdk14ObjectPool is a bounded, thread-safe {@link ObjectPool} based on the
 * intrinsic lock ({@code synchronized/wait/notifyAll}). Idle objects are
 * optionally validated, trimmed and expired by tasks running on a daemon
 * {@link Timer}.
 * 
 * @author Fred Feng
 * @date 2019-07-12
 * @since 2.0.1
 */
public class Jdk14ObjectPool implements ObjectPool {

	private static final Log log = LogFactory.getLog(ObjectPool.class);
	private final Object lock = new Object();
	private final LinkedList<Object> busyQueue = new LinkedList<Object>();
	private final LinkedList<Object> idleQueue = new LinkedList<Object>();
	private final IdentityHashMap<Object, PooledObject> pooledObjects = new IdentityHashMap<Object, PooledObject>();
	private volatile int maxPoolSize = 8;
	private int minIdleSize = 1;
	private volatile int maxIdleSize;
	private volatile int maxUsage = -1;
	private long checkIdleSizeInterval = 60L * 1000;
	private volatile int maxTestTimes = 3;

	private volatile int poolSize;
	private boolean testWhileIdle;
	private long testWhileIdleInterval = 3L * 1000;
	private boolean checkObjectExpired;
	private long checkObjectExpiredInterval = 60L * 1000;
	private volatile long maxWaitTimeForExpiration = 60L * 1000;
	private volatile long closeTimeout = 60L * 1000;
	private final AtomicBoolean running;
	private final Timer timer = new Timer("objectpool-maintainer", true);
	private TimerTask checkIdleSizeFuture;
	private TimerTask testWhileIdleFuture;
	private TimerTask checkObjectExpiredFuture;
	private final ObjectFactory objectFactory;

	public Jdk14ObjectPool(ObjectFactory objectFactory) {
		if (objectFactory == null) {
			throw new IllegalArgumentException("ObjectFactory must not be null");
		}
		this.objectFactory = objectFactory;
		this.running = new AtomicBoolean(true);
	}

	/**
	 *
	 * PooledObject
	 *
	 * @author Fred Feng
	 * @since 2.0.1
	 */
	static class PooledObject implements ObjectDetail {

		private final long created;
		private final Object object;
		private long lastBorrowed;
		private long lastReturned;
		private long lastTested;
		private int usage;
		private int returns;

		PooledObject(Object object) {
			this.created = System.currentTimeMillis();
			this.object = object;
		}

		public long getCreated() {
			return created;
		}

		public Object getObject() {
			return object;
		}

		public long getLastBorrowed() {
			return lastBorrowed;
		}

		public void setLastBorrowed(long lastBorrowed) {
			this.lastBorrowed = lastBorrowed;
		}

		public long getLastReturned() {
			return lastReturned;
		}

		public void setLastReturned(long lastReturned) {
			this.lastReturned = lastReturned;
		}

		public long getLastTested() {
			return lastTested;
		}

		public void setLastTested(long lastTested) {
			this.lastTested = lastTested;
		}

		public int getUsage() {
			return usage;
		}

		public void setUsage(int usage) {
			this.usage = usage;
		}

		public int getReturns() {
			return returns;
		}

		public void setReturns(int returns) {
			this.returns = returns;
		}

		public static PooledObject of(Object object) {
			return new PooledObject(object);
		}

		public String toString() {
			return "PooledObject [object=" + object + ", usage=" + usage + ", returns=" + returns + "]";
		}

	}

	public void setMaxPoolSize(int maxPoolSize) {
		this.maxPoolSize = maxPoolSize;
	}

	public int getMaxPoolSize() {
		return maxPoolSize;
	}

	public int getMaxIdleSize() {
		return maxIdleSize;
	}

	public int getMinIdleSize() {
		return minIdleSize;
	}

	public void setMinIdleSize(int minIdleSize) {
		this.minIdleSize = minIdleSize;
	}

	public int getIdleSize() {
		synchronized (lock) {
			return idleQueue.size();
		}
	}

	public int getBusySize() {
		synchronized (lock) {
			return busyQueue.size();
		}
	}

	/**
	 * Set the max count of idle objects. A positive value starts a background
	 * task (non-positive values are ignored) which discards the redundant idle objects every
	 * {@link #getCheckIdleSizeInterval()} milliseconds.
	 *
	 * @param maxIdleSize
	 */
	public synchronized void setMaxIdleSize(int maxIdleSize) {
		if (maxIdleSize > 0) {
			this.maxIdleSize = maxIdleSize;
			checkIdleSizeFuture = reschedule(checkIdleSizeFuture, true, new CheckIdleSizeTask(), checkIdleSizeInterval);
		}
	}

	public long getCheckIdleSizeInterval() {
		return checkIdleSizeInterval;
	}

	public synchronized void setCheckIdleSizeInterval(long checkIdleSizeInterval) {
		this.checkIdleSizeInterval = checkIdleSizeInterval;
		if (checkIdleSizeFuture != null) {
			checkIdleSizeFuture = reschedule(checkIdleSizeFuture, true, new CheckIdleSizeTask(), checkIdleSizeInterval);
		}
	}

	public boolean isTestWhileIdle() {
		return testWhileIdle;
	}

	/**
	 * Enable or disable the background task which validates idle objects every
	 * {@link #getTestWhileIdleInterval()} milliseconds (when there are more
	 * than {@link #getMinIdleSize()} idle objects) and discards the invalid
	 * ones.
	 *
	 * @param testWhileIdle
	 */
	public synchronized void setTestWhileIdle(boolean testWhileIdle) {
		this.testWhileIdle = testWhileIdle;
		testWhileIdleFuture = reschedule(testWhileIdleFuture, testWhileIdle, new TestWhileIdleTask(), testWhileIdleInterval);
	}

	public long getTestWhileIdleInterval() {
		return testWhileIdleInterval;
	}

	public synchronized void setTestWhileIdleInterval(long testWhileIdleInterval) {
		this.testWhileIdleInterval = testWhileIdleInterval;
		if (testWhileIdleFuture != null) {
			testWhileIdleFuture = reschedule(testWhileIdleFuture, true, new TestWhileIdleTask(), testWhileIdleInterval);
		}
	}

	public boolean isCheckObjectExpired() {
		return checkObjectExpired;
	}

	/**
	 * Enable or disable the background task which discards the objects borrowed
	 * for longer than {@link #getMaxWaitTimeForExpiration()} milliseconds.
	 *
	 * @param checkObjectExpired
	 */
	public synchronized void setCheckObjectExpired(boolean checkObjectExpired) {
		this.checkObjectExpired = checkObjectExpired;
		checkObjectExpiredFuture = reschedule(checkObjectExpiredFuture, checkObjectExpired, new CheckObjectExpiredTask(),
				checkObjectExpiredInterval);
	}

	public long getMaxWaitTimeForExpiration() {
		return maxWaitTimeForExpiration;
	}

	public void setMaxWaitTimeForExpiration(long maxWaitTimeForExpiration) {
		this.maxWaitTimeForExpiration = maxWaitTimeForExpiration;
	}

	public int getMaxTestTimes() {
		return maxTestTimes;
	}

	public void setMaxTestTimes(int maxTestTimes) {
		this.maxTestTimes = maxTestTimes;
	}

	public int getPoolSize() {
		return poolSize;
	}

	public int getMaxUsage() {
		return maxUsage;
	}

	/**
	 * Set the max times an object can be borrowed. The object will be destroyed
	 * when it has been given back for the last time. A non-positive value means
	 * no limit.
	 *
	 * @param maxUsage
	 */
	public void setMaxUsage(int maxUsage) {
		this.maxUsage = maxUsage;
	}

	public long getCheckObjectExpiredInterval() {
		return checkObjectExpiredInterval;
	}

	public synchronized void setCheckObjectExpiredInterval(long checkObjectExpiredInterval) {
		this.checkObjectExpiredInterval = checkObjectExpiredInterval;
		if (checkObjectExpiredFuture != null) {
			checkObjectExpiredFuture = reschedule(checkObjectExpiredFuture, true, new CheckObjectExpiredTask(), checkObjectExpiredInterval);
		}
	}

	public long getCloseTimeout() {
		return closeTimeout;
	}

	/**
	 * Set the max time(ms) {@link #close()} waits for the borrowed objects to be
	 * given back before destroying them forcibly.
	 *
	 * @param closeTimeout
	 */
	public void setCloseTimeout(long closeTimeout) {
		this.closeTimeout = closeTimeout;
	}

	private TimerTask reschedule(TimerTask future, boolean enabled, TimerTask task, long interval) {
		if (future != null) {
			future.cancel();
		}
		if (enabled && running.get() && interval > 0) {
			try {
				timer.schedule(task, interval, interval);
				return task;
			} catch (IllegalStateException e) {
				return null;
			}
		}
		return null;
	}

	@Override
	public ObjectDetail getDetail(Object object) {
		synchronized (lock) {
			return pooledObjects.get(object);
		}
	}

	@Override
	public Object borrowObject() throws Exception {
		return doBorrowObject(false, 0L);
	}

	/**
	 * Borrow one from pool within given time.
	 *
	 * @param timeout
	 * @param timeUnit
	 * @return
	 * @throws Exception
	 */
	public Object borrowObject(long timeout, TimeUnit timeUnit) throws Exception {
		return doBorrowObject(true, timeUnit.toNanos(timeout));
	}

	@Override
	public Object borrowObject(long timeout) throws Exception {
		return doBorrowObject(true, TimeUnit.MILLISECONDS.toNanos(timeout));
	}

	private Object doBorrowObject(boolean timed, long nanos) throws Exception {
		synchronized (lock) {
			while (true) {
				if (!running.get()) {
					throw new PooledObjectException("ObjectPool has been closed.");
				}
				Object object;
				while ((object = idleQueue.pollFirst()) != null) {
					if (testObject(object)) {
						return markBorrowed(pooledObjects.get(object));
					}
					log.warn("Discard invalid idle object: " + object);
					destroyObject(pooledObjects.remove(object));
				}
				if (poolSize < maxPoolSize) {
					poolSize++;
					Object newObject = null;
					boolean ok = false;
					try {
						newObject = objectFactory.createObject();
						if (newObject == null) {
							throw new PooledObjectException("ObjectFactory created a null object.");
						}
						if (!testObject(newObject)) {
							throw new PooledObjectException("Invalid object created: " + newObject);
						}
						if (log.isDebugEnabled()) {
							log.debug("Create new object: " + newObject);
						}
						PooledObject pooledObject = PooledObject.of(newObject);
						pooledObjects.put(newObject, pooledObject);
						ok = true;
						return markBorrowed(pooledObject);
					} finally {
						if (!ok) {
							poolSize--;
							if (newObject != null) {
								destroyQuietly(newObject);
							}
							lock.notifyAll();
						}
					}
				}
				try {
					if (timed) {
						if (nanos <= 0) {
							throw new PooledObjectException("Can not borrow any object now.");
						}
						nanos = waitNanos(nanos);
					} else {
						lock.wait();
					}
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
					throw new PooledObjectException("Interrupted while borrowing object.", e);
				}
			}
		}
	}

	private long waitNanos(long nanos) throws InterruptedException {
		final long deadline = System.nanoTime() + nanos;
		TimeUnit.NANOSECONDS.timedWait(lock, nanos);
		return deadline - System.nanoTime();
	}

	private Object markBorrowed(PooledObject pooledObject) {
		pooledObject.setLastBorrowed(System.currentTimeMillis());
		pooledObject.setUsage(pooledObject.getUsage() + 1);
		busyQueue.add(pooledObject.getObject());
		return pooledObject.getObject();
	}

	private boolean testObject(Object object) {
		final int times = Math.max(0, maxTestTimes) + 1;
		for (int i = 0; i < times; i++) {
			try {
				if (objectFactory.testObject(object)) {
					PooledObject pooledObject = pooledObjects.get(object);
					if (pooledObject != null) {
						pooledObject.setLastTested(System.currentTimeMillis());
					}
					return true;
				}
			} catch (Exception e) {
				log.debug("Failed to test object: " + object, e);
			}
		}
		return false;
	}

	private void destroyObject(PooledObject pooledObject) {
		if (pooledObject != null) {
			// destroy first, so that a pool size observed as decreased never refers to a live object
			destroyQuietly(pooledObject.getObject());
			poolSize--;
			lock.notifyAll();
		}
	}

	private void destroyQuietly(Object object) {
		try {
			objectFactory.destroyObject(object);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	private static boolean removeIdentity(List<Object> list, Object object) {
		for (Iterator<Object> iter = list.iterator(); iter.hasNext();) {
			if (iter.next() == object) {
				iter.remove();
				return true;
			}
		}
		return false;
	}

	@Override
	public void givebackObject(Object object) throws Exception {
		synchronized (lock) {
			PooledObject pooledObject = pooledObjects.get(object);
			if (pooledObject == null) {
				throw new PooledObjectException("Unpooled object!");
			}
			if (!removeIdentity(busyQueue, object)) {
				throw new PooledObjectException("Do not giveback pooled object '" + object.getClass().getName() + "' repeatedly!");
			}
			if (log.isDebugEnabled()) {
				log.debug("Giveback object: " + object);
			}
			pooledObject.setReturns(pooledObject.getReturns() + 1);
			pooledObject.setLastReturned(System.currentTimeMillis());
			if (!running.get() || (maxUsage > 0 && pooledObject.getUsage() >= maxUsage)) {
				destroyObject(pooledObjects.remove(object));
			} else {
				idleQueue.add(object);
				lock.notifyAll();
			}
		}
	}

	@Override
	public void discardObject(Object object) throws Exception {
		synchronized (lock) {
			PooledObject pooledObject = pooledObjects.remove(object);
			if (pooledObject != null) {
				if (log.isDebugEnabled()) {
					log.debug("Destroy object: " + object);
				}
				removeIdentity(busyQueue, object);
				removeIdentity(idleQueue, object);
				poolSize--;
				try {
					objectFactory.destroyObject(object);
				} finally {
					lock.notifyAll();
				}
			}
		}
	}

	/**
	 * Close the pool. Idle objects are destroyed immediately, borrowed objects
	 * are destroyed when given back or forcibly after
	 * {@link #getCloseTimeout()} milliseconds. Calling it more than once has no
	 * effect.
	 */
	@Override
	public void close() throws Exception {
		if (!running.compareAndSet(true, false)) {
			return;
		}
		timer.cancel();
		synchronized (lock) {
			lock.notifyAll();
			Object object;
			while ((object = idleQueue.pollLast()) != null) {
				destroyObject(pooledObjects.remove(object));
			}
			long nanos = TimeUnit.MILLISECONDS.toNanos(closeTimeout);
			try {
				while (!busyQueue.isEmpty() && nanos > 0) {
					nanos = waitNanos(nanos);
				}
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
			while ((object = busyQueue.pollLast()) != null) {
				log.warn("Destroy unreturned object: " + object);
				destroyObject(pooledObjects.remove(object));
			}
		}
	}

	public boolean isRunning() {
		return running.get();
	}

	private final Comparator<Object> idleQueueSorter = new Comparator<Object>() {
		public int compare(Object left, Object right) {
			PooledObject leftDetail = pooledObjects.get(left);
			PooledObject rightDetail = pooledObjects.get(right);
			return Integer.compare(rightDetail.getUsage(), leftDetail.getUsage());
		}
	};

	class TestWhileIdleTask extends TimerTask {

		@Override
		public void run() {
			synchronized (lock) {
				try {
					if (idleQueue.size() > minIdleSize) {
						for (Object idleObject : new ArrayList<Object>(idleQueue)) {
							if (!testObject(idleObject)) {
								log.warn("Discard invalid object: " + idleObject);
								removeIdentity(idleQueue, idleObject);
								destroyObject(pooledObjects.remove(idleObject));
							}
						}
					}
				} catch (RuntimeException e) {
					log.error(e.getMessage(), e);
				}
			}
		}

	}

	class CheckIdleSizeTask extends TimerTask {

		@Override
		public void run() {
			synchronized (lock) {
				try {
					if (maxIdleSize > 0 && idleQueue.size() > maxIdleSize) {
						List<Object> queue = new ArrayList<Object>(idleQueue);
						Collections.sort(queue, idleQueueSorter);
						final int destroyedSize = queue.size() - maxIdleSize;
						for (int i = 0; i < destroyedSize; i++) {
							Object object = queue.get(i);
							log.warn("Discard redundant object: " + object);
							removeIdentity(idleQueue, object);
							destroyObject(pooledObjects.remove(object));
						}
					}
				} catch (RuntimeException e) {
					log.error(e.getMessage(), e);
				}
			}
		}

	}

	class CheckObjectExpiredTask extends TimerTask {

		@Override
		public void run() {
			synchronized (lock) {
				try {
					final long now = System.currentTimeMillis();
					for (Object busyObject : new ArrayList<Object>(busyQueue)) {
						PooledObject pooledObject = pooledObjects.get(busyObject);
						if (pooledObject != null && now - pooledObject.getLastBorrowed() > maxWaitTimeForExpiration) {
							log.warn("Discard expired object: " + busyObject);
							removeIdentity(busyQueue, busyObject);
							destroyObject(pooledObjects.remove(busyObject));
						}
					}
				} catch (RuntimeException e) {
					log.error(e.getMessage(), e);
				}
			}
		}

	}

}
