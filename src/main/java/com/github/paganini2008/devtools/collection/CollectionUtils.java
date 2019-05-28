package com.github.paganini2008.devtools.collection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;

import com.github.paganini2008.devtools.Assert;
import com.github.paganini2008.devtools.ObjectUtils;
import com.github.paganini2008.devtools.StringUtils;
import com.github.paganini2008.devtools.beans.Getter;

/**
 * CollectionUtils
 * 
 * @author Fred Feng
 * @version 1.0
 */
@SuppressWarnings("all")
public class CollectionUtils {

	private CollectionUtils() {
	}

	public static <T> Collection<T> unmodifiableCollection(T... args) {
		return unmodifiableCollection(Arrays.asList(args));
	}

	public static <T> Collection<T> unmodifiableCollection(Collection<T> args) {
		return args != null ? Collections.unmodifiableCollection(args) : Collections.EMPTY_LIST;
	}

	public static boolean isEmpty(Enumeration<?> e) {
		return !isNotEmpty(e);
	}

	public static boolean isNotEmpty(Enumeration<?> e) {
		return e == null ? false : e.hasMoreElements();
	}

	public static boolean isEmpty(Iterator<?> it) {
		return !isNotEmpty(it);
	}

	public static boolean isNotEmpty(Iterator<?> it) {
		return it == null ? false : it.hasNext();
	}

	public static boolean isEmpty(Collection<?> collection) {
		return collection == null ? true : collection.isEmpty();
	}

	public static boolean isNotEmpty(Collection<?> collection) {
		return !isEmpty(collection);
	}

	public static <T> T getLast(Enumeration<T> en) {
		return getLast(en, null);
	}

	public static <T> T getLast(Enumeration<T> en, T defaultValue) {
		if (en == null || !en.hasMoreElements()) {
			return defaultValue;
		}
		T last;
		while (true) {
			last = en.nextElement();
			if (!en.hasMoreElements()) {
				return last;
			}
		}
	}

	public static <T> T getLast(Collection<T> c) {
		return getLast(c, null);
	}

	public static <T> T getLast(Collection<T> c, T defaultValue) {
		if (c == null) {
			return defaultValue;
		}
		return getLast(c.iterator());
	}

	public static <T> T getLast(Iterator<T> it) {
		return getLast(it, null);
	}

	public static <T> T getLast(Iterator<T> it, T defaultValue) {
		if (it == null || !it.hasNext()) {
			return defaultValue;
		}
		T last;
		while (true) {
			last = it.next();
			if (!it.hasNext()) {
				return last;
			}
		}
	}

	public static <T> T getFirst(Enumeration<T> en) {
		return getFirst(en, null);
	}

	public static <T> T getFirst(Enumeration<T> en, T defaultValue) {
		if (en != null && en.hasMoreElements()) {
			return en.nextElement();
		}
		return defaultValue;
	}

	public static <T> T getFirst(Iterator<T> it) {
		return getFirst(it, null);
	}

	public static <T> T getFirst(Iterator<T> it, T defaultValue) {
		if (it != null && it.hasNext()) {
			return it.next();
		}
		return defaultValue;
	}

	public static <T> T getFirst(Collection<T> c) {
		return getFirst(c, null);
	}

	public static <T> T getFirst(Collection<T> c, T defaultValue) {
		if (c == null) {
			return defaultValue;
		}
		return getFirst(c.iterator());
	}

	public static <T> Enumeration<T> enumeration(final T[] array) {
		return new Enumeration<T>() {
			int index = 0;

			public boolean hasMoreElements() {
				return index < array.length;
			}

			public T nextElement() {
				return array[index++];
			}
		};
	}

	public static <T> Enumeration<T> enumeration(final Iterator<T> it) {
		return new Enumeration<T>() {
			public boolean hasMoreElements() {
				return it.hasNext();
			}

			public T nextElement() {
				return it.next();
			}
		};
	}

	public static <E> Iterator<E> emptyIterator() {
		return new Iterator<E>() {

			public boolean hasNext() {
				return false;
			}

			public E next() {
				throw new UnsupportedOperationException("next");
			}

			public void remove() {
				throw new UnsupportedOperationException("remove");
			}
		};
	}

	public static <E> Enumeration<E> emptyEnumeration() {
		return new Enumeration<E>() {

			public boolean hasMoreElements() {
				return false;
			}

			public E nextElement() {
				throw new UnsupportedOperationException("nextElement");
			}

		};
	}

	public static <T> Iterator<T> iterator(final T[] array) {
		Assert.isNull(array, "Null array.");
		return new Iterator<T>() {
			int index = 0;

			public boolean hasNext() {
				return index < array.length;
			}

			public T next() {
				return array[index++];
			}

			public void remove() {
				throw new UnsupportedOperationException("remove");
			}
		};
	}

