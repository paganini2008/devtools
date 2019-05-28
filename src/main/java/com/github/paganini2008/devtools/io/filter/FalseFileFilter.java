package com.github.paganini2008.devtools.io.filter;

import java.io.File;

/**
 * FalseFileFilter
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class FalseFileFilter extends LogicalFileFilter {
	
	public static final FileFilter INSTANCE = new FileFileFilter();

	FalseFileFilter() {
	}

	public boolean accept(File file) {
		return false;
	}

}
