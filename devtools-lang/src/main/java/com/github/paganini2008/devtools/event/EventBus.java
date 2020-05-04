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
import com.github.paganini2008.devtools.multithreads.Actor;
import com.github.paganini2008.devtools.multithreads.AtomicUnsignedInteger;

/**
 * 
 * EventBus
 *
 * @author Fred Feng
 * @since 1.0
 */
public class EventBus<E extends Event<T>, T> {

	public EventBus() {
		this(8, true);
	}

	public EventBus(int nThreads, boolean multicast) {
		this(Executors.newFixedThreadPool(nThreads), multicast);
		this.autoShutdown = true;
	}

	public EventBus(Executor executor, boolean multicast) {
		this.eventHandler = new EventHandler<E, T>(executor, multicast);
		this.autoShutdown = false;
	}

	private EventHandler<E, T> eventHandler;
	private boolean autoShutdown;

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

	/**
	 * 
	 * EventHandler
	 *
	 * @author Fred Feng
	 * @since 1.0
	 */
	static class EventHandler<E extends Event<T>, T> extends Actor<Runnable> {

		final boolean multicast;
		final ConcurrentMap<Class<?>, EventGroup<E, T>> eventGroups = new ConcurrentHashMap<Class<?>, EventGroup<E, T>>();

		EventHandler(Executor executor, boolean multicast) {
			super(executor, -1);
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
	 * @since 1.0
	 */
	static class QueueGroup<E extends Event<T>, T> implements EventGroup<E, T> {

		final Actor<Runnable> actor;
		final List<EventSubscriber<E, T>> list;
		final boolean multicast;
		final AtomicUnsignedInteger index = new AtomicUnsignedInteger(0);

		QueueGroup(Actor<Runnable> actor, boolean multicast) {
			this.actor = actor;
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
			actor.accept(() -> {
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
	 * @version 1.0
	 */
	static class PubSubGroup<E extends Event<T>, T> implements EventGroup<E, T> {

		final Actor<Runnable> actor;
		final BlockingQueue<EventSubscriber<E, T>> q;
		final boolean multicast;

		PubSubGroup(Actor<Runnable> actor, boolean multicast) {
			this.actor = actor;
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
			if (multicast) {
				q.forEach(subscriber -> {
					actor.accept(() -> {
						subscriber.onEventFired(event);
					});
				});
			} else {
				List<EventSubscriber<E, T>> list = new ArrayList<EventSubscriber<E, T>>();
				if (q.drainTo(list) > 0) {
					list.forEach(subscriber -> {
						actor.accept(() -> {
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
