package com.github.paganini2008.devtools;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;

/**
 * 
 * Console
 * 
 * @author Jimmy Hoff
 *
 * @since 1.0
 */
public abstract class Console {

	public static void log(String msg) {
		if (StringUtils.isNotBlank(msg)) {
			System.out.println(msg);
		}
	}

	public static void logf(String msg, Object... args) {
		if (StringUtils.isNotBlank(msg)) {
			System.out.printf(msg, args);
			System.out.println();
		}
	}

	public static <T> void log(Enumeration<T> en) {
		if (en != null) {
			while (en.hasMoreElements()) {
				System.out.println(en.nextElement());
			}
		}
	}

	public static <T> void log(Iterator<T> it) {
		if (it != null) {
			while (it.hasNext()) {
				System.out.println(it.next());
			}
		}
	}

	public static <T> void log(Iterable<T> iterable) {
		if (iterable != null) {
			for (T t : iterable) {
				System.out.println(t);
			}
		}
	}

	public static <T> void log(T[] array) {
		if (array != null) {
			for (T t : array) {
				System.out.println(t);
			}
		}
	}

	public static <K, V> void log(Map<K, V> map) {
		if (map != null) {
			for (Map.Entry<K, V> en : map.entrySet()) {
				System.out.println(en);
			}
		}
	}

}
