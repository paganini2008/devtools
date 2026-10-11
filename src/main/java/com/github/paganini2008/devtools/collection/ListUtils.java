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
 * <p>
 * Null-safe utility methods for lists.
 * 
 * @author Fred Feng
 * @date 2019-05-29
 * @since 2.0.1
 */
@SuppressWarnings("all")
public abstract class ListUtils {

	/**
	 * Returns the immutable empty list.
	 */
	public static <E> List<E> emptyList() {
		return Collections.EMPTY_LIST;
	}

	/**
	 * Checks if the object is a List (false for null).
	 */
	public static boolean isList(Object obj) {
		return obj == null ? false : obj instanceof List;
	}

	public static boolean isNotList(Object obj) {
		return !isList(obj);
	}

	/**
	 * Checks if a list is null or empty.
	 *
	 * <pre>
	 * ListUtils.isEmpty(null)  = true
	 * ListUtils.isEmpty([])    = true
	 * ListUtils.isEmpty(["a"]) = false
	 * </pre>
	 */
	public static boolean isEmpty(List<?> list) {
		return !isNotEmpty(list);
	}

	/**
	 * Checks if a list is neither null nor empty.
	 */
	public static boolean isNotEmpty(List<?> list) {
		return list != null && list.size() > 0;
	}

	/**
	 * Returns an unmodifiable list of the given elements.
	 */
	public static <T> List<T> unmodifiableList(T... args) {
		return Collections.unmodifiableList(Arrays.asList(args));
	}

	/**
	 * Returns an unmodifiable view of the collection as a list (empty for null).
	 */
	public static <T> List<T> unmodifiableList(Collection<T> c) {
		return Collections.unmodifiableList(toList(c));
	}

	/**
	 * Returns a new synchronized ArrayList.
	 */
	public static <T> List<T> synchronizedList() {
		return Collections.synchronizedList(new ArrayList<T>());
	}

	/**
	 * Returns a synchronized view of the collection as a list (empty for null).
	 */
	public static <T> List<T> synchronizedList(Collection<T> c) {
		return Collections.synchronizedList(toList(c));
	}

	/**
	 * Returns a new mutable ArrayList holding the given elements.
	 */
	public static <T> List<T> create(T... args) {
		return ArrayUtils.isNotEmpty(args) ? new ArrayList<T>(Arrays.asList(args)) : new ArrayList<T>();
	}

	/**
	 * Returns the collection itself if it is a List, otherwise a new ArrayList copied from it (empty for null).
	 */
	public static <T> List<T> toList(Collection<T> c) {
		if (c == null) {
			return new ArrayList<T>();
		}
		return c instanceof List ? (List<T>) c : new ArrayList<T>(c);
	}

	private static int indexFor(List<?> list, int index) {
		int size = list.size();
		if (index > size - 1) {
			return -1;
		}
		if (index < 0) {
			index = size + index;
		}
		return index;
	}

	/**
	 * Returns the element at the index; a negative index counts from the end. Returns
	 * null if the list is null or the index is out of range.
	 *
	 * <pre>
	 * ListUtils.get(["a", "b", "c"], 0)  = "a"
	 * ListUtils.get(["a", "b", "c"], -1) = "c"
	 * ListUtils.get(["a", "b", "c"], -4) = null
	 * ListUtils.get(["a", "b", "c"], 3)  = null
	 * </pre>
	 */
	public static <T> T get(List<T> list, int index) {
		return get(list, index, null);
	}

	/**
	 * Returns the element at the index (negative counts from the end), or the default value if the list is null or the index is out of range.
	 */
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

	/**
	 * Returns the first element, or null if the list is null or empty.
	 */
	public static <T> T getFirst(List<T> list) {
		return getFirst(list, null);
	}

	/**
	 * Returns the first element, or the default value if the list is null or empty.
	 */
	public static <T> T getFirst(List<T> list, T defaultValue) {
		if (isEmpty(list)) {
			return defaultValue;
		}
		return list.get(0);
	}

