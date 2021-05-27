package com.github.paganini2008.devtools.io.filter;

import java.io.File;

import com.github.paganini2008.devtools.io.PathUtils;

/**
 * NameFileFilter
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class NameFileFilter extends LogicalFileFilter {

	public NameFileFilter(String str, boolean ignoreCase) {
		this.str = str;
		this.ignoreCase = ignoreCase;
	}

	private final String str;
	private final boolean ignoreCase;

	public boolean accept(File file, String name) {
		String base = PathUtils.getBaseName(name);
		return ignoreCase ? base.equalsIgnoreCase(str) : base.equals(str);
	}

}
