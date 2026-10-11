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
package com.github.paganini2008.devtools.io;

import java.io.File;

/**
 * 
 * Statistics of a directory produced by a {@link DirectoryWalker}: its file and folder counts, total length and child
 * directories.
 *
 * @author Fred Feng
 * @date 2020-05-09
 * @since 2.0.1
 */
public interface Directory {

	File getFile();

	Directory[] getChildren();

	/**
	 * Returns the total length of all files in this directory tree.
	 */
	long getLength();

	/**
	 * Returns the latest modification time of this directory and its sub directories.
	 */
	long getLastModified();

	/**
	 * Returns the number of files directly in this directory, or in the whole tree if {@code recursive}.
	 */
	int getFileCount(boolean recursive);

	/**
	 * Returns the number of sub directories directly in this directory, or in the whole tree if {@code recursive}.
	 */
	int getFolderCount(boolean recursive);
	
	/**
	 * Returns the parent directory statistics, or {@code null} for the root.
	 */
	Directory getParent();

}
