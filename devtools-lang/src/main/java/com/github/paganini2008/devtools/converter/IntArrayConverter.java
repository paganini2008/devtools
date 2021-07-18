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

import java.util.List;

import com.github.paganini2008.devtools.StringUtils;
import com.github.paganini2008.devtools.primitives.Ints;

/**
 * 
 * IntArrayConverter 
 *
 * @author Fred Feng
 * @version 1.0
 */
public class IntArrayConverter extends BasicConverter<int[]> {

	private final Converter<CharSequence, int[]> charSequenceConverter = new Converter<CharSequence, int[]>() {
		public int[] convertValue(CharSequence source, int[] defaultValue) {
			if (StringUtils.isBlank(source)) {
				return defaultValue;
			}
			List<String> result = StringUtils.split(source, ",");
			return result != null ? Ints.parseMany(result.toArray(new String[result.size()])) : defaultValue;
		}
	};

	private final Converter<String[], int[]> stringArrayConverter = new Converter<String[], int[]>() {
		public int[] convertValue(String[] source, int[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Ints.parseMany(source);
		}
	};

	private final Converter<Number[], int[]> numberArrayConverter = new Converter<Number[], int[]>() {
		public int[] convertValue(Number[] source, int[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Ints.casts(source);
		}
	};

	private final Converter<char[], int[]> charArrayConverter = new Converter<char[], int[]>() {
		public int[] convertValue(char[] source, int[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Ints.casts(source);
		}
	};

	private final Converter<boolean[], int[]> booleanArrayConverter = new Converter<boolean[], int[]>() {
		public int[] convertValue(boolean[] source, int[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Ints.casts(source);
		}
	};

	private final Converter<byte[], int[]> byteArrayConverter = new Converter<byte[], int[]>() {
		public int[] convertValue(byte[] source, int[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Ints.casts(source);
		}
	};

	private final Converter<short[], int[]> shortArrayConverter = new Converter<short[], int[]>() {
		public int[] convertValue(short[] source, int[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Ints.casts(source);
		}
	};

	private final Converter<long[], int[]> longArrayConverter = new Converter<long[], int[]>() {
		public int[] convertValue(long[] source, int[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Ints.casts(source);
		}
	};

	private final Converter<float[], int[]> floatArrayConverter = new Converter<float[], int[]>() {
		public int[] convertValue(float[] source, int[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Ints.casts(source);
		}
	};

	private final Converter<double[], int[]> doubleArrayConverter = new Converter<double[], int[]>() {
		public int[] convertValue(double[] source, int[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Ints.casts(source);
		}
	};

	public IntArrayConverter() {
		registerType(CharSequence.class, charSequenceConverter);
		registerType(Number[].class, numberArrayConverter);
		registerType(String[].class, stringArrayConverter);
		registerType(char[].class, charArrayConverter);
		registerType(boolean[].class, booleanArrayConverter);
		registerType(byte[].class, byteArrayConverter);
		registerType(int[].class, shortArrayConverter);
		registerType(long[].class, longArrayConverter);
		registerType(float[].class, floatArrayConverter);
		registerType(double[].class, doubleArrayConverter);
	}

}
