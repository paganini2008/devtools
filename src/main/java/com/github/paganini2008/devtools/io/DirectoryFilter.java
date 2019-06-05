package com.github.paganini2008.devtools.io;

/**
 * 
 * DirectoryFilter
 * 
 * @author Fred Feng
 * @revised 2019-05
 * @version 1.0
 */
public interface DirectoryFilter {

	boolean accept(DirectoryInfo fileInfo);

}
