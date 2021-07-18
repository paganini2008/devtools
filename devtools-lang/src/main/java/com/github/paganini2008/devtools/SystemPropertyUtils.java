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
 * @author Fred Feng
 * @version 1.0
 */
public abstract class SystemPropertyUtils {

	public static Boolean getBoolean(String key) {
		return getBoolean(key, null);
	}

	public static Boolean getBoolean(String key, Boolean defaultValue) {
		return Booleans.valueOf(getString(key), defaultValue);
	}

	public static Float getFloat(String key) {
		return getFloat(key, null);
	}

	public static Float getFloat(String key, Float defaultValue) {
		return Floats.valueOf(getString(key), defaultValue);
	}

	public static Double getDouble(String key) {
		return getDouble(key, null);
	}

	public static Double getDouble(String key, Double defaultValue) {
		return Doubles.valueOf(getString(key), defaultValue);
	}

	public static Byte getByte(String key) {
		return getByte(key, null);
	}

	public static Byte getByte(String key, Byte defaultValue) {
		return Bytes.valueOf(getString(key), defaultValue);
	}

	public static Short getShort(String key) {
		return getShort(key, null);
	}

	public static Short getShort(String key, Short defaultValue) {
		return Shorts.valueOf(getString(key), defaultValue);
	}

	public static Integer getInteger(String key) {
		return getInteger(key, null);
	}

	public static Integer getInteger(String key, Integer defaultValue) {
		return Ints.valueOf(getString(key), defaultValue);
	}

	public static Long getLong(String key) {
		return getLong(key, null);
	}

	public static Long getLong(String key, Long defaultValue) {
		return Longs.valueOf(getString(key), defaultValue);
	}

	public static Character getCharacter(String key) {
		return getCharacter(key, null);
	}

	public static Character getCharacter(String key, Character defaultValue) {
		try {
			return getString(key).charAt(0);
		} catch (RuntimeException e) {
			return defaultValue;
		}
	}

	public static String getString(String key) {
		return getString(key, null);
	}

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
