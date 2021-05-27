package com.github.paganini2008.devtools.io;

import java.io.IOException;

/**
 * 
 * StreamCopier
 * 
 * @author Fred Feng
 *
 * @version 1.0
 */
public interface StreamCopier {

	default void onStart() {
	}

	void copy(byte[] bytes, int offset, int length) throws IOException;

	default void onProgress(long length) {
	}

	default void onEnd(long length) {
	}

}