	/**
	 * Removes the element at the index (negative counts from the end).
	 *
	 * <pre>
	 * ListUtils.remove(["a", "b", "a", "b"], 2) = "a"   (list becomes ["a", "b", "b"])
	 * ListUtils.remove(["a"], 5)                = null
	 * ListUtils.remove(null, 0)                 = null
	 * </pre>
	 *
	 * @return the removed element, or null if the list is null or the index is out of range
	 */
	public static <T> T remove(List<T> list, int index) {
		if (isEmpty(list)) {
			return null;
		}
		index = indexFor(list, index);
		if (index < 0) {
			return null;
		}
		return list.remove(index);
	}

	/**
	 * Removes and returns the first element, or returns null if the list is null or empty.
	 */
	public static <T> T removeFirst(List<T> list) {
		if (isEmpty(list)) {
			return null;
		}
		return list.remove(0);
	}

	/**
	 * Returns the last element, or null if the list is null or empty.
	 */
	public static <T> T getLast(List<T> list) {
		return getLast(list, null);
	}

	/**
	 * Returns the last element, or the default value if the list is null or empty.
	 */
	public static <T> T getLast(List<T> list, T defaultValue) {
		if (isEmpty(list)) {
			return defaultValue;
		}
		return list.get(list.size() - 1);
	}

	/**
	 * Removes and returns the last element, or returns null if the list is null or empty.
	 */
	public static <T> T removeLast(List<T> list) {
		if (isEmpty(list)) {
			return null;
		}
		return list.remove(list.size() - 1);
	}

	/**
	 * Copies at most the first <code>count</code> elements into a new list.
	 */
	public static <T> List<T> copy(List<T> src, int count) {
		return new ArrayList<T>(src.subList(0, Math.min(count, src.size())));
	}

	/**
	 * Copies the elements from <code>startIndex</code> (inclusive) to
	 * <code>count</code> (exclusive) into a new list. Note that the third argument
	 * is used as the end index.
	 *
	 * <pre>
	 * ListUtils.copy(["a", "b", "c", "d"], 1, 3) = ["b", "c"]
	 * </pre>
	 */
	public static <T> List<T> copy(List<T> src, int startIndex, int count) {
		return new ArrayList<T>(src.subList(startIndex, count));
	}

	/**
	 * Overwrites at most <code>count</code> elements of dest starting at
	 * <code>destFrom</code> with the elements of src starting at
	 * <code>srcFrom</code>, stopping at the end of either list.
	 *
	 * <pre>
	 * ListUtils.copy(["a", "b", "c", "d"], 1, ["1", "2", "3", "4"], 0, 2) =&gt; dest = ["b", "c", "3", "4"]
	 * </pre>
	 *
	 * @throws IllegalArgumentException if either list is null
	 */
	public static <T> void copy(List<T> src, int srcFrom, List<T> dest, int destFrom, int count) {
		Assert.isNull(src, "Source list must not be null.");
		Assert.isNull(dest, "Destination list must not be null.");
		for (int i = srcFrom, j = destFrom, n = 0; n < count && i < src.size() && j < dest.size(); i++, j++, n++) {
			dest.set(j, src.get(i));
		}
	}

	/**
	 * Returns a read-only view of the first <code>limit</code> elements (-1 for all).
	 */
	public static <T> List<T> slice(List<T> list, int limit) {
		return slice(list, limit, 0);
	}

	/**
	 * Returns a read-only view of at most <code>limit</code> elements (-1 for all)
	 * starting at <code>offset</code>, or an empty list if the offset is beyond the
	 * end.
	 *
	 * <pre>
	 * ListUtils.slice(["a", "b", "c", "d"], 5, 2)  = ["c", "d"]
	 * ListUtils.slice(["a", "b", "c", "d"], -1, 1) = ["b", "c", "d"]
	 * ListUtils.slice(["a", "b", "c", "d"], 2, 4)  = []
	 * ListUtils.slice(["a", "b", "c", "d"], 0, 0)  = []
	 * </pre>
	 *
	 * @throws IllegalArgumentException if the list is null, the limit is less than
	 *             -1, or the offset is negative
	 */
	public static <T> List<T> slice(List<T> list, int limit, int offset) {
		Assert.isNull(list, "List must not be null.");
		int length = list.size();
		if (offset >= 0 && (limit == 0 || (offset >= length && (limit > 0 || limit == -1)))) {
			return Collections.emptyList();
		}
		if (limit > 0 && offset >= 0) {
			return Collections.unmodifiableList(list.subList(offset, (int) Math.min((long) offset + limit, length)));
		} else if (limit == -1 && offset >= 0) {
			return Collections.unmodifiableList(list.subList(offset, length));
		}
		throw new IllegalArgumentException("limit=" + limit + ", offset=" + offset);
	}

