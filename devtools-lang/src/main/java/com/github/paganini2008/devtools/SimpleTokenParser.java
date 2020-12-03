package com.github.paganini2008.devtools;

import java.util.function.Function;

/**
 * 
 * SimpleTokenParser
 *
 * @author Jimmy Hoff
 * @version 1.0
 */
public class SimpleTokenParser implements TokenParser<Integer, String> {

	private final String placeholder;

	public SimpleTokenParser(String placeholder) {
		this.placeholder = placeholder;
	}

	@Override
	public String parse(String text, Function<Integer, Object> function) {
		int offset = 0;
		int start = text.indexOf(placeholder, offset);
		if (start == -1) {
			return text;
		}
		StringBuilder builder = new StringBuilder();
		final char[] src = text.toCharArray();
		int n = 0;
		while (start > -1) {
			if (start > 0 && src[start - 1] == '\\') {
				builder.append(src, offset, start - offset - 1).append(placeholder);
				offset = start + placeholder.length();
			} else {
				builder.append(src, offset, start - offset);
				builder.append(function.apply(n++));
				offset = start + placeholder.length();
			}
			start = text.indexOf(placeholder, offset);
		}
		if (offset < src.length) {
			builder.append(src, offset, src.length - offset);
		}
		return builder.toString();
	}

}
