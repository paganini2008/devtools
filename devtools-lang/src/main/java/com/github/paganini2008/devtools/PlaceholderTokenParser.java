package com.github.paganini2008.devtools;

import java.util.function.Function;

/**
 * 
 * PlaceholderTokenParser
 *
 * @author Fred Feng
 * @version 1.0
 */
public class PlaceholderTokenParser implements TokenParser<String, String> {

	private final String prefix;
	private final String suffix;

	public PlaceholderTokenParser(String prefix, String suffix) {
		this.prefix = prefix;
		this.suffix = suffix;
	}

	public String parse(String text, Function<String, Object> function) {
		int offset = 0;
		int start = text.indexOf(prefix, offset);
		if (start == -1) {
			return text;
		}
		StringBuilder builder = new StringBuilder();
		final char[] src = text.toCharArray();
		while (start > -1) {
			if (start > 0 && src[start - 1] == '\\') {
				builder.append(src, offset, start - offset - 1).append(prefix);
				offset = start + prefix.length();
			} else {
				int end = text.indexOf(suffix, start);
				if (end == -1) {
					builder.append(src, offset, src.length - offset);
					offset = src.length;
				} else {
					builder.append(src, offset, start - offset);
					offset = start + prefix.length();
					String part = new String(src, offset, end - offset);
					if (StringUtils.isNotBlank(part)) {
						Object value = function.apply(part);
						if (value == null) {
							value = System.getProperty(part);
						}
						builder.append(value);
					} else {
						// builder.append(src, start, end - start + suffix.length());
					}
					offset = end + suffix.length();
				}
			}
			start = text.indexOf(prefix, offset);
		}
		if (offset < src.length) {
			builder.append(src, offset, src.length - offset);
		}
		return builder.toString();
	}

}
