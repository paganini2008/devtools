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

/**
 * ComparatorHelper
 * 
 * @author Fred Feng
 * @version 1.0
 */
public abstract class ComparatorHelper {

	public static int valueOf(double result) {
		if (result < 0) {
			return -1;
		} else if (result > 0) {
			return 1;
		} else {
			return 0;
		}
	}

	public static int valueOf(long result) {
		if (result < 0) {
			return -1;
		} else if (result > 0) {
			return 1;
		} else {
			return 0;
		}
	}

	public static <T extends Comparable<T>> int compareTo(T a, T b) {
		if (a != null && b != null) {
			return valueOf(a.compareTo(b));
		}
		if (a != null && b == null) {
			return 1;
		}
		if (a == null && b != null) {
			return -1;
		}
		return 0;
	}

	public static int compareTo(String a, String b, boolean ignoreCase) {
		if (a != null && b != null) {
			return valueOf(ignoreCase ? a.compareToIgnoreCase(b) : a.compareTo(b));
		}
		if (a != null && b == null) {
			return 1;
		}
		if (a == null && b != null) {
			return -1;
		}
		return 0;
	}
}
