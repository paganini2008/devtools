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
package com.github.paganini2008.devtools;

/**
 * Argument checks that throw an exception when the described condition holds.
 * By design, every method is named after the failure condition: {@code isNull(x)} throws if x <em>is</em> null,
 * {@code isTrue(c, ...)} throws if c <em>is</em> true, {@code hasNoText(s)} throws if s has no text, and so on.
 *
 * @author Fred Feng
 * @date 2019-05-29
 * @since 2.0.1
 */
public abstract class Assert {

	/**
	 * Throws IllegalArgumentException if the text is null or empty.
	 */
	public static void hasNoLength(CharSequence text) {
		hasNoLength(text, "The string parameter must not be null or empty.");
	}

	/**
	 * Throws IllegalArgumentException with the formatted message if the text is null
	 * or empty.
	 */
	public static void hasNoLength(CharSequence text, String msg, Object... args) {
		isTrue(StringUtils.isEmpty(text), msg, args);
	}

	/**
	 * Throws IllegalArgumentException if the text is null, empty or whitespace only.
	 */
	public static void hasNoText(CharSequence text) {
		hasNoText(text, "The string parameter must not be null or no length.");
	}

	/**
	 * Throws IllegalArgumentException with the formatted message if the text is
	 * blank.
	 */
	public static void hasNoText(CharSequence text, String msg, Object... args) {
		isTrue(StringUtils.isBlank(text), msg, args);
	}

	/**
	 * Throws IllegalArgumentException if the argument is null.
	 */
	public static void isNull(Object arg) {
		isNull(arg, "The argument must not be null.");
	}

	/**
	 * Throws IllegalArgumentException with the formatted message if the argument is
	 * null.
	 */
	public static void isNull(Object arg, String msg, Object... args) {
		isTrue(arg == null, msg, args);
	}

	/**
	 * Throws the given exception if the argument is null.
	 */
	public static void isNull(Object arg, RuntimeException e) {
		isTrue(arg == null, e);
	}

	/**
	 * Throws IllegalArgumentException if the argument is not an array (or null).
	 */
	public static void isNotArray(Object arg) {
		isNotArray(arg, arg != null ? "The argument type of '" + arg.getClass() + "' is not array." : "Null Object");
	}

	/**
	 * Throws IllegalArgumentException with the formatted message if the argument is
	 * not an array.
	 */
	public static void isNotArray(Object arg, String msg, Object... args) {
		isTrue(ObjectUtils.isNotArray(arg), msg, args);
	}

	/**
	 * Throws IllegalArgumentException if a &lt; b.
	 */
	public static <T extends Comparable<T>> void lt(T a, T b, String msg, Object... args) {
		isTrue(a != null && a.compareTo(b) < 0, msg, args);
	}

	/**
	 * Throws IllegalArgumentException if a &lt;= b.
	 */
	public static <T extends Comparable<T>> void lte(T a, T b, String msg, Object... args) {
		isTrue(a != null && a.compareTo(b) <= 0, msg, args);
	}

	/**
	 * Throws IllegalArgumentException if a &gt; b.
	 */
	public static <T extends Comparable<T>> void gt(T a, T b, String msg, Object... args) {
		isTrue(a != null && a.compareTo(b) > 0, msg, args);
	}

	/**
	 * Throws IllegalArgumentException if a &gt;= b.
	 */
	public static <T extends Comparable<T>> void gte(T a, T b, String msg, Object... args) {
		isTrue(a != null && a.compareTo(b) >= 0, msg, args);
	}

	/**
	 * Throws IllegalArgumentException if a is not equal to b.
	 */
	public static <T extends Comparable<T>> void ne(T a, T b, String msg, Object... args) {
		isTrue(a != null && a.compareTo(b) != 0, msg, args);
	}

	/**
	 * Throws IllegalArgumentException if a equals b.
	 */
	public static <T extends Comparable<T>> void eq(T a, T b, String msg, Object... args) {
		isTrue(a != null && a.compareTo(b) == 0, msg, args);
	}

	/**
	 * Throws IllegalArgumentException if the value is within [minValue, maxValue].
	 */
	public static <T extends Comparable<T>> void range(T value, T minValue, T maxValue, String msg, Object... args) {
		isTrue(value != null && value.compareTo(minValue) >= 0 && value.compareTo(maxValue) <= 0, msg, args);
	}

	/**
	 * Throws IllegalArgumentException if the value is outside [minValue, maxValue].
	 */
	public static <T extends Comparable<T>> void outOfRange(T value, T minValue, T maxValue, String msg, Object... args) {
		isTrue(value != null && (value.compareTo(minValue) < 0 || value.compareTo(maxValue) > 0), msg, args);
	}

	/**
	 * Throws IllegalArgumentException if the result is true. The message is
	 * formatted with {@link String#format(String, Object...)} only when arguments
	 * are given.
	 */
	public static void isTrue(boolean result, String msg, Object... args) {
		if (result) {
			throw new IllegalArgumentException(args != null && args.length > 0 ? String.format(msg, args) : msg);
		}
	}

	/**
	 * Throws IllegalArgumentException if the result is false.
	 */
	public static void isFalse(boolean result, String msg, Object... args) {
		if (!result) {
			throw new IllegalArgumentException(args != null && args.length > 0 ? String.format(msg, args) : msg);
		}
	}

	/**
	 * Throws the given runtime exception if the result is true.
	 */
	public static <T extends RuntimeException> void isTrue(boolean result, T e) {
		if (result == true) {
			throw e;
		}
	}

	/**
	 * Throws the given runtime exception if the result is false.
	 */
	public static <T extends RuntimeException> void isFalse(boolean result, T e) {
		if (result == false) {
			throw e;
		}
	}

	/**
	 * Throws the given exception if the result is true.
	 */
	public static <T extends Exception> void isTrue(boolean result, T e) throws T {
		if (result == true) {
			throw e;
		}
	}

	/**
	 * Throws the given exception if the result is false.
	 */
	public static <T extends Exception> void isFalse(boolean result, T e) throws T {
		if (result == false) {
			throw e;
		}
	}

	/**
	 * Throws the given error if the result is true.
	 */
	public static <T extends Error> void isTrue(boolean result, T e) throws T {
		if (result == true) {
			throw e;
		}
	}

	/**
	 * Throws the given error if the result is false.
	 */
	public static <T extends Error> void isFalse(boolean result, T e) throws T {
		if (result == false) {
			throw e;
		}
	}

}
