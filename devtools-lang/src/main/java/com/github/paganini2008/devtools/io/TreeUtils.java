/**
* Copyright 2017-2021 Fred Feng (paganini.fy@gmail.com)

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
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * TreeUtils
 *
 * @author Fred Feng
 * @version 2.0.5
 */
public abstract class TreeUtils {

	public static String[] scan(File directory, int maxDepth) throws IOException {
		return scan(directory, maxDepth, true);
	}

	public static String[] scan(File directory, int maxDepth, boolean directoryOnly) throws IOException {
		return scan(directory, maxDepth, directoryOnly ? new DirectoryTreeFilter() : new DefaultTreeFilter());
	}

	private static String[] scan(File directory, int maxDepth, TreeMatcher treeMatcher) throws IOException {
		List<String> list = new ArrayList<String>();
		FileUtils.scan(directory, null, new ScanFilter() {

			@Override
			public boolean filterDirectory(File directory, int depth) throws IOException {
				if (depth <= maxDepth && treeMatcher.matchDirectory(directory, depth)) {
					list.add(treeMatcher.getText(directory, depth));
					return true;
				}
				return false;
			}

			@Override
			public void filterFile(File directory, int depth, File file) throws IOException {
				if (depth <= maxDepth && treeMatcher.matchFile(directory, depth, file)) {
					list.add(treeMatcher.getText(directory, depth, file));
				}
			}
		});
		return list.toArray(new String[0]);
	}

}
