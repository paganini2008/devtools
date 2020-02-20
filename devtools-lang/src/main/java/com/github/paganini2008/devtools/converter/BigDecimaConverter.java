package com.github.paganini2008.devtools.converter;

import java.math.BigDecimal;

import com.github.paganini2008.devtools.math.BigDecimalUtils;

/**
 * BigDecimaConverter
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class BigDecimaConverter extends BasicConverter<BigDecimal> {

	private final Converter<Number, BigDecimal> numberConverter = new Converter<Number, BigDecimal>() {
		public BigDecimal getValue(Number source, BigDecimal defaultValue) {
			return BigDecimalUtils.valueOf(source, defaultValue);
		}
	};

	private final Converter<Boolean, BigDecimal> booleanConverter = new Converter<Boolean, BigDecimal>() {
		public BigDecimal getValue(Boolean source, BigDecimal defaultValue) {
			return BigDecimalUtils.valueOf(source, defaultValue);
		}
	};

	private final Converter<Character, BigDecimal> characterConverter = new Converter<Character, BigDecimal>() {
		public BigDecimal getValue(Character source, BigDecimal defaultValue) {
			return BigDecimalUtils.valueOf(source, defaultValue);
		}
	};

	private final Converter<String, BigDecimal> stringConverter = new Converter<String, BigDecimal>() {
		public BigDecimal getValue(String source, BigDecimal defaultValue) {
			return BigDecimalUtils.valueOf(source, defaultValue);
		}
	};

	public BigDecimaConverter() {
		put(Number.class, numberConverter);
		put(Character.class, characterConverter);
		put(Boolean.class, booleanConverter);
		put(String.class, stringConverter);
	}

}
