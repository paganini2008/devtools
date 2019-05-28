package com.github.paganini2008.devtools.io;

import java.io.File;
import java.net.URL;

public class Finder {

	private static final String classPath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
	private final String packageName;
	private int maxDepth = -1;

	public Finder(String packageName) {
		this.packageName = packageName;
	}

	public void setMaxDepth(int maxDepth) {
		this.maxDepth = maxDepth;
	}

	public String getPackageName() {
		return packageName;
	}

	public void walk() {
		String packagePath = packageName.replace(".", "/");
		URL url = Thread.currentThread().getContextClassLoader().getResource(packagePath);
		if (!"file".equals(url.getProtocol())) {
			throw new IllegalArgumentException("This package name is not be included in the current propject.");
		}
		walk(url.getPath(), 0);
	}

	private void walk(String filePath, int depth) {
		File file = new File(filePath);
		File[] javaFiles = file.listFiles();
		for (File javaFile : javaFiles) {
			if (javaFile.isDirectory()) {
				if (maxDepth < 0 || maxDepth >= depth) {
					walk(javaFile.getPath(), depth + 1);
				}
			} else {
				String path = javaFile.getPath();
				path = path.replace("\\", "/");
				if (path.endsWith(".class")) {
					path = path.substring(path.indexOf(classPath) + classPath.length(), path.lastIndexOf("."));
					path = path.replace("/", ".");
					handleClass(javaFile, path, depth);
				}
			}
		}
	}

	protected void handleClass(File javaFile, String className, int depth) {
	}

}
