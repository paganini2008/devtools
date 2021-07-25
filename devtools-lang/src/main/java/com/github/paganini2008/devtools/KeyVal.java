/**
* Copyright 2017-2021 Fred Feng (paganini.fy@gmail.com)

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
package com.github.paganini2008.devtools;

import java.io.Serializable;

/**
 * 
 * KeyVal
 *
 * @author Fred Feng
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
