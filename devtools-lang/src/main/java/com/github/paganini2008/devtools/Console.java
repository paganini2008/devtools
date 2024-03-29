/**
* Copyright 2017-2022 Fred Feng (paganini.fy@gmail.com)

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

import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;

/**
 * 
 * Console
 * 
 * @author Fred Feng
 *
 * @since 2.0.1
 */
public abstract class Console {

	public static void log(String msg) {
		if (StringUtils.isNotBlank(msg)) {
			System.out.println(msg);
		}
	}

	public static void err(String msg) {
		if (StringUtils.isNotBlank(msg)) {
			System.err.println(msg);
		}
	}

	public static void logf(String msg, Object... args) {
		if (StringUtils.isNotBlank(msg)) {
			System.out.printf(msg, args);
			System.out.println();
		}
	}

	public static void errf(String msg, Object... args) {
		if (StringUtils.isNotBlank(msg)) {
			System.err.printf(msg, args);
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

	public static <T> void err(Enumeration<T> en) {
		if (en != null) {
			while (en.hasMoreElements()) {
				System.err.println(en.nextElement());
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

	public static <T> void err(Iterator<T> it) {
		if (it != null) {
			while (it.hasNext()) {
				System.err.println(it.next());
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

	public static <T> void err(Iterable<T> iterable) {
		if (iterable != null) {
			for (T t : iterable) {
				System.err.println(t);
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

	public static <T> void err(T[] array) {
		if (array != null) {
			for (T t : array) {
				System.err.println(t);
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

	public static <K, V> void err(Map<K, V> map) {
		if (map != null) {
			for (Map.Entry<K, V> en : map.entrySet()) {
				System.err.println(en);
			}
		}
	}

}
