package com.github.paganini2008.devtools.io.filter;

import java.io.File;

import com.github.paganini2008.devtools.io.PathUtils;

/**
 * ExtensionFileFilter
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class ExtensionFileFilter extends FileFileFilter {

	public ExtensionFileFilter(String extension) {
		this(extension, true);
	}

	public ExtensionFileFilter(String extension, boolean ignoreCase) {
		this.extension = extension;
		this.ignoreCase = ignoreCase;
	}

	private final String extension;
	private final boolean ignoreCase;

	public boolean accept(File file, String name) {
		if (super.accept(file)) {
			String ext = PathUtils.getExtension(name);
			return ignoreCase ? extension.equalsIgnoreCase(ext) : extension.equals(ext);
		}
		return false;
	}

}
