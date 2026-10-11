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

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import com.github.paganini2008.devtools.Assert;
import com.github.paganini2008.devtools.ClassUtils;
import com.github.paganini2008.devtools.multithreads.ExecutorUtils;
import com.github.paganini2008.devtools.multithreads.ForEach;

/**
 * 
 * EventBus
 * <p>
 * A simple in-process event bus. Subscribers are grouped by the event class they declare; events
 * are dispatched asynchronously on an {@link Executor}. Pub/sub subscribers receive every event
 * (multicast) or only the next one (non-multicast); queue subscribers share events round robin
 * (multicast) or consume one event each (non-multicast).
 *
 * @author Fred Feng
 * @date 2019-05-29
 * @since 2.0.1
 */
public class EventBus<E extends Event<T>, T> {

	/**
	 * Creates a bus with its own fixed thread pool, shut down by {@link #close()}.
	 *
	 * @param nThreads
	 *            the number of dispatching threads
	 * @param multicast
	 *            the dispatch mode (see the class description)
	 */
	public EventBus(int nThreads, boolean multicast) {
		this(Executors.newFixedThreadPool(nThreads), multicast, true);
	}

	/**
	 * Creates a bus dispatching on the given executor.
	 *
	 * @param executor
	 *            the executor running subscribers
	 * @param multicast
	 *            the dispatch mode (see the class description)
	 * @param autoShutdownExecutor
	 *            whether {@link #close()} shuts the executor down
	 */
	public EventBus(Executor executor, boolean multicast, boolean autoShutdownExecutor) {
		this.eventHandler = new EventHandler<E, T>(executor, multicast);
		this.autoShutdown = autoShutdownExecutor;
	}

	private EventHandler<E, T> eventHandler;
	private final boolean autoShutdown;

	/**
	 * Publishes an event to the subscribers of its exact class. Events without subscribers, and
	 * null events, are dropped. Publishing from a subscriber does not block even when the bus is
	 * saturated (the nested event is then dispatched in the calling thread).
	 *
	 * @param event
	 *            the event
	 */
	public void publish(E event) {
		eventHandler.publish(event);
	}

	/**
	 * Subscribes to the event class declared by the subscriber's generic type.
	 *
	 * @param subscriber
	 *            the subscriber
	 * @throws IllegalArgumentException
	 *             if the subscriber is null
	 * @throws IllegalStateException
	 *             if the event class cannot be determined (e.g. a lambda)
	 */
	public void subscribe(EventSubscriber<E, T> subscriber) {
		eventHandler.subscribe(subscriber);
	}

	/**
	 * Removes a subscriber.
	 *
	 * @param subscriber
	 *            the subscriber
	 * @throws IllegalArgumentException
	 *             if the subscriber is null
	 */
	public void unsubscribe(EventSubscriber<E, T> subscriber) {
		eventHandler.unsubscribe(subscriber);
	}

	/**
	 * Waits until all dispatched events have been handled and, if configured, shuts the executor
	 * down. When called from a subscriber (where waiting would deadlock), it returns immediately
	 * and the shutdown, if configured, is completed asynchronously.
	 */
	public void close() {
		eventHandler.join(autoShutdown);
	}

	private static int getMaxPermits(Executor executor) {
		int maxPoolSize = ExecutorUtils.getMaximumPoolSize(executor, 0);
		return maxPoolSize > 0 ? maxPoolSize * 2 : 0;
	}

	/**
	 * 
	 * EventHandler
	 *
	 * @author Fred Feng
	 * @since 2.0.1
	 */
	static class EventHandler<E extends Event<T>, T> extends ForEach<Runnable> {

		final boolean multicast;
		final ConcurrentMap<Class<?>, EventGroup<E, T>> eventGroups = new ConcurrentHashMap<Class<?>, EventGroup<E, T>>();

		EventHandler(Executor executor, boolean multicast) {
			super(executor, getMaxPermits(executor));
			this.multicast = multicast;
		}

		public void publish(E event) {
			if (event == null) {
				return;
			}
			EventGroup<E, T> eventGroup = eventGroups.get(event.getClass());
			if (eventGroup != null) {
				eventGroup.onEventFired(event);
			}
		}

		public void subscribe(EventSubscriber<E, T> subscriber) {
			Assert.isNull(subscriber, "Nullable subscriber");
			final Class<?> eventClass = findParameterizedType(subscriber.getClass());
			EventGroup<E, T> eventGroup = eventGroups.get(eventClass);
			if (eventGroup == null) {
				eventGroups.putIfAbsent(eventClass,
						subscriber.isPubSub() ? new PubSubGroup<>(this, multicast) : new QueueGroup<>(this, multicast));
				eventGroup = eventGroups.get(eventClass);
			}
			eventGroup.subscribe(subscriber);
		}

