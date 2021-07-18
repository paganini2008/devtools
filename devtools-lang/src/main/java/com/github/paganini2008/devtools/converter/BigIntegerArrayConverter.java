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
 * BigIntegerArrayConverter
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class BigIntegerArrayConverter extends BasicConverter<BigInteger[]> {

	private final Converter<byte[], BigInteger[]> byteArrayConverter = new Converter<byte[], BigInteger[]>() {
		public BigInteger[] convertValue(byte[] source, BigInteger[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return BigIntegerUtils.valueOf(source);
		}
	};

	private final Converter<short[], BigInteger[]> shortArrayConverter = new Converter<short[], BigInteger[]>() {
		public BigInteger[] convertValue(short[] source, BigInteger[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return BigIntegerUtils.valueOf(source);
		}
	};

	private final Converter<int[], BigInteger[]> intArrayConverter = new Converter<int[], BigInteger[]>() {
		public BigInteger[] convertValue(int[] source, BigInteger[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return BigIntegerUtils.valueOf(source);
		}
	};

	private final Converter<long[], BigInteger[]> longArrayConverter = new Converter<long[], BigInteger[]>() {
		public BigInteger[] convertValue(long[] source, BigInteger[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return BigIntegerUtils.valueOf(source);
		}
	};

	private final Converter<Number[], BigInteger[]> numberArrayConverter = new Converter<Number[], BigInteger[]>() {
		public BigInteger[] convertValue(Number[] source, BigInteger[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return BigIntegerUtils.valueOf(source);
		}
	};

	private final Converter<String[], BigInteger[]> stringArrayConverter = new Converter<String[], BigInteger[]>() {
		public BigInteger[] convertValue(String[] source, BigInteger[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return BigIntegerUtils.valueOf(source);
		}
	};

	public BigIntegerArrayConverter() {
		registerType(byte[].class, byteArrayConverter);
		registerType(short[].class, shortArrayConverter);
		registerType(int[].class, intArrayConverter);
		registerType(long[].class, longArrayConverter);
		registerType(Number[].class, numberArrayConverter);
		registerType(String[].class, stringArrayConverter);
	}

}
