/**
* Copyright 2017-2021 Fred Feng (paganini.fy@gmail.com)

* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.github.paganini2008.devtools;

import java.util.function.Function;

/**
 * 
 * PrefixationTokenParser
 *
 * @author Fred Feng
 * @since 2.0.1
 */
public class PrefixationTokenParser implements TokenParser<String, String> {

	public PrefixationTokenParser(String token) {
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
