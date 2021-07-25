/**
* Copyright 2017-2021 Fred Feng (paganini.fy@gmail.com)

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

import com.github.paganini2008.devtools.primitives.Chars;

/**
 * CharacterConverter
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class CharacterConverter extends BasicConverter<Character> {

	private final Converter<String, Character> stringConverter = new Converter<String, Character>() {
		public Character convertValue(String source, Character defaultValue) {
			return Chars.valueOf(source, defaultValue);
		}
	};

	private final Converter<Integer, Character> integerConverter = new Converter<Integer, Character>() {
		public Character convertValue(Integer source, Character defaultValue) {
			return Chars.valueOf(source, defaultValue);
		}
	};

	private final Converter<Boolean, Character> booleanConverter = new Converter<Boolean, Character>() {
		public Character convertValue(Boolean source, Character defaultValue) {
			return Chars.valueOf(source, defaultValue);
		}
	};

	public CharacterConverter() {
		registerType(String.class, stringConverter);
		registerType(Integer.class, integerConverter);
		registerType(Boolean.class, booleanConverter);
	}

}
