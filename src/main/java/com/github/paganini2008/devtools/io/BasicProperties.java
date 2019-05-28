package com.github.paganini2008.devtools.io;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.net.URL;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.github.paganini2008.devtools.collection.ListUtils;
import com.github.paganini2008.devtools.collection.MapUtils;
import com.github.paganini2008.devtools.collection.MatchMode;

/**
 * 
 * BasicProperties
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class BasicProperties extends Properties {

	private static final long serialVersionUID = 4564916474223591678L;

	public void load(File file, String charset) throws IOException {
		Reader reader = null;
		try {
			reader = FileUtils.getBufferedReader(file, charset);
			load(reader);
		} finally {
			IOUtils.closeQuietly(reader);
		}
	}

	public void load(String name, String charset) throws IOException {
		load(name, charset, null);
	}

	public void load(String name, String charset, ClassLoader loader) throws IOException {
		Enumeration<URL> urls;
		if (loader == null) {
			urls = ClassLoader.getSystemResources(name);
		} else {
			urls = loader.getResources(name);
		}
		List<URL> urlList = ListUtils.reverse(urls);
		Reader reader;
		for (URL url : urlList) {
			reader = IOUtils.getBufferedReader(url.openStream(), charset);
			try {
				load(reader);
			} finally {
				IOUtils.closeQuietly(reader);
			}
		}
	}

	public BasicProperties filter(String prefix) {
		BasicProperties dest = new BasicProperties();
		MapUtils.copyProperties(this, prefix, dest);
		return dest;
	}

	public BasicProperties filter(String substr, MatchMode mode) {
		BasicProperties dest = new BasicProperties();
		MapUtils.copyProperties(this, substr, mode, dest);
		return dest;
	}

	public Map<String, String> toMap() {
		return MapUtils.toMap(this);
	}

	public Map<String, String> toMap(String prefix) {
		return MapUtils.toMap(this, prefix);
	}

	public Map<String, String> toMap(String substr, MatchMode mode) {
		return MapUtils.toMap(this, substr, mode);
	}

}
