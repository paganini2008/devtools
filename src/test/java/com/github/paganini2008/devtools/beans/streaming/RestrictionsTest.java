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

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import com.github.paganini2008.devtools.MatchMode;

/**
 * RestrictionsTest
 * 
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class RestrictionsTest {

	private static List<Integer> select(Predicate<Product> predicate) {
		return Selector.from(Product.samples()).filter(predicate).list().stream().map(Product::getId).collect(Collectors.toList());
	}

	private static final Collection<Float> PRICES = Arrays.asList(200f, 400f);

	@Test
	public void testComparison() {
		assertEquals(Arrays.asList(2), select(Restrictions.eq("price", 300f)));
		assertEquals(Arrays.asList(2), select(Restrictions.eq("price", Float.class, 300f)));
		assertEquals(Arrays.asList(1, 3, 4, 5, 6), select(Restrictions.ne("price", 300f)));
		assertEquals(Arrays.asList(1, 3, 4, 5, 6), select(Restrictions.ne("price", Float.class, 300f)));
		assertEquals(Arrays.asList(1, 6), select(Restrictions.lt("price", 200f)));
		assertEquals(Arrays.asList(1, 6), select(Restrictions.lt("price", Double.class, 200d)));
		assertEquals(Arrays.asList(1, 3, 6), select(Restrictions.lte("price", 200f)));
		assertEquals(Arrays.asList(1, 3, 6), select(Restrictions.lte("price", Float.class, 200f)));
		assertEquals(Arrays.asList(4, 5), select(Restrictions.gt("price", 300f)));
		assertEquals(Arrays.asList(4, 5), select(Restrictions.gt("price", Float.class, 300f)));
		assertEquals(Arrays.asList(2, 4, 5), select(Restrictions.gte("price", 300f)));
		assertEquals(Arrays.asList(2, 4, 5), select(Restrictions.gte("price", Float.class, 300f)));
		// converted by required type
		assertEquals(Arrays.asList(2, 4, 5), select(Restrictions.gte("price", BigDecimal.class, new BigDecimal("300"))));
	}

	@Test
	public void testBetween() {
		assertEquals(Arrays.asList(2, 3, 4), select(Restrictions.between("price", 200f, 400f, false)));
		assertEquals(Arrays.asList(2), select(Restrictions.between("price", 200f, 400f, true)));
		assertEquals(Arrays.asList(2, 3, 4), select(Restrictions.<Product>junction().between("price", Float.class, 200f, 400f, false)));
		assertEquals(Arrays.asList(2), select(Restrictions.<Product>junction().between("price", Float.class, 200f, 400f, true)));
	}

	@Test
	public void testNull() {
		assertEquals(Arrays.asList(4), select(Restrictions.isNull("freight")));
		assertEquals(Arrays.asList(1, 2, 3, 5, 6), select(Restrictions.nonNull("freight")));
	}

	@Test
	public void testMatches() {
		assertEquals(Arrays.asList(1, 2, 6), select(Restrictions.matches("location", "Lon", MatchMode.START)));
		assertEquals(Arrays.asList(3, 4), select(Restrictions.matches("location", "kyo", MatchMode.END)));
		assertEquals(Arrays.asList(3, 4), select(Restrictions.<Product>matches("location", "o", MatchMode.ANY_WHERE)
				.and(Restrictions.<Product>matches("location", "London", MatchMode.ANY_WHERE).negate())));
		assertEquals(Arrays.asList(5), select(Restrictions.matches("location", "P*s", MatchMode.WILDCARD)));
		assertEquals(Arrays.asList(1, 2, 4, 6), select(Restrictions.<Product>matches("salesman.name", "^(Jack|Tony|Petter)$", MatchMode.REGEX)
				.and(Restrictions.ne("id", 3)).and(Restrictions.ne("id", 5))));
	}

	@Test
	public void testJunction() {
		Restriction<Product> r = Restrictions.junction();
		assertEquals(Arrays.asList(1, 2, 3, 4, 5, 6), select(r));
		r.eq("location", "London").gte("price", 150f).nonNull("salesman").isNull("name");
		assertEquals(Arrays.asList(), select(r));

		r = Restrictions.<Product>junction().eq("location", String.class, "London").ne("style", Product.Style.LARGE)
				.between("price", 100f, 150f, false).lt("price", 1000f).lte("price", Float.class, 150f);
		assertEquals(Arrays.asList(1, 6), select(r));

		// functional style inherited from FunctionalPredicate
		Restriction<Product> f = Restrictions.junction();
		f.gt(Product::getPrice, 300f);
		assertEquals(Arrays.asList(4, 5), select(f));
	}

	@Test
	public void testAnyAll() {
		assertEquals(Arrays.asList(3, 4), select(Restrictions.<Product>junction().eqAny("price", PRICES)));
		assertEquals(Arrays.asList(3, 4), select(Restrictions.<Product>junction().eqAny("price", Float.class, PRICES)));
		assertEquals(Arrays.asList(), select(Restrictions.<Product>junction().eqAll("price", PRICES)));
		assertEquals(Arrays.asList(3), select(Restrictions.<Product>junction().eqAll("price", Float.class, Arrays.asList(200f, 200f))));

		assertEquals(Arrays.asList(1, 2, 3, 6), select(Restrictions.<Product>junction().ltAny("price", PRICES)));
		assertEquals(Arrays.asList(1, 2, 3, 6), select(Restrictions.<Product>junction().ltAny("price", Float.class, PRICES)));
		assertEquals(Arrays.asList(1, 6), select(Restrictions.<Product>junction().ltAll("price", PRICES)));
		assertEquals(Arrays.asList(1, 6), select(Restrictions.<Product>junction().ltAll("price", Float.class, PRICES)));

		assertEquals(Arrays.asList(1, 2, 3, 4, 6), select(Restrictions.<Product>junction().lteAny("price", PRICES)));
		assertEquals(Arrays.asList(1, 2, 3, 4, 6), select(Restrictions.<Product>junction().lteAny("price", Float.class, PRICES)));
		assertEquals(Arrays.asList(1, 3, 6), select(Restrictions.<Product>junction().lteAll("price", PRICES)));
		assertEquals(Arrays.asList(1, 3, 6), select(Restrictions.<Product>junction().lteAll("price", Float.class, PRICES)));

		assertEquals(Arrays.asList(2, 4, 5), select(Restrictions.<Product>junction().gtAny("price", PRICES)));
		assertEquals(Arrays.asList(2, 4, 5), select(Restrictions.<Product>junction().gtAny("price", Float.class, PRICES)));
		assertEquals(Arrays.asList(5), select(Restrictions.<Product>junction().gtAll("price", PRICES)));
		assertEquals(Arrays.asList(5), select(Restrictions.<Product>junction().gtAll("price", Float.class, PRICES)));

		assertEquals(Arrays.asList(2, 3, 4, 5), select(Restrictions.<Product>junction().gteAny("price", PRICES)));
		assertEquals(Arrays.asList(2, 3, 4, 5), select(Restrictions.<Product>junction().gteAny("price", Float.class, PRICES)));
		assertEquals(Arrays.asList(4, 5), select(Restrictions.<Product>junction().gteAll("price", PRICES)));
		assertEquals(Arrays.asList(4, 5), select(Restrictions.<Product>junction().gteAll("price", Float.class, PRICES)));
	}

}
