package com.github.paganini2008.devtools.io;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.github.paganini2008.devtools.io.DirectoryWalker.FileInfo;

/**
 * 
 * FileSearchUtils
 *
 * @author Fred Feng
 * @since 1.0
 */
public abstract class FileSearchUtils {

	public static File[] search(File directory, FileInfoFilter fileInfoFilter, int nThreads, int maxDepth) {
		List<File> files = new ArrayList<File>();
		ForkJoinDirectoryWalker directoryWalker = new ForkJoinDirectoryWalker(directory, new DirectoryWalkerHandler() {

			@Override
			public void handleDirectoryEnd(File file, FileInfo fileInfo, int depth) throws IOException {
				if (fileInfoFilter.accept(fileInfo)) {
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
		File[] files = FileSearchUtils.search(directory, new FileInfoFilter() {
			@Override
			public boolean accept(FileInfo fileInfo) {
				if (fileInfo.getLength() > 50 * FileUtils.MB) {
					return true;
				}
				return false;
			}
		}, 8, -1);
		for (File file : files) {
			System.out.println(file);
		}
	}

}
