package com.github.paganini2008.devtools.io;

import java.io.File;
import java.io.IOException;

/**
 * 
 * ScanHandler
 * 
 * @author Fred Feng
 *
 * @version 1.0
 */
@FunctionalInterface
public interface ScanHandler {

	void handleFile(File directory, File file) throws IOException;

}
