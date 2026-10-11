/**
* Copyright 2017-2026 Fred Feng (paganini.fy@gmail.com)

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
package com.github.paganini2008.devtools.io;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.function.BiFunction;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

/**
 * SizeUnitTest
 *
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class SizeUnitTest {

	private static final BigDecimal K = BigDecimal.valueOf(1024);

	@SuppressWarnings("unchecked")
	private static final BiFunction<SizeUnit, Long, BigDecimal>[] CONVERTERS = new BiFunction[] {
			(BiFunction<SizeUnit, Long, BigDecimal>) (u, s) -> u.toBytes(s), (BiFunction<SizeUnit, Long, BigDecimal>) (u, s) -> u.toKB(s),
			(BiFunction<SizeUnit, Long, BigDecimal>) (u, s) -> u.toMB(s), (BiFunction<SizeUnit, Long, BigDecimal>) (u, s) -> u.toGB(s),
			(BiFunction<SizeUnit, Long, BigDecimal>) (u, s) -> u.toTB(s), (BiFunction<SizeUnit, Long, BigDecimal>) (u, s) -> u.toPB(s),
			(BiFunction<SizeUnit, Long, BigDecimal>) (u, s) -> u.toEB(s), (BiFunction<SizeUnit, Long, BigDecimal>) (u, s) -> u.toZB(s),
			(BiFunction<SizeUnit, Long, BigDecimal>) (u, s) -> u.toYB(s) };

	private static BigDecimal expected(SizeUnit from, int to, long size) {
		int diff = from.ordinal() - to;
		BigDecimal value = BigDecimal.valueOf(size);
		if (diff > 0) {
			return value.multiply(K.pow(diff));
		} else if (diff < 0) {
			return value.divide(K.pow(-diff), 2, RoundingMode.HALF_UP);
		}
		return value;
	}

	@ParameterizedTest
	@EnumSource(SizeUnit.class)
	public void conversions_matchPowersOf1024(SizeUnit unit) {
		long[] sizes = { 0, 1, 3, 1536, 5L * 1024 * 1024 * 1024 };
		for (long size : sizes) {
			for (int to = 0; to < CONVERTERS.length; to++) {
				BigDecimal actual = CONVERTERS[to].apply(unit, size);
				assertEquals(0, expected(unit, to, size).compareTo(actual), unit + " -> " + to + " of " + size + " = " + actual);
			}
		}
	}

	@ParameterizedTest
	@EnumSource(SizeUnit.class)
	public void convert_interpretsSizeInGivenUnit(SizeUnit unit) {
		for (SizeUnit source : SizeUnit.values()) {
			BigDecimal actual = unit.convert(2048, source);
			assertEquals(0, expected(source, unit.ordinal(), 2048).compareTo(actual), source + " -> " + unit);
		}
	}

	@Test
	public void bytesToKB_usesKilobytesNotMegabytes() {
		assertEquals(new BigDecimal("2.00"), SizeUnit.BYTES.toKB(2048));
		assertEquals(new BigDecimal("1.50"), SizeUnit.BYTES.toKB(1536));
		assertEquals(new BigDecimal("0.00"), SizeUnit.BYTES.toMB(1));
		assertEquals(BigDecimal.valueOf(1024L * 1024), SizeUnit.MB.toBytes(1));
		assertEquals(new BigDecimal("1.00"), SizeUnit.KB.convert(1024, SizeUnit.BYTES));
	}
}
