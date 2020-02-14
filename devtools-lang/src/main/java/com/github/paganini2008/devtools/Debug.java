package com.github.paganini2008.devtools;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;

/**
 * 
 * Debug
 * 
 * @author Fred Feng
 * @version 1.0
 */
public abstract class Debug {

	public static <T> void print(Enumeration<T> en) {
		while (en.hasMoreElements()) {
			System.out.println(en.nextElement());
		}
	}

	public static <T> void print(Iterator<T> it) {
		while (it.hasNext()) {
			System.out.println(it.next());
		}
	}

	public static <T> void print(Iterable<T> it) {
		for (T t : it) {
			System.out.println(t);
		}
	}

	public static <K, V> void print(Map<K, V> map) {
		if (map != null) {
			for (Map.Entry<K, V> en : map.entrySet()) {
				System.out.println(en);
			}
		}
	}

}
