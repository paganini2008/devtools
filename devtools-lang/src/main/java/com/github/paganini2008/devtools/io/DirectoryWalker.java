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

/**
 * 
 * DirectoryWalker
 *
 * @author Fred Feng
 * @since 1.0
 */
public interface DirectoryWalker {

	void setThreadCount(int threadCount);

	void setProgressable(Progressable progressable);

	Directory walk() throws IOException;

	/**
	 * 
	 * FileInfo
	 *
	 * @author Fred Feng
	 * @since 1.0
	 */
	interface FileInfo extends Directory {

		FileInfo newChildFileInfo(File childFile);

		void process(File file);

		void done();

		long getElapsed();

		boolean isDone();
	}

	/**
	 * 
	 * Progressable
	 *
	 * @author Fred Feng
	 * @since 1.0
	 */
	interface Progressable {

		void progress(int fileCount, int folderCount, long length, float completedRatio, long elapsed);

		default int refreshInterval() {
			return 1;
		}

	}

	/**
	 * 
	 * ProgressBar
	 *
	 * @author Fred Feng
	 * @since 1.0
	 */
	interface ProgressBar {

		void processBegin(File file);

		void processEnd(File file);

		int getFileCount();

		int getFolderCount();

		long getLength();

		float getCompletionRate();

		long getElapsed();

		void waitForTermination();

		void cancel();

		boolean isCancelled();

		boolean isDone();

	}

}