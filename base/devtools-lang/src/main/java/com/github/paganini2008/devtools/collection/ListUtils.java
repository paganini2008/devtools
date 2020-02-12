package com.github.paganini2008.devtools.collection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import com.github.paganini2008.devtools.ArrayUtils;
import com.github.paganini2008.devtools.Assert;
import com.github.paganini2008.devtools.ObjectUtils;

/**
 * ListUtils
 * 
 * @author Fred Feng
 * @version 1.0
 */
@SuppressWarnings("all")
public abstract class ListUtils {

	public static boolean isList(Object obj) {
		return obj == null ? false : obj instanceof List;
	}

	public static boolean isNotList(Object obj) {
		return !isList(obj);
	}

	public static boolean isEmpty(List<?> list) {
		return !isNotEmpty(list);
	}

	public static boolean isNotEmpty(List<?> list) {
		return list != null && list.size() > 0;
	}

	public static <T> List<T> unmodifiableList(T... args) {
		return Collections.unmodifiableList(Arrays.asList(args));
	}

	public static <T> List<T> unmodifiableList(Collection<T> c) {
		return Collections.unmodifiableList(new ArrayList<T>(c));
	}

	public static <T> List<T> create(T... args) {
		return args != null ? new ArrayList<T>(Arrays.asList(args)) : new ArrayList<T>();
	}

	public static <T> List<T> toList(Collection<T> c) {
		return c instanceof List ? (List<T>) c : new ArrayList<T>(c);
	}

	private static int indexFor(List<?> list, int index) {
		int size = list.size();
		if (index > size - 1) {
			return -1;
		}
		if (index < 0) {
			index = size - Math.abs(index);
		}
		return Math.min(size - 1, index);
	}

	public static <T> T get(List<T> list, int index) {
		return get(list, index, null);
	}

	public static <T> T get(List<T> list, int index, T defaultValue) {
		if (isEmpty(list)) {
			return defaultValue;
		}
		index = indexFor(list, index);
		if (index < 0) {
			return defaultValue;
		}
		return list.get(index);
	}

	public static <T> T getFirst(List<T> list) {
		return getFirst(list, null);
	}

	public static <T> T getFirst(List<T> list, T defaultValue) {
		if (isEmpty(list)) {
			return defaultValue;
		}
		return list.get(0);
	}

	public static <T> T remove(List<T> list, int index) {
		T t = get(list, index);
		list.remove(t);
		return t;
	}

	public static <T> T removeFirst(List<T> list) {
		T t = getFirst(list);
		if (list != null) {
			list.remove(t);
		}
		return t;
	}

	public static <T> T getLast(List<T> list) {
		return getLast(list, null);
	}

	public static <T> T getLast(List<T> list, T defaultValue) {
		if (isEmpty(list)) {
			return defaultValue;
		}
		return list.get(list.size() - 1);
	}

	public static <T> T removeLast(List<T> list) {
		T t = getLast(list);
		if (list != null) {
			list.remove(t);
		}
		return t;
	}

	public static <T> List<T> copy(List<T> src, int count) {
		return new ArrayList<T>(src.subList(0, count));
	}

	public static <T> List<T> copy(List<T> src, int startIndex, int count) {
		return new ArrayList<T>(src.subList(startIndex, count));
	}

	public static <T> void copy(List<T> src, int srcFrom, List<T> dest, int destFrom, int count) {
		Assert.isNull(src, "Source list must not be null.");
		Assert.isNull(dest, "Destination list must not be null.");
		int size = Math.min(src.size(), dest.size());
		size = Math.min(size, count);
		for (int i = srcFrom, j = destFrom; i < size && j < size; i++, j++) {
			dest.set(j, src.get(i));
		}
	}

	public static <T> List<T> slice(List<T> list, int limit) {
		return slice(list, limit, 0);
	}

