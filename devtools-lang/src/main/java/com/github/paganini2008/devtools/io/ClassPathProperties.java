package com.github.paganini2008.devtools.io;

import java.util.Properties;

import com.github.paganini2008.devtools.collection.RefreshingProperties;

/**
 * 
 * ClassPathProperties
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class ClassPathProperties extends RefreshingProperties {

	private static final long serialVersionUID = 1038119041409409706L;
	
	private final String[] names;
	private final ClassLoader loader;

	public ClassPathProperties(String... names) {
		this(names, ClassPathProperties.class.getClassLoader());
	}

	public ClassPathProperties(String[] names, ClassLoader loader) {
		this.names = names;
		this.loader = loader;
	}

	private String charset = "UTF-8";

	public void setCharset(String charset) {
		this.charset = charset;
	}

	protected Properties createObject() throws Exception {
		BasicProperties p = new BasicProperties();
		if (names != null && names.length > 0) {
			for (String name : names) {
				p.load(name, charset, loader);
			}
		}
		return p;
	}

}
