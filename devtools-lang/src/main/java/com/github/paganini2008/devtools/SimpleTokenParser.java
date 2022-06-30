/**
* Copyright 2017-2022 Fred Feng (paganini.fy@gmail.com)

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
 * SimpleTokenParser
 *
 * @author Fred Feng
 * @since 2.0.1
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
