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
 * BigDecimalArrayConverter
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class BigDecimalArrayConverter extends BasicConverter<BigDecimal[]> {

	private final Converter<byte[], BigDecimal[]> byteArrayConverter = new Converter<byte[], BigDecimal[]>() {
		public BigDecimal[] convertValue(byte[] source, BigDecimal[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return BigDecimalUtils.valueOf(source);
		}
	};

	private final Converter<short[], BigDecimal[]> shortArrayConverter = new Converter<short[], BigDecimal[]>() {
		public BigDecimal[] convertValue(short[] source, BigDecimal[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return BigDecimalUtils.valueOf(source);
		}
	};

	private final Converter<int[], BigDecimal[]> intArrayConverter = new Converter<int[], BigDecimal[]>() {
		public BigDecimal[] convertValue(int[] source, BigDecimal[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return BigDecimalUtils.valueOf(source);
		}
	};

	private final Converter<long[], BigDecimal[]> longArrayConverter = new Converter<long[], BigDecimal[]>() {
		public BigDecimal[] convertValue(long[] source, BigDecimal[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return BigDecimalUtils.valueOf(source);
		}
	};

	private final Converter<float[], BigDecimal[]> floatArrayConverter = new Converter<float[], BigDecimal[]>() {
		public BigDecimal[] convertValue(float[] source, BigDecimal[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return BigDecimalUtils.valueOf(source);
		}
	};

	private final Converter<double[], BigDecimal[]> doubleArrayConverter = new Converter<double[], BigDecimal[]>() {
		public BigDecimal[] convertValue(double[] source, BigDecimal[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return BigDecimalUtils.valueOf(source);
		}
	};

	private final Converter<Number[], BigDecimal[]> numberArrayConverter = new Converter<Number[], BigDecimal[]>() {
		public BigDecimal[] convertValue(Number[] source, BigDecimal[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return BigDecimalUtils.valueOf(source);
		}
	};

	private final Converter<String[], BigDecimal[]> stringArrayConverter = new Converter<String[], BigDecimal[]>() {
		public BigDecimal[] convertValue(String[] source, BigDecimal[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return BigDecimalUtils.valueOf(source);
		}
	};

	public BigDecimalArrayConverter() {
		registerType(byte[].class, byteArrayConverter);
		registerType(short[].class, shortArrayConverter);
		registerType(int[].class, intArrayConverter);
		registerType(long[].class, longArrayConverter);
		registerType(float[].class, floatArrayConverter);
		registerType(double[].class, doubleArrayConverter);
		registerType(Number[].class, numberArrayConverter);
		registerType(String[].class, stringArrayConverter);
	}

}
