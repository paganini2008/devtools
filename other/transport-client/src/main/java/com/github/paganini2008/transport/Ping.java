package com.github.paganini2008.transport;

import java.util.Map;

/**
 * 
 * Ping
 *
 * @author Fred Feng
 * @created 2020-01
 * @revised 2020-02
 * @version 1.0
 */
public final class Ping implements Tuple {

	public Ping() {
	}

	public boolean hasField(String fieldName) {
		throw new IllegalStateException();
	}

	public void setField(String fieldName, Object value) {
		throw new IllegalStateException();
	}

	public Object getField(String fieldName) {
		throw new IllegalStateException();
	}

	public <T> T getField(String fieldName, Class<T> requiredType) {
		throw new IllegalStateException();
	}

	public void fill(Object object) {
		throw new IllegalStateException();
	}

	public Map<String, Object> toMap() {
		throw new IllegalStateException();
	}

	public Tuple clone() {
		throw new IllegalStateException();
	}

	public String toString() {
		return "PING";
	}

}
