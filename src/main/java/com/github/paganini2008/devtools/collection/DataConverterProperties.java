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

import java.util.Properties;

import com.github.paganini2008.devtools.MissingKeyException;
import com.github.paganini2008.devtools.primitives.Booleans;
import com.github.paganini2008.devtools.primitives.Bytes;
import com.github.paganini2008.devtools.primitives.Chars;
import com.github.paganini2008.devtools.primitives.Doubles;
import com.github.paganini2008.devtools.primitives.Floats;
import com.github.paganini2008.devtools.primitives.Ints;
import com.github.paganini2008.devtools.primitives.Longs;
import com.github.paganini2008.devtools.primitives.Shorts;

/**
 * 
 * DataConverterProperties
 * <p>
 * Properties with typed getters converting property values.
 *
 * @author Fred Feng
 * @date 2019-05-29
 * @since 2.0.1
 */
public class DataConverterProperties extends Properties {

	private static final long serialVersionUID = -891559340734908695L;

	/**
	 * Returns the property converted to a Boolean.
	 *
	 * @throws com.github.paganini2008.devtools.MissingKeyException if the property does not exist
	 */
	public Boolean getRequiredBoolean(String name) {
		assertKeyExisted(name);
		return Booleans.valueOf(getProperty(name));
	}

	/**
	 * Returns the first character of the property, throwing MissingKeyException if it does not exist.
	 */
	public Character getRequiredCharacter(String name) {
		assertKeyExisted(name);
		return Chars.valueOf(getProperty(name));
	}

	/**
	 * Returns the property converted to a Byte, throwing MissingKeyException if it does not exist.
	 */
	public Byte getRequiredByte(String name) {
		assertKeyExisted(name);
		return Bytes.valueOf(getProperty(name));
	}

	/**
	 * Returns the property converted to a Short, throwing MissingKeyException if it does not exist.
	 */
	public Short getRequiredShort(String name) {
		assertKeyExisted(name);
		return Shorts.valueOf(getProperty(name));
	}

	/**
	 * Returns the property converted to an Integer, throwing MissingKeyException if it does not exist.
	 */
	public Integer getRequiredInteger(String name) {
		assertKeyExisted(name);
		return Ints.valueOf(getProperty(name));
	}

	/**
	 * Returns the property converted to a Float, throwing MissingKeyException if it does not exist.
	 */
	public Float getRequiredFloat(String name) {
		assertKeyExisted(name);
		return Floats.valueOf(getProperty(name));
	}

	/**
	 * Returns the property converted to a Double, throwing MissingKeyException if it does not exist.
	 */
	public Double getRequiredDouble(String name) {
		assertKeyExisted(name);
		return Doubles.valueOf(getProperty(name));
	}

	/**
	 * Returns the property converted to a Long, throwing MissingKeyException if it does not exist.
	 */
	public Long getRequiredLong(String name) {
		assertKeyExisted(name);
		return Longs.valueOf(getProperty(name));
	}

	private void assertKeyExisted(String name) {
		if (!containsKey(name)) {
			throw new MissingKeyException(name);
		}
	}

	/**
	 * Returns the property converted to a Boolean, or the default value if it is absent or blank.
	 */
	public Boolean getBoolean(String name, Boolean defaultValue) {
		return Booleans.valueOf(getProperty(name), defaultValue);
	}

	/**
	 * Returns the first character of the property, or the default value if it is absent or blank.
	 */
	public Character getCharacter(String name, Character defaultValue) {
		return Chars.valueOf(getProperty(name), defaultValue);
	}

	/**
	 * Returns the property converted to a Byte, or the default value if it is absent or invalid.
	 */
	public Byte getByte(String name, Byte defaultValue) {
		return Bytes.valueOf(getProperty(name), defaultValue);
	}

	/**
	 * Returns the property converted to a Short, or the default value if it is absent or invalid.
	 */
	public Short getShort(String name, Short defaultValue) {
		return Shorts.valueOf(getProperty(name), defaultValue);
	}

	/**
	 * Returns the property converted to an Integer, or the default value if it is absent or invalid.
	 */
	public Integer getInteger(String name, Integer defaultValue) {
		return Ints.valueOf(getProperty(name), defaultValue);
	}

	/**
	 * Returns the property converted to a Float, or the default value if it is absent or invalid.
	 */
	public Float getFloat(String name, Float defaultValue) {
		return Floats.valueOf(getProperty(name), defaultValue);
	}

	/**
	 * Returns the property converted to a Double, or the default value if it is absent or invalid.
	 */
	public Double getDouble(String name, Double defaultValue) {
		return Doubles.valueOf(getProperty(name), defaultValue);
	}

	/**
	 * Returns the property converted to a Long, or the default value if it is absent or invalid.
	 */
	public Long getLong(String name, Long defaultValue) {
		return Longs.valueOf(getProperty(name), defaultValue);
	}

}
