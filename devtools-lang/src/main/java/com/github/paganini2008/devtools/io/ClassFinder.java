package com.github.paganini2008.devtools.io;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import com.github.paganini2008.devtools.Assert;
import com.github.paganini2008.devtools.StringUtils;

/**
 * Find classes under inputed package name
 * 
 * @author Jimmy Hoff
 * @version 1.0
 */
public class ClassFinder {

	private static final ClassLoader currentClassLoader = Thread.currentThread().getContextClassLoader();
	private static final String classPath;
	private static final int classPathCharLength;
	static {
		String path = currentClassLoader.getResource("").getPath().replace("\\", "/");
		if (PathUtils.isWindowsOS()) {
			path = path.substring(1);
		}
		classPath = path;
		classPathCharLength = classPath.length();
	}

	public static void lookup(String packageName, boolean recursive, Handler handler) throws IOException {
		Assert.hasNoText("Please set package name.");
		ClassLoader classLoader = currentClassLoader;
		String packagePath = packageName.replace(".", "/");
		URL url = classLoader.getResource(packagePath);
		if (url != null) {
			String type = url.getProtocol();
			String path = url.getPath();
			if (type.equals("file")) {
				lookupFromProject(path, recursive, handler);
			} else if (type.equals("jar")) {
				lookupFromJar(path, recursive, handler);
			}
		}
	}

	/**
	 * Lookup from your project files
	 * 
	 * @param filePath
	 * @param recursive
	 * @param handler
	 */
	private static void lookupFromProject(String filePath, boolean recursive, Handler handler) {
		filePath = filePath.replace("%20", " ");
		File file = new File(filePath);
		File[] childFiles = file.listFiles();
		if (childFiles != null) {
			List<File> fileList = new ArrayList<File>(Arrays.asList(childFiles));
			Collections.sort(fileList, new Comparator<File>() {
				public int compare(File left, File right) {
					return right.getName().compareTo(left.getName());
				}
			});
			String currentName = null;
			for (File childFile : fileList) {
				if (childFile.isDirectory()) {
					if (recursive) {
						lookupFromProject(childFile.getPath(), recursive, handler);
					}
				} else if (childFile.getName().endsWith(".class")) {
					String path = childFile.getPath().replace("\\", "/");
					String fullName = path.substring(path.indexOf(classPath) + classPathCharLength, path.lastIndexOf("."));
					String simpleName = fullName.substring(fullName.lastIndexOf("/") + 1);
					if (currentName != null && simpleName.startsWith(currentName)) {
						int index = simpleName.indexOf("$");
						if (index != -1) {
							try {
								Integer.parseInt(simpleName.substring(index + 1));
								continue;
							} catch (RuntimeException e) {
							}
						}
					} else {
						currentName = simpleName;
					}
					if (handler != null) {
						String fileName = path.substring(path.indexOf(classPath) + classPathCharLength - 1);
						handler.publish(fileName, fullName.replace("/", "."));
					}
				}
			}
		}

	}

	/**
	 * Lookup from your jar files
	 * 
	 * @param jarPath
	 * @param recursive
	 * @param handler
	 * @throws IOException
	 */
	private static void lookupFromJar(String jarPath, boolean recursive, Handler handler) throws IOException {
		String[] jarInfo = jarPath.split("!");
		String jarFilePath = jarInfo[0].substring(jarInfo[0].indexOf("/"));
		String packagePath = jarInfo[1].substring(1);
		JarFile jarFile = new JarFile(jarFilePath);
		try {
			Enumeration<JarEntry> entrys = jarFile.entries();
			while (entrys.hasMoreElements()) {
				JarEntry jarEntry = entrys.nextElement();
				String entryName = jarEntry.getName();
				if (entryName.endsWith(".class")) {
					String fileName = null;
					if (recursive) {
						if (entryName.startsWith(packagePath)) {
							fileName = entryName;
						}
					} else {
						int index = entryName.lastIndexOf("/");
						String myPackagePath;
						if (index != -1) {
							myPackagePath = entryName.substring(0, index);
						} else {
							myPackagePath = entryName;
						}
						if (myPackagePath.equals(packagePath)) {
							fileName = entryName;
						}
					}
					if (handler != null) {
						if (StringUtils.isNotBlank(fileName)) {
							String className = fileName.replace("/", ".").substring(0, entryName.lastIndexOf("."));
							handler.publish(fileName, className);
						}
					}
				}
			}
		} finally {
			jarFile.close();
		}
	}

	private ClassFinder() {
	}

	public static void main(String[] args) throws Exception {
		String packageName = "lazycat.concurrent";
		ClassFinder.lookup(packageName, true, new Handler() {
			public void publish(String fileName, String className) {
				System.out.println(fileName + ": " + className);
			}
		});
	}

}
