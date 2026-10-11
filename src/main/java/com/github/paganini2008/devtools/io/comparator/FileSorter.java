/**
* Copyright 2017-2026 Fred Feng (paganini.fy@gmail.com)

* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.github.paganini2008.devtools.io.comparator;

import java.io.File;

/**
 * Static helpers to sort file arrays in place with the comparators of this package.
 *
 * @author Fred Feng
 * @date 2022-04-24
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

	/**
	 * Sorts the files in place so that directories come first.
	 *
	 * @return the same array, <code>null</code> if <code>files</code> is <code>null</code>
	 */
	public static File[] sort(File[] files) {
		return DEFAULT.sort(files);
	}

	/**
	 * Sorts the files in place, directories first, then by base name (case insensitive).
	 */
	public static File[] sortByName(File[] files) {
		return NAME_COMPARATOR.sort(files);
	}

	/**
	 * Sorts the files in place, directories first, then by number of child entries.
	 */
	public static File[] sortByChildrenCount(File[] files) {
		return CHILDREN_COUNT_COMPARATOR.sort(files);
	}

	/**
	 * Sorts the files in place, directories first, then by size (the total length of the files for a directory).
	 */
	public static File[] sortBySize(File[] files) {
		return SIZE_COMPARATOR.sort(files);
	}

	/**
	 * Sorts the files in place, directories first, then by extension (case insensitive).
	 */
	public static File[] sortByExtension(File[] files) {
		return EXTENSION_COMPARATOR.sort(files);
	}

	/**
	 * Sorts the files in place, directories first, then by length.
	 */
	public static File[] sortByLength(File[] files) {
		return LENGTH_COMPARATOR.sort(files);
	}

	/**
	 * Sorts the files in place, directories first, then by last modified time.
	 */
	public static File[] sortByLastModified(File[] files) {
		return LAST_MODIFIED_COMPARATOR.sort(files);
	}

	/**
	 * Sorts the files in place, directories first, then by absolute path (case insensitive).
	 */
	public static File[] sortByPath(File[] files) {
		return PATH_COMPARATOR.sort(files);
	}
}
