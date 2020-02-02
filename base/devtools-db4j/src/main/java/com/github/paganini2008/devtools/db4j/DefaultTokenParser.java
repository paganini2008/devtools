package com.github.paganini2008.devtools.db4j;

import com.github.paganini2008.devtools.Assert;

/**
 * DefaultTokenParser
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class DefaultTokenParser implements TokenParser {

	private final String token;

	public DefaultTokenParser(String token) {
		Assert.hasNoText(token, "Token string is required.");
		this.token = token;
	}

	public ParsedSql parse(String text) {
		Assert.hasNoText(text, "Can't parse empty string.");
		ParsedSql parsedSql = new ParsedSql();
		StringBuilder builder = parsedSql.getOriginalSql();
		char[] src = text.toCharArray();
		int offset = 0;
		int start = text.indexOf(token, offset);
		while (start > -1) {
			if (start > 0 && src[start - 1] == '\\') {
				builder.append(src, offset, start - offset - 1).append(token);
				offset = start + token.length();
			} else {
				int i = start + token.length();
				StringBuilder variable = new StringBuilder();
				while ((i < src.length) && (src[i] == '_' || Character.isLetterOrDigit(src[i]))) {
					variable.append(src[i]);
					i++;
				}
				if (variable.length() == 0) {
					throw new IllegalArgumentException("Bad string format. Index: " + start);
				}
				builder.append(src, offset, start - offset);
				String key = variable.toString();
				parsedSql.getParameterNames().add(key);
				builder.append("?");
				offset = start + token.length() + variable.length();
			}
			start = text.indexOf(token, offset);
		}
		if (offset < src.length) {
			builder.append(src, offset, src.length - offset);
		}
		return parsedSql;
	}

}
