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
package com.github.paganini2008.devtools.regex;

import java.util.Enumeration;
import java.util.regex.Matcher;

import com.github.paganini2008.devtools.Assert;

/**
 * 
 * RegexTokenizer
 * 
 * Splits a string into tokens separated by matches of one or more regexes (the earliest match wins).
 * Empty tokens are skipped and zero-length matches are ignored; delimiters can optionally be
 * returned as tokens.
 * 
 * @author Fred Feng
 * @date 2019-05-29
 * @since 2.0.1
 */
public class RegexTokenizer implements Enumeration<String> {

	private final String value;
	private final String[] regexes;
	private final int maxPosition;
	private final boolean returnDelims;
	private int startPosition = 0;
	private int endPosition = -1;
	private int offset;
	private String last;
	private String delim;
	private boolean empty;
	private boolean even;
	private int flags = 0;

	/**
	 * Creates a tokenizer with a single delimiter regex.
	 *
	 * @throws IllegalArgumentException if the content is null
	 */
	public RegexTokenizer(CharSequence content, String regex, boolean returnDelim) {
		this(content, new String[] { regex }, returnDelim);
	}

	/**
	 * Creates a tokenizer with several delimiter regexes.
	 *
	 * @throws IllegalArgumentException if the content is null
	 */
	public RegexTokenizer(CharSequence content, String[] regexes, boolean returnDelim) {
		Assert.isNull(content, "Content must not be null.");
		this.value = content.toString();
		this.maxPosition = content.length();
		this.regexes = regexes != null ? regexes : new String[0];
		this.returnDelims = returnDelim;
	}

	/**
	 * Sets the {@link java.util.regex.Pattern} flags; call before tokenizing.
	 */
	public void setFlags(int flags) {
		this.flags = flags;
	}

	private int locate(int fromIndex) {
		int j = -1;
		String found = null;
		for (String regex : regexes) {
			Matcher matcher = RegexUtils.getPattern(regex, flags).matcher(value);
			int from = fromIndex;
			while (from <= maxPosition && matcher.find(from)) {
				if (matcher.end() > matcher.start()) {
					if (j == -1 || matcher.start() < j) {
						found = matcher.group();
						j = matcher.start();
					}
					break;
				}
				from = matcher.start() + 1;
			}
		}
		this.delim = found;
		this.offset = found != null ? found.length() : 0;
		if (j == -1) {
			j = maxPosition;
		}
		return j;
	}

	private boolean test() {
		endPosition = locate(startPosition);
		empty = startPosition == endPosition;
		if (empty && returnDelims) {
			even = true;
		} else {
			even = false;
		}
		return empty;
	}

	public boolean hasMoreElements() {
		if (startPosition >= maxPosition) {
			if (returnDelims) {
				return even && offset > 0;
			} else {
				return false;
			}
		}
		if (returnDelims && even) {
			return true;
		}
		while (startPosition < maxPosition && test() && !even) {
			startPosition = endPosition + offset;
		}
		return startPosition < maxPosition;
	}

	public String nextElement() {
		if (returnDelims && even) {
			last = delim;
			delim = null;
			even = false;
			if (empty) {
				startPosition = endPosition + offset;
			}
		} else {
			last = value.substring(startPosition, endPosition);
			startPosition = endPosition + offset;
			even = true;
		}
		return last;
	}

	/**
	 * Returns the token returned by the last call of {@link #nextElement()}, or null.
	 */
	public String getLastElement() {
		return last;
	}

}
