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

import java.util.Locale;

import com.github.paganini2008.devtools.LocaleUtils;

/**
 * LocaleConverter
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class LocaleConverter extends BasicConverter<Locale> {

	private final Converter<String, Locale> stringConverter = new Converter<String, Locale>() {
		public Locale convertValue(String source, Locale defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return LocaleUtils.getLocale(source);
		}
	};

	public LocaleConverter() {
		registerType(String.class, stringConverter);
	}

}
