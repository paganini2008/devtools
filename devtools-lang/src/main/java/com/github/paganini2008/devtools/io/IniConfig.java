package com.github.paganini2008.devtools.io;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import com.github.paganini2008.devtools.MatchMode;

/**
 * 
 * IniConfig
 * 
 * @author Jimmy Hoff
 * @version 1.0
 */
public interface IniConfig extends Iterable<String> {

	Map<String, String> set(String section, Map<String, String> kwargs);

	String set(String section, String name, String value);

	void store(OutputStream os, String charset) throws IOException;

	String[] sections();

	Map<String, String> get(String section);

	Map<String, String> get(String section, String substr, MatchMode mode);

	String getString(String section, String name);

	String getString(String section, String name, String defaultValue);

	Byte getByte(String section, String name);

	Byte getByte(String section, String name, Byte defaultValue);

	Short getShort(String section, String name);

	Short getShort(String section, String name, Short defaultValue);

	Integer getInteger(String section, String name);

	Integer getInteger(String section, String name, Integer defaultValue);

	Long getLong(String section, String name);

	Long getLong(String section, String name, Long defaultValue);

	Float getFloat(String section, String name);

	Float getFloat(String section, String name, Float defaultValue);

	Double getDouble(String section, String name);

	Double getDouble(String section, String name, Double defaultValue);

	Boolean getBoolean(String section, String name);

	Boolean getBoolean(String section, String name, Boolean defaultValue);

	Map<String, Map<String, String>> toMap();

}
