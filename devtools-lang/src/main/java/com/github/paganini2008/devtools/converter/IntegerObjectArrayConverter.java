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

import java.util.List;

import com.github.paganini2008.devtools.StringUtils;
import com.github.paganini2008.devtools.primitives.Ints;

/**
 * IntegerObjectArrayConverter
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class IntegerObjectArrayConverter extends BasicConverter<Integer[]> {

	private final Converter<CharSequence, Integer[]> charSequenceConverter = new Converter<CharSequence, Integer[]>() {
		public Integer[] convertValue(CharSequence source, Integer[] defaultValue) {
			if (StringUtils.isBlank(source)) {
				return defaultValue;
			}
			List<String> result = StringUtils.split(source, delimiter);
			return result != null ? Ints.valueOf(result.toArray(new String[result.size()])) : defaultValue;
		}
	};

	private final Converter<Number[], Integer[]> numberArrayConverter = new Converter<Number[], Integer[]>() {
		public Integer[] convertValue(Number[] source, Integer[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Ints.valueOf(source);
		}
	};

	private final Converter<String[], Integer[]> stringArrayConverter = new Converter<String[], Integer[]>() {
		public Integer[] convertValue(String[] source, Integer[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Ints.valueOf(source);
		}
	};

	private final Converter<byte[], Integer[]> byteArrayConverter = new Converter<byte[], Integer[]>() {
		public Integer[] convertValue(byte[] source, Integer[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Ints.toWrappers(source);
		}
	};

	private final Converter<short[], Integer[]> shortArrayConverter = new Converter<short[], Integer[]>() {
		public Integer[] convertValue(short[] source, Integer[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Ints.toWrappers(source);
		}
	};

	private final Converter<char[], Integer[]> charArrayConverter = new Converter<char[], Integer[]>() {
		public Integer[] convertValue(char[] source, Integer[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Ints.toWrappers(source);
		}
	};

	private final Converter<int[], Integer[]> intArrayConverter = new Converter<int[], Integer[]>() {
		public Integer[] convertValue(int[] source, Integer[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Ints.toWrappers(source);
		}
	};

	public IntegerObjectArrayConverter() {
		registerType(CharSequence.class, charSequenceConverter);
		registerType(Number[].class, numberArrayConverter);
		registerType(String[].class, stringArrayConverter);
		registerType(byte[].class, byteArrayConverter);
		registerType(char[].class, shortArrayConverter);
		registerType(short[].class, charArrayConverter);
		registerType(int[].class, intArrayConverter);
	}
	
	private String delimiter = ",";

	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}

}
