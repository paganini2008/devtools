/**
* Copyright 2021 Fred Feng (paganini.fy@gmail.com)

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
 * FileSearchUtils
 *
 * @author Fred Feng
 * @since 1.0
 */
public abstract class FileSearchUtils {

	public static File[] search(File directory, DirectoryFilter fileInfoFilter, int nThreads, int maxDepth) {
		List<File> files = new ArrayList<File>();
		ForkJoinDirectoryWalker directoryWalker = new ForkJoinDirectoryWalker(directory, new DirectoryWalkerHandler() {

			@Override
			public void handleDirectoryEnd(File file, Directory directory, int depth) throws IOException {
				if (fileInfoFilter.accept(directory)) {
					files.add(file);
				}
			}

			@Override
			public void handleFile(File file, int depth) throws Exception {
				if (fileInfoFilter.accept(file)) {
					files.add(file);
				}
			}
		});
		directoryWalker.setThreadCount(nThreads);
		directoryWalker.setMaxDepth(maxDepth);
		directoryWalker.walk();
		return files.toArray(new File[0]);
	}

	public static void main(String[] args) {
		File directory = new File("d:/sql");
		File[] files = FileSearchUtils.search(directory, new DirectoryFilter() {
			@Override
			public boolean accept(Directory fileInfo) {
				if (fileInfo.getLength() > 50 * FileUtils.MB) {
					return true;
				}
				return false;
			}
		}, 8, 5);
		for (File file : files) {
			System.out.println(file);
		}
	}

}
