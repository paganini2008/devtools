package com.github.paganini2008.devtools.db4j;

import com.github.paganini2008.devtools.Assert;

/**
 * PropertyTokenParser
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class PropertyTokenParser implements TokenParser {

	private final String openToken;
	private final String closeToken;

	public PropertyTokenParser(String openToken, String closeToken) {
		Assert.hasNoText(openToken, "Open token string is required.");
		Assert.hasNoText(closeToken, "Close token string is required.");
		this.openToken = openToken;
		this.closeToken = closeToken;
	}

	public ParsedSql parse(String text) {
		Assert.hasNoText(text, "Can't parse empty string.");
		ParsedSql parsedSql = new ParsedSql();
		StringBuilder builder = parsedSql.getOriginalSql();
		char[] src = text.toCharArray();
		int offset = 0;
		int start = text.indexOf(openToken, offset);
		while (start > -1) {
			if (start > 0 && src[start - 1] == '\\') {
				builder.append(src, offset, start - offset - 1).append(openToken);
				offset = start + openToken.length();
			} else {
				int end = text.indexOf(closeToken, start);
				if (end == -1) {
					builder.append(src, offset, src.length - offset);
					offset = src.length;
				} else {
					builder.append(src, offset, start - offset);
					offset = start + openToken.length();
					String content = new String(src, offset, end - offset);
					parsedSql.getParameterNames().add(content);
					builder.append("?");
					offset = end + closeToken.length();
				}
			}
			start = text.indexOf(openToken, offset);
		}
		if (offset < src.length) {
			builder.append(src, offset, src.length - offset);
		}
		return parsedSql;
	}

}
