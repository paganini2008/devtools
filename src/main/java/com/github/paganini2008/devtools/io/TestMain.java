package com.github.paganini2008.devtools.io;

import java.io.File;
import java.util.List;

import com.github.paganini2008.devtools.io.filter.LengthFileFilter;

public class TestMain {

	public static void test1() throws Exception {
		File directory = new File("d:\\sql");
		List<File> files = DirectoryWalker.searchFiles(directory, 50, LengthFileFilter.gte(100, SizeUnit.MB),
				new Progressable() {
					public void progress(int fileCount, int folderCount, long length, long queueSize,
							String completedRatio, long elapsed) {
						System.out.println("fileCount: " + fileCount + ", folderCount: " + folderCount + ", length: "
								+ length + ", queueSize: " + queueSize + ", completedRatio: " + completedRatio
								+ ", elapsed: " + elapsed);
					}
				});
		System.out.println(files);

	}

	public static void test2() throws Exception {
		File directory = new File("H:\\Howbuy2014");
		List<File> files = RecursiveDirectoryWalker.searchFiles(directory, 50, LengthFileFilter.gte(100, SizeUnit.MB),
				new Progressable() {
					public void progress(int fileCount, int folderCount, long length, long concurrents,
							String completedRatio, long elapsed) {
//						System.out.println("fileCount: " + fileCount + ", folderCount: " + folderCount + ", length: "
//								+ length + ", concurrents: " + concurrents + ", completedRatio: " + completedRatio
//								+ ", elapsed: " + elapsed);
					}
				});
		System.out.println(files);

	}

	public static void main(String[] args) throws Exception {
		test1();
	}

}
