package com.github.paganini2008.devtools.collection;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;

/**
 * 
 * ReadonlyProperties
 * 
 * @author Fred Feng
 * @version 1.0
 */
public abstract class ReadonlyProperties extends Properties {

	private static final long serialVersionUID = 1L;

	public Object put(Object key, Object value) {
		throw new UnsupportedOperationException();
	}

	public Object putIfAbsent(Object key, Object value) {
		throw new UnsupportedOperationException();
	}

	public Object setProperty(String key, String value) {
		throw new UnsupportedOperationException();
	}

	public void load(Reader reader) throws IOException {
		throw new UnsupportedOperationException();
	}

	public void load(InputStream inStream) throws IOException {
		throw new UnsupportedOperationException();
	}

	public void save(OutputStream out, String comments) {
		throw new UnsupportedOperationException();
	}

	public void loadFromXML(InputStream in) throws IOException, InvalidPropertiesFormatException {
		throw new UnsupportedOperationException();
	}

}
