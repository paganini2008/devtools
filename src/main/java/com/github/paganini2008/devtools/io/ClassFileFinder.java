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
import java.net.URISyntaxException;
import java.net.URL;

/**
 * 
 * Walks the class files of a package located in a class path directory and calls {@link #handleClass(File, String, int)}
 * for each of them. Subclasses override {@code handleClass} to process the classes.
 *
 * @author Fred Feng
 * @date 2019-05-29
 *
 * @since 2.0.1
 */
public class ClassFileFinder {

	private final String packageName;
	private int maxDepth = -1;

	public ClassFileFinder(String packageName) {
		this.packageName = packageName;
	}

	/**
	 * Limits the depth of sub packages entered (negative for unlimited). Classes of a sub package at depth
	 * {@code maxDepth + 1} are still reported.
	 */
	public void setMaxDepth(int maxDepth) {
		this.maxDepth = maxDepth;
	}

	public String getPackageName() {
		return packageName;
	}

	/**
	 * Walks the class files of the package.
	 *
	 * @throws IllegalArgumentException if the package name is {@code null}, cannot be found or is not located in a class path
	 *         directory
	 */
	public void walk() {
		if (packageName == null) {
			throw new IllegalArgumentException("Package name must not be null.");
		}
		String packagePath = packageName.replace(".", "/");
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		if (loader == null) {
			loader = ClassFileFinder.class.getClassLoader();
		}
		URL url = loader.getResource(packagePath);
		if (url == null || !"file".equals(url.getProtocol())) {
			throw new IllegalArgumentException("This package name is not be included in the current propject.");
		}
		File directory;
		try {
			directory = new File(url.toURI());
		} catch (URISyntaxException | IllegalArgumentException e) {
			directory = new File(url.getPath().replace("%20", " "));
		}
		walk(directory, packageName, 0);
	}

	private void walk(File directory, String currentPackage, int depth) {
		File[] javaFiles = directory.listFiles();
		if (javaFiles == null) {
			return;
		}
		for (File javaFile : javaFiles) {
			String name = javaFile.getName();
			if (javaFile.isDirectory()) {
				if (maxDepth < 0 || maxDepth >= depth) {
					walk(javaFile, currentPackage.length() > 0 ? currentPackage + "." + name : name, depth + 1);
				}
			} else if (name.endsWith(".class")) {
				String simpleName = name.substring(0, name.length() - ".class".length());
				handleClass(javaFile, currentPackage.length() > 0 ? currentPackage + "." + simpleName : simpleName, depth);
			}
		}
	}

	/**
	 * Receives each class file with its fully qualified class name; classes of the package itself have depth 0.
	 */
	protected void handleClass(File javaFile, String className, int depth) {
	}

}
