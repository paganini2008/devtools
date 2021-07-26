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

import java.util.List;

import com.github.paganini2008.devtools.StringUtils;
import com.github.paganini2008.devtools.primitives.Floats;

/**
 * FloatArrayConverter
 * 
 * @author Fred Feng
 * @since 2.0.1
 */
public class FloatArrayConverter extends BasicConverter<float[]> {

	private final Converter<CharSequence, float[]> charSequenceConverter = new Converter<CharSequence, float[]>() {
		public float[] convertValue(CharSequence source, float[] defaultValue) {
			if (StringUtils.isBlank(source)) {
				return defaultValue;
			}
			List<String> result = StringUtils.split(source, delimiter);
			return result != null ? Floats.parseMany(result.toArray(new String[result.size()])) : defaultValue;
		}
	};

	private final Converter<String[], float[]> stringArrayConverter = new Converter<String[], float[]>() {
		public float[] convertValue(String[] source, float[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Floats.parseMany(source);
		}
	};

	private final Converter<Number[], float[]> numberArrayConverter = new Converter<Number[], float[]>() {
		public float[] convertValue(Number[] source, float[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Floats.casts(source);
		}
	};

	private final Converter<char[], float[]> charArrayConverter = new Converter<char[], float[]>() {
		public float[] convertValue(char[] source, float[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Floats.casts(source);
		}
	};

	private final Converter<boolean[], float[]> booleanArrayConverter = new Converter<boolean[], float[]>() {
		public float[] convertValue(boolean[] source, float[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Floats.casts(source);
		}
	};

	private final Converter<byte[], float[]> byteArrayConverter = new Converter<byte[], float[]>() {
		public float[] convertValue(byte[] source, float[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Floats.casts(source);
		}
	};

	private final Converter<int[], float[]> intArrayConverter = new Converter<int[], float[]>() {
		public float[] convertValue(int[] source, float[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Floats.casts(source);
		}
	};

	private final Converter<long[], float[]> longArrayConverter = new Converter<long[], float[]>() {
		public float[] convertValue(long[] source, float[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Floats.casts(source);
		}
	};

	private final Converter<double[], float[]> doubleArrayConverter = new Converter<double[], float[]>() {
		public float[] convertValue(double[] source, float[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Floats.casts(source);
		}
	};

	public FloatArrayConverter() {
		registerType(CharSequence.class, charSequenceConverter);
		registerType(Number[].class, numberArrayConverter);
		registerType(String[].class, stringArrayConverter);
		registerType(char[].class, charArrayConverter);
		registerType(boolean[].class, booleanArrayConverter);
		registerType(byte[].class, byteArrayConverter);
		registerType(int[].class, intArrayConverter);
		registerType(long[].class, longArrayConverter);
		registerType(double[].class, doubleArrayConverter);
	}

	private String delimiter = ",";

	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}

}
