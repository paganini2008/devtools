package com.github.paganini2008.devtools.event;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.PriorityBlockingQueue;

import com.github.paganini2008.devtools.ClassUtils;
import com.github.paganini2008.devtools.multithreads.Producer;
import com.github.paganini2008.devtools.multithreads.Producer.Consumer;
import com.github.paganini2008.devtools.multithreads.ThreadPool;
import com.github.paganini2008.devtools.multithreads.ThreadUtils;

/**
 * 
 * EventBus
 * 
 * @author Fred Feng
 * @revised 2019-05
 * @created 2019-05
 * @version 1.0
 */
public class EventBus<E extends Event<T>, T> implements EventPubSub<E, T> {

	public EventBus(int nThreads, boolean multicast) {
		this(ThreadUtils.commonPool(nThreads), multicast);
	}

	public EventBus(ThreadPool threadPool, boolean multicast) {
		this.delegate = new EventHandler<E, T>(threadPool, multicast);
	}

	private EventHandler<E, T> delegate;

	public void publish(E event) {
		delegate.publish(event);
	}

	public void subscribe(EventSubscriber<E, T> subscriber) {
		delegate.subscribe(subscriber);
	}

	public void unsubscribe(EventSubscriber<E, T> subscriber) {
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
	static class EventHandler<E extends Event<T>, T> implements Consumer<Runnable, Object>, EventPubSub<E, T> {

		final Producer<Runnable, Object> producer;
		final boolean multicast;
		final ConcurrentMap<Class<?>, EventGroup> eventGroups = new ConcurrentHashMap<Class<?>, EventGroup>();

		EventHandler(ThreadPool threadPool, boolean multicast) {
			this.producer = new Producer<Runnable, Object>(threadPool, this);
			this.multicast = multicast;
		}

		public void publish(E event) {
			if (eventGroups.containsKey(event.getClass())) {
				eventGroups.get(event.getClass()).onEventFired(event);
			}
		}

		public void subscribe(EventSubscriber<E, T> subscriber) {
			final Class<?> eventClass = findParameterizedType(subscriber.getClass());
			EventGroup<E, T> eventGroup = eventGroups.get(eventClass);
			if (eventGroup == null) {
				eventGroups.putIfAbsent(eventClass, new EventGroup<E, T>(producer, multicast));
				eventGroup = eventGroups.get(eventClass);
			}
			if (eventGroup != null) {
				Queue<EventSubscriber<E, T>> q = eventGroup.q;
				if (!q.contains(subscriber)) {
					q.offer(subscriber);
				}
			}
		}

		public void unsubscribe(EventSubscriber<E, T> subscriber) {
			final Class<?> eventClass = findParameterizedType(subscriber.getClass());
			if (eventGroups.containsKey(eventClass)) {
				EventGroup<E, T> eventGroup = eventGroups.get(eventClass);
				eventGroup.q.remove(subscriber);
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
	static class EventGroup<E extends Event<T>, T> implements EventSubscriber<E, T> {

		final Producer<Runnable, Object> producer;
		final BlockingQueue<EventSubscriber<E, T>> q;
		final boolean multicast;

		EventGroup(Producer<Runnable, Object> producer, boolean multicast) {
			this.producer = producer;
			this.multicast = multicast;
			this.q = new PriorityBlockingQueue<EventSubscriber<E, T>>();
		}

		public void onEventFired(final E event) {
			if (multicast) {
				q.forEach(subscriber -> {
					producer.produce(() -> {
						subscriber.onEventFired(event);
					});
				});
			} else {
				List<EventSubscriber<E, T>> list = new ArrayList<EventSubscriber<E, T>>();
				int elements = q.drainTo(list);
				if (elements > 0) {
					list.forEach(subscriber -> {
						producer.produce(() -> {
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
