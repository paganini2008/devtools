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

import com.github.paganini2008.devtools.primitives.Bytes;

/**
 * ByteConverter
 * 
 * @author Fred Feng
 * @since 2.0.1
 */
public class ByteConverter extends BasicConverter<Byte> {

	private final Converter<Boolean, Byte> booleanConverter = new Converter<Boolean, Byte>() {
		public Byte convertValue(Boolean source, Byte defaultValue) {
			return Bytes.valueOf(source, defaultValue);
		}
	};

	private final Converter<Character, Byte> characterConverter = new Converter<Character, Byte>() {
		public Byte convertValue(Character source, Byte defaultValue) {
			return Bytes.valueOf(source, defaultValue);
		}
	};

	private final Converter<Number, Byte> numberConverter = new Converter<Number, Byte>() {
		public Byte convertValue(Number source, Byte defaultValue) {
			return Bytes.valueOf(source, defaultValue);
		}
	};

	private final Converter<String, Byte> stringConverter = new Converter<String, Byte>() {
		public Byte convertValue(String source, Byte defaultValue) {
			return Bytes.valueOf(source, defaultValue);
		}
	};

	public ByteConverter() {
		registerType(Boolean.class, booleanConverter);
		registerType(Character.class, characterConverter);
		registerType(Number.class, numberConverter);
		registerType(String.class, stringConverter);
	}
}
