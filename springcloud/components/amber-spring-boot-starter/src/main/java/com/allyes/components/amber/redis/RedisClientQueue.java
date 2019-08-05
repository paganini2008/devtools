package com.allyes.components.amber.redis;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.allyes.developer.utils.concurrent.AtomicPositiveInteger;

/**
 * 
 * RedisClientQueue
 * 
 * @author Fred Feng
 * @created 2018-03
 */
public class RedisClientQueue {

	private final AtomicPositiveInteger counter = new AtomicPositiveInteger();
	private final List<String> clientIds = new CopyOnWriteArrayList<String>();

	public RedisClientQueue() {
		clientIds.add(RedisDispatcher.ID);
	}

	public void removeId(String clientId) {
		clientIds.remove(clientId);
	}

	public void acceptId(String clientId) {
		if (!clientIds.contains(clientId)) {
			clientIds.add(clientId);
		}
	}

	public int size() {
		return clientIds.size();
	}

	public String selectId() {
		int index = counter.getAndIncrement() % size();
		return clientIds.get(index);
	}
	
	public String toString() {
		return clientIds.toString();
	}

}
