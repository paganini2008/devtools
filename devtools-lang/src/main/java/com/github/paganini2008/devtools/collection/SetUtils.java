package com.github.paganini2008.devtools.collection;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * SetUtils
 * 
 * @author Fred Feng
 * @version 1.0
 */
@SuppressWarnings("all")
public abstract class SetUtils {

	public static <T> Set<T> unmodifiableSet(T... args) {
		return Collections.unmodifiableSet(create(args));
	}

	public static <T> Set<T> unmodifiableSet(Collection<T> c) {
		return Collections.unmodifiableSet((c instanceof Set ? (Set<T>) c : new HashSet(c)));
	}

	public static <T> Set<T> create(T... args) {
		return toSet(Arrays.asList(args));
	}

	public static <T> Set<T> toSet(Collection<T> c) {
		return c instanceof Set ? (Set<T>) c : new HashSet(c);
	}

	public static boolean isSet(Object obj) {
		return obj == null ? false : obj instanceof Set;
	}

	public static boolean isNotSet(Object obj) {
		return !isSet(obj);
	}

}
