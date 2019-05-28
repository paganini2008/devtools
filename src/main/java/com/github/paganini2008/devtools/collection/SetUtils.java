package com.github.paganini2008.devtools.collection;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * SetUtils
 * 
 * @author Fred Feng
 * @version 1.0
 */
@SuppressWarnings("all")
public class SetUtils {

	private SetUtils() {
	}

	public static <T> Set<T> unmodifiableSet(T... args) {
		return Collections.unmodifiableSet(create(args));
	}

	public static <T> Set<T> unmodifiableSet(Collection<T> c) {
		return Collections.unmodifiableSet(toSet(c));
	}

	public static <T> Set<T> create(T... args) {
		return args != null ? new LinkedHashSet<T>(Arrays.asList(args)) : new LinkedHashSet();
	}

	public static <T> Set<T> toSet(Collection<T> c) {
		return c instanceof LinkedHashSet ? (LinkedHashSet<T>) c : new LinkedHashSet(c);
	}

	public static boolean isSet(Object obj) {
		return obj == null ? false : obj instanceof Set;
	}

	public static boolean isNotSet(Object obj) {
		return !isSet(obj);
	}

}
