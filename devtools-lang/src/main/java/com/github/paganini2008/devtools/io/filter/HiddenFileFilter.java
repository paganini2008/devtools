package com.github.paganini2008.devtools.io.filter;

import java.io.File;

/**
 * HiddenFileFilter
 * 
 * @author Jimmy Hoff
 * @version 1.0
 */
public class HiddenFileFilter extends LogicalFileFilter {

	public boolean accept(File file) {
		return file.isHidden();
	}

}
