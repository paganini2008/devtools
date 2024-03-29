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

/**
 * MatchMode
 * 
 * @author Fred Feng
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
			return StringUtils.isNotBlank(pattern) && StringUtils.matchesWildcard(pattern, substr);
		}
	};

	public abstract boolean matches(String pattern, String substr);
}
