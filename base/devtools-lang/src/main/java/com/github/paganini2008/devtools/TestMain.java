package com.github.paganini2008.devtools;

import java.io.File;

public class TestMain {

	public static void main(String[] args) throws Exception {
		File directory = new File("d:/sql/");
		File file = new File("d:/sql/abc/123/jkl.jsp");
		String path = file.getAbsolutePath().replace(directory.getAbsolutePath(), "");
		String name = path.substring(1, path.indexOf(File.separatorChar, 1));
		System.out.println(name);
	}

}
