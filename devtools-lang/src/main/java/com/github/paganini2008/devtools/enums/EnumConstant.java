package com.github.paganini2008.devtools.enums;

/**
 * 
 * EnumConstant
 *
 * @author Jimmy Hoff
 * 
 * 
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