	public static <T> List<T> slice(List<T> list, int limit, int offset) {
		Assert.isNull(list, "List must not be null.");
		int length = list.size();
		if (limit > 0 && offset >= 0) {
			return Collections.unmodifiableList(list.subList(offset, Math.min(offset + limit, length)));
		} else if (limit == -1 && offset >= 0) {
			return Collections.unmodifiableList(list.subList(offset, length));
		}
		throw new IllegalArgumentException("limit=" + limit + ", offset=" + offset);
	}

	public static <T extends Comparable<T>> void asc(List<T> list) {
		Assert.isNull(list, "List must not be null.");
		if (list.size() > 0) {
			T[] array = (T[]) list.toArray();
			ArrayUtils.asc(array);
			ListIterator<T> it = list.listIterator();
			for (int j = 0; j < array.length; j++) {
				it.next();
				it.set(array[j]);
			}
		}
	}

	public static <T extends Comparable<T>> void desc(List<T> list) {
		Assert.isNull(list, "List must not be null.");
		if (list.size() > 0) {
			T[] array = (T[]) list.toArray();
			ArrayUtils.desc(array);
			ListIterator<T> it = list.listIterator();
			for (int j = 0; j < array.length; j++) {
				it.next();
				it.set(array[j]);
			}
		}
	}

	public static <T> void sort(List<T> list, Comparator<T> c) {
		Assert.isNull(list, "List must not be null.");
		if (list.size() > 0) {
			T[] array = (T[]) list.toArray();
			ArrayUtils.sort(array, c);
			ListIterator<T> it = list.listIterator();
			for (int j = 0; j < array.length; j++) {
				it.next();
				it.set(array[j]);
			}
		}
	}

	public static <T> List<T> list(Iterator<T> it) {
		Assert.isNull(it, "Iterator must not be null.");
		List<T> list = new ArrayList<T>();
		while (it.hasNext()) {
			list.add(it.next());
		}
		return list;
	}

	public static <T> List<T> list(Enumeration<T> en) {
		Assert.isNull(en, "Enumeration must not be null.");
		List<T> list = new ArrayList<T>();
		while (en.hasMoreElements()) {
			list.add(en.nextElement());
		}
		return list;
	}

	public static <T> List<T> reverse(Iterator<T> it) {
		Assert.isNull(it, "Iterator must not be null.");
		List<T> list = list(it);
		reverse(list);
		return list;
	}

	public static <T> List<T> reverse(Enumeration<T> en) {
		Assert.isNull(en, "Enumeration must not be null.");
		List<T> list = list(en);
		reverse(list);
		return list;
	}

	public static <T> void reverse(List<T> list) {
		int s = list.size();
		ListIterator<T> fit = list.listIterator();
		ListIterator<T> mit = list.listIterator(s);
		T t, a;
		for (int i = 0, m = s >> 1; i < m; i++) {
			t = fit.next();
			a = mit.previous();
			fit.set(a);
			mit.set(t);
		}
	}

	public static <T> void swap(List<T> list, int i, int j) {
		final List<T> l = list;
		l.set(i, l.set(j, l.get(i)));
	}

	public static List<String> toStringList(Collection<?> c) {
		if (c == null || c.isEmpty()) {
			return null;
		}
		return toStringList(c.iterator());
	}

	public static List<String> toStringList(Iterator<?> it) {
		if (it == null || !it.hasNext()) {
			return null;
		}
		List<String> list = new ArrayList<String>();
		String arg;
		Object o;
		while (true) {
			o = it.next();
			arg = ObjectUtils.toString(o);
			list.add(arg);
			if (!it.hasNext()) {
				break;
			}
		}
		return list;
	}

	public static List<String> toStringList(Enumeration<?> e) {
		if (e == null || !e.hasMoreElements()) {
			return null;
		}
		List<String> list = new ArrayList<String>();
		String arg;
		Object o;
		while (true) {
			o = e.nextElement();
			arg = ObjectUtils.toString(o);
			list.add(arg);
			if (!e.hasMoreElements()) {
				break;
			}
		}
		return list;
	}

}
