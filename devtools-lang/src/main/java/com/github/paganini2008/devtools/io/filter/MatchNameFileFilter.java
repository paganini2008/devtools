package com.github.paganini2008.devtools.io.filter;

import java.io.File;

import com.github.paganini2008.devtools.io.PathUtils;

/**
 * MatchNameFileFilter
 * 
 * @author Jimmy Hoff
 * @version 1.0
 */
public class MatchNameFileFilter extends LogicalFileFilter {

	public static enum MatchMode {

		START {
			boolean matches(String left, String right) {
				return left.startsWith(right);
			}
		},
		END {
			boolean matches(String left, String right) {
				return left.endsWith(right);
			}
		},
		ANY_WHERE {
			boolean matches(String left, String right) {
				return left.contains(right);
			}
		},

		REGEX {
			boolean matches(String left, String right) {
				return left.matches(right);
			}
		};

		abstract boolean matches(String left, String right);
	}

	private final String substr;
	private final MatchMode matchMode;

	public MatchNameFileFilter(String substr, MatchMode matchMode) {
		this.substr = substr;
		this.matchMode = matchMode;
	}

	public boolean accept(File file, String name) {
		String baseName = PathUtils.getBaseName(name);
		return matchMode.matches(baseName, substr);
	}

}