package com.github.paganini2008.devtools;

import java.util.function.Function;

/**
 * 
 * PrefixationTokenizer
 *
 * @author Fred Feng
 * @revised 2019-07
 * @created 2019-03
 * @version 1.0
 */
public class PrefixationTokenizer implements Tokenizer<String, String> {

	public PrefixationTokenizer(String token) {
		this.token = token;
	}

	private final String token;

	@Override
	public String parse(String text, Function<String, Object> function) {
		int offset = 0;
		int start = text.indexOf(token, offset);
		if (start == -1) {
			return text;
		}
		StringBuilder builder = new StringBuilder();
		final char[] src = text.toCharArray();
		StringBuilder variable = null;
		while (start > -1) {
			if (start > 0 && src[start - 1] == '\\') {
				builder.append(src, offset, start - offset - 1).append(token);
				offset = start + token.length();
			} else {
				builder.append(src, offset, start - offset);
				int i = start + token.length();
				while ((i < src.length) && (acceptCharacter(src[i]))) {
					if (variable == null) {
						variable = new StringBuilder();
					}
					variable.append(src[i]);
					i++;
				}
				if (StringUtils.isNotBlank(variable)) {
					builder.append(function.apply(variable.toString()));
					offset = start + token.length() + variable.length();
				} else {
					offset = start + token.length();
				}
			}
			if (variable != null) {
				variable.delete(0, variable.length());
			}
			start = text.indexOf(token, offset);
		}
		if (offset < src.length) {
			builder.append(src, offset, src.length - offset);
		}
		return builder.toString();
	}

	protected boolean acceptCharacter(char ch) {
		return Character.isLetterOrDigit(ch);
	}

}