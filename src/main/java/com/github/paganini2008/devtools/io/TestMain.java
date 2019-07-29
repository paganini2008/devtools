package com.github.paganini2008.devtools.io;

import java.io.File;
import java.util.List;

import com.github.paganini2008.devtools.io.filter.LengthFileFilter;
import com.github.paganini2008.devtools.multithreads.RecursiveDirectoryWalker;

public class TestMain {

	public static void test1() throws Exception {
		File directory = new File("D:\\JD插件&小程序&运营平台三期0510");
		List<File> files = DirectoryWalker.searchFiles(directory, 200, LengthFileFilter.gte(100, SizeUnit.MB), new Progressable() {
			public void progress(String processing, int fileCount, int folderCount, long length, String completedRatio, long elapsed,
					String description) {
				System.out.println("fileCount: " + fileCount + ", folderCount: " + folderCount + ", length: " + length
						+ ", completedRatio: " + completedRatio + ", elapsed: " + elapsed);
			}
		});
		System.out.println(files);

	}

	public static void test2() throws Exception {
		File directory = new File("c:\\Users");
		List<File> files = RecursiveDirectoryWalker.searchFiles(directory, 50, LengthFileFilter.gte(100, SizeUnit.MB), new Progressable() {
			public void progress(String processing, int fileCount, int folderCount, long length, String completedRatio, long elapsed,
					String description) {
				System.out.println("fileCount: " + fileCount + ", folderCount: " + folderCount + ", length: " + length
						+ ", completedRatio: " + completedRatio + ", elapsed: " + elapsed);
			}
		});
		System.out.println(files);

	}

	public static void main(String[] args) throws Exception {
		test1();
	}

}
