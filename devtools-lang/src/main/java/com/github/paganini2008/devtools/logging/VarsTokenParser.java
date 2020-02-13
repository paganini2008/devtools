package com.github.paganini2008.devtools.logging;

import com.github.paganini2008.devtools.StringUtils;

/**
 * VarsTokenParser
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class VarsTokenParser implements TokenParser {

	public VarsTokenParser(String token) {
		this.token = token;
	}

	private final String token;

	public String parse(String text, Object... args) {
		return StringUtils.parseText(text, token, args);
	}
}
