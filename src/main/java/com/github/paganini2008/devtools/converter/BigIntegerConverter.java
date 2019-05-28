package com.github.paganini2008.devtools.converter;

import java.math.BigInteger;

import com.github.paganini2008.devtools.math.BigIntegers;

/**
 * BigIntegerConverter
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class BigIntegerConverter extends BaseConverter<BigInteger> {

	private final Converter<Number, BigInteger> numberConverter = new Converter<Number, BigInteger>() {
		public BigInteger getValue(Number source, BigInteger defaultValue) {
			return BigIntegers.valueOf(source, defaultValue);
		}
	};

	private final Converter<Boolean, BigInteger> booleanConverter = new Converter<Boolean, BigInteger>() {
		public BigInteger getValue(Boolean source, BigInteger defaultValue) {
			return BigIntegers.valueOf(source, defaultValue);
		}
	};

	private final Converter<Character, BigInteger> characterConverter = new Converter<Character, BigInteger>() {
		public BigInteger getValue(Character source, BigInteger defaultValue) {
			return BigIntegers.valueOf(source, defaultValue);
		}
	};

	private final Converter<String, BigInteger> stringConverter = new Converter<String, BigInteger>() {
		public BigInteger getValue(String source, BigInteger defaultValue) {
			return BigIntegers.valueOf(source, defaultValue);
		}
	};

	public BigIntegerConverter() {
		put(Number.class, numberConverter);
		put(String.class, stringConverter);
		put(Boolean.class, booleanConverter);
		put(Character.class, characterConverter);
	}

}
