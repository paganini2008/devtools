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
package com.github.paganini2008.devtools.regex;

import java.util.Enumeration;

import com.github.paganini2008.devtools.Assert;
import com.github.paganini2008.devtools.StringUtils;

/**
 * 
 * RegexTokenizer
 * 
 * @author Fred Feng
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

	public RegexTokenizer(CharSequence content, String regex, boolean returnDelim) {
		this(content, new String[] { regex }, returnDelim);
	}

	public RegexTokenizer(CharSequence content, String[] regexes, boolean returnDelim) {
		Assert.hasNoText(content, "Content must not be null or empty.");
		this.value = content.toString();
		this.maxPosition = content.length();
		this.regexes = regexes;
		this.returnDelims = returnDelim;
	}

	public void setFlags(int flags) {
		this.flags = flags;
	}

	private int locate(int fromIndex) {
		String m;
		int i;
		int j = -1;
		for (String regex : regexes) {
			if (StringUtils.isNotBlank(m = RegexUtils.match(value, regex, flags, fromIndex))) {
				i = value.indexOf(m, fromIndex);
				if (j == -1 || i < j) {
					this.delim = m;
					j = i;
				}
			}
		}

		offset = this.delim != null ? this.delim.length() : 0;
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
		while (test() && !even) {
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

	public String getLastElement() {
		return last;
	}

}
