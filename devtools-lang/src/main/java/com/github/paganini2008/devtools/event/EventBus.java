/**
* Copyright 2017-2022 Fred Feng (paganini.fy@gmail.com)

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

import com.github.paganini2008.devtools.Assert;
import com.github.paganini2008.devtools.ClassUtils;
import com.github.paganini2008.devtools.multithreads.AtomicIntegerSequence;
import com.github.paganini2008.devtools.multithreads.ExecutorUtils;
import com.github.paganini2008.devtools.multithreads.ForEach;

/**
 * 
 * EventBus
 *
 * @author Fred Feng
 * @since 2.0.1
 */
public class EventBus<E extends Event<T>, T> {

	public EventBus(int nThreads, boolean multicast) {
		this(Executors.newFixedThreadPool(nThreads), multicast, true);
	}

	public EventBus(Executor executor, boolean multicast, boolean autoShutdownExecutor) {
		this.eventHandler = new EventHandler<E, T>(executor, multicast);
		this.autoShutdown = autoShutdownExecutor;
	}

	private EventHandler<E, T> eventHandler;
	private final boolean autoShutdown;

	public void publish(E event) {
		eventHandler.publish(event);
	}

	public void subscribe(EventSubscriber<E, T> subscriber) {
		eventHandler.subscribe(subscriber);
	}

	public void unsubscribe(EventSubscriber<E, T> subscriber) {
		eventHandler.unsubscribe(subscriber);
	}

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
			if (eventGroups.containsKey(event.getClass())) {
				eventGroups.get(event.getClass()).onEventFired(event);
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
		final List<EventSubscriber<E, T>> list;
		final boolean multicast;
		final AtomicIntegerSequence index = new AtomicIntegerSequence(0);

		QueueGroup(ForEach<Runnable> forEach, boolean multicast) {
			this.forEach = forEach;
			this.multicast = multicast;
			this.list = new CopyOnWriteArrayList<EventSubscriber<E, T>>();
		}

		@Override
		public void subscribe(EventSubscriber<E, T> subscriber) {
			if (!list.contains(subscriber)) {
				list.add(subscriber);
				index.setMaxValue(list.size() - 1);
			}
		}

		@Override
		public void unsubscribe(EventSubscriber<E, T> subscriber) {
			if (list.contains(subscriber)) {
				list.remove(subscriber);
				index.setMaxValue(list.size() - 1);
			}
		}

		@Override
		public void onEventFired(final E event) {
			if (list.isEmpty()) {
				return;
			}
			EventSubscriber<E, T> subscriber;
			if (multicast) {
				subscriber = list.get(index.getAndIncrement());
			} else {
				subscriber = list.remove(index.getAndIncrement());
			}
			forEach.accept(() -> {
				subscriber.onEventFired(event);
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
		public void subscribe(EventSubscriber<E, T> subscriber) {
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
				q.forEach(subscriber -> {
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
				Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
				return (Class<?>) actualTypeArguments[0];
			}
		}
		throw new IllegalStateException(implementation.getName());
	}

}
