package com.github.paganini2008.devtools.converter;

import java.math.BigInteger;

import com.github.paganini2008.devtools.math.BigIntegerUtils;

/**
 * BigIntegerConverter
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class BigIntegerConverter extends BasicConverter<BigInteger> {

	private final Converter<Number, BigInteger> numberConverter = new Converter<Number, BigInteger>() {
		public BigInteger convertValue(Number source, BigInteger defaultValue) {
			return BigIntegerUtils.valueOf(source, defaultValue);
		}
	};

	private final Converter<Boolean, BigInteger> booleanConverter = new Converter<Boolean, BigInteger>() {
		public BigInteger convertValue(Boolean source, BigInteger defaultValue) {
			return BigIntegerUtils.valueOf(source, defaultValue);
		}
	};

	private final Converter<Character, BigInteger> characterConverter = new Converter<Character, BigInteger>() {
		public BigInteger convertValue(Character source, BigInteger defaultValue) {
			return BigIntegerUtils.valueOf(source, defaultValue);
		}
	};

	private final Converter<String, BigInteger> stringConverter = new Converter<String, BigInteger>() {
		public BigInteger convertValue(String source, BigInteger defaultValue) {
			return BigIntegerUtils.valueOf(source, defaultValue);
		}
	};

	public BigIntegerConverter() {
		registerType(Number.class, numberConverter);
		registerType(String.class, stringConverter);
		registerType(Boolean.class, booleanConverter);
		registerType(Character.class, characterConverter);
	}

}
