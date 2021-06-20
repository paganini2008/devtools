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
import com.github.paganini2008.devtools.primitives.Shorts;

/**
 * ShortArrayConverter
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class ShortArrayConverter extends BasicConverter<short[]> {

	private final Converter<CharSequence, short[]> charSequenceConverter = new Converter<CharSequence, short[]>() {
		public short[] convertValue(CharSequence source, short[] defaultValue) {
			if (StringUtils.isBlank(source)) {
				return defaultValue;
			}
			List<String> results = StringUtils.split(source, delimiter);
			return Shorts.parseMany(results.toArray(new String[results.size()]));
		}
	};

	private final Converter<String[], short[]> stringArrayConverter = new Converter<String[], short[]>() {
		public short[] convertValue(String[] source, short[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Shorts.parseMany(source);
		}
	};

	private final Converter<Number[], short[]> numberArrayConverter = new Converter<Number[], short[]>() {
		public short[] convertValue(Number[] source, short[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Shorts.casts(source);
		}
	};

	private final Converter<char[], short[]> charArrayConverter = new Converter<char[], short[]>() {
		public short[] convertValue(char[] source, short[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Shorts.casts(source);
		}
	};

	private final Converter<boolean[], short[]> booleanArrayConverter = new Converter<boolean[], short[]>() {
		public short[] convertValue(boolean[] source, short[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Shorts.casts(source);
		}
	};

	private final Converter<byte[], short[]> byteArrayConverter = new Converter<byte[], short[]>() {
		public short[] convertValue(byte[] source, short[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Shorts.casts(source);
		}
	};

	private final Converter<int[], short[]> intArrayConverter = new Converter<int[], short[]>() {
		public short[] convertValue(int[] source, short[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Shorts.casts(source);
		}
	};

	private final Converter<long[], short[]> longArrayConverter = new Converter<long[], short[]>() {
		public short[] convertValue(long[] source, short[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Shorts.casts(source);
		}
	};

	private final Converter<float[], short[]> floatArrayConverter = new Converter<float[], short[]>() {
		public short[] convertValue(float[] source, short[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Shorts.casts(source);
		}
	};

	private final Converter<double[], short[]> doubleArrayConverter = new Converter<double[], short[]>() {
		public short[] convertValue(double[] source, short[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Shorts.casts(source);
		}
	};

	public ShortArrayConverter() {
		registerType(CharSequence.class, charSequenceConverter);
		registerType(Number[].class, numberArrayConverter);
		registerType(String[].class, stringArrayConverter);
		registerType(char[].class, charArrayConverter);
		registerType(boolean[].class, booleanArrayConverter);
		registerType(byte[].class, byteArrayConverter);
		registerType(int[].class, intArrayConverter);
		registerType(long[].class, longArrayConverter);
		registerType(float[].class, floatArrayConverter);
		registerType(double[].class, doubleArrayConverter);
	}

	private String delimiter = ",";

	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}

}
