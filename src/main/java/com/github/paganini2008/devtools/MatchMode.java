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
package com.github.paganini2008.devtools;

/**
 * Strategies for matching a string against a substring, regex or wildcard
 * pattern.
 * 
 * @author Fred Feng
 * @date 2019-05-29
 * @since 2.0.1
 */
public enum MatchMode {

	START {
		public boolean matches(String pattern, String substr) {
			return StringUtils.isNotBlank(pattern) && pattern.startsWith(substr);
		}
	},

	END {
		public boolean matches(String pattern, String substr) {
			return StringUtils.isNotBlank(pattern) && pattern.endsWith(substr);
		}
	},

	ANY_WHERE {
		public boolean matches(String pattern, String substr) {
			return StringUtils.isNotBlank(pattern) && pattern.contains(substr);
		}
	},

	REGEX {
		public boolean matches(String pattern, String substr) {
			return StringUtils.isNotBlank(pattern) && (pattern.equals(substr) || pattern.matches(substr));
		}
	},

	WILDCARD {
		public boolean matches(String pattern, String substr) {
			return StringUtils.isNotBlank(pattern) && StringUtils.matchesWildcard(substr, pattern);
		}
	};

	/**
	 * Tests the string against the substring or pattern. A blank string never
	 * matches.
	 *
	 * <pre>
	 * MatchMode.START.matches("abc", "ab")    = true
	 * MatchMode.REGEX.matches("abc", "a.c")   = true
	 * MatchMode.WILDCARD.matches("abc", "a*") = true
	 * </pre>
	 *
	 * @param pattern the string to test (despite its name)
	 * @param substr the substring, regex or wildcard pattern
	 */
	public abstract boolean matches(String pattern, String substr);
}
