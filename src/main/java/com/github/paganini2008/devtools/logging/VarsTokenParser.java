/**
* Copyright 2017-2026 Fred Feng (paganini.fy@gmail.com)

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
package com.github.paganini2008.devtools.logging;

import com.github.paganini2008.devtools.StringUtils;

/**
 * A {@link TokenParser} which replaces each occurrence of a token (e.g. <code>{}</code>) with the next argument. A token followed by digits (e.g. <code>{}1</code>) refers to the argument at that index, and an escaped token (<code>\{}</code>) is kept literally.
 * 
 * @author Fred Feng
 * @date 2019-05-29
 * @since 2.0.1
 */
public class VarsTokenParser implements TokenParser {

	public VarsTokenParser(String token) {
		this.token = token;
	}

	private final String token;

	public String parse(String text, Object... args) {
		if (StringUtils.isBlank(text) || StringUtils.isEmpty(token) || args == null || args.length == 0) {
			return text;
		}
		final int n = token.length();
		final StringBuilder builder = new StringBuilder(text.length() + 16);
		int offset = 0, next = 0, start;
		while ((start = text.indexOf(token, offset)) != -1) {
			if (start > 0 && text.charAt(start - 1) == '\\') {
				builder.append(text, offset, start - 1).append(token);
				offset = start + n;
				continue;
			}
			builder.append(text, offset, start);
			int i = start + n;
			while (i < text.length() && i - start - n < 9 && Character.isDigit(text.charAt(i))) {
				i++;
			}
			if (i > start + n) {
				int index = Integer.parseInt(text.substring(start + n, i));
				builder.append(index < args.length ? String.valueOf(args[index]) : text.substring(start, i));
			} else if (next < args.length) {
				builder.append(args[next++]);
			} else {
				builder.append(token);
			}
			offset = i;
		}
		builder.append(text, offset, text.length());
		return builder.toString();
	}
}
