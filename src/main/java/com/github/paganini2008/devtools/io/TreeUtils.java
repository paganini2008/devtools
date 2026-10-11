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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import com.github.paganini2008.devtools.CharsetUtils;

/**
 * 
 * Renders a directory tree as text lines, e.g. for printing the structure of a project.
 *
 * @author Fred Feng
 * @date 2022-04-24
 * @version 2.0.5
 */
public abstract class TreeUtils {

	/**
	 * Renders the visible sub directories of the directory up to {@code maxDepth} (direct children have depth 0).
	 *
	 * <pre>
	 * TreeUtils.scan(root, 1) = { "|--- sub" + NEWLINE, "|    |--- deep" + NEWLINE }
	 * </pre>
	 */
	public static String[] scan(File directory, int maxDepth) throws IOException {
		return scan(directory, maxDepth, false, true);
	}

	/**
	 * Renders the directory tree up to {@code maxDepth}, optionally including hidden entries and files.
	 */
	public static String[] scan(File directory, int maxDepth, boolean hidden, boolean directoryOnly) throws IOException {
		return scan(directory, maxDepth, hidden, directoryOnly ? new DirectoryTreeMatcher() : new DefaultTreeMatcher());
	}

	/**
	 * Renders the directory tree up to {@code maxDepth} with the given matcher; hidden entries are skipped unless
	 * {@code hidden} is {@code true}.
	 */
	public static String[] scan(File directory, int maxDepth, boolean hidden, TreeMatcher treeMatcher) throws IOException {
		List<String> list = new ArrayList<String>();
		FileUtils.scan(directory, null, new ScanFilter() {

			@Override
			public boolean filterDirectory(File directory, int depth) throws IOException {
				if (depth <= maxDepth && (hidden || !directory.isHidden())
						&& treeMatcher.matchDirectory(directory, depth, depth == maxDepth)) {
					list.add(treeMatcher.getText(directory, depth, depth == maxDepth));
					return true;
				}
				return false;
			}

			@Override
			public void filterFile(File directory, int depth, File file) throws IOException {
				if (depth <= maxDepth && (hidden || !file.isHidden())
						&& treeMatcher.matchFile(directory, depth, file, depth == maxDepth)) {
					list.add(treeMatcher.getText(directory, depth, file, depth == maxDepth));
				}
			}
		});
		return list.toArray(new String[0]);
	}

	/**
	 * Writes the rendered directory tree to the stream using the platform charset. The stream is not closed.
	 */
	public static void scanAndSave(File directory, int maxDepth, OutputStream output) throws IOException {
		scanAndSave(directory, maxDepth, false, true, output);
	}

	/**
	 * Writes the rendered directory tree to the file.
	 */
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
