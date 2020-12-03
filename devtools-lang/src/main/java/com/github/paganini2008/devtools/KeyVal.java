package com.github.paganini2008.devtools;

import java.io.Serializable;

/**
 * 
 * KeyVal
 *
 * @author Jimmy Hoff
 * @version 1.0
 */
public class KeyVal<K, V> implements Serializable {

	private static final long serialVersionUID = 5086777628773199284L;

	public KeyVal(K key, V value) {
		this.key = key;
		this.value = value;
	}

	private K key;
	private V value;

	public K getKey() {
		return key;
	}

	public V getValue() {
		return value;
	}
	
	public static <K, V> KeyVal<K, V> of(K key, V val) {
		return new KeyVal<K, V>(key, val);
	}

	public String toString() {
		return "key=" + key + ", value=" + value;
	}

}
