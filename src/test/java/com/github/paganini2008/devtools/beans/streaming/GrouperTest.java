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
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import com.github.paganini2008.devtools.collection.Tuple;

/**
 * GrouperTest
 * 
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class GrouperTest {

	private static View<Product> summaryView() {
		return new View<Product>() {
			protected void setAttributes(Tuple tuple, Group<Product> group) {
				tuple.set("count", group.count());
				tuple.set("maxPrice", group.max("price", Float.class));
				tuple.set("minPrice", group.min("price", Float.class));
				tuple.set("avgFreight", group.avg("freight"));
				tuple.set("sumSales", group.sum("sales"));
			}
		};
	}

	@Test
	public void testGroupBySingleAttribute() {
		/*
		 * select location, count(*), max(price), min(price), avg(freight), sum(sales)
		 *   from Product group by location
		 */
		List<Tuple> rows = Selector.from(Product.samples()).groupBy("location", String.class).setTransformer(summaryView()).list();
		assertEquals(3, rows.size());

		Tuple london = rows.get(0);
		assertEquals("London", london.get("location"));
		assertEquals(3, london.get("count"));
		assertEquals(300f, london.get("maxPrice"));
		assertEquals(100f, london.get("minPrice"));
		assertEquals(new BigDecimal("58.33"), london.get("avgFreight"));
		assertEquals(new BigDecimal("35"), london.get("sumSales"));

		// null values are ignored by the aggregation functions
		Tuple tokyo = rows.get(1);
		assertEquals("Tokyo", tokyo.get("location"));
		assertEquals(2, tokyo.get("count"));
		assertEquals(new BigDecimal("60.00"), tokyo.get("avgFreight"));
		assertEquals(new BigDecimal("40"), tokyo.get("sumSales"));

		Tuple paris = rows.get(2);
		assertEquals("Paris", paris.get("location"));
		assertEquals(1, paris.get("count"));
		assertEquals(500f, paris.get("maxPrice"));
		assertEquals(500f, paris.get("minPrice"));
	}

	@Test
	public void testGroupByMultipleAttributes() {
		// select location, style, count(*) from Product group by location, style
		List<Group<Product>> groups = Selector.from(Product.samples()).groupBy("location", String.class)
				.groupBy("style", Product.Style.class).setTransformer(g -> g).list();
		assertEquals(4, groups.size());
		Group<Product> first = groups.get(0);
		assertEquals("London", first.getAttributes().get("location"));
		assertEquals(Product.Style.SMALL, first.getAttributes().get("style"));
		assertEquals(Arrays.asList("location", "style"), Arrays.asList(first.getAttributes().keySet().toArray()));
		assertEquals(Arrays.asList(1, 6), first.elements().stream().map(Product::getId).collect(Collectors.toList()));

		// rollup is the parent group
		Group<Product> parent = first.rollup();
		assertEquals(1, parent.getAttributes().size());
		assertEquals(3, parent.count());
		assertSame(parent, groups.get(1).rollup());
		assertEquals(0, parent.rollup().getAttributes().size());
		assertEquals(6, parent.rollup().count());
		assertNull(parent.rollup().rollup());

		assertEquals("London", groups.get(1).getAttributes().get("location"));
		assertEquals(Product.Style.LARGE, groups.get(1).getAttributes().get("style"));
		assertEquals("Tokyo", groups.get(2).getAttributes().get("location"));
		assertEquals("Paris", groups.get(3).getAttributes().get("location"));
	}

	@Test
	public void testHaving() {
		/*
		 * select location, style, avg(freight) from Product group by location, style
		 *   having avg(freight) > 55
		 */
		List<Tuple> rows = Selector.from(Product.samples()).groupBy("location", String.class).groupBy("style", Product.Style.class)
				.having(group -> group.avg("freight").compareTo(BigDecimal.valueOf(55)) > 0).setTransformer(summaryView()).list();
		assertEquals(3, rows.size());
		assertEquals("London", rows.get(0).get("location"));
		assertEquals(Product.Style.LARGE, rows.get(0).get("style"));
		assertEquals("Tokyo", rows.get(1).get("location"));
		assertEquals("Paris", rows.get(2).get("location"));

		// having count(*) >= 2
		List<Group<Product>> groups = Selector.from(Product.samples()).groupBy("location", String.class)
				.having(Having.gte(Functions.count(), 2)).setTransformer(g -> g).list();
		assertEquals(2, groups.size());

		// having sum(sales) between 30 and 40
		groups = Selector.from(Product.samples()).groupBy("location", String.class)
				.having(Having.between(Functions.sum("sales"), new BigDecimal("30"), new BigDecimal("40"))).setTransformer(g -> g)
				.list();
		assertEquals(2, groups.size());
		assertEquals("London", groups.get(0).getAttributes().get("location"));
		assertEquals("Tokyo", groups.get(1).getAttributes().get("location"));
	}

	@Test
	public void testHavingFunctions() {
		Groupable<Product> g = Selector.from(Product.samples()).groupBy("location", String.class);
		List<Group<Product>> groups = g.setTransformer(x -> x).list();
		Group<Product> london = groups.get(0), tokyo = groups.get(1), paris = groups.get(2);
		Aggregation<Product, Float> maxPrice = Functions.max("price", Float.class);
		assertTrue(Having.eq(maxPrice, 300f).test(london));
		assertTrue(!Having.eq(maxPrice, 300f).test(tokyo));
		assertTrue(Having.ne(maxPrice, 300f).test(tokyo));
		assertTrue(Having.lt(maxPrice, 400f).test(london));
		assertTrue(!Having.lt(maxPrice, 400f).test(tokyo));
		assertTrue(Having.lte(maxPrice, 400f).test(tokyo));
		assertTrue(Having.gt(maxPrice, 400f).test(paris));
		assertTrue(!Having.gt(maxPrice, 500f).test(paris));
		assertTrue(Having.gte(maxPrice, 500f).test(paris));
		assertTrue(Having.between(maxPrice, 300f, 400f).test(london));
		assertTrue(Having.between(maxPrice, 300f, 400f).test(tokyo));
		assertTrue(!Having.between(maxPrice, 300f, 400f).test(paris));
	}

	@Test
	public void testGroupOrderBy() {
		// select location, max(price) from Product group by location order by location
		List<Group<Product>> groups = Selector.from(Product.samples()).groupBy("location", String.class)
				.orderBy(Orders.groupAscending("location", String.class)).setTransformer(g -> g).list();
		assertEquals(Arrays.asList("London", "Paris", "Tokyo"),
				groups.stream().map(g -> g.getAttributes().get("location")).collect(Collectors.toList()));

		groups = Selector.from(Product.samples()).groupBy("location", String.class)
				.orderBy(Orders.groupDescending("location", String.class)).setTransformer(g -> g).list();
		assertEquals(Arrays.asList("Tokyo", "Paris", "London"),
				groups.stream().map(g -> g.getAttributes().get("location")).collect(Collectors.toList()));

		// order by count(*) desc, max(price) desc
		Sorter<Group<Product>> sorter = Orders.groupDescending((GroupFunction<Product, Integer>) Group::count);
		sorter = sorter.descending(g -> g.max("price", Float.class));
		groups = Selector.from(Product.samples()).groupBy("location", String.class).orderBy(sorter).setTransformer(g -> g).list();
		assertEquals(Arrays.asList("London", "Tokyo", "Paris"),
				groups.stream().map(g -> g.getAttributes().get("location")).collect(Collectors.toList()));

		// order by avg(freight) asc
		groups = Selector.from(Product.samples()).groupBy("location", String.class)
				.orderBy(Orders.groupAscending((GroupFunction<Product, BigDecimal>) g -> g.avg("freight"))).setTransformer(g -> g)
				.list();
		assertEquals(Arrays.asList("London", "Tokyo", "Paris"),
				groups.stream().map(g -> g.getAttributes().get("location")).collect(Collectors.toList()));
	}

	@Test
	public void testPagingGroups() {
		Groupable<Product> g = Selector.from(Product.samples()).groupBy("location", String.class)
				.orderBy(Orders.groupAscending("location", String.class));
		assertEquals(3, g.setTransformer(x -> x).rowCount());
		List<String> page = g.setTransformer(x -> (String) x.getAttributes().get("location")).list(2, 1);
		assertEquals(Arrays.asList("Paris", "Tokyo"), page);
	}

	@Test
	public void testGroupByNullKey() {
		// sql groups the null values together
		List<Product> products = Product.samples();
		products.get(1).setLocation(null);
		products.get(4).setLocation(null);
		List<Group<Product>> groups = Selector.from(products).groupBy(Product::getLocation, "location").setTransformer(x -> x).list();
		assertEquals(3, groups.size());
		assertEquals("London", groups.get(0).getAttributes().get("location"));
		assertEquals(2, groups.get(0).count());
		assertNull(groups.get(1).getAttributes().get("location"));
		assertTrue(groups.get(1).getAttributes().containsKey("location"));
		assertEquals(2, groups.get(1).count());
		assertEquals("Tokyo", groups.get(2).getAttributes().get("location"));
	}

	@Test
	public void testGroupWithoutGroupBy() {
		Grouper<Product> grouper = new Grouper<Product>(Product.samples());
		List<Group<Product>> groups = grouper.setTransformer(x -> x).list();
		assertEquals(1, groups.size());
		Group<Product> all = groups.get(0);
		assertEquals(6, all.count());
		assertEquals(new BigDecimal("125"), all.sum("sales"));
		assertEquals(new BigDecimal("65.000"), all.avg("freight", 3, RoundingMode.HALF_UP));
		assertEquals(Integer.valueOf(6), all.summarize(Functions.count()));
	}

}
