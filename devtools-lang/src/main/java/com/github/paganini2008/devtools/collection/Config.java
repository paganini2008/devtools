package com.github.paganini2008.devtools.collection;

import java.util.Properties;

import com.github.paganini2008.devtools.primitives.Booleans;
import com.github.paganini2008.devtools.primitives.Bytes;
import com.github.paganini2008.devtools.primitives.Chars;
import com.github.paganini2008.devtools.primitives.Doubles;
import com.github.paganini2008.devtools.primitives.Floats;
import com.github.paganini2008.devtools.primitives.Ints;
import com.github.paganini2008.devtools.primitives.Longs;
import com.github.paganini2008.devtools.primitives.Shorts;

public class Config extends Properties {

	private static final long serialVersionUID = -891559340734908695L;

	public Boolean getBoolean(String name, Boolean defaultValue) {
		return Booleans.valueOf(getProperty(name), defaultValue);
	}

	public Character getCharacter(String name, Character defaultValue) {
		return Chars.valueOf(getProperty(name), defaultValue);
	}

	public Byte getByte(String name, Byte defaultValue) {
		return Bytes.valueOf(getProperty(name), defaultValue);
	}

	public Short getShort(String name, Short defaultValue) {
		return Shorts.valueOf(getProperty(name), defaultValue);
	}

	public Integer getInteger(String name, Integer defaultValue) {
		return Ints.valueOf(getProperty(name), defaultValue);
	}

	public Float getFloat(String name, Float defaultValue) {
		return Floats.valueOf(getProperty(name), defaultValue);
	}

	public Double getDouble(String name, Double defaultValue) {
		return Doubles.valueOf(getProperty(name), defaultValue);
	}

	public Long getLong(String name, Long defaultValue) {
		return Longs.valueOf(getProperty(name), defaultValue);
	}

}
