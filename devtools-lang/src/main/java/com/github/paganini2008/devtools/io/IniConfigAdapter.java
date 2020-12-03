package com.github.paganini2008.devtools.io;

import com.github.paganini2008.devtools.collection.MultiMappedMap;

/**
 * 
 * IniConfigAdapter
 * 
 * @author Jimmy Hoff
 * @version 1.0
 */
public interface IniConfigAdapter {

	void adapt(MultiMappedMap<String, String, String> config);

}
