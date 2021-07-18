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
package com.github.paganini2008.devtools;

/**
 * 
 * Assert
 *
 * @author Fred Feng
 * @version 1.0
 */
public abstract class Assert {

	public static void hasNoLength(CharSequence text) {
		hasNoLength(text, "The string type parameter must not be null or empty.");
	}

	public static void hasNoLength(CharSequence text, String msg, Object... args) {
		isTrue(StringUtils.isEmpty(text), msg, args);
	}

	public static void hasNoText(CharSequence text) {
		hasNoText(text, "The string type parameter must not be null or empty.");
	}

	public static void hasNoText(CharSequence text, String msg, Object... args) {
		isTrue(StringUtils.isBlank(text), msg, args);
	}

	public static void isNull(Object arg) {
		isNull(arg, "The argument must not be null.");
	}

	public static void isNull(Object arg, String msg, Object... args) {
		isTrue(arg == null, msg, args);
	}

	public static void isNull(Object arg, RuntimeException e) {
		isTrue(arg == null, e);
	}

	public static void isNotArray(Object arg) {
		isNotArray(arg, arg != null ? "The argument type of '" + arg.getClass() + "' is not array." : "Null Object");
	}

	public static void isNotArray(Object arg, String msg, Object... args) {
		isTrue(ObjectUtils.isNotArray(arg), msg, args);
	}

	public static <T extends Comparable<T>> void lt(T a, T b, String msg, Object... args) {
		isTrue(a.compareTo(b) < 0, msg, args);
	}

	public static <T extends Comparable<T>> void lte(T a, T b, String msg, Object... args) {
		isTrue(a.compareTo(b) <= 0, msg, args);
	}

	public static <T extends Comparable<T>> void gt(T a, T b, String msg, Object... args) {
		isTrue(a.compareTo(b) > 0, msg, args);
	}

	public static <T extends Comparable<T>> void gte(T a, T b, String msg, Object... args) {
		isTrue(a.compareTo(b) >= 0, msg, args);
	}

	public static <T extends Comparable<T>> void ne(T a, T b, String msg, Object... args) {
		isTrue(a.compareTo(b) != 0, msg, args);
	}

	public static <T extends Comparable<T>> void eq(T a, T b, String msg, Object... args) {
		isTrue(a.compareTo(b) == 0, msg, args);
	}

	public static void isTrue(boolean result, String msg, Object... args) {
		isTrue(result, new IllegalArgumentException(args != null && args.length > 0 ? String.format(msg, args) : msg));
	}

	public static void isFalse(boolean result, String msg, Object... args) {
		isFalse(result, new IllegalArgumentException(args != null && args.length > 0 ? String.format(msg, args) : msg));
	}

	public static <T extends RuntimeException> void isTrue(boolean result, T e) {
		if (result == true) {
			throw e;
		}
	}

	public static <T extends RuntimeException> void isFalse(boolean result, T e) {
		if (result == false) {
			throw e;
		}
	}

	public static <T extends Exception> void isTrue(boolean result, T e) throws T {
		if (result == true) {
			throw e;
		}
	}

	public static <T extends Exception> void isFalse(boolean result, T e) throws T {
		if (result == false) {
			throw e;
		}
	}

	public static <T extends Error> void isTrue(boolean result, T e) throws T {
		if (result == true) {
			throw e;
		}
	}

	public static <T extends Error> void isFalse(boolean result, T e) throws T {
		if (result == false) {
			throw e;
		}
	}

}
