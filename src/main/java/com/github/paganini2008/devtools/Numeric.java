package com.github.paganini2008.devtools;

import java.math.BigDecimal;
import java.math.BigInteger;

import com.github.paganini2008.devtools.math.BigDecimals;

public class Numeric extends Number {

	private static final long serialVersionUID = 7879573969079837365L;
	private final BigDecimal value;

	public Numeric(double delta) {
		this.value = BigDecimal.valueOf(delta);
	}

	public Numeric(long delta) {
		this.value = BigDecimal.valueOf(delta);
	}

	public Numeric(BigInteger delta) {
		this.value = new BigDecimal(delta);
	}

	public Numeric(BigDecimal delta) {
		this.value = delta;
	}

	public Numeric(String str) {
		this.value = BigDecimals.parse(str);
	}

	public int intValue() {
		return value.intValue();
	}

	public long longValue() {
		return value.longValue();
	}

	public float floatValue() {
		return value.floatValue();
	}

	public double doubleValue() {
		return value.doubleValue();
	}

	public byte byteValue() {
		return value.byteValue();
	}

	public short shortValue() {
		return value.shortValue();
	}

}
