package com.github.paganini2008.devtools.http;

import java.io.InputStream;

/**
 * BasicNameValuePair
 * 
 * @author Jimmy Hoff
 * @version 1.0
 */
public class BasicNameValuePair implements NameValuePair {

	private String name;
	private String value;
	private InputStream stream;
	
	public BasicNameValuePair() {
	}

	public BasicNameValuePair(String name, String value) {
		this.name = name;
		this.value = value;
	}

	public BasicNameValuePair(String name, String value, InputStream stream) {
		this.name = name;
		this.value = value;
		this.stream = stream;
	}

	public NameValuePair name(String name) {
		this.name = name;
		return this;
	}

	public String name() {
		return name;
	}

	public NameValuePair value(String value) {
		this.value = value;
		return this;
	}

	public String value() {
		return value;
	}

	public NameValuePair inputStream(InputStream inputStream) {
		this.stream = inputStream;
		return this;
	}

	public InputStream inputStream() {
		return stream;
	}

	public boolean hasInputStream() {
		return stream != null;
	}

}
