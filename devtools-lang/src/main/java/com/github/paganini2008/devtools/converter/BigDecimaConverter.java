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

import java.math.BigDecimal;

import com.github.paganini2008.devtools.math.BigDecimalUtils;

/**
 * BigDecimaConverter
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class BigDecimaConverter extends BasicConverter<BigDecimal> {

	private final Converter<Number, BigDecimal> numberConverter = new Converter<Number, BigDecimal>() {
		public BigDecimal convertValue(Number source, BigDecimal defaultValue) {
			return BigDecimalUtils.valueOf(source, defaultValue);
		}
	};

	private final Converter<Boolean, BigDecimal> booleanConverter = new Converter<Boolean, BigDecimal>() {
		public BigDecimal convertValue(Boolean source, BigDecimal defaultValue) {
			return BigDecimalUtils.valueOf(source, defaultValue);
		}
	};

	private final Converter<Character, BigDecimal> characterConverter = new Converter<Character, BigDecimal>() {
		public BigDecimal convertValue(Character source, BigDecimal defaultValue) {
			return BigDecimalUtils.valueOf(source, defaultValue);
		}
	};

	private final Converter<String, BigDecimal> stringConverter = new Converter<String, BigDecimal>() {
		public BigDecimal convertValue(String source, BigDecimal defaultValue) {
			return BigDecimalUtils.valueOf(source, defaultValue);
		}
	};

	public BigDecimaConverter() {
		registerType(Number.class, numberConverter);
		registerType(Character.class, characterConverter);
		registerType(Boolean.class, booleanConverter);
		registerType(String.class, stringConverter);
	}

}
