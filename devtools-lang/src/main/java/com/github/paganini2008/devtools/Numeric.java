/**
* Copyright 2018-2021 Fred Feng (paganini.fy@gmail.com)

* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.github.paganini2008.devtools;

import java.math.BigDecimal;
import java.math.BigInteger;

import com.github.paganini2008.devtools.math.BigDecimalUtils;

/**
 * 
 * Numeric
 *
 * @author Fred Feng
 * @version 1.0
 */
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
		this.value = BigDecimalUtils.parse(str);
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
