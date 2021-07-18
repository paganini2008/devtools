/**
* Copyright 2018-2021 Fred Feng (paganini.fy@gmail.com)

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
package com.github.paganini2008.devtools.comparator;

import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;

import com.github.paganini2008.devtools.collection.MapUtils;

/**
 * MapComparator
 * 
 * @author Fred Feng
 * @version 1.0
 */
public abstract class MapComparator<K, V> implements Comparator<Map.Entry<K, V>> {

	public Map<K, V> sort(Map<K, V> map) {
		return MapUtils.sort(map, this);
	}

	public ReverseComparator<Map.Entry<K, V>> reverse() {
		return new ReverseComparator<Map.Entry<K, V>>(this);
	}

	static class ByKeyMapComparator<K extends Comparable<K>, V> extends MapComparator<K, V> {

		public int compare(Entry<K, V> a, Entry<K, V> b) {
			return ComparatorHelper.compareTo(a.getKey(), b.getKey());
		}

	}

	static class ByValueMapComparator<K, V extends Comparable<V>> extends MapComparator<K, V> {

		public int compare(Entry<K, V> a, Entry<K, V> b) {
			return ComparatorHelper.compareTo(a.getValue(), b.getValue());
		}

	}

	public static <K extends Comparable<K>, V> MapComparator<K, V> byKey() {
		return new ByKeyMapComparator<K, V>();
	}

	public static <K, V extends Comparable<V>> MapComparator<K, V> byValue() {
		return new ByValueMapComparator<K, V>();
	}

	public static void main(String[] args) {
	}

}
