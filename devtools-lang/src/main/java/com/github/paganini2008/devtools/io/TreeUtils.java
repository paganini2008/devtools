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
package com.github.paganini2008.devtools.io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import com.github.paganini2008.devtools.CharsetUtils;

/**
 * 
 * TreeUtils
 *
 * @author Fred Feng
 * @version 2.0.5
 */
public abstract class TreeUtils {

	public static String[] scan(File directory, int maxDepth) throws IOException {
		return scan(directory, maxDepth, false, true);
	}

	public static String[] scan(File directory, int maxDepth, boolean hidden, boolean directoryOnly) throws IOException {
		return scan(directory, maxDepth, hidden, directoryOnly ? new DirectoryTreeMatcher() : new DefaultTreeMatcher());
	}

	public static String[] scan(File directory, int maxDepth, boolean hidden, TreeMatcher treeMatcher) throws IOException {
		List<String> list = new ArrayList<String>();
		FileUtils.scan(directory, null, new ScanFilter() {

			@Override
			public boolean filterDirectory(File directory, int depth) throws IOException {
				if (depth <= maxDepth && (hidden || directory.isHidden() == hidden)
						&& treeMatcher.matchDirectory(directory, depth, depth == maxDepth)) {
					list.add(treeMatcher.getText(directory, depth, depth == maxDepth));
					return true;
				}
				return false;
			}

			@Override
			public void filterFile(File directory, int depth, File file) throws IOException {
				if (depth <= maxDepth && (hidden || directory.isHidden() == hidden)
						&& treeMatcher.matchFile(directory, depth, file, depth == maxDepth)) {
					list.add(treeMatcher.getText(directory, depth, file, depth == maxDepth));
				}
			}
		});
		return list.toArray(new String[0]);
	}

	public static void scanAndSave(File directory, int maxDepth, OutputStream output) throws IOException {
		scanAndSave(directory, maxDepth, false, true, output);
	}

	public static void scanAndSave(File directory, int maxDepth, File file) throws IOException {
		try (FileOutputStream output = FileUtils.openOutputStream(file)) {
			scanAndSave(directory, maxDepth, output);
		}
	}

	public static void scanAndSave(File directory, int maxDepth, boolean hidden, boolean directoryOnly, OutputStream output)
			throws IOException {
		String[] lines = scan(directory, maxDepth, hidden, directoryOnly);
		IOUtils.copy(lines, output, CharsetUtils.DEFAULT);
	}

	public static void scanAndSave(File directory, int maxDepth, boolean hidden, boolean directoryOnly, File file) throws IOException {
		try (FileOutputStream output = FileUtils.openOutputStream(file)) {
			scanAndSave(directory, maxDepth, hidden, directoryOnly, output);
		}
	}

	public static void scanAndSave(File directory, int maxDepth, boolean hidden, TreeMatcher treeMatcher, OutputStream output)
			throws IOException {
		String[] lines = scan(directory, maxDepth, hidden, treeMatcher);
		IOUtils.copy(lines, output, CharsetUtils.DEFAULT);
	}

	public static void scanAndSave(File directory, int maxDepth, boolean hidden, TreeMatcher treeMatcher, File file) throws IOException {
		try (FileOutputStream output = FileUtils.openOutputStream(file)) {
			scanAndSave(directory, maxDepth, hidden, treeMatcher, output);
		}
	}

}
