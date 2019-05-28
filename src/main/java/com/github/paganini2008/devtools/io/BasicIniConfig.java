package com.github.paganini2008.devtools.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.github.paganini2008.devtools.StringUtils;
import com.github.paganini2008.devtools.collection.ListUtils;
import com.github.paganini2008.devtools.collection.MapUtils;
import com.github.paganini2008.devtools.collection.MatchMode;
import com.github.paganini2008.devtools.collection.MultiMapMap;

/**
 * 
 * BasicIniConfig
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class BasicIniConfig implements IniConfig, Serializable {

	private static final long serialVersionUID = 7872169560893030827L;
	private final MultiMapMap<String, String, String> config;

	public BasicIniConfig() {
		this(new MultiMapMap<String, String, String>());
	}

	protected BasicIniConfig(MultiMapMap<String, String, String> config) {
		this.config = config;
	}

	public void loadFromClassPath(String name, String charset, ClassLoader loader) throws IOException {
		Enumeration<URL> urls;
		if (loader == null) {
			urls = ClassLoader.getSystemResources(name);
		} else {
			urls = loader.getResources(name);
		}
		List<URL> urlList = ListUtils.reverse(urls);
		BufferedReader br;
		for (URL url : urlList) {
			br = IOUtils.getBufferedReader(url.openStream(), charset);
			try {
				String line;
				String section = null;
				int index;
				while (null != (line = br.readLine())) {
					line = line.trim();
					if (StringUtils.isBlank(line)) {
						continue;
					}
					index = line.indexOf("#");
					if (index == 0) {
						continue;
					} else if (index > 0) {
						line = line.substring(0, index).trim();
					}
					if (line.startsWith("[") && line.endsWith("]") && line.length() > 2) {
						section = line.substring(1, line.length() - 1);
					} else if (StringUtils.isNotBlank(section)) {
						index = line.indexOf("=");
						if (index > 0) {
							config.put(section, line.substring(0, index).trim(), line.substring(index + 1).trim());
						}
					}
				}
			} finally {
				IOUtils.closeQuietly(br);
			}
		}
	}

	public void load(File file, String charset) throws IOException {
		BufferedReader br = null;
		try {
			br = FileUtils.getBufferedReader(file, charset);
			String line;
			String section = null;
			int index;
			while (null != (line = br.readLine())) {
				line = line.trim();
				if (StringUtils.isBlank(line)) {
					continue;
				}
				index = line.indexOf("#");
				if (index == 0) {
					continue;
				} else if (index > 0) {
					line = line.substring(0, index).trim();
				}
				if (line.startsWith("[") && line.endsWith("]") && line.length() > 2) {
					section = line.substring(1, line.length() - 1);
				} else if (StringUtils.isNotBlank(section)) {
					index = line.indexOf("=");
					if (index > 0) {
						config.put(section, line.substring(0, index).trim(), line.substring(index + 1).trim());
					}
				}
			}
		} finally {
			IOUtils.closeQuietly(br);
		}
	}

	public String[] sections() {
		return config.keySet().toArray(new String[0]);
	}

	public Map<String, String> get(String section) {
		return new HashMap<String, String>(config.get(section));
	}

	public Map<String, String> get(String section, String substr, MatchMode mode) {
		return MapUtils.toMatchedKeyMap(get(section), substr, mode);
	}

	public String set(String section, String name, String value) {
		return config.put(section, name, value);
	}

	public Map<String, String> set(String section, Map<String, String> kwargs) {
		return config.put(section, kwargs);
	}

	public String getString(String section, String name) {
		return getString(section, name, null);
	}

	public String getString(String section, String name, String defaultValue) {
		return config.get(section, name, defaultValue);
	}

	public Byte getByte(String section, String name) {
		return getByte(section, name, null);
	}

	public Byte getByte(String section, String name, Byte defaultValue) {
		return MapUtils.getByte(config.get(section), name, defaultValue);
	}

	public Short getShort(String section, String name) {
		return getShort(section, name, null);
	}

	public Short getShort(String section, String name, Short defaultValue) {
		return MapUtils.getShort(config.get(section), name, defaultValue);
	}

	public Integer getInteger(String section, String name) {
		return getInteger(section, name, null);
	}

	public Integer getInteger(String section, String name, Integer defaultValue) {
		return MapUtils.getInteger(config.get(section), name, defaultValue);
	}

	public Long getLong(String section, String name) {
		return getLong(section, name, null);
	}

	public Long getLong(String section, String name, Long defaultValue) {
		return MapUtils.getLong(config.get(section), name, defaultValue);
	}

	public Float getFloat(String section, String name) {
		return getFloat(section, name, null);
	}

	public Float getFloat(String section, String name, Float defaultValue) {
		return MapUtils.getFloat(config.get(section), name, defaultValue);
	}

	public Double getDouble(String section, String name) {
		return getDouble(section, name, null);
	}

	public Double getDouble(String section, String name, Double defaultValue) {
		return MapUtils.getDouble(config.get(section), name, defaultValue);
	}

	public Boolean getBoolean(String section, String name) {
		return getBoolean(section, name, null);
	}

	public Boolean getBoolean(String section, String name, Boolean defaultValue) {
		return MapUtils.getBoolean(config.get(section), name, defaultValue);
	}

	public Map<String, Map<String, String>> toMap() {
		return new LinkedHashMap<String, Map<String, String>>(config);
	}

	public Iterator<String> iterator() {
		return config.keySet().iterator();
	}

	public String toString() {
		return config.toString();
	}

	public void store(File file, String charset) throws IOException {
		OutputStream output = null;
		try {
			output = FileUtils.openOutputStream(file);
			store(output, charset);
		} finally {
			IOUtils.closeQuietly(output);
		}
	}

	public void store(OutputStream os, String charset) throws IOException {
		BufferedWriter bw = null;
		try {
			bw = IOUtils.getBufferedWriter(os, charset);
			for (String section : sections()) {
				bw.write("[" + section + "]");
				bw.newLine();
				for (Map.Entry<String, String> inner : config.get(section).entrySet()) {
					bw.write(inner.getKey() + "=" + inner.getValue());
					bw.newLine();
				}
			}
		} finally {
			IOUtils.flushQuietly(bw);
		}
	}

}
