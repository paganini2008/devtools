package com.github.paganini2008.devtools.event;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executor;
import java.util.concurrent.PriorityBlockingQueue;

import com.github.paganini2008.devtools.ClassUtils;
import com.github.paganini2008.devtools.multithreads.ExecutorUtils;
import com.github.paganini2008.devtools.multithreads.Producer;
import com.github.paganini2008.devtools.multithreads.Producer.Consumer;

/**
 * 
 * EventBus
 * 
 * @author Fred Feng
 * @revised 2019-05
 * @version 1.0
 */
public class EventBus implements EventPubSub {

	public EventBus(int nThreads) {
		this(nThreads, nThreads * 2);
	}

	public EventBus(int nThreads, int queueLength) {
		this(nThreads, queueLength, 60);
	}

	public EventBus(int nThreads, int queueLength, int timeout) {
		this.delegate = new EventHandler(nThreads, queueLength, timeout);
	}

	public EventBus(Executor executor, int maxPermits, long timeout) {
		this.delegate = new EventHandler(executor, maxPermits, timeout);
	}

	private EventHandler delegate;

	public void publish(Event event) {
		delegate.publish(event);
	}

	public <E extends Event> void subscribe(EventSubscriber<E> subscriber) {
		delegate.subscribe(subscriber);
	}

	public <E extends Event> void unsubscribe(EventSubscriber<E> subscriber) {
		delegate.unsubscribe(subscriber);
	}

	public void close() {
		delegate.producer.join();
	}

	/**
	 * 
	 * EventHandler
	 * 
	 * @author Fred Feng
	 * @revised 2019-05
	 * @version 1.0
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	static class EventHandler implements Consumer<Runnable, Object>, EventPubSub {

		final Producer<Runnable, Object> producer;
		final ConcurrentMap<Class<?>, EventGroup> eventGroups = new ConcurrentHashMap<Class<?>, EventGroup>();

		EventHandler(int nThreads, int maxPermits, long timeout) {
			this(ExecutorUtils.commonPool(nThreads), maxPermits, timeout);
		}

		EventHandler(Executor executor, int maxPermits, long timeout) {
			this.producer = new Producer<Runnable, Object>(executor, maxPermits, 0.5F, false, timeout,
					Integer.MAX_VALUE, this);
		}

		public void publish(Event event) {
			if (eventGroups.containsKey(event.getClass())) {
				eventGroups.get(event.getClass()).onEventFired(event);
			}
		}

		public <E extends Event> void subscribe(EventSubscriber<E> subscriber) {
			final Class<?> eventClass = findParameterizedType(subscriber.getClass());
			EventGroup<E> eventGroup = eventGroups.get(eventClass);
			if (eventGroup == null) {
				eventGroups.putIfAbsent(eventClass, new EventGroup<Event>(producer));
				eventGroup = eventGroups.get(eventClass);
			}
			Queue<EventSubscriber<E>> q = eventGroup.subscribers;
			if (!q.contains(subscriber)) {
				q.offer(subscriber);
			}
		}

		public <E extends Event> void unsubscribe(EventSubscriber<E> subscriber) {
			final Class<?> eventClass = findParameterizedType(subscriber.getClass());
			if (eventGroups.containsKey(eventClass)) {
				EventGroup<E> eventGroup = eventGroups.get(eventClass);
				eventGroup.subscribers.remove(subscriber);
			}
		}

		public Object consume(Runnable action) {
			action.run();
			return null;
		}

		public void onRejection(Runnable action) {
			action.run();
		}

	}

	/**
	 * 
	 * EventGroup
	 * 
	 * @author Fred Feng
	 * @revised 2019-05
	 * @version 1.0
	 */
	static class EventGroup<E extends Event> implements EventSubscriber<E> {

		final Producer<Runnable, Object> producer;
		final Queue<EventSubscriber<E>> subscribers = new PriorityBlockingQueue<EventSubscriber<E>>();

		EventGroup(Producer<Runnable, Object> producer) {
			this.producer = producer;
		}

		public void onEventFired(final E event) {
			producer.submit(() -> {
				final Queue<EventSubscriber<E>> q = new ArrayDeque<EventSubscriber<E>>(subscribers);
				while (!q.isEmpty()) {
					q.poll().onEventFired(event);
				}
			});
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
