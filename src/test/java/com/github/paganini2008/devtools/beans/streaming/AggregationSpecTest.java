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
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

/**
 * AggregationSpecTest
 * <p>
 * Specification of the aggregate functions (count, sum, avg, min, max) on
 * empty, all null and partially null values, BigDecimal precision with scale
 * and rounding mode, and of 'group by' (single, multiple and nested columns,
 * null keys, converted keys).
 * 
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class AggregationSpecTest {

	private static final List<Item> EMPTY = Collections.emptyList();

	private static List<Item> allNull() {
		List<Item> list = new ArrayList<Item>();
		list.add(new Item(1, null, null, null, null, null, null));
		list.add(new Item(2, null, null, null, null, null, null));
		return list;
	}

	// ---------------------------------------------------------------- count

	@Test
	public void count_countsRowsIncludingNullValues() {
		assertEquals(Integer.valueOf(5), Functions.<Item>count().getResult(Item.samples()));
		assertEquals(Integer.valueOf(2), Functions.<Item>count().getResult(allNull()));
		assertEquals(Integer.valueOf(0), Functions.<Item>count().getResult(EMPTY));
	}

	// ---------------------------------------------------------------- sum

	@Test
	public void sum_keepsTheExactBigDecimalValue() {
		// 1.10 + 2.20 + 0.30 + 3.335, the null is ignored
		assertEquals(new BigDecimal("6.935"), Functions.<Item>sum("amount").getResult(Item.samples()));
		assertEquals(new BigDecimal("6.935"), Functions.<Item>sum(Item::getAmount).getResult(Item.samples()));
	}

	@Test
	public void sum_convertsOtherNumericTypes() {
		// qty is Integer, big is Long, ratio is Double
		assertEquals(0, new BigDecimal("65").compareTo(Functions.<Item>sum("qty").getResult(Item.samples())));
		assertEquals(0, new BigDecimal("100").compareTo(Functions.<Item>sum("big").getResult(Item.samples())));
		assertEquals(0, new BigDecimal("7.5").compareTo(Functions.<Item>sum("ratio").getResult(Item.samples())));
		// numeric strings are converted, an unconvertible one is ignored like null: "10" + "9" + "30"
		assertEquals(0, new BigDecimal("49").compareTo(Functions.<Item>sum("code").getResult(Item.samples())));
	}

	@Test
	public void sum_emptyAndAllNullAreZero() {
		// note: sql returns null here
		assertSame(BigDecimal.ZERO, Functions.<Item>sum("amount").getResult(EMPTY));
		assertSame(BigDecimal.ZERO, Functions.<Item>sum("amount").getResult(allNull()));
	}

	@Test
	public void sum_ofLargeValuesDoesNotOverflow() {
		List<Item> items = new ArrayList<Item>();
		for (int i = 0; i < 3; i++) {
			items.add(new Item(i, Integer.MAX_VALUE, Long.MAX_VALUE, null, null, null, null));
		}
		assertEquals(BigDecimal.valueOf(Integer.MAX_VALUE).multiply(BigDecimal.valueOf(3)), Functions.<Item>sum("qty").getResult(items));
		assertEquals(BigDecimal.valueOf(Long.MAX_VALUE).multiply(BigDecimal.valueOf(3)), Functions.<Item>sum("big").getResult(items));
	}

	// ---------------------------------------------------------------- avg

	@Test
	public void avg_ignoresNullsAndRoundsToTheScale() {
		// (1.10 + 2.20 + 0.30 + 3.335) / 4 = 1.73375
		assertEquals(new BigDecimal("1.73"), Functions.<Item>avg("amount", 2, RoundingMode.HALF_UP).getResult(Item.samples()));
		assertEquals(new BigDecimal("1.734"), Functions.<Item>avg("amount", 3, RoundingMode.HALF_UP).getResult(Item.samples()));
		assertEquals(new BigDecimal("1.733"), Functions.<Item>avg("amount", 3, RoundingMode.DOWN).getResult(Item.samples()));
		assertEquals(new BigDecimal("1.7338"), Functions.<Item>avg("amount", 4, RoundingMode.HALF_EVEN).getResult(Item.samples()));
		assertEquals(new BigDecimal("1.73375"), Functions.<Item>avg("amount", 5, RoundingMode.UNNECESSARY).getResult(Item.samples()));
		assertEquals(new BigDecimal("2"), Functions.<Item>avg("amount", 0, RoundingMode.CEILING).getResult(Item.samples()));
		// a negative scale rounds to tens
		assertEquals(new BigDecimal("2E+1"), Functions.<Item>avg("qty", -1, RoundingMode.HALF_UP).getResult(Item.samples()));
	}

	@Test
	public void avg_unnecessaryRoundingOfARepeatingDecimalFails() {
		// 10 / 3
		List<Item> items = Arrays.asList(new Item(1, 3, null, null, null, null, null), new Item(2, 3, null, null, null, null, null),
				new Item(3, 4, null, null, null, null, null));
		assertThrows(ArithmeticException.class, () -> Functions.<Item>avg("qty", 2, RoundingMode.UNNECESSARY).getResult(items));
		assertEquals(new BigDecimal("3.33"), Functions.<Item>avg("qty", 2, RoundingMode.HALF_UP).getResult(items));
	}

	@Test
	public void avg_emptyAndAllNullAreNull() {
		assertNull(Functions.<Item>avg("amount", 2, RoundingMode.HALF_UP).getResult(EMPTY));
		assertNull(Functions.<Item>avg("amount", 2, RoundingMode.HALF_UP).getResult(allNull()));
	}

	// ---------------------------------------------------------------- min / max

	@Test
	public void max_ignoresNulls() {
		assertEquals(Integer.valueOf(30), Functions.<Item, Integer>max("qty", Integer.class).getResult(Item.samples()));
		assertEquals(Long.valueOf(30L), Functions.<Item, Long>max("qty", Long.class).getResult(Item.samples()));
		assertNull(Functions.<Item, Integer>max("qty", Integer.class).getResult(allNull()));
		assertNull(Functions.<Item, Integer>max("qty", Integer.class).getResult(EMPTY));
	}

	@Test
	public void min_regardsNullAsTheMinimum() {
		// note: sql ignores the null and returns 5
		assertNull(Functions.<Item, Integer>min("qty", Integer.class).getResult(Item.samples()));
		assertEquals(Integer.valueOf(5), Functions.<Item, Integer>min("qty", Integer.class).getResult(
				Item.samples().stream().filter(i -> i.getQty() != null).collect(Collectors.toList())));
		assertNull(Functions.<Item, Integer>min("qty", Integer.class).getResult(allNull()));
		assertNull(Functions.<Item, Integer>min("qty", Integer.class).getResult(EMPTY));
	}

	@Test
	public void minMax_onBigDecimalAreScaleInsensitiveAndKeepTheFirstOfEqualValues() {
		List<Item> items = Arrays.asList(new Item(1, null, null, null, null, new BigDecimal("1.0"), null),
				new Item(2, null, null, null, null, new BigDecimal("1.00"), null));
		assertEquals("1.0", Functions.<Item, BigDecimal>min(Item::getAmount).getResult(items).toPlainString());
		assertEquals("1.0", Functions.<Item, BigDecimal>max(Item::getAmount).getResult(items).toPlainString());
	}

	@Test
	public void minMax_onStringsAndConvertedValues() {
		// code compared as strings: "10" < "30" < "9" < "abc"
		assertEquals("abc", Functions.<Item, String>max("code", String.class).getResult(Item.samples()));
		// compared as integers, "abc" is not convertible and ignored by max
		assertEquals(Integer.valueOf(30), Functions.<Item, Integer>max("code", Integer.class).getResult(Item.samples()));
	}

	// ---------------------------------------------------------------- Summary defaults

	@Test
	public void summary_defaultsDelegateToFunctions() {
		Group<Item> all = new Grouper<Item>(Item.samples()).setTransformer(g -> g).first();
		assertEquals(5, all.count());
		assertEquals(Integer.valueOf(30), all.max("qty", Integer.class));
		assertNull(all.min("qty", Integer.class));
		assertEquals(new BigDecimal("6.935"), all.sum("amount"));
		// avg defaults to scale 2 and HALF_UP
		assertEquals(new BigDecimal("1.73"), all.avg("amount"));
		assertEquals(new BigDecimal("1.7338"), all.avg("amount", 4, RoundingMode.HALF_UP));
	}

	// ---------------------------------------------------------------- group by

	@Test
	public void groupBy_singleColumnMatchesJdkGrouping() {
		List<Product> products = Datasets.randomProducts(21L, 400);
		List<Group<Product>> groups = Selector.from(products).groupBy("location", String.class).setTransformer(g -> g).list();
		Map<String, List<Product>> expected = Datasets.groupBy(products, Product::getLocation);
		assertEquals(new ArrayList<String>(expected.keySet()),
				groups.stream().map(g -> (String) g.getAttributes().get("location")).collect(Collectors.toList()));
		for (Group<Product> g : groups) {
			assertEquals(expected.get(g.getAttributes().get("location")), g.elements());
		}
	}

	@Test
	public void groupBy_multipleColumnsMatchesJdkGrouping() {
		// select location, style, salesman.name, count(*) from products group by location, style, salesman.name
		List<Product> products = Datasets.randomProducts(22L, 400);
		List<Group<Product>> groups = Selector.from(products).groupBy("location", String.class).groupBy("style", Product.Style.class)
				.groupBy("salesman.name", String.class).setTransformer(g -> g).list();
		Map<String, List<Product>> expected = Datasets.groupBy(products, Product::getLocation).values().stream()
				.flatMap(l -> Datasets.groupBy(l, Product::getStyle).values().stream())
				.flatMap(l -> Datasets.groupBy(l, p -> p.getSalesman() == null ? null : p.getSalesman().getName()).values().stream())
				.collect(Collectors.toMap(l -> l.get(0).getId() + "", l -> l, (a, b) -> a, java.util.LinkedHashMap::new));
		assertEquals(expected.size(), groups.size());
		List<List<Product>> expectedLists = new ArrayList<List<Product>>(expected.values());
		for (int i = 0; i < groups.size(); i++) {
			Group<Product> g = groups.get(i);
			assertEquals(expectedLists.get(i), g.elements());
			Product first = g.elements().get(0);
			assertEquals(Arrays.asList("location", "style", "salesman.name"), new ArrayList<String>(g.getAttributes().keySet()));
			assertEquals(first.getLocation(), g.getAttributes().get("location"));
			assertEquals(first.getStyle(), g.getAttributes().get("style"));
			// salesman.name is null when the salesman is null (no exception)
			assertEquals(first.getSalesman() == null ? null : first.getSalesman().getName(), g.getAttributes().get("salesman.name"));
			// the rollup chain reaches the top group of all the products
			assertEquals(products.size(), g.rollup().rollup().rollup().count());
		}
	}

	@Test
	public void groupBy_nullKeysAreGroupedTogether() {
		// owner.address.city: Paris, London, null (no address), null (no owner), Paris
		List<Group<Item>> groups = Selector.from(Item.samples()).groupBy("owner.address.city", String.class).setTransformer(g -> g).list();
		assertEquals(3, groups.size());
		assertEquals("Paris", groups.get(0).getAttributes().get("owner.address.city"));
		assertEquals(Arrays.asList(1, 5), groups.get(0).elements().stream().map(Item::getId).collect(Collectors.toList()));
		assertNull(groups.get(2).getAttributes().get("owner.address.city"));
		assertEquals(Arrays.asList(3, 4), groups.get(2).elements().stream().map(Item::getId).collect(Collectors.toList()));
	}

	@Test
	public void groupBy_convertedKeys() {
		// group by code as integer: 10, 9, null ("abc" and null), 30
		List<Group<Item>> groups = Selector.from(Item.samples()).groupBy("code", Integer.class).setTransformer(g -> g).list();
		assertEquals(Arrays.asList(10, 9, null, 30), groups.stream().map(g -> g.getAttributes().get("code")).collect(Collectors.toList()));
		assertEquals(2, groups.get(2).count());
	}

	@Test
	public void groupBy_withAliasAndFunction() {
		// select qty >= 10 as big, count(*) from items group by qty >= 10
		List<Group<Item>> groups = Selector.from(Item.samples()).groupBy(i -> i.getQty() != null && i.getQty() >= 10, "big")
				.setTransformer(g -> g).list();
		assertEquals(2, groups.size());
		assertEquals(Boolean.TRUE, groups.get(0).getAttributes().get("big"));
		assertEquals(3, groups.get(0).count());
		assertEquals(2, groups.get(1).count());
	}

	@Test
	public void groupBy_eachGroupHasItsOwnAttributes() {
		List<Group<Item>> groups = Selector.from(Item.samples()).groupBy("owner.address.city", String.class).setTransformer(g -> g).list();
		groups.get(0).getAttributes().put("extra", 1);
		assertEquals(1, groups.get(1).getAttributes().size());
		assertEquals(0, groups.get(1).rollup().getAttributes().size());
	}

}
