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
package com.github.paganini2008.devtools.converter;

import com.github.paganini2008.devtools.primitives.Ints;

/**
 * IntegerConverter
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class IntegerConverter extends BasicConverter<Integer> {

	private final Converter<Boolean, Integer> booleanConverter = new Converter<Boolean, Integer>() {
		public Integer convertValue(Boolean source, Integer defaultValue) {
			return Ints.valueOf(source, defaultValue);
		}
	};

	private final Converter<Character, Integer> characterConverter = new Converter<Character, Integer>() {
		public Integer convertValue(Character source, Integer defaultValue) {
			return Ints.valueOf(source, defaultValue);
		}
	};

	private final Converter<Number, Integer> numberConverter = new Converter<Number, Integer>() {
		public Integer convertValue(Number source, Integer defaultValue) {
			return Ints.valueOf(source, defaultValue);
		}
	};

	private final Converter<String, Integer> stringConverter = new Converter<String, Integer>() {
		public Integer convertValue(String source, Integer defaultValue) {
			return Ints.valueOf(source, defaultValue);
		}
	};

	public IntegerConverter() {
		registerType(Boolean.class, booleanConverter);
		registerType(Character.class, characterConverter);
		registerType(Number.class, numberConverter);
		registerType(String.class, stringConverter);
	}

}
