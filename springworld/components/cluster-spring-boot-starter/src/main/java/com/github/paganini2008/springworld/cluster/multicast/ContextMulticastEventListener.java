package com.github.paganini2008.springworld.cluster.multicast;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import com.github.paganini2008.devtools.collection.MapUtils;

/**
 * 
 * ContextMulticastEventListener
 * 
 * @author Fred Feng
 * @created 2019-08
 * @revised 2019-11
 * @version 1.0
 */
public class ContextMulticastEventListener {

	public static final String GLOBAL_TOPIC = "*";
	private final Map<String, List<ContextMulticastEventHandler>> topicHandlers = new ConcurrentHashMap<String, List<ContextMulticastEventHandler>>();

	public void fireOnJoin(final String clusterId) {
		List<ContextMulticastEventHandler> eventHandlers = topicHandlers.get(GLOBAL_TOPIC);
		if (eventHandlers != null) {
			eventHandlers.forEach(handler -> {
				handler.onJoin(clusterId);
			});
		}
	}

	public void fireOnLeave(final String clusterId) {
		List<ContextMulticastEventHandler> eventHandlers = topicHandlers.get(GLOBAL_TOPIC);
		if (eventHandlers != null) {
			eventHandlers.forEach(handler -> {
				handler.onLeave(clusterId);
			});
		}
	}

	public void fireOnMessage(final String clusterId, final String topic, final Object message) {
		if (GLOBAL_TOPIC.equals(topic)) {
			List<ContextMulticastEventHandler> eventHandlers = topicHandlers.get(topic);
			if (eventHandlers != null) {
				eventHandlers.forEach(handler -> {
					handler.onGlobalMessage(clusterId, message);
				});
			}
		} else {
			List<ContextMulticastEventHandler> eventHandlers = topicHandlers.get(topic);
			if (eventHandlers != null) {
				eventHandlers.forEach(handler -> {
					handler.onMessage(clusterId, message);
				});
			}
		}
	}

	public void addHandler(ContextMulticastEventHandler multicastEventHandler) {
		String topic = multicastEventHandler.getTopic();
		List<ContextMulticastEventHandler> handlers = MapUtils.get(topicHandlers, topic, () -> {
			return new CopyOnWriteArrayList<ContextMulticastEventHandler>();
		});
		if (!handlers.contains(multicastEventHandler)) {
			handlers.add(multicastEventHandler);
		}
		handlers = MapUtils.get(topicHandlers, GLOBAL_TOPIC, () -> {
			return new CopyOnWriteArrayList<ContextMulticastEventHandler>();
		});
		if (!handlers.contains(multicastEventHandler)) {
			handlers.add(multicastEventHandler);
		}
	}

}
