package com.github.paganini2008.devtools.io.comparator;

import java.io.File;

import com.github.paganini2008.devtools.comparator.ComparatorHelper;

/**
 * FileLastModifiedComparator
 * 
 * @author Jimmy Hoff
 * @version 1.0
 */
public class FileLastModifiedComparator extends FileComparator {

	protected int continueCompare(File left, File right) {
		return ComparatorHelper.valueOf(left.lastModified() - right.lastModified());
	}

}
