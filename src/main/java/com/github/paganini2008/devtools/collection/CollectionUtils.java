/**
* Copyright 2017-2026 Fred Feng (paganini.fy@gmail.com)

* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.github.paganini2008.devtools.collection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.function.Function;

import com.github.paganini2008.devtools.Assert;
import com.github.paganini2008.devtools.ObjectUtils;
import com.github.paganini2008.devtools.StringUtils;

/**
 * CollectionUtils
 * <p>
 * Null-safe utility methods for collections, iterators and enumerations.
 * 
 * @author Fred Feng
 * @date 2019-05-29
 * @since 2.0.1
 */
@SuppressWarnings("all")
public abstract class CollectionUtils {

	/**
	 * Returns an unmodifiable collection of the given elements.
	 */
	public static <T> Collection<T> unmodifiableCollection(T... args) {
		return unmodifiableCollection(Arrays.asList(args));
	}

	/**
	 * Returns an unmodifiable view of the collection, or an empty list if it is null.
	 */
	public static <T> Collection<T> unmodifiableCollection(Collection<T> args) {
		return args != null ? Collections.unmodifiableCollection(args) : Collections.EMPTY_LIST;
	}

	/**
	 * Checks if an enumeration is null or has no more elements.
	 */
	public static boolean isEmpty(Enumeration<?> e) {
		return !isNotEmpty(e);
	}

	/**
	 * Checks if an enumeration is not null and has more elements.
	 */
	public static boolean isNotEmpty(Enumeration<?> e) {
		return e == null ? false : e.hasMoreElements();
	}

	/**
	 * Checks if an iterator is null or has no more elements.
	 */
	public static boolean isEmpty(Iterator<?> it) {
		return !isNotEmpty(it);
	}

	/**
	 * Checks if an iterator is not null and has more elements.
	 */
	public static boolean isNotEmpty(Iterator<?> it) {
		return it == null ? false : it.hasNext();
	}

	/**
	 * Checks if a collection is null or empty.
	 *
	 * <pre>
	 * CollectionUtils.isEmpty(null)        = true
	 * CollectionUtils.isEmpty([])          = true
	 * CollectionUtils.isEmpty(["a"])       = false
	 * </pre>
	 */
	public static boolean isEmpty(Collection<?> collection) {
		return collection == null ? true : collection.isEmpty();
	}

	/**
	 * Checks if a collection is neither null nor empty.
	 */
	public static boolean isNotEmpty(Collection<?> collection) {
		return !isEmpty(collection);
	}

	/**
	 * Returns the last element of the enumeration (consuming it), or null if it is null or empty.
	 */
	public static <T> T getLast(Enumeration<T> en) {
		return getLast(en, null);
	}

	/**
	 * Returns the last element of the enumeration (consuming it), or the default value if it is null or empty.
	 */
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

	/**
	 * Returns the last element of the collection in iteration order.
	 *
	 * <pre>
	 * CollectionUtils.getLast(null)            = null
	 * CollectionUtils.getLast([])              = null
	 * CollectionUtils.getLast(["a", "b", "c"]) = "c"
	 * </pre>
	 */
	public static <T> T getLast(Collection<T> c) {
		return getLast(c, null);
	}

	/**
	 * Returns the last element of the collection, or the default value if it is null or empty.
	 *
	 * <pre>
	 * CollectionUtils.getLast(null, "x") = "x"
	 * CollectionUtils.getLast([], "x")   = "x"
	 * </pre>
	 */
	public static <T> T getLast(Collection<T> c, T defaultValue) {
		if (c == null) {
			return defaultValue;
		}
		return getLast(c.iterator(), defaultValue);
	}

	/**
	 * Returns the last element of the iterator (consuming it), or null if it is null or empty.
	 */
	public static <T> T getLast(Iterator<T> it) {
		return getLast(it, null);
	}

	/**
	 * Returns the last element of the iterator (consuming it), or the default value if it is null or empty.
	 */
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

	/**
	 * Returns the element at the index of the iterator, or null if the index is out of range.
	 */
	public static <T> T get(Iterator<T> it, int index) {
		return get(it, index, null);
	}

	/**
	 * Returns the element at the index of the iterator, or the default value if the
	 * iterator is null or the index is out of range (including negative indexes).
	 *
	 * <pre>
	 * CollectionUtils.get(["a", "b", "c"].iterator(), 1)     = "b"
	 * CollectionUtils.get(["a"].iterator(), 5, "x")          = "x"
	 * CollectionUtils.get(["a"].iterator(), -1, "x")         = "x"
	 * </pre>
	 */
	public static <T> T get(Iterator<T> it, int index, T defaultValue) {
		if (it == null || index < 0) {
			return defaultValue;
		}
		int i = 0;
		T find;
		while (it.hasNext()) {
			find = it.next();
			if (i++ == index) {
				return find;
			}
		}
		return defaultValue;
	}

