package com.github.paganini2008.devtools.io;

import java.nio.ByteBuffer;

import com.github.paganini2008.devtools.CharsetUtils;

/**
 * 
 * ByteBuffers
 * 
 * @author Fred Feng
 * @version 1.0
 */
public abstract class ByteBuffers {

	public static byte[] toByteArray(ByteBuffer bb) {
		byte[] bytes = new byte[bb.remaining()];
		bb.get(bytes);
		return bytes;
	}

	public static void main(String[] args) {
		byte[] bytes = toByteArray(ByteBuffer.wrap("Hello world!".getBytes(CharsetUtils.UTF_8)));
		System.out.println(new String(bytes, CharsetUtils.UTF_8));
	}

}
