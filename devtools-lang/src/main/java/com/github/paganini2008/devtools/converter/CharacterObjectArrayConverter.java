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

import com.github.paganini2008.devtools.primitives.Chars;

/**
 * CharacterObjectArrayConverter
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class CharacterObjectArrayConverter extends BasicConverter<Character[]> {

	private final Converter<char[], Character[]> charArrayConverter = new Converter<char[], Character[]>() {
		public Character[] convertValue(char[] source, Character[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Chars.toWrappers(source);
		}
	};

	private final Converter<CharSequence, Character[]> stringConverter = new Converter<CharSequence, Character[]>() {
		public Character[] convertValue(CharSequence source, Character[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Chars.toCharacterArray(source);
		}
	};

	public CharacterObjectArrayConverter() {
		registerType(char[].class, charArrayConverter);
		registerType(CharSequence.class, stringConverter);
	}

}
