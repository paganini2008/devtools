package com.github.paganini2008.devtools.converter;

import java.nio.charset.Charset;

import com.github.paganini2008.devtools.StringUtils;

/**
 * CharsetConverter
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class CharsetConverter extends BaseConverter<Charset> {

	private final Converter<String, Charset> stringConverter = new Converter<String, Charset>() {
		public Charset getValue(String source, Charset defaultValue) {
			if (StringUtils.isBlank(source)) {
				return defaultValue;
			}
			return Charset.forName(source);
		}
	};

	public CharsetConverter() {
		put(String.class, stringConverter);
	}

}
