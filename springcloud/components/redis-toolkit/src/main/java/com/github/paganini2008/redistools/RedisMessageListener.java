package com.github.paganini2008.redistools;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.context.ApplicationListener;

import com.github.paganini2008.redistools.RedisMessageSender.MessageEntry;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * RedisMessageListener
 * 
 * @author Fred Feng
 * @revised 2019-05
 * @version 1.0
 */
@Slf4j
public class RedisMessageListener implements ApplicationListener<RedisMessageEvent> {

	private final ConcurrentMap<String, List<RedisMessageHandler>> durableHandlers = new ConcurrentHashMap<String, List<RedisMessageHandler>>();
	private final ConcurrentMap<String, List<RedisMessageHandler>> temporaryHandlers = new ConcurrentHashMap<String, List<RedisMessageHandler>>();

	public void onApplicationEvent(RedisMessageEvent event) {
		String json = event.getMessage();
		final MessageEntry messageEntry = JsonUtils.readObject(json, RedisMessageSender.MessageEntry.class);
		List<RedisMessageHandler> handlers = durableHandlers.get(messageEntry.getChannel());
		if (handlers != null) {
			for (RedisMessageHandler handler : handlers) {
				try {
					handler.handleMessage(messageEntry.getChannel(), messageEntry.getMessage());
				} catch (Exception e) {
					log.error(e.getMessage(), e);
				}
			}
		}
		handlers = temporaryHandlers.get(messageEntry.getChannel());
		if (handlers != null) {
			for (RedisMessageHandler handler : handlers) {
				try {
					handler.handleMessage(messageEntry.getChannel(), messageEntry.getMessage());
				} catch (Exception e) {
					log.error(e.getMessage(), e);
				}
				handlers.remove(handler);
			}
		}
	}

	public void registerMessageHandler(String channel, RedisMessageHandler messageHandler) {
		registerMessageHandler(channel, messageHandler, false);
	}

	public void registerMessageHandler(String channel, RedisMessageHandler messageHandler, boolean temporary) {
		if (messageHandler != null) {
			ConcurrentMap<String, List<RedisMessageHandler>> selectedHandlers = selectedHandlers(temporary);
			List<RedisMessageHandler> handlers = selectedHandlers.get(channel);
			if (handlers == null) {
				selectedHandlers.putIfAbsent(channel, new CopyOnWriteArrayList<RedisMessageHandler>());
				handlers = selectedHandlers.get(channel);
			}
			handlers.add(messageHandler);
		}
	}

	public void registerMessageHandler(String channel, int index, RedisMessageHandler messageHandler) {
		registerMessageHandler(channel, index, messageHandler, false);
	}

	public void registerMessageHandler(String channel, int index, RedisMessageHandler messageHandler, boolean temporary) {
		if (messageHandler != null) {
			ConcurrentMap<String, List<RedisMessageHandler>> selectedHandlers = selectedHandlers(temporary);
			List<RedisMessageHandler> handlers = selectedHandlers.get(channel);
			if (handlers == null) {
				selectedHandlers.putIfAbsent(channel, new CopyOnWriteArrayList<RedisMessageHandler>());
				handlers = selectedHandlers.get(channel);
			}
			handlers.add(index, messageHandler);
		}
	}

	private ConcurrentMap<String, List<RedisMessageHandler>> selectedHandlers(boolean temporary) {
		return temporary ? temporaryHandlers : durableHandlers;
	}

}
