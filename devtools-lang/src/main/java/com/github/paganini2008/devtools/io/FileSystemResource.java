package com.github.paganini2008.devtools.io;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import com.github.paganini2008.devtools.collection.MapUtils;

/**
 * 
 * FileSystemResource
 * 
 * @author Jimmy Hoff
 * @version 1.0
 */
public class FileSystemResource extends RefreshingResource {

	private static final long serialVersionUID = -637256906307889113L;

	public FileSystemResource(File... files) {
		this.files = files;
	}

	private final File[] files;
	private String charset = "UTF-8";

	public void setCharset(String charset) {
		this.charset = charset;
	}

	protected Map<String, String> getConfig() throws IOException {
		BasicProperties p = new BasicProperties();
		if (files != null && files.length > 0) {
			for (File file : files) {
				p.load(file, charset);
			}
		}
		return MapUtils.toMap(p);
	}

}