	public static <T> Iterator<T> iterator(final Enumeration<T> en) {
		Assert.isNull(en, "Null enumeration.");
		return new Iterator<T>() {
			public boolean hasNext() {
				return en.hasMoreElements();
			}

			public T next() {
				return en.nextElement();
			}

			public void remove() {
				throw new UnsupportedOperationException("remove");
			}
		};
	}

	public static <T> Iterable<T> forEach(final Iterator<T> delegate) {
		Assert.isNull(delegate, "Null iterator.");
		return new Iterable<T>() {
			public Iterator<T> iterator() {
				return delegate;
			}
		};
	}

	public static <T> List<T> toList(Iterator<T> delegate) {
		Assert.isNull(delegate, "Null iterator.");
		List<T> list = new ArrayList<T>();
		while (delegate.hasNext()) {
			list.add(delegate.next());
		}
		return list;
	}

	public static <T> List<T> toList(Iterable<T> iterable) {
		List<T> list = new ArrayList<T>();
		for (T t : iterable) {
			list.add(t);
		}
		return list;
	}

	public static <T> Iterable<T> forEach(Enumeration<T> delegate) {
		return forEach(iterator(delegate));
	}

	public static <T> List<T> toList(Enumeration<T> delegate) {
		Assert.isNull(delegate, "Null enumeration.");
		List<T> list = new ArrayList<T>();
		while (delegate.hasMoreElements()) {
			list.add(delegate.nextElement());
		}
		return list;
	}

