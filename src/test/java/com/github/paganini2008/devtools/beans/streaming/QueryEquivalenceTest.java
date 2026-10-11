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
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.github.paganini2008.devtools.MatchMode;
import com.github.paganini2008.devtools.collection.Tuple;

/**
 * QueryEquivalenceTest
 * <p>
 * Property style tests: complete queries (where + group by + having + order by
 * + limit/offset) with randomly chosen parameters on randomly generated data
 * (fixed seeds), each compared row by row with the same query computed by
 * plain JDK code.
 * 
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class QueryEquivalenceTest {

	private static List<Integer> ids(List<Product> list) {
		return list.stream().map(Product::getId).collect(Collectors.toList());
	}

	/**
	 * <pre>
	 * select * from products
	 *  where price between :lo and :hi and location is not null and (salesman.name = :name or sales &gt;= :sales)
	 *  order by location asc, price desc, id asc
	 *  limit :limit offset :offset
	 * </pre>
	 */
	@ParameterizedTest
	@ValueSource(longs = { 1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L })
	public void whereOrderByLimitOffset(long seed) {
		List<Product> products = Datasets.randomProducts(seed, 1000);
		Random random = new Random(seed * 31);
		int nonEmpty = 0;
		for (int round = 0; round < 20; round++) {
			float lo = random.nextInt(500), hi = lo + random.nextInt(600);
			String name = Datasets.SALESMEN[random.nextInt(Datasets.SALESMEN.length)];
			int sales = random.nextInt(60);
			int limit = random.nextInt(5) == 0 ? -1 : random.nextInt(50);
			int offset = random.nextInt(10) == 0 ? 400 + random.nextInt(400) : random.nextInt(60);

			Predicate<Product> where = Restrictions.<Product, Float>between("price", lo, hi, false).and(Restrictions.nonNull("location"))
					.and(Restrictions.<Product, String>eq("salesman.name", name).or(Restrictions.gte("sales", sales)));
			Sorter<Product> sorter = Orders.ascending("location", String.class);
			sorter = sorter.descending("price", Float.class).ascending("id", Integer.class);
			List<Product> actual = Selector.from(products).filter(where).orderBy(sorter).list(limit, offset);

			List<Product> expected = products.stream()
					.filter(p -> p.getPrice() != null && p.getPrice() >= lo && p.getPrice() <= hi && p.getLocation() != null)
					.filter(p -> (p.getSalesman() != null && name.equals(p.getSalesman().getName()))
							|| (p.getSales() != null && p.getSales() >= sales))
					.sorted(Comparator.comparing(Product::getLocation).thenComparing(Product::getPrice, Comparator.reverseOrder())
							.thenComparing(Product::getId))
					.skip(offset).limit(limit < 0 ? Long.MAX_VALUE : limit).collect(Collectors.toList());
			assertEquals(ids(expected), ids(actual), "round " + round);
			nonEmpty += actual.isEmpty() ? 0 : 1;
		}
		// the random parameters must not make the comparison trivial
		assertTrue(nonEmpty >= 10, "Only " + nonEmpty + " non-empty results");
	}

	/**
	 * <pre>
	 * select location, style, count(*), sum(sales), avg(freight), max(price) from products
	 *  where name not like 'p1%' and price &gt;= :minPrice
	 *  group by location, style
	 *  having count(*) &gt;= :minCount and sum(sales) &gt; :minSales
	 *  order by sum(sales) desc, location, style
	 *  limit :limit offset :offset
	 * </pre>
	 */
	@ParameterizedTest
	@ValueSource(longs = { 11L, 12L, 13L, 14L, 15L, 16L })
	public void whereGroupByHavingOrderByLimitOffset(long seed) {
		List<Product> products = Datasets.randomProducts(seed, 1500);
		Random random = new Random(seed * 17);
		int nonEmpty = 0;
		for (int round = 0; round < 10; round++) {
			float minPrice = random.nextInt(600);
			int minCount = random.nextInt(40);
			BigDecimal minSales = BigDecimal.valueOf(random.nextInt(1500));
			int limit = random.nextInt(4) == 0 ? -1 : 1 + random.nextInt(8);
			int offset = random.nextInt(6);

			Sorter<Group<Product>> sorter = Orders.groupDescending((GroupFunction<Product, BigDecimal>) g -> g.sum("sales"));
			sorter = sorter.ascending(g -> (String) g.getAttributes().get("location"))
					.ascending(g -> (Product.Style) g.getAttributes().get("style"));
			List<String> actual = Selector.from(products)
					.filter(Restrictions.<Product>matches("name", "p1", MatchMode.START).negate().and(Restrictions.gte("price", minPrice)))
					.groupBy("location", String.class).groupBy("style", Product.Style.class)
					.having(Having.gte(Functions.<Product>count(), minCount).and(Having.gt(Functions.sum("sales"), minSales))).orderBy(sorter)
					.setTransformer(new View<Product>() {
						protected void setAttributes(Tuple tuple, Group<Product> group) {
							tuple.set("count", group.count());
							tuple.set("sumSales", group.sum("sales"));
							tuple.set("avgFreight", group.avg("freight"));
							tuple.set("maxPrice", group.max(Product::getPrice));
						}
					}).list(limit, offset).stream().map(t -> t.get("location") + "|" + t.get("style") + "|" + t.get("count") + "|"
							+ ((BigDecimal) t.get("sumSales")).toPlainString() + "|" + t.get("avgFreight") + "|" + t.get("maxPrice"))
					.collect(Collectors.toList());

			List<Product> filtered = products.stream().filter(p -> !p.getName().startsWith("p1"))
					.filter(p -> p.getPrice() != null && p.getPrice() >= minPrice).collect(Collectors.toList());
			List<List<Product>> groups = new ArrayList<List<Product>>();
			for (List<Product> byLocation : Datasets.groupBy(filtered, Product::getLocation).values()) {
				groups.addAll(Datasets.groupBy(byLocation, Product::getStyle).values());
			}
			List<String> expected = groups.stream().filter(g -> g.size() >= minCount && Datasets.sumSales(g).compareTo(minSales) > 0)
					.sorted(Comparator.<List<Product>, BigDecimal>comparing(Datasets::sumSales).reversed()
							.thenComparing(g -> g.get(0).getLocation(), Datasets.<String>nullsFirst())
							.thenComparing(g -> g.get(0).getStyle(), Datasets.<Product.Style>nullsFirst()))
					.skip(offset).limit(limit < 0 ? Long.MAX_VALUE : limit)
					.map(g -> g.get(0).getLocation() + "|" + g.get(0).getStyle() + "|" + g.size() + "|" + Datasets.sumSales(g).toPlainString()
							+ "|" + Datasets.avgFreight(g, 2, RoundingMode.HALF_UP) + "|" + Datasets.maxPrice(g))
					.collect(Collectors.toList());
			assertEquals(expected, actual, "round " + round);
			nonEmpty += actual.isEmpty() ? 0 : 1;
		}
		assertTrue(nonEmpty >= 3, "Only " + nonEmpty + " non-empty results");
	}

	/**
	 * <pre>
	 * select salesman.name, count(*), min(price) from products
	 *  group by salesman.name
	 *  order by min(price) asc
	 * </pre>
	 * 
	 * Grouping by a nested property whose intermediate bean may be null, and ordering by an aggregation that can be null.
	 */
	@ParameterizedTest
	@ValueSource(longs = { 21L, 22L, 23L })
	public void groupByNestedPropertyOrderByNullableAggregation(long seed) {
		List<Product> products = Datasets.randomProducts(seed, 800);
		List<String> actual = Selector.from(products).groupBy("salesman.name", String.class)
				.orderBy(Orders.groupAscending((GroupFunction<Product, Float>) g -> g.min("price", Float.class)))
				.setTransformer(g -> g.getAttributes().get("salesman.name") + "|" + g.count() + "|" + g.min("price", Float.class)).list();

		Map<String, List<Product>> groups = Datasets.groupBy(products, p -> p.getSalesman() == null ? null : p.getSalesman().getName());
		List<String> expected = groups.entrySet().stream()
				.sorted(Comparator.comparing(e -> Datasets.minPrice(e.getValue()), Datasets.<Float>nullsFirst()))
				.map(e -> e.getKey() + "|" + e.getValue().size() + "|" + Datasets.minPrice(e.getValue())).collect(Collectors.toList());
		assertEquals(expected, actual);
	}

}
