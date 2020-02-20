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
