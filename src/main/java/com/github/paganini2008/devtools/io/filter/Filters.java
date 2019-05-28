package com.github.paganini2008.devtools.io.filter;

import com.github.paganini2008.devtools.io.SizeUnit;
import com.github.paganini2008.devtools.io.filter.MatchNameFileFilter.MatchMode;

/**
 * 
 * Filters
 * 
 * @author Fred Feng
 * @revised 2019-05
 * @version 1.0
 */
public class Filters {

	public static LogicalFileFilter findByName(String name, boolean ignoreCase) {
		return new NameFileFilter(name, ignoreCase);
	}

	public static LogicalFileFilter findByName(String name, MatchMode matchMode) {
		return new MatchNameFileFilter(name, matchMode);
	}

	public static LogicalFileFilter findByLength(long length, SizeUnit sizeUnit, Operator operator) {
		return new LengthFileFilter(length, sizeUnit, operator);
	}

	public static LogicalFileFilter findByExtension(String extension, boolean ignoreCase) {
		return new ExtensionFileFilter(extension, ignoreCase);
	}

	public static LogicalFileFilter findByLastModified(long lastModified, Operator operator) {
		return new LastModifiedFileFilter(lastModified, operator);
	}

	public static LogicalFileFilter hasSubfolders(int size, Operator operator) {
		return new ChildFileSizeFileFilter(size, operator);
	}

	public static LogicalFileFilter isFile() {
		return new FileFileFilter();
	}

	public static LogicalFileFilter isDirectory() {
		return new DirectoryFileFilter();
	}

	private Filters() {
	}

}
