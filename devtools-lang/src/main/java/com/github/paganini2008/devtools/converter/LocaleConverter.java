package com.github.paganini2008.devtools.converter;

import java.util.Locale;

import com.github.paganini2008.devtools.LocaleUtils;

/**
 * LocaleConverter
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class LocaleConverter extends BasicConverter<Locale> {

	private final Converter<String, Locale> stringConverter = new Converter<String, Locale>() {
		public Locale getValue(String source, Locale defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return LocaleUtils.getLocale(source);
		}
	};

	public LocaleConverter() {
		put(String.class, stringConverter);
	}

}
