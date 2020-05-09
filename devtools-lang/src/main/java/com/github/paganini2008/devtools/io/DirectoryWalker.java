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

	FileInfo walk() throws IOException;

	interface FileInfo {

		File getFile();

		FileInfo[] getChildren();

		long getLength();

		long getLastModified();

		int getFileCount(boolean recursive);

		int getFolderCount(boolean recursive);

		FileInfo newChildInfo(File childFile);

		FileInfo getParent();

		void sum(File file);

		void done();

		long getElapsed();

		boolean isDone();
	}

	interface Progressable {

		void progress(int fileCount, int folderCount, long length, float completedRatio, long elapsed);

		default int refreshInterval() {
			return 1;
		}

	}

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