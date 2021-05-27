package com.github.paganini2008.devtools.io;

import java.io.IOException;
import java.util.Properties;

import com.github.paganini2008.devtools.MatchMode;
import com.github.paganini2008.devtools.collection.LruMap;

/**
 * PropertiesUtils
 * 
 * @author Fred Feng
 * @version 1.0
 */
public abstract class PropertiesUtils {

	private final static LruMap<String, Properties> cache = new LruMap<String, Properties>(128);
	private static String charset = "UTF-8";

	public static void setCharset(String charset) {
		PropertiesUtils.charset = charset;
	}

	public static Properties loadProperties(String name) throws IOException {
		return loadProperties(name, PropertiesUtils.class.getClassLoader());
	}

	public static Properties loadProperties(String name, ClassLoader loader) throws IOException {
		BasicProperties p = new BasicProperties();
		p.load(name, charset);
		return p;
	}

	public static Properties getProperties(String name, String substr, MatchMode matchMode) throws IOException {
		return getProperties(name, PropertiesUtils.class.getClassLoader(), substr, matchMode);
	}

	public static Properties getProperties(String name, ClassLoader loader, String substr, MatchMode matchMode) throws IOException {
		Properties p = getProperties(name, loader);
		return ((BasicProperties) p).filter(substr, matchMode);
	}

	public static Properties getProperties(String name, String prefix) throws IOException {
		return getProperties(name, PropertiesUtils.class.getClassLoader(), prefix);
	}

	public static Properties getProperties(String name, ClassLoader loader, String prefix) throws IOException {
		Properties p = getProperties(name, loader);
		return ((BasicProperties) p).filter(prefix);
	}

	public static Properties getProperties(String name) throws IOException {
		return getProperties(name, PropertiesUtils.class.getClassLoader());
	}

	public static Properties getProperties(String name, ClassLoader loader) throws IOException {
		Properties config = cache.get(name);
		if (config == null) {
			cache.put(name, loadProperties(name, loader));
			config = cache.get(name);
		}
		return config;
	}

}
