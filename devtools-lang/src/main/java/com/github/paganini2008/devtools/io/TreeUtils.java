package com.github.paganini2008.devtools.io;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.github.paganini2008.devtools.Console;
import com.github.paganini2008.devtools.StringUtils;

/**
 * 
 * TreeUtils
 *
 * @author Fred Feng
 * @version 2.0.5
 */
public abstract class TreeUtils {

	public static String[] scan(File directory, int maxDepth) throws IOException {
		return scan(directory, maxDepth, new DefaultTreeFilter());
	}

	private static String[] scan(File directory, int maxDepth, TreeFilter treeFilter) throws IOException {
		List<String> list = new ArrayList<String>();
		FileUtils.scan(directory, null, new ScanFilter() {

			@Override
			public boolean filterDirectory(File directory, int depth) throws IOException {
				if (depth <= maxDepth && treeFilter.filterDirectory(directory, depth)) {
					list.add(treeFilter.getText(directory, depth));
					return true;
				}
				return false;
			}

			@Override
			public void filterFile(File directory, int depth, File file) throws IOException {
				if (treeFilter.filterFile(directory, depth, file)) {
					list.add(treeFilter.getText(directory, depth, file));
				}
			}
		});
		return list.toArray(new String[0]);
	}

	public static class DefaultTreeFilter implements TreeFilter {

		@Override
		public String getText(File directory, int depth) {
			int n = depth + 1;
			StringBuilder str = new StringBuilder();
			for (int i = 0; i < n; i++) {
				str.append("|");
				if (i != n - 1) {
					str.append(StringUtils.repeat(' ', 4));
				} else {
					str.append("--- ");
				}
			}
			str.append(directory.getName());
			return str.toString();
		}

		@Override
		public String getText(File directory, int depth, File file) {
			int n = depth + 1;
			StringBuilder str = new StringBuilder();
			for (int i = 0; i < n; i++) {
				str.append("|");
				if (i != n - 1) {
					str.append(StringUtils.repeat(' ', 4));
				} else {
					str.append("--- ");
				}
			}
			str.append(file.getName());
			return str.toString();
		}

	}

	public static void main(String[] args) throws IOException {
		String[] lines = TreeUtils.scan(new File("D:\\fabu"),2);
		Console.log(lines);
		System.out.println();

	}

}