	/**
	 * Returns the element at the index of the enumeration, or null if the index is out of range.
	 */
	public static <T> T get(Enumeration<T> en, int index) {
		return get(en, index, null);
	}

	/**
	 * Returns the element at the index of the enumeration, or the default value if the enumeration is null or the index is out of range.
	 */
	public static <T> T get(Enumeration<T> en, int index, T defaultValue) {
		if (en == null || index < 0) {
			return defaultValue;
		}
		int i = 0;
		T find;
		while (en.hasMoreElements()) {
			find = en.nextElement();
			if (i++ == index) {
				return find;
			}
		}
		return defaultValue;
	}

	/**
	 * Returns the next element of the enumeration, or null if it is null or empty.
	 */
	public static <T> T getFirst(Enumeration<T> en) {
		return getFirst(en, null);
	}

	/**
	 * Returns the next element of the enumeration, or the default value if it is null or empty.
	 */
	public static <T> T getFirst(Enumeration<T> en, T defaultValue) {
		if (en != null && en.hasMoreElements()) {
			return en.nextElement();
		}
		return defaultValue;
	}

	/**
	 * Returns the next element of the iterator, or null if it is null or empty.
	 */
	public static <T> T getFirst(Iterator<T> it) {
		return getFirst(it, null);
	}

	/**
	 * Returns the next element of the iterator, or the default value if it is null or empty.
	 */
	public static <T> T getFirst(Iterator<T> it, T defaultValue) {
		if (it != null && it.hasNext()) {
			return it.next();
		}
		return defaultValue;
	}

	/**
	 * Returns the first element of the collection in iteration order.
	 *
	 * <pre>
	 * CollectionUtils.getFirst(null)       = null
	 * CollectionUtils.getFirst(["a", "b"]) = "a"
	 * </pre>
	 */
	public static <T> T getFirst(Collection<T> c) {
		return getFirst(c, null);
	}

	/**
	 * Returns the first element of the collection, or the default value if it is null or empty.
	 */
	public static <T> T getFirst(Collection<T> c, T defaultValue) {
		if (c == null) {
			return defaultValue;
		}
		return getFirst(c.iterator(), defaultValue);
	}

	/**
	 * Returns an enumeration over the array.
	 */
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

	/**
	 * Adapts an iterator to an enumeration.
	 */
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

	/**
	 * Returns an iterator without elements, whose next() throws UnsupportedOperationException.
	 */
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

	/**
	 * Returns an enumeration without elements, whose nextElement() throws UnsupportedOperationException.
	 */
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

