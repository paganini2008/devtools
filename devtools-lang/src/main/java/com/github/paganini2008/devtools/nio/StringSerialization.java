package com.github.paganini2008.devtools.nio;

import java.nio.charset.Charset;

import com.github.paganini2008.devtools.CharsetUtils;

/**
 * 
 * StringSerialization
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class StringSerialization implements Serialization {

	private final Charset charset;

	public StringSerialization() {
		this(CharsetUtils.UTF_8);
	}

	public StringSerialization(Charset charset) {
		this.charset = charset;
	}

	@Override
	public byte[] serialize(Object object) {
		if (object == null) {
			return null;
		}
		return object.toString().getBytes(charset);
	}

	@Override
	public String deserialize(byte[] bytes) {
		if (bytes == null) {
			return "";
		}
		return new String(bytes, charset);
	}

}
