package com.github.paganini2008.springworld.config.examples;

import com.github.paganini2008.devtools.enums.EnumConstant;

/**
 * 
 * Number
 * 
 * @author Fred Feng
 * @created 2019-08
 * @revised 2019-08
 * @version 1.0
 */
public enum Number implements EnumConstant {

	ONE(0, "first"), TWO(1, "second"), THREE(2, "third");

	private Number(int value, String repr) {
		this.value = value;
		this.repr = repr;
	}

	private final int value;
	private final String repr;

	@Override
	public int getValue() {
		return value;
	}

	@Override
	public String getRepr() {
		return repr;
	}

}