	/**
	 * Sorts the list in natural ascending order.
	 *
	 * @throws IllegalArgumentException if the list is null
	 */
	public static <T extends Comparable<T>> void asc(List<T> list) {
		Assert.isNull(list, "List must not be null.");
		if (list.size() > 0) {
			Collections.sort(list);
		}
	}

	/**
	 * Sorts the list in natural descending order.
	 *
	 * @throws IllegalArgumentException if the list is null
	 */
	public static <T extends Comparable<T>> void desc(List<T> list) {
		Assert.isNull(list, "List must not be null.");
		if (list.size() > 0) {
			Collections.sort(list, Collections.reverseOrder());
		}
	}

	/**
	 * Sorts the list with the comparator.
	 *
	 * @throws IllegalArgumentException if the list is null
	 */
	public static <T> void sort(List<T> list, Comparator<T> c) {
		Assert.isNull(list, "List must not be null.");
		if (list.size() > 0) {
			Collections.sort(list, c);
		}
	}

	/**
	 * Copies the remaining elements of the iterator into a new list.
	 *
	 * @throws IllegalArgumentException if the iterator is null
	 */
	public static <T> List<T> list(Iterator<T> it) {
		Assert.isNull(it, "Iterator must not be null.");
		List<T> list = new ArrayList<T>();
		while (it.hasNext()) {
			list.add(it.next());
		}
		return list;
	}

	/**
	 * Copies the remaining elements of the enumeration into a new list.
	 *
	 * @throws IllegalArgumentException if the enumeration is null
	 */
	public static <T> List<T> list(Enumeration<T> en) {
		Assert.isNull(en, "Enumeration must not be null.");
		List<T> list = new ArrayList<T>();
		while (en.hasMoreElements()) {
			list.add(en.nextElement());
		}
		return list;
	}

	/**
	 * Copies the remaining elements of the iterator into a new list in reverse order.
	 *
	 * @throws IllegalArgumentException if the iterator is null
	 */
	public static <T> List<T> reverse(Iterator<T> it) {
		Assert.isNull(it, "Iterator must not be null.");
		List<T> list = list(it);
		reverse(list);
		return list;
	}

	/**
	 * Copies the remaining elements of the enumeration into a new list in reverse order.
	 *
	 * @throws IllegalArgumentException if the enumeration is null
	 */
	public static <T> List<T> reverse(Enumeration<T> en) {
		Assert.isNull(en, "Enumeration must not be null.");
		List<T> list = list(en);
		reverse(list);
		return list;
	}

	/**
	 * Reverses the list in place, doing nothing for null.
	 */
	public static <T> void reverse(List<T> list) {
		if (list == null) {
			return;
		}
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

	/**
	 * Swaps the elements at the two positions.
	 */
	public static <T> void swap(List<T> list, int i, int j) {
		final List<T> l = list;
		l.set(i, l.set(j, l.get(i)));
	}

	/**
	 * Converts every element to its trimmed string ("" for null).
	 *
	 * <pre>
	 * ListUtils.toStringList([1, 2]) = ["1", "2"]
	 * ListUtils.toStringList([])     = null
	 * </pre>
	 *
	 * @return a new list, or null if the collection is null or empty
	 */
	public static List<String> toStringList(Collection<?> c) {
		if (c == null || c.isEmpty()) {
			return null;
		}
		return toStringList(c.iterator());
	}

	/**
	 * Converts every remaining element to its trimmed string, returning null if the iterator is null or empty.
	 */
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

	/**
	 * Converts every remaining element to its trimmed string, returning null if the enumeration is null or empty.
	 */
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