	/**
	 * Returns a read-only iterator over the array.
	 *
	 * @throws IllegalArgumentException if the array is null
	 */
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
		};
	}

	/**
	 * Adapts an enumeration to an iterator.
	 *
	 * @throws IllegalArgumentException if the enumeration is null
	 */
	public static <T> Iterator<T> iterator(final Enumeration<T> en) {
		Assert.isNull(en, "Null enumeration.");
		return new Iterator<T>() {
			public boolean hasNext() {
				return en.hasMoreElements();
			}

			public T next() {
				return en.nextElement();
			}
		};
	}

	/**
	 * Wraps an iterator into an Iterable so that it can be used in a for-each
	 * loop. The returned Iterable can be iterated only once.
	 *
	 * @throws IllegalArgumentException if the iterator is null
	 */
	public static <T> Iterable<T> forEach(final Iterator<T> delegate) {
		Assert.isNull(delegate, "Null iterator.");
		return () -> delegate;
	}

	/**
	 * Copies the remaining elements of the iterator into a new list.
	 *
	 * @throws IllegalArgumentException if the iterator is null
	 */
	public static <T> List<T> toList(Iterator<T> delegate) {
		Assert.isNull(delegate, "Null iterator.");
		List<T> list = new ArrayList<T>();
		while (delegate.hasNext()) {
			list.add(delegate.next());
		}
		return list;
	}

	/**
	 * Copies the elements into a new list, or returns an empty list if the iterable is null.
	 */
	public static <T> List<T> toList(Iterable<T> iterable) {
		List<T> list = new ArrayList<T>();
		if (iterable == null) {
			return list;
		}
		for (T t : iterable) {
			list.add(t);
		}
		return list;
	}

	/**
	 * Wraps an enumeration into an Iterable which can be iterated only once.
	 */
	public static <T> Iterable<T> forEach(Enumeration<T> delegate) {
		return forEach(iterator(delegate));
	}

	/**
	 * Copies the remaining elements of the enumeration into a new list.
	 *
	 * @throws IllegalArgumentException if the enumeration is null
	 */
	public static <T> List<T> toList(Enumeration<T> delegate) {
		Assert.isNull(delegate, "Null enumeration.");
		List<T> list = new ArrayList<T>();
		while (delegate.hasMoreElements()) {
			list.add(delegate.nextElement());
		}
		return list;
	}

	/**
	 * Returns a read-only iterator which maps every element of the enumeration with the function.
	 */
	public static <E, T> Iterator<T> iterator(final Enumeration<E> delegate, final Function<E, T> getter) {
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

	/**
	 * Returns a read-only iterator which maps every element of the iterator with the function.
	 */
	public static <E, T> Iterator<T> iterator(final Iterator<E> delegate, final Function<E, T> getter) {
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

	/**
	 * Returns the elements joined by "," and enclosed in brackets.
	 *
	 * <pre>
	 * CollectionUtils.toString(["a", "b"]) = "[a,b]"
	 * CollectionUtils.toString(null)       = "[]"
	 * </pre>
	 */
	public static String toString(Iterable<?> c) {
		return "[" + join(c) + "]";
	}

	/**
	 * Returns the remaining elements joined by "," and enclosed in brackets.
	 */
	public static String toString(Iterator<?> it) {
		return "[" + join(it) + "]";
	}

	/**
	 * Returns the remaining elements joined by "," and enclosed in brackets.
	 */
	public static String toString(Enumeration<?> e) {
		return "[" + join(e) + "]";
	}

	/**
	 * Joins the elements with ",", returning "" for null. A null element is output as "".
	 */
	public static String join(Iterable<?> c) {
		return join(c, ",");
	}

	/**
	 * Joins the elements with the delimiter (a null delimiter is treated as "").
	 *
	 * <pre>
	 * CollectionUtils.join(null, "|")         = ""
	 * CollectionUtils.join(["a", "b"], "|")   = "a|b"
	 * </pre>
	 */
	public static String join(Iterable<?> c, String delimiter) {
		if (c == null) {
			return "";
		}
		return join(c.iterator(), delimiter);
	}

	/**
	 * Joins pairs of elements of both iterables, using the delimiter both inside and between pairs.
	 */
	public static String join(Iterable<?> left, Iterable<?> right, String delimiter) {
		return join(left, right, delimiter, delimiter);
	}

	/**
	 * Joins pairs of elements of both iterables until either one is exhausted.
	 *
	 * <pre>
	 * CollectionUtils.join(["a", "b"], ["1", "2", "3"], "=", ",") = "a=1,b=2"
	 * CollectionUtils.join(null, ["1"], "=", ",")                 = ""
	 * </pre>
	 */
	public static String join(Iterable<?> left, Iterable<?> right, String conjunction, String delimiter) {
		if (left == null || right == null) {
			return "";
		}
		return join(left.iterator(), right.iterator(), conjunction, delimiter);
	}

	/**
	 * Joins the remaining elements with ",".
	 */
	public static String join(Iterator<?> it) {
		return join(it, ",");
	}

	/**
	 * Joins the remaining elements with the delimiter, returning "" for a null or empty iterator.
	 */
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

	/**
	 * Formats every element with the pattern, in which "{}" is replaced by the
	 * element, and joins the results with the delimiter.
	 *
	 * <pre>
	 * CollectionUtils.join(["a", "b"].iterator(), ",", "&lt;{}&gt;") = "&lt;a&gt;,&lt;b&gt;"
	 * </pre>
	 */
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

	/**
	 * Joins pairs of elements of both iterators, using the delimiter both inside and between pairs.
	 */
	public static String join(Iterator<?> left, Iterator<?> right, String delimiter) {
		return join(left, right, delimiter, delimiter);
	}

	/**
	 * Joins pairs of elements of both iterators until either one is exhausted, returning "" if either is null or empty.
	 */
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
			content.append(ObjectUtils.toString(left.next())).append(conjunction).append(ObjectUtils.toString(right.next()));
			if (left.hasNext() && right.hasNext()) {
				content.append(delimiter);
			} else {
				break;
			}
		}
		return content.toString();
	}

	/**
	 * Joins the remaining elements with ",".
	 */
	public static String join(Enumeration<?> e) {
		return join(e, ",");
	}

	/**
	 * Joins the remaining elements with the delimiter, returning "" for a null or empty enumeration.
	 */
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

	/**
	 * Joins pairs of elements of both enumerations, using the delimiter both inside and between pairs.
	 */
	public static String join(Enumeration<?> left, Enumeration<?> right, String delimiter) {
		return join(left, right, delimiter, delimiter);
	}

	/**
	 * Joins pairs of elements of both enumerations until either one is exhausted, returning "" if either is null or empty.
	 */
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
			content.append(ObjectUtils.toString(left.nextElement())).append(conjunction).append(ObjectUtils.toString(right.nextElement()));
			if (left.hasMoreElements() && right.hasMoreElements()) {
				content.append(delimiter);
			} else {
				break;
			}
		}
		return content.toString();
	}

	/**
	 * Checks if the object is a Collection (false for null).
	 */
	public static boolean isCollection(Object obj) {
		return obj == null ? false : obj instanceof Collection;
	}

	public static boolean isNotCollection(Object obj) {
		return !isCollection(obj);
	}

	/**
	 * Checks if the object is an Iterator (false for null).
	 */
	public static boolean isIterator(Object obj) {
		return obj == null ? false : obj instanceof Iterator;
	}

	public static boolean isNotIterator(Object obj) {
		return !isIterator(obj);
	}

	/**
	 * Checks if the object is an Enumeration (false for null).
	 */
	public static boolean isEnumeration(Object obj) {
		return obj == null ? false : obj instanceof Enumeration;
	}

	public static boolean isNotEnumeration(Object obj) {
		return !isEnumeration(obj);
	}

	/**
	 * Checks if the object is a Queue (false for null).
	 */
	public static boolean isQueue(Object obj) {
		return obj == null ? false : obj instanceof Queue;
	}

	public static boolean isNotQueue(Object obj) {
		return !isQueue(obj);
	}

	/**
	 * Compares two iterables element by element. Iterables of different classes are
	 * never equal.
	 *
	 * <pre>
	 * CollectionUtils.deepEquals(null, null)               = true
	 * CollectionUtils.deepEquals(["a"], null)              = false
	 * CollectionUtils.deepEquals(asList("a"), asList("a")) = true
	 * CollectionUtils.deepEquals(asList("a"), asList("b")) = false
	 * </pre>
	 */
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

	/**
	 * Compares the remaining elements of two iterators of the same class.
	 */
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

	/**
	 * Compares the remaining elements of two enumerations of the same class.
	 */
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

	/**
	 * Computes a hash code from the elements (null elements are skipped), or 0 for null.
	 */
	public static int deepHashCode(Iterable<?> it) {
		if (it == null) {
			return 0;
		}
		return deepHashCode(it.iterator());
	}

	/**
	 * Computes a hash code from the remaining elements (null elements are skipped), or 0 for null.
	 */
	public static int deepHashCode(Iterator<?> it) {
		if (it == null) {
			return 0;
		}
		Object o;
		int prime = 31;
		int hash = 1;
		while (it.hasNext()) {
			o = it.next();
			if (o != null) {
				hash = prime * hash + ObjectUtils.hashCode(o);
			}
		}
		return hash;
	}

	/**
	 * Computes a hash code from the remaining elements (null elements are skipped), or 0 for null.
	 */
	public static int deepHashCode(Enumeration<?> en) {
		if (en == null) {
			return 0;
		}
		Object o;
		int prime = 31;
		int hash = 1;
		while (en.hasMoreElements()) {
			o = en.nextElement();
			if (o != null) {
				hash = prime * hash + ObjectUtils.hashCode(o);
			}
		}
		return hash;
	}

	/**
	 * Checks if the collection contains the string, ignoring case.
	 *
	 * <pre>
	 * CollectionUtils.containsIgnoreCase(["a", "b"], "A") = true
	 * CollectionUtils.containsIgnoreCase(null, "A")       = false
	 * </pre>
	 */
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

	/**
	 * Returns the elements of left which are not in right. If either argument is
	 * null, the other one is returned as it is.
	 *
	 * <pre>
	 * CollectionUtils.minus(["a", "b", "c"], ["b", "d"]) = ["a", "c"]
	 * CollectionUtils.minus(["a"], null)                 = ["a"]
	 * CollectionUtils.minus(null, ["b"])                 = ["b"]
	 * CollectionUtils.minus(null, null)                  = null
	 * </pre>
	 */
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

	/**
	 * Returns the elements of left which are also in right, or null if either is null.
	 *
	 * <pre>
	 * CollectionUtils.intersect(["a", "b", "c"], ["b", "d"]) = ["b"]
	 * CollectionUtils.intersect(["a"], null)                 = null
	 * </pre>
	 */
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

	/**
	 * Returns the distinct elements of both collections. If either argument is
	 * null, the other one is returned as it is.
	 *
	 * <pre>
	 * CollectionUtils.union(["a", "b", "c"], ["b", "d"]) = ["a", "b", "c", "d"]
	 * CollectionUtils.union(null, null)                  = null
	 * </pre>
	 */
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

	/**
	 * Returns all elements of both collections, keeping duplicates. If either
	 * argument is null, the other one is returned as it is.
	 *
	 * <pre>
	 * CollectionUtils.unionAll(["a", "b", "c"], ["b", "d"]) = ["a", "b", "c", "b", "d"]
	 * </pre>
	 */
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

	/**
	 * Adds all elements of the array to the collection, doing nothing if either is null.
	 */
	public static <T> void addAll(Collection<T> c, T[] elements) {
		if (c == null || elements == null) {
			return;
		}
		for (int i = 0, size = elements.length; i < size; i++) {
			c.add(elements[i]);
		}
	}

	/**
	 * Adds the remaining elements of the iterator to the collection, doing nothing if either is null.
	 */
	public static <T> void addAll(Collection<T> c, Iterator<T> it) {
		if (c == null || it == null) {
			return;
		}
		while (it.hasNext()) {
			c.add(it.next());
		}
	}

	/**
	 * Adds the remaining elements of the enumeration to the collection, doing nothing if either is null.
	 */
	public static <T> void addAll(Collection<T> c, Enumeration<T> en) {
		if (c == null || en == null) {
			return;
		}
		while (en.hasMoreElements()) {
			c.add(en.nextElement());
		}
	}

	/**
	 * Formats every element with {@link String#format(String, Object...)}.
	 *
	 * <pre>
	 * CollectionUtils.formats([1, 2], "&lt;%d&gt;") = ["&lt;1&gt;", "&lt;2&gt;"]
	 * </pre>
	 *
	 * @throws IllegalArgumentException if the collection is null
	 */
	public static List<String> formats(Collection<?> src, String format) {
		Assert.isNull(src, "List must not be null.");
		return formats(src.iterator(), format);
	}

	/**
	 * Formats every remaining element with {@link String#format(String, Object...)}.
	 *
	 * @throws IllegalArgumentException if the enumeration is null
	 */
	public static List<String> formats(Enumeration<?> src, String format) {
		Assert.isNull(src, "Enumeration must not be null.");
		List<String> list = new ArrayList<String>();
		while (src.hasMoreElements()) {
			list.add(String.format(format, src.nextElement()));
		}
		return list;
	}

	/**
	 * Formats every remaining element with {@link String#format(String, Object...)}.
	 *
	 * @throws IllegalArgumentException if the iterator is null
	 */
	public static List<String> formats(Iterator<?> src, String format) {
		Assert.isNull(src, "Iterator must not be null.");
		List<String> list = new ArrayList<String>();
		for (; src.hasNext();) {
			list.add(String.format(format, src.next()));
		}
		return list;
	}

	/**
	 * Returns an iterator which walks through the given iterators one after another,
	 * skipping empty ones.
	 *
	 * @throws NullPointerException if the collection is null
	 */
	public static <T> Iterator<T> toSequentialIterator(Collection<Iterator<T>> c) {
		return new SequentialIterator<T>(c);
	}

	/**
	 * Returns an iterator which walks through the given enumerations one after
	 * another, skipping empty ones.
	 *
	 * @throws NullPointerException if the collection is null
	 */
	public static <T> Iterator<T> toSequentialEnumeration(Collection<Enumeration<T>> c) {
		return new SequentialEnumeration<T>(c);
	}

	/**
	 * An iterator chaining several enumerations.
	 */
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
			while (item != null && !item.hasMoreElements()) {
				item = iterator.hasNext() ? iterator.next() : null;
			}
			return item != null;
		}

		public T next() {
			if (!hasNext()) {
				throw new NoSuchElementException();
			}
			return item.nextElement();
		}

	}

	/**
	 * An iterator chaining several iterators.
	 */
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
			while (item != null && !item.hasNext()) {
				item = iterator.hasNext() ? iterator.next() : null;
			}
			return item != null;
		}

		public T next() {
			if (!hasNext()) {
				throw new NoSuchElementException();
			}
			return item.next();
		}

	}

}
