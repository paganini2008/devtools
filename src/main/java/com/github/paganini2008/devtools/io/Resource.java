package com.github.paganini2008.devtools.io;

import java.util.Map;

import com.github.paganini2008.devtools.collection.MatchMode;

/**
 * 
 * Resource
 * 
 * @author Fred Feng
 * @version 1.0
 */
public interface Resource {

	String getString(String name);

	String getString(String name, String defaultValue);

	Byte getByte(String name);

	Byte getByte(String name, Byte defaultValue);

	Short getShort(String name);

	Short getShort(String name, Short defaultValue);

	Integer getInteger(String name);

	Integer getInteger(String name, Integer defaultValue);

	Long getLong(String name);

	Long getLong(String name, Long defaultValue);

	Float getFloat(String name);

	Float getFloat(String name, Float defaultValue);

	Double getDouble(String name);

	Double getDouble(String name, Double defaultValue);

	Boolean getBoolean(String name);

	Boolean getBoolean(String name, Boolean defaultValue);

	Map<String, String> toMap();

	Map<String, String> toMap(String substr, MatchMode mode);

}
