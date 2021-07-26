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
import com.github.paganini2008.devtools.primitives.Longs;

/**
 * LongArrayConverter
 * 
 * @author Fred Feng
 * @since 2.0.1
 */
public class LongArrayConverter extends BasicConverter<long[]> {

	private final Converter<CharSequence, long[]> charSequenceConverter = new Converter<CharSequence, long[]>() {
		public long[] convertValue(CharSequence source, long[] defaultValue) {
			if (StringUtils.isBlank(source)) {
				return defaultValue;
			}
			List<String> result = StringUtils.split(source, delimiter);
			return result != null ? Longs.parseMany(result.toArray(new String[result.size()])) : defaultValue;
		}
	};

	private final Converter<String[], long[]> stringArrayConverter = new Converter<String[], long[]>() {
		public long[] convertValue(String[] source, long[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Longs.parseMany(source);
		}
	};

	private final Converter<Number[], long[]> numberArrayConverter = new Converter<Number[], long[]>() {
		public long[] convertValue(Number[] source, long[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Longs.casts(source);
		}
	};

	private final Converter<char[], long[]> charArrayConverter = new Converter<char[], long[]>() {
		public long[] convertValue(char[] source, long[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Longs.casts(source);
		}
	};

	private final Converter<boolean[], long[]> booleanArrayConverter = new Converter<boolean[], long[]>() {
		public long[] convertValue(boolean[] source, long[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Longs.casts(source);
		}
	};

	private final Converter<byte[], long[]> byteArrayConverter = new Converter<byte[], long[]>() {
		public long[] convertValue(byte[] source, long[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Longs.casts(source);
		}
	};

	private final Converter<short[], long[]> shortArrayConverter = new Converter<short[], long[]>() {
		public long[] convertValue(short[] source, long[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Longs.casts(source);
		}
	};

	private final Converter<float[], long[]> floatArrayConverter = new Converter<float[], long[]>() {
		public long[] convertValue(float[] source, long[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Longs.casts(source);
		}
	};

	private final Converter<double[], long[]> doubleArrayConverter = new Converter<double[], long[]>() {
		public long[] convertValue(double[] source, long[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Longs.casts(source);
		}
	};

	public LongArrayConverter() {
		registerType(CharSequence.class, charSequenceConverter);
		registerType(Number[].class, numberArrayConverter);
		registerType(String[].class, stringArrayConverter);
		registerType(char[].class, charArrayConverter);
		registerType(boolean[].class, booleanArrayConverter);
		registerType(byte[].class, byteArrayConverter);
		registerType(int[].class, shortArrayConverter);
		registerType(float[].class, floatArrayConverter);
		registerType(double[].class, doubleArrayConverter);
	}
	
	private String delimiter = ",";

	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}

}
