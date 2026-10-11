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
package com.github.paganini2008.devtools.io.filter;

import com.github.paganini2008.devtools.MatchMode;
import com.github.paganini2008.devtools.io.SizeUnit;

/**
 * Factory methods for the common file filters of this package.
 * 
 * @author Fred Feng
 * @date 2019-05-29
 * 
 * @since 2.0.1
 */
public abstract class Filters {

	/**
	 * Creates a filter matching files whose base name (without extension) equals the given name.
	 */
	public static LogicalFileFilter findByName(String name, boolean ignoreCase) {
		return new NameFileFilter(name, ignoreCase);
	}

	/**
	 * Creates a filter matching the base name (without extension) of files with the given match mode.
	 */
	public static LogicalFileFilter findByName(String name, MatchMode matchMode) {
		return new MatchNameFileFilter(name, matchMode);
	}

	/**
	 * Creates a filter comparing {@link java.io.File#length()} with <code>length</code> in the given unit.
	 */
	public static LogicalFileFilter findByLength(long length, SizeUnit sizeUnit, Operator operator) {
		return new LengthFileFilter(length, sizeUnit, operator);
	}

	/**
	 * Creates a filter matching regular files by extension, e.g. <code>"txt"</code> or <code>".txt"</code>.
	 */
	public static LogicalFileFilter findByExtension(String extension, boolean ignoreCase) {
		return new ExtensionFileFilter(extension, ignoreCase);
	}

	/**
	 * Creates a filter comparing the last modified time of files with the given timestamp in milliseconds.
	 */
	public static LogicalFileFilter findByLastModified(long lastModified, Operator operator) {
		return new LastModifiedFileFilter(lastModified, operator);
	}

	/**
	 * Creates a filter comparing the number of child entries (files and directories) of a directory with <code>size</code>.
	 */
	public static LogicalFileFilter hasSubfolders(int size, Operator operator) {
		return new FileSizeFileFilter(size, operator);
	}

	/**
	 * Creates a filter accepting regular files only.
	 */
	public static LogicalFileFilter isFile() {
		return new FileFileFilter();
	}

	/**
	 * Creates a filter accepting directories only.
	 */
	public static LogicalFileFilter isDirectory() {
		return new DirectoryFileFilter();
	}

	/**
	 * Creates a filter accepting hidden files only.
	 */
	public static LogicalFileFilter isHidden() {
		return new HiddenFileFilter();
	}

}
