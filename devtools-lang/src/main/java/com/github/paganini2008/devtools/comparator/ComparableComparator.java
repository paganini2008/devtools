/**
* Copyright 2017-2021 Fred Feng (paganini.fy@gmail.com)

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
package com.github.paganini2008.devtools.comparator;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * ComparableComparator
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class ComparableComparator<T extends Comparable<T>> extends AbstractComparator<T> {

	public ComparableComparator() {
	}

	public int compare(T a, T b) {
		return ComparatorHelper.compareTo(a, b);
	}

	public static final AbstractComparator<Byte> BYTE = new ComparableComparator<Byte>();
	public static final AbstractComparator<Short> SHORT = new ComparableComparator<Short>();
	public static final AbstractComparator<Integer> INTEGER = new ComparableComparator<Integer>();
	public static final AbstractComparator<Long> LONG = new ComparableComparator<Long>();
	public static final AbstractComparator<Float> FLOAT = new ComparableComparator<Float>();
	public static final AbstractComparator<Double> DOUBLE = new ComparableComparator<Double>();
	public static final AbstractComparator<BigInteger> BIGINT = new ComparableComparator<BigInteger>();
	public static final AbstractComparator<BigDecimal> DECIMAL = new ComparableComparator<BigDecimal>();
}
