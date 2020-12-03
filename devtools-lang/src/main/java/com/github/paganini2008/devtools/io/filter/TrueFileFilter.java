package com.github.paganini2008.devtools.io.filter;

import java.io.File;

/**
 * TrueFileFilter
 * 
 * @author Jimmy Hoff
 * @version 1.0
 */
public class TrueFileFilter extends LogicalFileFilter {
	
	public static final FileFilter INSTANCE = new TrueFileFilter();

	TrueFileFilter() {
	}

	public boolean accept(File file) {
		return file != null;
	}

}
