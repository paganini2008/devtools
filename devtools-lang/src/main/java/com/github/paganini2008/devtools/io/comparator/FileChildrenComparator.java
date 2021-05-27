package com.github.paganini2008.devtools.io.comparator;

import java.io.File;

import com.github.paganini2008.devtools.comparator.ComparatorHelper;

/**
 * FileChildrenComparator
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class FileChildrenComparator extends FileComparator {

	protected int continueCompare(File left, File right) {
		String[] names = left.list();
		int leftLen = names != null ? names.length : 0;
		names = right.list();
		int rightLen = names != null ? names.length : 0;
		return ComparatorHelper.valueOf(leftLen - rightLen);
	}

}
