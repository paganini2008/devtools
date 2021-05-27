package com.github.paganini2008.devtools;

/**
 * MatchMode
 * 
 * @author Fred Feng
 * @version 1.0
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
			return StringUtils.isNotBlank(pattern) && pattern.matches(substr);
		}
	};

	public abstract boolean matches(String pattern, String substr);
}
