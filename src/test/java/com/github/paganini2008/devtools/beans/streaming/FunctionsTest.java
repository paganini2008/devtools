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
package com.github.paganini2008.devtools.beans.streaming;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * FunctionsTest
 * 
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class FunctionsTest {

	private final List<Product> products = Product.samples();
	private final List<Product> empty = Collections.emptyList();

	@Test
	public void testMinMax() {
		assertEquals(Float.valueOf(100f), Functions.<Product, Float>min("price", Float.class).getResult(products));
		assertEquals(Float.valueOf(500f), Functions.<Product, Float>max("price", Float.class).getResult(products));
		assertEquals(Float.valueOf(100f), Functions.<Product, Float>min(Product::getPrice).getResult(products));
		// min regards null as the minimum value, max ignores it
		assertNull(Functions.<Product, BigDecimal>min(Product::getFreight).getResult(products));
		assertEquals(new BigDecimal("50"), Functions.<Product, BigDecimal>min(Product::getFreight).getResult(products.subList(0, 3)));
		assertEquals(new BigDecimal("90"), Functions.<Product, BigDecimal>max(Product::getFreight).getResult(products));
		// empty or all null
		assertNull(Functions.<Product, Float>min("price", Float.class).getResult(empty));
		assertNull(Functions.<Product, Float>max("price", Float.class).getResult(empty));
		List<Product> nullOnly = Arrays.asList(products.get(3));
		assertNull(Functions.<Product, BigDecimal>min(Product::getFreight).getResult(nullOnly));
		assertNull(Functions.<Product, BigDecimal>max(Product::getFreight).getResult(nullOnly));
		// min/max on strings
		assertEquals("London", Functions.<Product, String>min("location", String.class).getResult(products));
		assertEquals("Tokyo", Functions.<Product, String>max("location", String.class).getResult(products));
	}

	@Test
	public void testSum() {
		assertEquals(new BigDecimal("325"), Functions.<Product>sum("freight").getResult(products));
		assertEquals(new BigDecimal("125"), Functions.<Product>sum("sales").getResult(products));
		assertEquals(BigDecimal.ZERO, Functions.<Product>sum("sales").getResult(empty));
		assertEquals(new BigDecimal("1650"), Functions.<Product>sum(p -> new BigDecimal(p.getPrice().toString())).getResult(products)
				.setScale(0, RoundingMode.HALF_UP));
	}

	@Test
	public void testAvg() {
		// 325 / 5 (the null freight is not counted)
		assertEquals(new BigDecimal("65.00"), Functions.<Product>avg("freight", 2, RoundingMode.HALF_UP).getResult(products));
		// (10 + 20 + 40 + 50 + 5) / 5, the null sales is not counted
		assertEquals(new BigDecimal("25.0"),
				Functions.<Product>avg(p -> p.getSales() != null ? BigDecimal.valueOf(p.getSales()) : null, 1, RoundingMode.HALF_UP)
						.getResult(products));
		assertNull(Functions.<Product>avg("freight", 2, RoundingMode.HALF_UP).getResult(empty));
		assertNull(Functions.<Product>avg("freight", 2, RoundingMode.HALF_UP).getResult(Arrays.asList(products.get(3))));
	}

	@Test
	public void testCount() {
		assertEquals(Integer.valueOf(6), Functions.<Product>count().getResult(products));
		assertEquals(Integer.valueOf(0), Functions.<Product>count().getResult(empty));
	}

}
