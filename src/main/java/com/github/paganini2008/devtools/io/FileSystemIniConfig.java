package com.github.paganini2008.devtools.io;

import java.io.File;
import java.io.IOException;

/**
 * FileSystemIniResource
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class FileSystemIniConfig extends RefreshingIniConfig {

	private static final long serialVersionUID = 3413344527835245795L;

	public FileSystemIniConfig(File... files) {
		this.files = files;
	}

	private File[] files;
	private String charset = "UTF-8";

	public void setCharset(String charset) {
		this.charset = charset;
	}

	protected IniConfig getConfig() throws IOException {
		BasicIniConfig config = new BasicIniConfig();
		for (File file : files) {
			config.load(file, charset);
		}
		return config;
	}

}
