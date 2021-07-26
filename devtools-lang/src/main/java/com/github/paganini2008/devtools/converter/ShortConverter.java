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

import com.github.paganini2008.devtools.primitives.Shorts;

/**
 * ShortConverter
 * 
 * @author Fred Feng
 * @since 2.0.1
 */
public class ShortConverter extends BasicConverter<Short> {

	private final Converter<Boolean, Short> booleanConverter = new Converter<Boolean, Short>() {
		public Short convertValue(Boolean source, Short defaultValue) {
			return Shorts.valueOf(source, defaultValue);
		}
	};

	private final Converter<Character, Short> characterConverter = new Converter<Character, Short>() {
		public Short convertValue(Character source, Short defaultValue) {
			return Shorts.valueOf(source, defaultValue);
		}
	};

	private final Converter<Number, Short> numberConverter = new Converter<Number, Short>() {
		public Short convertValue(Number source, Short defaultValue) {
			return Shorts.valueOf(source, defaultValue);
		}
	};

	private final Converter<String, Short> stringConverter = new Converter<String, Short>() {
		public Short convertValue(String source, Short defaultValue) {
			return Shorts.valueOf(source, defaultValue);
		}
	};

	public ShortConverter() {
		registerType(Boolean.class, booleanConverter);
		registerType(Character.class, characterConverter);
		registerType(Number.class, numberConverter);
		registerType(String.class, stringConverter);
	}
}
