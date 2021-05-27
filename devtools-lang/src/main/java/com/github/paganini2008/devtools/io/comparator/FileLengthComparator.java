package com.github.paganini2008.devtools.io.comparator;

import java.io.File;

import com.github.paganini2008.devtools.comparator.ComparatorHelper;

/**
 * FileLengthComparator
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class FileLengthComparator extends FileComparator {

	protected int continueCompare(File left, File right) {
		return ComparatorHelper.valueOf(left.length() - right.length());
	}

}
