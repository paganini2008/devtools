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
package com.github.paganini2008.devtools.converter;

import com.github.paganini2008.devtools.primitives.Doubles;

/**
 * DoubleConverter
 * 
 * @author Fred Feng
 * @since 2.0.1
 */
public class DoubleConverter extends BasicConverter<Double> {

	private final Converter<Number, Double> numberConverter = new Converter<Number, Double>() {
		public Double convertValue(Number source, Double defaultValue) {
			return Doubles.valueOf(source, defaultValue);
		}
	};

	private final Converter<String, Double> stringConverter = new Converter<String, Double>() {
		public Double convertValue(String source, Double defaultValue) {
			return Doubles.valueOf(source, defaultValue);
		}
	};

	private final Converter<Character, Double> characterConverter = new Converter<Character, Double>() {
		public Double convertValue(Character source, Double defaultValue) {
			return Doubles.valueOf(source, defaultValue);
		}
	};

	public DoubleConverter() {
		registerType(Number.class, numberConverter);
		registerType(String.class, stringConverter);
		registerType(Character.class, characterConverter);
	}

}