	public static <E, T> Iterator<T> iterator(final Enumeration<E> delegate, final Getter<E, T> getter) {
		return new Iterator<T>() {

			public boolean hasNext() {
				return delegate.hasMoreElements();
			}

			public T next() {
				return getter.apply(delegate.nextElement());
			}

			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}

	public static <E, T> Iterator<T> iterator(final Iterator<E> delegate, final Getter<E, T> getter) {
		return new Iterator<T>() {

			public boolean hasNext() {
				return delegate.hasNext();
			}

			public T next() {
				return getter.apply(delegate.next());
			}

			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}

	public static String toString(Iterable<?> c) {
		return "[" + join(c) + "]";
	}

	public static String toString(Iterator<?> it) {
		return "[" + join(it) + "]";
	}

	public static String toString(Enumeration<?> e) {
		return "[" + join(e) + "]";
	}

	public static String join(Iterable<?> c) {
		return join(c, ",");
	}

	public static String join(Iterable<?> c, String delimiter) {
		if (c == null) {
			return "";
		}
		return join(c.iterator(), delimiter);
	}

	public static String join(Iterable<?> left, Iterable<?> right, String delimiter) {
		return join(left, right, delimiter, delimiter);
	}

	public static String join(Iterable<?> left, Iterable<?> right, String conjunction, String delimiter) {
		if (left == null || right == null) {
			return "";
		}
		return join(left.iterator(), right.iterator(), conjunction, delimiter);
	}

	public static String join(Iterator<?> it) {
		return join(it, ",");
	}

	public static String join(Iterator<?> it, String delimiter) {
		if (it == null || !it.hasNext()) {
			return "";
		}
		if (delimiter == null) {
			delimiter = "";
		}
		StringBuilder content = new StringBuilder();
		Object o;
		while (true) {
			o = it.next();
			content.append(ObjectUtils.toString(o));
			if (it.hasNext()) {
				content.append(delimiter);
			} else {
				break;
			}
		}
		return content.toString();
	}

	public static String join(Iterator<?> it, String delimiter, String pattern) {
		if (it == null || !it.hasNext()) {
			return "";
		}
		if (delimiter == null) {
			delimiter = "";
		}
		StringBuilder content = new StringBuilder();
		Object o;
		String s;
		while (true) {
			o = it.next();
			s = StringUtils.parseText(pattern, "{}", new Object[] { ObjectUtils.toString(o) });
			content.append(s);
			if (it.hasNext()) {
				content.append(delimiter);
			} else {
				break;
			}
		}
		return content.toString();
	}

	public static String join(Iterator<?> left, Iterator<?> right, String delimiter) {
		return join(left, right, delimiter, delimiter);
	}

	public static String join(Iterator<?> left, Iterator<?> right, String conjunction, String delimiter) {
		if (left == null || !left.hasNext() || right == null || !right.hasNext()) {
			return "";
		}
		if (conjunction == null) {
			conjunction = "";
		}
		if (delimiter == null) {
			delimiter = "";
		}
		StringBuilder content = new StringBuilder();
		while (true) {
			content.append(ObjectUtils.toString(left.next())).append(conjunction)
					.append(ObjectUtils.toString(right.next()));
			if (left.hasNext() && right.hasNext()) {
				content.append(delimiter);
			} else {
				break;
			}
		}
		return content.toString();
	}

	public static String join(Enumeration<?> e) {
		return join(e, ",");
	}

	public static String join(Enumeration<?> e, String delimiter) {
		if (e == null || !e.hasMoreElements()) {
			return "";
		}
		if (delimiter == null) {
			delimiter = "";
		}
		StringBuilder content = new StringBuilder();
		Object o;
		while (true) {
			o = e.nextElement();
			content.append(ObjectUtils.toString(o));
			if (e.hasMoreElements()) {
				content.append(delimiter);
			} else {
				break;
			}
		}
		return content.toString();
	}

	public static String join(Enumeration<?> left, Enumeration<?> right, String delimiter) {
		return join(left, right, delimiter, delimiter);
	}

	public static String join(Enumeration<?> left, Enumeration<?> right, String conjunction, String delimiter) {
		if (left == null || !left.hasMoreElements() || right == null || !right.hasMoreElements()) {
			return "";
		}
		if (conjunction == null) {
			conjunction = "";
		}
		if (delimiter == null) {
			delimiter = "";
		}
		StringBuilder content = new StringBuilder();
		while (true) {
			content.append(ObjectUtils.toString(left.nextElement())).append(conjunction)
					.append(ObjectUtils.toString(right.nextElement()));
			if (left.hasMoreElements() && right.hasMoreElements()) {
				content.append(delimiter);
			} else {
				break;
			}
		}
		return content.toString();
	}

	public static boolean isCollection(Object obj) {
		return obj == null ? false : obj instanceof Collection;
	}

	public static boolean isNotCollection(Object obj) {
		return !isCollection(obj);
	}

	public static boolean isIterator(Object obj) {
		return obj == null ? false : obj instanceof Iterator;
	}

	public static boolean isNotIterator(Object obj) {
		return !isIterator(obj);
	}

	public static boolean isEnumeration(Object obj) {
		return obj == null ? false : obj instanceof Enumeration;
	}

	public static boolean isNotEnumeration(Object obj) {
		return !isEnumeration(obj);
	}

	public static boolean isQueue(Object obj) {
		return obj == null ? false : obj instanceof Queue;
	}

	public static boolean isNotQueue(Object obj) {
		return !isQueue(obj);
	}
	
	public static void main(String[] args) {
		List<String> a = new ArrayList<String>();
		List<String> b = new ArrayList<String>();
		
		a.add("1");
		a.add("2");
		
		//b.add("2");
		b.add("1");
		
		System.out.println(deepEquals(a, b));
	}

	public static boolean deepEquals(Iterable<?> left, Iterable<?> right) {
		if (left == right) {
			return true;
		}
		if (left == null || right == null) {
			return false;
		}
		if (left.getClass() != right.getClass()) {
			return false;
		}
		return deepEquals(left.iterator(), right.iterator());
	}

	public static boolean deepEquals(Iterator<?> left, Iterator<?> right) {
		if (left == right) {
			return true;
		}
		if (left == null || right == null) {
			return false;
		}
		if (left.getClass() != right.getClass()) {
			return false;
		}
		Object o1, o2;
		for (; left.hasNext() && right.hasNext();) {
			o1 = left.next();
			o2 = right.next();
			if (ObjectUtils.notEquals(o1, o2)) {
				return false;
			}
		}
		return !(left.hasNext() || right.hasNext());
	}

	public static boolean deepEquals(Enumeration<?> left, Enumeration<?> right) {
		if (left == right) {
			return true;
		}
		if (left == null) {
			return right == null;
		} else if (right == null) {
			return false;
		}
		if (left.getClass() != right.getClass()) {
			return false;
		}
		Object o1, o2;
		for (; left.hasMoreElements() && right.hasMoreElements();) {
			o1 = left.nextElement();
			o2 = right.nextElement();
			if (ObjectUtils.notEquals(o1, o2)) {
				return false;
			}
		}
		return !(left.hasMoreElements() || right.hasMoreElements());
	}

	public static int deepHashCode(Iterable<?> it) {
		Assert.isNull(it, "Source collection must not be null.");
		return deepHashCode(it.iterator());
	}

	public static int deepHashCode(Iterator<?> it) {
		Assert.isNull(it, "Source iterator must not be null.");
		Object o;
		int hash = 0;
		while (it.hasNext()) {
			o = it.next();
			if (o != null) {
				hash += ObjectUtils.hashCode(o);
			}
		}
		return hash;
	}

	public static int deepHashCode(Enumeration<?> en) {
		Assert.isNull(en, "Source enumeration must not be null.");
		Object o;
		int hash = 0;
		while (en.hasMoreElements()) {
			o = en.nextElement();
			if (o != null) {
				hash += ObjectUtils.hashCode(o);
			}
		}
		return hash;
	}

	public static boolean containsIgnoreCase(Collection<String> c, String s) {
		if (c != null) {
			for (String a : c) {
				if (StringUtils.equalsIgnoreCase(a, s)) {
					return true;
				}
			}
		}
		return false;
	}

	public static <T> Collection<T> minus(Collection<T> left, Collection<T> right) {
		if (left != null && right == null) {
			return left;
		} else if (left == null && right != null) {
			return right;
		} else if (left != null && right != null) {
			List<T> results = new ArrayList<T>();
			for (T t : left) {
				if (!right.contains(t)) {
					results.add(t);
				}
			}
			return results;
		}
		return null;
	}

	public static <T> Collection<T> intersect(Collection<T> left, Collection<T> right) {
		if (left == null || right == null) {
			return null;
		}
		List<T> results = new ArrayList<T>();
		for (T t : left) {
			if (right.contains(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static <T> Collection<T> union(Collection<T> left, Collection<T> right) {
		if (left != null && right == null) {
			return left;
		} else if (left == null && right != null) {
			return right;
		} else if (left != null && right != null) {
			List<T> list = new ArrayList<T>();
			for (T t : left) {
				if (!list.contains(t)) {
					list.add(t);
				}
			}
			for (T t : right) {
				if (!list.contains(t)) {
					list.add(t);
				}
			}
			return list;
		}
		return null;
	}

	public static <T> Collection<T> unionAll(Collection<T> left, Collection<T> right) {
		if (left != null && right == null) {
			return left;
		} else if (left == null && right != null) {
			return right;
		} else if (left != null && right != null) {
			List<T> results = new ArrayList<T>();
			results.addAll(left);
			results.addAll(right);
			return results;
		}
		return null;
	}

	public static <T> void addAll(Collection<T> c, T[] elements) {
		for (int i = 0, size = elements.length; i < size; i++) {
			c.add(elements[i]);
		}
	}

	public static <T> void addAll(Collection<T> c, Iterator<T> it) {
		while (it.hasNext()) {
			c.add(it.next());
		}
	}

	public static <T> void addAll(Collection<T> c, Enumeration<T> en) {
		while (en.hasMoreElements()) {
			c.add(en.nextElement());
		}
	}

	public static List<String> formats(Collection<?> src, String format) {
		Assert.isNull(src, "List must not be null.");
		return formats(src.iterator(), format);
	}

	public static List<String> formats(Enumeration<?> src, String format) {
		Assert.isNull(src, "Enumeration must not be null.");
		List<String> list = new ArrayList<String>();
		while (src.hasMoreElements()) {
			list.add(String.format(format, src.nextElement()));
		}
		return list;
	}

	public static List<String> formats(Iterator<?> src, String format) {
		Assert.isNull(src, "Iterator must not be null.");
		List<String> list = new ArrayList<String>();
		for (; src.hasNext();) {
			list.add(String.format(format, src.next()));
		}
		return list;
	}

	public static <T> Iterator<T> toSequentialIterator(Collection<Iterator<T>> c) {
		return new SequentialIterator<T>(c);
	}

	public static <T> Iterator<T> toSequentialEnumeration(Collection<Enumeration<T>> c) {
		return new SequentialEnumeration<T>(c);
	}

	public static class SequentialEnumeration<T> implements Iterator<T> {

		private final Iterator<Enumeration<T>> iterator;

		SequentialEnumeration(Collection<Enumeration<T>> c) {
			if (c == null) {
				throw new NullPointerException("Null collection");
			}
			iterator = c.iterator();
			item = iterator.hasNext() ? iterator.next() : null;
		}

		private Enumeration<T> item;

		public boolean hasNext() {
			return item != null && item.hasMoreElements();
		}

		public T next() {
			T t = item.nextElement();
			if (!item.hasMoreElements()) {
				item = iterator.hasNext() ? iterator.next() : null;
			}
			return t;
		}

	}

	public static class SequentialIterator<T> implements Iterator<T> {

		private final Iterator<Iterator<T>> iterator;

		SequentialIterator(Collection<Iterator<T>> c) {
			if (c == null) {
				throw new NullPointerException("Null collection");
			}
			iterator = c.iterator();
			item = iterator.hasNext() ? iterator.next() : null;
		}

		private Iterator<T> item;

		public boolean hasNext() {
			return item != null && item.hasNext();
		}

		public T next() {
			T t = item.next();
			if (!item.hasNext()) {
				item = iterator.hasNext() ? iterator.next() : null;
			}
			return t;
		}

	}

}
