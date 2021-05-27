package com.github.paganini2008.devtools.io;

import java.io.File;
import java.util.Properties;

import com.github.paganini2008.devtools.collection.RefreshingProperties;

/**
 * 
 * FileSystemProperties
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class FileSystemProperties extends RefreshingProperties {

	private static final long serialVersionUID = -3729821807160075318L;

	public FileSystemProperties(File... files) {
		this.files = files;
	}

	private final File[] files;
	private String charset = "UTF-8";

	public void setCharset(String charset) {
		this.charset = charset;
	}

	protected Properties createObject() throws Exception {
		BasicProperties p = new BasicProperties();
		if (files != null && files.length > 0) {
			for (File file : files) {
				p.load(file, charset);
			}
		}
		return p;
	}

}
