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

import com.github.paganini2008.devtools.primitives.Booleans;
import com.github.paganini2008.devtools.primitives.Bytes;
import com.github.paganini2008.devtools.primitives.Doubles;
import com.github.paganini2008.devtools.primitives.Floats;
import com.github.paganini2008.devtools.primitives.Ints;
import com.github.paganini2008.devtools.primitives.Longs;
import com.github.paganini2008.devtools.primitives.Shorts;

/**
 * SystemPropertyUtils
 * 
 * Typed access to configuration values. A value is looked up in the system properties first and
 * then in the environment variables; blank values count as absent.
 * 
 * @author Fred Feng
 * @date 2019-05-29
 * @since 2.0.1
 */
public abstract class SystemPropertyUtils {

	/**
	 * Returns the value as a Boolean, or null if absent or invalid.
	 */
	public static Boolean getBoolean(String key) {
		return getBoolean(key, null);
	}

	/**
	 * Returns the value as a Boolean, or the default value if absent or invalid.
	 */
	public static Boolean getBoolean(String key, Boolean defaultValue) {
		return Booleans.valueOf(getString(key), defaultValue);
	}

	/**
	 * Returns the value as a number, or null if absent or not parseable.
	 */
	public static Float getFloat(String key) {
		return getFloat(key, null);
	}

	/**
	 * Returns the value as a number, or the default value if absent or not parseable.
	 */
	public static Float getFloat(String key, Float defaultValue) {
		return Floats.valueOf(getString(key), defaultValue);
	}

	/**
	 * Returns the value as a number, or null if absent or not parseable.
	 */
	public static Double getDouble(String key) {
		return getDouble(key, null);
	}

	/**
	 * Returns the value as a number, or the default value if absent or not parseable.
	 */
	public static Double getDouble(String key, Double defaultValue) {
		return Doubles.valueOf(getString(key), defaultValue);
	}

	/**
	 * Returns the value as a number, or null if absent or not parseable.
	 */
	public static Byte getByte(String key) {
		return getByte(key, null);
	}

	/**
	 * Returns the value as a number, or the default value if absent or not parseable.
	 */
	public static Byte getByte(String key, Byte defaultValue) {
		return Bytes.valueOf(getString(key), defaultValue);
	}

	/**
	 * Returns the value as a number, or null if absent or not parseable.
	 */
	public static Short getShort(String key) {
		return getShort(key, null);
	}

	/**
	 * Returns the value as a number, or the default value if absent or not parseable.
	 */
	public static Short getShort(String key, Short defaultValue) {
		return Shorts.valueOf(getString(key), defaultValue);
	}

	/**
	 * Returns the value as a number, or null if absent or not parseable.
	 */
	public static Integer getInteger(String key) {
		return getInteger(key, null);
	}

	/**
	 * Returns the value as a number, or the default value if absent or not parseable.
	 */
	public static Integer getInteger(String key, Integer defaultValue) {
		return Ints.valueOf(getString(key), defaultValue);
	}

	/**
	 * Returns the value as a number, or null if absent or not parseable.
	 */
	public static Long getLong(String key) {
		return getLong(key, null);
	}

	/**
	 * Returns the value as a number, or the default value if absent or not parseable.
	 */
	public static Long getLong(String key, Long defaultValue) {
		return Longs.valueOf(getString(key), defaultValue);
	}

	/**
	 * Returns the first character of the value, or null if absent.
	 */
	public static Character getCharacter(String key) {
		return getCharacter(key, null);
	}

	/**
	 * Returns the first character of the value, or the default value if absent.
	 */
	public static Character getCharacter(String key, Character defaultValue) {
		try {
			return getString(key).charAt(0);
		} catch (RuntimeException e) {
			return defaultValue;
		}
	}

	/**
	 * Returns the value, or null if absent.
	 */
	public static String getString(String key) {
		return getString(key, null);
	}

	/**
	 * Returns the value, or the default value if absent.
	 *
	 * @param key the system property or environment variable name
	 * @param defaultValue the value returned when the key is absent or blank
	 */
	public static String getString(String key, String defaultValue) {
		String value = System.getProperty(key);
		if (StringUtils.isBlank(value)) {
			value = System.getenv(key);
		}
		if (StringUtils.isBlank(value)) {
			value = defaultValue;
		}
		return value;
	}
}
