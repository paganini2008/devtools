package com.github.paganini2008.devtools.collection;

import com.github.paganini2008.devtools.StringUtils;

/**
 * MatchMode
 * 
 * @author Fred Feng
 * @version 1.0
 */
public enum MatchMode {

	START {
		public boolean matches(String name, String substr) {
			return StringUtils.isNotBlank(name) && name.startsWith(substr);
		}
	},
	END {
		public boolean matches(String name, String substr) {
			return StringUtils.isNotBlank(name) && name.endsWith(substr);
		}
	},
	ANY_WHERE {
		public boolean matches(String name, String substr) {
			return StringUtils.isNotBlank(name) && name.contains(substr);
		}
	},

	REGEX {
		public boolean matches(String name, String substr) {
			return StringUtils.isNotBlank(name) && name.matches(substr);
		}
	};

	public abstract boolean matches(String name, String substr);
}
