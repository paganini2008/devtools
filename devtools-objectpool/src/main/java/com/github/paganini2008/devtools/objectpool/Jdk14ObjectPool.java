/**
* Copyright 2017-2021 Fred Feng (paganini.fy@gmail.com)

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
import java.util.IdentityHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.github.paganini2008.devtools.logging.Log;
import com.github.paganini2008.devtools.logging.LogFactory;
import com.github.paganini2008.devtools.multithreads.ThreadUtils;

/**
 * 
 * Jdk14ObjectPool
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class Jdk14ObjectPool implements ObjectPool {

	private static final Log log = LogFactory.getLog(ObjectPool.class);
	private final Object lock = new Object();
	private final LinkedList<Object> busyQueue = new LinkedList<Object>();
	private final LinkedList<Object> idleQueue = new LinkedList<Object>();
	private final IdentityHashMap<Object, PooledObject> pooledObjects = new IdentityHashMap<Object, PooledObject>();
	private int maxPoolSize = 8;
	private int minIdleSize = 1;
	private int maxIdleSize;
	private int maxUsage = -1;
	private long checkIdleSizeInterval = 60L * 1000;
	private int maxTestTimes = 3;
	private volatile int poolSize;
	private boolean testWhileIdle;
	private long testWhileIdleInterval = 3L * 1000;
	private boolean checkObjectExpired;
	private long checkObjectExpiredInterval = 60L * 1000;
	private long maxWaitTimeForExpiration = 60L * 1000;
	private volatile boolean running;
	private Timer timer = new Timer();
	private final ObjectFactory objectFactory;

	public Jdk14ObjectPool(ObjectFactory objectFactory) {
		this.objectFactory = objectFactory;
		this.running = true;
	}

	/**
	 * 
	 * PooledObject
	 * 
	 * @author Fred Feng
	 * @version 1.0
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
		return idleQueue.size();
	}

	public int getBusySize() {
		return busyQueue.size();
	}

	public void setMaxIdleSize(int maxIdleSize) {
		if (maxIdleSize > 0) {
			this.maxIdleSize = maxIdleSize;
			timer.scheduleAtFixedRate(new CheckIdleSizeTask(), checkIdleSizeInterval, checkIdleSizeInterval);
		}
	}

	public long getCheckIdleSizeInterval() {
		return checkIdleSizeInterval;
	}

	public void setCheckIdleSizeInterval(long checkIdleSizeInterval) {
		this.checkIdleSizeInterval = checkIdleSizeInterval;
	}

	public boolean isTestWhileIdle() {
		return testWhileIdle;
	}

	public void setTestWhileIdle(boolean testWhileIdle) {
		this.testWhileIdle = testWhileIdle;
		if (testWhileIdle) {
			timer.scheduleAtFixedRate(new TestWhileIdleTask(), testWhileIdleInterval, testWhileIdleInterval);
		}
	}

	public long getTestWhileIdleInterval() {
		return testWhileIdleInterval;
	}

	public void setTestWhileIdleInterval(long testWhileIdleInterval) {
		this.testWhileIdleInterval = testWhileIdleInterval;
	}

	public boolean isCheckObjectExpired() {
		return checkObjectExpired;
	}

	public void setCheckObjectExpired(boolean checkObjectExpired) {
		this.checkObjectExpired = checkObjectExpired;
		if (checkObjectExpired) {
			timer.scheduleAtFixedRate(new CheckObjectExpiredTask(), checkObjectExpiredInterval, checkObjectExpiredInterval);
		}
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

	public void setMaxUsage(int maxUsage) {
		this.maxUsage = maxUsage;
	}

	public long getCheckObjectExpiredInterval() {
		return checkObjectExpiredInterval;
	}

	public void setCheckObjectExpiredInterval(long checkObjectExpiredInterval) {
		this.checkObjectExpiredInterval = checkObjectExpiredInterval;
	}

	@Override
	public Object borrowObject() throws Exception {
		while (running) {
			synchronized (lock) {
				Object availableObject = idleQueue.pollFirst();
				if (availableObject != null) {
					return testWhileBorrow(availableObject);
				}
				if (poolSize < maxPoolSize) {
					Object newObject = objectFactory.createObject();
					if (log.isDebugEnabled()) {
						log.debug("Create new object: " + newObject);
					}
					availableObject = testWhileBorrow(newObject);
					busyQueue.add(availableObject);
					poolSize++;
				}
				if (availableObject != null) {
					return availableObject;
				} else {
					try {
						lock.wait(1000L);
					} catch (InterruptedException ignored) {
						break;
					}
				}
			}
		}
		throw new PooledObjectException("Can not borrow any object now.");
	}

	private Object testWhileIdle(Object object) {
		int i = 0;
		Exception cause = null;
		do {
			try {
				if (objectFactory.testObject(object)) {
					PooledObject pooledObject = pooledObjects.get(object);
					pooledObject.setLastTested(System.currentTimeMillis());
					return object;
				}
			} catch (Exception e) {
				cause = e;
			}
		} while (i++ < maxTestTimes);
		throw new PooledObjectException("Can not borrow any object now.", cause);
	}

	private Object testWhileBorrow(Object object) {
		int i = 0;
		Exception cause = null;
		do {
			try {
				if (objectFactory.testObject(object)) {
					PooledObject pooledObject = pooledObjects.get(object);
					if (pooledObject == null) {
						pooledObjects.put(object, PooledObject.of(object));
						pooledObject = pooledObjects.get(object);
					}
					pooledObject.setLastBorrowed(System.currentTimeMillis());
					pooledObject.setUsage(pooledObject.getUsage() + 1);
					return object;
				}
			} catch (Exception e) {
				cause = e;
			}
		} while (i++ < maxTestTimes);
		throw new PooledObjectException("Can not borrow any object now.", cause);
	}

	@Override
	public Object borrowObject(long timeout) throws Exception {
		final long begin = System.nanoTime();
		long elapsed;
		long m = timeout;
		long n = 0;
		while (running) {
			synchronized (lock) {
				Object availableObject = idleQueue.pollFirst();
				if (availableObject != null) {
					return testWhileBorrow(availableObject);
				}
				if (poolSize < maxPoolSize) {
					Object newObject = objectFactory.createObject();
					if (log.isDebugEnabled()) {
						log.debug("Create new object: " + newObject);
					}
					availableObject = testWhileBorrow(newObject);
					busyQueue.add(availableObject);
					poolSize++;
				}
				if (availableObject != null) {
					return availableObject;
				} else {
					if (m > 0) {
						try {
							lock.wait(m, (int) n);
						} catch (InterruptedException ignored) {
							break;
						}
						elapsed = (System.nanoTime() - begin);
						m -= elapsed / 1000000L;
						n = elapsed % 1000000L;
					} else {
						break;
					}
				}
			}
		}
		throw new PooledObjectException("Can not borrow any object now.");
	}

	@Override
	public void givebackObject(Object object) throws Exception {
		synchronized (lock) {
			PooledObject pooledObject = pooledObjects.get(object);
			if (pooledObject != null) {
				if (log.isDebugEnabled()) {
					log.debug("Giveback object: " + object);
				}
				if (pooledObject.getReturns() + 1 != pooledObject.getUsage()) {
					throw new PooledObjectException("Do not giveback pooled object '" + object.getClass().getName() + "' repeatedly!");
				}
				object = pooledObject.getObject();
				if (pooledObject.getUsage() == maxUsage) {
					discardObject(object);
				} else {
					busyQueue.remove(object);
					idleQueue.add(object);
					pooledObject.setReturns(pooledObject.getReturns() + 1);
					pooledObject.setLastReturned(System.currentTimeMillis());
					lock.notifyAll();
				}
			} else {
				throw new PooledObjectException("Unpooled object!");
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

				object = pooledObject.getObject();
				busyQueue.remove(object);
				idleQueue.remove(object);

				try {
					objectFactory.destroyObject(object);
				} finally {
					poolSize--;
					lock.notifyAll();
				}
			}
		}
	}

	@Override
	public ObjectDetail getDetail(Object object) {
		return pooledObjects.get(object);
	}

	@Override
	public void close() throws Exception {

		running = false;
		timer.cancel();

		synchronized (lock) {

			while (!busyQueue.isEmpty()) {
				ThreadUtils.randomSleep(1000L);
			}
			while (!idleQueue.isEmpty()) {
				Object idleObject = idleQueue.pollLast();
				try {
					objectFactory.destroyObject(idleObject);
				} catch (Exception e) {
					log.debug(e.getMessage(), e);
				}
				pooledObjects.remove(idleObject);
				poolSize--;
			}
		}
	}

	public boolean isRunning() {
		return running;
	}

	class TestWhileIdleTask extends TimerTask {

		@Override
		public void run() {
			synchronized (lock) {
				if (idleQueue.size() > minIdleSize) {
					List<Object> invalidObjects = new ArrayList<Object>();
					for (Object idleObject : idleQueue) {
						try {
							testWhileIdle(idleObject);
						} catch (Exception e) {
							invalidObjects.add(idleObject);
							log.error(e.getMessage(), e);
						}
					}
					if (invalidObjects.size() > 0) {
						for (Object invalidObject : invalidObjects) {
							try {
								discardObject(invalidObject);
								log.warn("Discard invalid object: " + invalidObject);
							} catch (Exception e) {
								log.error(e.getMessage(), e);
							}
						}
					}
				}
			}
		}

	}

	class CheckIdleSizeTask extends TimerTask {

		@Override
		public void run() {
			synchronized (lock) {
				if (idleQueue.size() > maxIdleSize) {
					int destroyedSize = idleQueue.size() - maxIdleSize;
					for (int i = 0; i < destroyedSize; i++) {
						Object object = idleQueue.pollFirst();
						try {
							discardObject(object);
							log.warn("Discard redundant object: " + object);
						} catch (Exception e) {
							log.error(e.getMessage(), e);
						}
					}
				}
			}
		}

	}

	class CheckObjectExpiredTask extends TimerTask {

		@Override
		public void run() {
			synchronized (lock) {
				PooledObject pooledObject;
				for (Object busyObject : busyQueue) {
					pooledObject = pooledObjects.get(busyObject);
					if (System.currentTimeMillis() - pooledObject.getLastBorrowed() > maxWaitTimeForExpiration) {
						try {
							discardObject(busyObject);
							log.warn("Discard expired object: " + busyObject);
						} catch (Exception e) {
							log.error(e.getMessage(), e);
						}
					}
				}
			}
		}

	}

}
