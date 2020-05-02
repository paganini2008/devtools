package com.github.paganini2008.devtools.nio.examples;

import java.io.IOException;

/**
 * 
 * IoAcceptor
 *
 * @author Fred Feng
 * @since 1.0
 */
public interface IoAcceptor {

	void start() throws IOException;

	void stop();

	boolean isOpened();

}