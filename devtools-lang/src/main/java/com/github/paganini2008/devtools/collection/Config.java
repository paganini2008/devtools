/**
* Copyright 2021 Fred Feng (paganini.fy@gmail.com)

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

import com.github.paganini2008.devtools.primitives.Booleans;
import com.github.paganini2008.devtools.primitives.Bytes;
import com.github.paganini2008.devtools.primitives.Chars;
import com.github.paganini2008.devtools.primitives.Doubles;
import com.github.paganini2008.devtools.primitives.Floats;
import com.github.paganini2008.devtools.primitives.Ints;
import com.github.paganini2008.devtools.primitives.Longs;
import com.github.paganini2008.devtools.primitives.Shorts;

public class Config extends Properties {

	private static final long serialVersionUID = -891559340734908695L;

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
