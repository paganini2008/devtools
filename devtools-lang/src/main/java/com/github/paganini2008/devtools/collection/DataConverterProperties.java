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
 *
 * @author Fred Feng
 * @since 2.0.1
 */
public class DataConverterProperties extends Properties {

	private static final long serialVersionUID = -891559340734908695L;

	public Boolean getRequiredBoolean(String name) {
		assertKeyExisted(name);
		return Booleans.valueOf(getProperty(name));
	}

	public Character getRequiredCharacter(String name) {
		assertKeyExisted(name);
		return Chars.valueOf(getProperty(name));
	}

	public Byte getRequiredByte(String name) {
		assertKeyExisted(name);
		return Bytes.valueOf(getProperty(name));
	}

	public Short getRequiredShort(String name) {
		assertKeyExisted(name);
		return Shorts.valueOf(getProperty(name));
	}

	public Integer getRequiredInteger(String name) {
		assertKeyExisted(name);
		return Ints.valueOf(getProperty(name));
	}

	public Float getRequiredFloat(String name) {
		assertKeyExisted(name);
		return Floats.valueOf(getProperty(name));
	}

	public Double getRequiredDouble(String name) {
		assertKeyExisted(name);
		return Doubles.valueOf(getProperty(name));
	}

	public Long getRequiredLong(String name) {
		assertKeyExisted(name);
		return Longs.valueOf(getProperty(name));
	}

	private void assertKeyExisted(String name) {
		if (!containsKey(name)) {
			throw new MissingKeyException(name);
		}
	}

	public Boolean getBoolean(String name, Boolean defaultValue) {
		return Booleans.valueOf(getProperty(name), defaultValue);
	}

	public Character getCharacter(String name, Character defaultValue) {
		return Chars.valueOf(getProperty(name), defaultValue);
	}

	public Byte getByte(String name, Byte defaultValue) {
		return Bytes.valueOf(getProperty(name), defaultValue);
	}

	public Short getShort(String name, Short defaultValue) {
		return Shorts.valueOf(getProperty(name), defaultValue);
	}

	public Integer getInteger(String name, Integer defaultValue) {
		return Ints.valueOf(getProperty(name), defaultValue);
	}

	public Float getFloat(String name, Float defaultValue) {
		return Floats.valueOf(getProperty(name), defaultValue);
	}

	public Double getDouble(String name, Double defaultValue) {
		return Doubles.valueOf(getProperty(name), defaultValue);
	}

	public Long getLong(String name, Long defaultValue) {
		return Longs.valueOf(getProperty(name), defaultValue);
	}

}
