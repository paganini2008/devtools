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
package com.github.paganini2008.devtools;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import com.github.paganini2008.devtools.io.FileUtils;

/**
 * CodeDebuger
 * 
 * @author Fred Feng
 * @since 2.0.1
 */
public class CodeDebuger {

	private static final String classNamePrefix = "Temp_";
	private static final String classPath;

	static {
		classPath = CodeDebuger.class.getClassLoader().getResource("").getPath();
		System.out.println(classPath);
	}

	private static final String executionWithReturn;
	private static final String executionWithVoid;

	static {

		StringBuilder javaSource = new StringBuilder();
		javaSource.append("public class $");
		javaSource.append("{");
		javaSource.append("    public Object getObject()");
		javaSource.append("    {");
		javaSource.append("        Object result = $;");
		javaSource.append("        return result;");
		javaSource.append("    }");
		javaSource.append("}");
		executionWithReturn = javaSource.toString();
		javaSource.delete(0, javaSource.length());

		javaSource = new StringBuilder();
		javaSource.append("public class $");
		javaSource.append("{");
		javaSource.append("    public Object getObject()");
		javaSource.append("    {");
		javaSource.append("        $;");
		javaSource.append("        return null;");
		javaSource.append("    }");
		javaSource.append("}");
		executionWithVoid = javaSource.toString();
	}

	private static Class<?> loadClass(String className, String pattern, String javaCode) throws IllegalStateException {
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
		StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, null);
		JavaFileObject file = new JavaStringSource(className, StringUtils.format(pattern, "$", new Object[] { className, javaCode }));
		List<JavaFileObject> javaObjects = Arrays.asList(file);
		List<String> options = new ArrayList<String>();
		options.add("-d");
		options.add(classPath);
		options.add("-encoding");
		options.add("UTF-8");
		JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, null, options, null, javaObjects);
		boolean result = task.call();
		if (result) {
			try {
				return Class.forName(className);
			} catch (ClassNotFoundException e) {
			}
		}
		throw new IllegalStateException("JavaCompiler error: \n" + diagnostics.getDiagnostics());
	}

	public static void execute(String javaCode) {
		String className = classNamePrefix + UUID.randomUUID().toString().replaceAll("-", "");
		Class<?> clazz = loadClass(className, executionWithVoid, javaCode);
		try {
			Method method = clazz.getMethod("getObject");
			method.setAccessible(true);
			method.invoke(clazz.newInstance());
		} catch (Exception e) {
			throw new IllegalStateException("Unable to execute target method.", e);
		} finally {
			File abondon = new File(classPath, className + ".class");
			if (abondon.exists()) {
				try {
					FileUtils.deleteFile(abondon);
				} catch (IOException e) {
					throw new IllegalStateException(e);
				}
			}
		}
	}

	public static Object executeAndReturn(String javaCode) {
		final String className = classNamePrefix + UUID.randomUUID().toString().replaceAll("-", "");
		Class<?> clazz = loadClass(className, executionWithReturn, javaCode);
		try {
			Method method = clazz.getMethod("getObject");
			method.setAccessible(true);
			return method.invoke(clazz.newInstance());
		} catch (Exception e) {
			throw new IllegalStateException("Unable to execute target method.", e);
		} finally {
			File abondon = new File(classPath, className + ".class");
			if (abondon.exists()) {
				try {
					FileUtils.deleteFile(abondon);
				} catch (IOException e) {
					throw new IllegalStateException(e);
				}
			}
		}
	}

	public static void main(String[] args) throws Exception {
		CodeDebuger.execute("System.out.println(System.currentTimeMillis());");
	}

}
