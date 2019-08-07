package com.github.paganini2008.springworld.support.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.github.paganini2008.devtools.enums.EnumConstant;
import com.github.paganini2008.devtools.enums.EnumUtils;

/**
 * 
 * Gender
 * 
 * @author Fred Feng
 * @created 2019-06
 * @version 2.0.0
 */
public enum Gender implements EnumConstant {
	
	MAN(0, "男"), WOMAN(1, "女");

	private final int ordinal;
	private final String repr;

	private Gender(int ordinal, String repr) {
		this.ordinal = ordinal;
		this.repr = repr;
	}

	@JsonValue
	public int getValue() {
		return ordinal;
	}

	public String getRepr() {
		return repr;
	}

	@JsonCreator
	public static Gender valueOf(int code) {
		return EnumUtils.valueOf(Gender.class, code);
	}
}
