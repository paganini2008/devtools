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

import java.nio.charset.Charset;

import com.github.paganini2008.devtools.StringUtils;

/**
 * CharsetConverter
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class CharsetConverter extends BasicConverter<Charset> {

	private final Converter<String, Charset> stringConverter = new Converter<String, Charset>() {
		public Charset convertValue(String source, Charset defaultValue) {
			if (StringUtils.isBlank(source)) {
				return defaultValue;
			}
			return Charset.forName(source);
		}
	};

	public CharsetConverter() {
		registerType(String.class, stringConverter);
	}

}
