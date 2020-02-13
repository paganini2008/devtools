package com.github.paganini2008.devtools.io;

import java.io.IOException;

/**
 * 
 * ClassPathIniConfig
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class ClassPathIniConfig extends RefreshingIniConfig {

	private static final long serialVersionUID = -4691878287460797044L;
	private final String[] names;
	private final ClassLoader loader;
	private String charset = "UTF-8";

	public ClassPathIniConfig(String... names) {
		this(names, ClassPathIniConfig.class.getClassLoader());
	}

	public ClassPathIniConfig(String[] names, ClassLoader loader) {
		this.names = names;
		this.loader = loader;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}

	protected IniConfig getConfig() throws IOException {
		BasicIniConfig config = new BasicIniConfig();
		if (names != null && names.length > 0) {
			for (String name : names) {
				config.loadFromClassPath(name, charset, loader);
			}
		}
		return config;
	}

}
