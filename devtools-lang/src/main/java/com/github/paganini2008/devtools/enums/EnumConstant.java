package com.github.paganini2008.devtools.enums;

/**
 * 
 * EnumConstant
 *
 * @author Fred Feng
 * @revised 2019-07
 * @created 2019-02
 * @version 1.0
 */
public interface EnumConstant {

	static final String DEFAULT_GROUP = "DEFAULT";

	int getValue();

	String getRepr();

	default String getGroup() {
		return DEFAULT_GROUP;
	}
}
