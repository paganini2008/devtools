package com.github.paganini2008.devtools.io.comparator;

import java.io.File;

/**
 * 
 * FileSorter
 *
 * @author Fred Feng
 * @version 2.0.5
 */
public abstract class FileSorter {

	private static final FileComparator DEFAULT = new FileComparator();
	private static final FileComparator NAME_COMPARATOR = new FileNameComparator();
	private static final FileComparator CHILDREN_COUNT_COMPARATOR = new FileChildrenCountComparator();
	private static final FileComparator SIZE_COMPARATOR = new FileSizeComparator();
	private static final FileComparator EXTENSION_COMPARATOR = new FileExtensionComparator();
	private static final FileComparator LENGTH_COMPARATOR = new FileLengthComparator();
	private static final FileComparator LAST_MODIFIED_COMPARATOR = new FileLastModifiedComparator();
	private static final FileComparator PATH_COMPARATOR = new FilePathComparator();

	public static File[] sort(File[] files) {
		return DEFAULT.sort(files);
	}

	public static File[] sortByName(File[] files) {
		return NAME_COMPARATOR.sort(files);
	}

	public static File[] sortByChildrenCount(File[] files) {
		return CHILDREN_COUNT_COMPARATOR.sort(files);
	}

	public static File[] sortBySize(File[] files) {
		return SIZE_COMPARATOR.sort(files);
	}

	public static File[] sortByExtension(File[] files) {
		return EXTENSION_COMPARATOR.sort(files);
	}

	public static File[] sortByLength(File[] files) {
		return LENGTH_COMPARATOR.sort(files);
	}

	public static File[] sortByLastModified(File[] files) {
		return LAST_MODIFIED_COMPARATOR.sort(files);
	}

	public static File[] sortByPath(File[] files) {
		return PATH_COMPARATOR.sort(files);
	}
}
