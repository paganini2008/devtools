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
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
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
 * Finds the classes of a package, either in a class path directory or in a jar file, using the context class loader of
 * the current thread.
 * 
 * @author Fred Feng
 * @date 2019-05-29
 * @since 2.0.1
 */
public class ClassFinder {

	private static ClassLoader getClassLoader() {
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		return loader != null ? loader : ClassFinder.class.getClassLoader();
	}

	/**
	 * Finds the classes of the package in the first class path entry containing it. In class path directories anonymous
	 * classes (e.g. {@code Foo$1}) are skipped and the file name passed to the handler starts with {@code '/'}; in jar files
	 * all classes are reported with the entry name as file name.
	 *
	 * @param recursive whether classes of sub packages are included
	 * @param handler receives each class; may be {@code null}
	 * @throws IllegalArgumentException if the package name is blank
	 */
	public static void lookup(String packageName, boolean recursive, Handler handler) throws IOException {
		Assert.hasNoText(packageName, "Please set package name.");
		ClassLoader classLoader = getClassLoader();
		String packagePath = packageName.replace(".", "/");
		URL url = classLoader.getResource(packagePath);
		if (url != null) {
			String type = url.getProtocol();
			if (type.equals("file")) {
				lookupFromProject(toFile(url.toExternalForm(), url.getPath()), packagePath, recursive, handler);
			} else if (type.equals("jar")) {
				lookupFromJar(url.getPath(), recursive, handler);
			}
		}
	}

	private static File toFile(String uri, String fallbackPath) {
		try {
			return new File(new URI(uri));
		} catch (URISyntaxException | IllegalArgumentException e) {
			return new File(fallbackPath.replace("%20", " "));
		}
	}

	/**
	 * Lookup from your project files
	 * 
	 * @param directory
	 * @param packagePath
	 * @param recursive
	 * @param handler
	 */
	private static void lookupFromProject(File directory, String packagePath, boolean recursive, Handler handler) {
		File[] childFiles = directory.listFiles();
		if (childFiles != null) {
			List<File> fileList = new ArrayList<File>(Arrays.asList(childFiles));
			Collections.sort(fileList, new Comparator<File>() {
				public int compare(File left, File right) {
					return right.getName().compareTo(left.getName());
				}
			});
			String currentName = null;
			for (File childFile : fileList) {
				String childPath = packagePath + "/" + childFile.getName();
				if (childFile.isDirectory()) {
					if (recursive) {
						lookupFromProject(childFile, childPath, recursive, handler);
					}
				} else if (childFile.getName().endsWith(".class")) {
					String fullName = childPath.substring(0, childPath.lastIndexOf("."));
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
						handler.publish("/" + childPath, fullName.replace("/", "."));
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
		int separator = jarPath.indexOf("!/");
		if (separator < 0) {
			return;
		}
		String jarLocation = jarPath.substring(0, separator);
		String packagePath = jarPath.substring(separator + 2);
		while (packagePath.endsWith("/")) {
			packagePath = packagePath.substring(0, packagePath.length() - 1);
		}
		String fallback = jarLocation.indexOf("/") >= 0 ? jarLocation.substring(jarLocation.indexOf("/")) : jarLocation;
		try (JarFile jarFile = new JarFile(toFile(jarLocation, fallback))) {
			Enumeration<JarEntry> entrys = jarFile.entries();
			while (entrys.hasMoreElements()) {
				JarEntry jarEntry = entrys.nextElement();
				String entryName = jarEntry.getName();
				if (entryName.endsWith(".class")) {
					String fileName = null;
					if (recursive) {
						if (entryName.startsWith(packagePath + "/")) {
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
		}
	}

	private ClassFinder() {
	}
}
