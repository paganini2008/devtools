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

import java.math.BigInteger;

import com.github.paganini2008.devtools.math.BigIntegerUtils;

/**
 * BigIntegerConverter
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class BigIntegerConverter extends BasicConverter<BigInteger> {

	private final Converter<Number, BigInteger> numberConverter = new Converter<Number, BigInteger>() {
		public BigInteger convertValue(Number source, BigInteger defaultValue) {
			return BigIntegerUtils.valueOf(source, defaultValue);
		}
	};

	private final Converter<Boolean, BigInteger> booleanConverter = new Converter<Boolean, BigInteger>() {
		public BigInteger convertValue(Boolean source, BigInteger defaultValue) {
			return BigIntegerUtils.valueOf(source, defaultValue);
		}
	};

	private final Converter<Character, BigInteger> characterConverter = new Converter<Character, BigInteger>() {
		public BigInteger convertValue(Character source, BigInteger defaultValue) {
			return BigIntegerUtils.valueOf(source, defaultValue);
		}
	};

	private final Converter<String, BigInteger> stringConverter = new Converter<String, BigInteger>() {
		public BigInteger convertValue(String source, BigInteger defaultValue) {
			return BigIntegerUtils.valueOf(source, defaultValue);
		}
	};

	public BigIntegerConverter() {
		registerType(Number.class, numberConverter);
		registerType(String.class, stringConverter);
		registerType(Boolean.class, booleanConverter);
		registerType(Character.class, characterConverter);
	}

}
