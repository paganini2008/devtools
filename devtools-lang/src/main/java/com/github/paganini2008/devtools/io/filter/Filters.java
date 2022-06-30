/**
* Copyright 2017-2022 Fred Feng (paganini.fy@gmail.com)

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
 * 
 * Filters
 * 
 * @author Fred Feng
 * 
 * @since 2.0.1
 */
public abstract class Filters {

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
		return new FileSizeFileFilter(size, operator);
	}

	public static LogicalFileFilter isFile() {
		return new FileFileFilter();
	}

	public static LogicalFileFilter isDirectory() {
		return new DirectoryFileFilter();
	}

	public static LogicalFileFilter isHidden() {
		return new HiddenFileFilter();
	}

}
