package com.github.paganini2008.devtools;

import java.net.URI;

import javax.tools.SimpleJavaFileObject;

/**
 * 
 * JavaStringSource
 *
 * @author Fred Feng
 * @version 1.0
 */
public class JavaStringSource extends SimpleJavaFileObject {

	private final String name;
	private final String code;

	public JavaStringSource(String name, String code) {
		super(URI.create("string:///" + name.replace('.', '/') + Kind.SOURCE.extension), Kind.SOURCE);
		this.name = name;
		this.code = code;
	}

	public CharSequence getCharContent(boolean ignoreEncodingErrors) {
		return code;
	}

	public String getName() {
		return name;
	}

	public String getCode() {
		return code;
	}

}