		public void unsubscribe(EventSubscriber<E, T> subscriber) {
			Assert.isNull(subscriber, "Nullable subscriber");
			final Class<?> eventClass = findParameterizedType(subscriber.getClass());
			if (eventGroups.containsKey(eventClass)) {
				EventGroup<E, T> eventGroup = eventGroups.get(eventClass);
				eventGroup.unsubscribe(subscriber);
			}
		}

		@Override
		protected void process(Runnable element) {
			element.run();
		}

	}

	/**
	 * 
	 * QueueGroup
	 *
	 * @author Fred Feng
	 * @since 2.0.1
	 */
	static class QueueGroup<E extends Event<T>, T> implements EventGroup<E, T> {

		final ForEach<Runnable> forEach;
		final CopyOnWriteArrayList<EventSubscriber<E, T>> list;
		final boolean multicast;
		final AtomicInteger index = new AtomicInteger(0);

		QueueGroup(ForEach<Runnable> forEach, boolean multicast) {
			this.forEach = forEach;
			this.multicast = multicast;
			this.list = new CopyOnWriteArrayList<EventSubscriber<E, T>>();
		}

		@Override
		public void subscribe(EventSubscriber<E, T> subscriber) {
			if (subscriber != null) {
				list.addIfAbsent(subscriber);
			}
		}

		@Override
		public void unsubscribe(EventSubscriber<E, T> subscriber) {
			list.remove(subscriber);
		}

		@Override
		public void onEventFired(final E event) {
			if (list.isEmpty()) {
				return;
			}
			EventSubscriber<E, T> subscriber = null;
			while (subscriber == null) {
				int size = list.size();
				if (size == 0) {
					return;
				}
				try {
					if (multicast) {
						// Round robin
						subscriber = list.get(Math.floorMod(index.getAndIncrement(), size));
					} else {
						// One-shot: the first subscriber consumes the event and is removed
						subscriber = list.remove(0);
					}
				} catch (IndexOutOfBoundsException e) {
					// Concurrently modified, retry
				}
			}
			final EventSubscriber<E, T> target = subscriber;
			forEach.accept(() -> {
				target.onEventFired(event);
			});
		}
	}

	/**
	 * 
	 * PubSubGroup
	 * 
	 * @author Fred Feng
	 * 
	 * @since 2.0.1
	 */
	static class PubSubGroup<E extends Event<T>, T> implements EventGroup<E, T> {

		final ForEach<Runnable> forEach;
		final BlockingQueue<EventSubscriber<E, T>> q;
		final boolean multicast;

		PubSubGroup(ForEach<Runnable> forEach, boolean multicast) {
			this.forEach = forEach;
			this.multicast = multicast;
			this.q = new PriorityBlockingQueue<EventSubscriber<E, T>>();
		}

		@Override
		public synchronized void subscribe(EventSubscriber<E, T> subscriber) {
			if (!q.contains(subscriber)) {
				q.add(subscriber);
			}
		}

		@Override
		public void unsubscribe(EventSubscriber<E, T> subscriber) {
			q.remove(subscriber);
		}

		@Override
		public void onEventFired(final E event) {
			if (q.isEmpty()) {
				return;
			}
			if (multicast) {
				// Iterate over a snapshot: PriorityBlockingQueue.forEach holds the queue lock, and
				// ForEach.accept may block until a subscriber (which may unsubscribe) completes
				List<EventSubscriber<E, T>> list = new ArrayList<EventSubscriber<E, T>>(q);
				list.forEach(subscriber -> {
					forEach.accept(() -> {
						subscriber.onEventFired(event);
					});
				});
			} else {
				List<EventSubscriber<E, T>> list = new ArrayList<EventSubscriber<E, T>>();
				if (q.drainTo(list) > 0) {
					list.forEach(subscriber -> {
						forEach.accept(() -> {
							subscriber.onEventFired(event);
						});
					});
				}
			}
		}

	}

	private static Class<?> findParameterizedType(Class<?> implementation) {
		List<ParameterizedType> parameterizedTypes = ClassUtils.getAllParameterizedTypes(implementation);
		for (ParameterizedType parameterizedType : parameterizedTypes) {
			if (parameterizedType.getRawType() == EventSubscriber.class) {
				Type eventType = parameterizedType.getActualTypeArguments()[0];
				if (eventType instanceof ParameterizedType) {
					eventType = ((ParameterizedType) eventType).getRawType();
				}
				if (eventType instanceof Class) {
					return (Class<?>) eventType;
				}
				break;
			}
		}
		throw new IllegalStateException(implementation.getName());
	}

}
