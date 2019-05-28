package com.github.paganini2008.devtools.io;

import com.github.paganini2008.devtools.collection.MultiMapMap;

/**
 * 
 * IniConfigAdapter
 * 
 * @author Fred Feng
 * @version 1.0
 */
public interface IniConfigAdapter {

	void adapt(MultiMapMap<String, String, String> config);

}
