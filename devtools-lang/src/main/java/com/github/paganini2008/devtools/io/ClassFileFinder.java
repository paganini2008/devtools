/**
* Copyright 2017-2022 Fred Feng (paganini.fy@gmail.com)

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
import java.net.URL;

/**
 * 
 * ClassFileFinder
 *
 * @author Fred Feng
 *
 * @since 2.0.1
 */
public class ClassFileFinder {

	private static final String classPath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
	private final String packageName;
	private int maxDepth = -1;

	public ClassFileFinder(String packageName) {
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
