package com.github.paganini2008.devtools.converter;

import java.math.BigDecimal;

import com.github.paganini2008.devtools.math.BigDecimalUtils;

/**
 * BigDecimaConverter
 * 
 * @author Jimmy Hoff
 * @version 1.0
 */
public class BigDecimaConverter extends BasicConverter<BigDecimal> {

	private final Converter<Number, BigDecimal> numberConverter = new Converter<Number, BigDecimal>() {
		public BigDecimal convertValue(Number source, BigDecimal defaultValue) {
			return BigDecimalUtils.valueOf(source, defaultValue);
		}
	};

	private final Converter<Boolean, BigDecimal> booleanConverter = new Converter<Boolean, BigDecimal>() {
		public BigDecimal convertValue(Boolean source, BigDecimal defaultValue) {
			return BigDecimalUtils.valueOf(source, defaultValue);
		}
	};

	private final Converter<Character, BigDecimal> characterConverter = new Converter<Character, BigDecimal>() {
		public BigDecimal convertValue(Character source, BigDecimal defaultValue) {
			return BigDecimalUtils.valueOf(source, defaultValue);
		}
	};

	private final Converter<String, BigDecimal> stringConverter = new Converter<String, BigDecimal>() {
		public BigDecimal convertValue(String source, BigDecimal defaultValue) {
			return BigDecimalUtils.valueOf(source, defaultValue);
		}
	};

	public BigDecimaConverter() {
		registerType(Number.class, numberConverter);
		registerType(Character.class, characterConverter);
		registerType(Boolean.class, booleanConverter);
		registerType(String.class, stringConverter);
	}

}
