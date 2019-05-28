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
 * Eval
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class Eval {

	private static final String prefix = "temp_";
	private static final String classPath;
	static {
		classPath = Eval.class.getClassLoader().getResource("").getPath();
		System.out.println(classPath);
	}

	private static final String executionReturn;
	private static final String executionVoid;
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
		executionReturn = javaSource.toString();

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
		executionVoid = javaSource.toString();
	}

	private final String expression;

	public Eval(String expression) {
		this.expression = expression;
	}

	private Class<?> loadClass(String className, String pattern) throws EvalException {
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
		StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, null);
		JavaFileObject file = new JavaStringSource(className, StringUtils.parseText(pattern, "$", new Object[] { className, expression }));
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
		throw new EvalException("JavaCompiler error.", diagnostics.getDiagnostics());
	}

	public Object call() throws EvalException {
		String className = prefix + UUID.randomUUID().toString().replaceAll("-", "");
		Class<?> clazz = loadClass(className, executionVoid);
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
				}
			}
		}
	}

	public Object execute() throws EvalException {
		String className = prefix + UUID.randomUUID().toString().replaceAll("-", "");
		Class<?> clazz = loadClass(className, executionReturn);
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
				}
			}
		}
	}

	public static void main(String[] args) throws Exception {
		Object rval = new Eval("System.out.println(System.currentTimeMillis())").call();
		System.out.println(rval);
	}

}
