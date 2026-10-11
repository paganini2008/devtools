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
import java.io.IOException;

/**
 * 
 * Walks a directory tree, typically with several threads, and returns the statistics of the walked tree.
 *
 * @author Fred Feng
 * @date 2019-05-29
 * @since 2.0.1
 */
public interface DirectoryWalker {

	/**
	 * Sets the number of threads used for walking (8 by default).
	 */
	void setThreadCount(int threadCount);

	/**
	 * Sets a listener notified periodically and at the end of the walk.
	 */
	void setProgressable(Progressable progressable);

	/**
	 * Walks the directory tree and blocks until the walk is finished or cancelled.
	 *
	 * @return the statistics of the root directory
	 */
	Directory walk() throws IOException;

	/**
	 * 
	 * Mutable statistics of a directory, filled while walking.
	 *
	 * @author Fred Feng
	 * @since 2.0.1
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
	 * Listener receiving the progress of a walk.
	 *
	 * @author Fred Feng
	 * @since 2.0.1
	 */
	interface Progressable {

		/**
		 * Reports the progress of a walk; {@code completedRatio} is the ratio (0 to 1) of completed top level entries.
		 */
		void progress(int fileCount, int folderCount, long length, float completedRatio, long elapsed);

		/**
		 * Returns the interval in seconds between two progress reports.
		 */
		default int refreshInterval() {
			return 1;
		}

	}

	/**
	 * 
	 * Tracks the progress of a walk; the walk is finished when every begun entry has ended.
	 *
	 * @author Fred Feng
	 * @since 2.0.1
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