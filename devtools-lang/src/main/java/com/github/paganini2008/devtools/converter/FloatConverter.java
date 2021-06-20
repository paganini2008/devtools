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
package com.github.paganini2008.devtools.converter;

import com.github.paganini2008.devtools.primitives.Floats;

/**
 * FloatConverter
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class FloatConverter extends BasicConverter<Float> {

	private final Converter<Number, Float> numberConverter = new Converter<Number, Float>() {
		public Float convertValue(Number source, Float defaultValue) {
			return Floats.valueOf(source, defaultValue);
		}
	};

	private final Converter<String, Float> stringConverter = new Converter<String, Float>() {
		public Float convertValue(String source, Float defaultValue) {
			return Floats.valueOf(source, defaultValue);
		}
	};

	private final Converter<Character, Float> characterConverter = new Converter<Character, Float>() {
		public Float convertValue(Character source, Float defaultValue) {
			return Floats.valueOf(source, defaultValue);
		}
	};

	public FloatConverter() {
		registerType(Number.class, numberConverter);
		registerType(String.class, stringConverter);
		registerType(Character.class, characterConverter);
	}

}
