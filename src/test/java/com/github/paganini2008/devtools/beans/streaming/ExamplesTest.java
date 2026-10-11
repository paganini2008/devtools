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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.github.paganini2008.devtools.RandomUtils;
import com.github.paganini2008.devtools.collection.Tuple;
import com.github.paganini2008.devtools.mock.BeanMocker;
import com.github.paganini2008.devtools.mock.MockContext;

/**
 * ExamplesTest
 * <p>
 * The original usage examples of the in-memory query, written exactly as they
 * were documented, each compared with the same query computed by plain JDK
 * code on 3000 mocked products (10000 in the original examples, fewer here to
 * keep the test fast).
 * 
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class ExamplesTest {

	private static List<ExampleProduct> products;

	@BeforeAll
	public static void mock() {
		String[] users = new String[] { "Petter", "Jack", "Tony" };
		String[] locations = new String[] { "London", "NewYork", "Tokyo", "HongKong", "Paris" };
		MockContext mc = new MockContext();
		mc.setStringSupplier("name", () -> {
			return "Product_" + RandomUtils.randomInt(1, 100000);
		});
		mc.setStringSupplier("location", () -> {
			return RandomUtils.randomChoice(locations);
		});
		mc.setStringSupplier("username", () -> {
			return RandomUtils.randomChoice(users);
		});
		products = BeanMocker.mockBeans(3000, ExampleProduct.class, mc);
		assertEquals(3000, products.size());
	}

	private static Map<String, List<ExampleProduct>> groupBy(List<ExampleProduct> list, java.util.function.Function<ExampleProduct, Object> key) {
		Map<String, List<ExampleProduct>> map = new LinkedHashMap<String, List<ExampleProduct>>();
		for (ExampleProduct p : list) {
			map.computeIfAbsent(String.valueOf(key.apply(p)), k -> new ArrayList<ExampleProduct>()).add(p);
		}
		return map;
	}

	private static BigDecimal avgFreight(List<ExampleProduct> list) {
		List<BigDecimal> values = list.stream().map(ExampleProduct::getFreight).filter(Objects::nonNull).collect(Collectors.toList());
		return values.isEmpty() ? null
				: values.stream().reduce(BigDecimal.ZERO, BigDecimal::add).divide(BigDecimal.valueOf(values.size()), 2, RoundingMode.HALF_UP);
	}

	private static void assertSummary(Tuple tuple, List<ExampleProduct> group) {
		assertEquals(group.stream().map(ExampleProduct::getPrice).max(Comparator.naturalOrder()).get(), tuple.get("maxPrice"));
		assertEquals(group.stream().map(ExampleProduct::getPrice).min(Comparator.naturalOrder()).get(), tuple.get("minPrice"));
		assertEquals(avgFreight(group), tuple.get("avgFreight"));
		BigDecimal sumSales = group.stream().map(p -> new BigDecimal(p.getSales())).reduce(BigDecimal.ZERO, BigDecimal::add);
		assertEquals(0, sumSales.compareTo((BigDecimal) tuple.get("sumSales")));
	}

	/**
	 * select * from Product where location='London'
	 */
	@Test
	public void test() {
		Predicate<ExampleProduct> predicate = Restrictions.eq("location", "London");
		List<ExampleProduct> result = Selector.from(products).filter(predicate).list();
		assertFalse(result.isEmpty());
		assertEquals(products.stream().filter(p -> "London".equals(p.getLocation())).collect(Collectors.toList()), result);
	}

	/**
	 * select * from Product where created &lt;= now() and salesman.name='Petter'
	 */
	@Test
	public void test1() {
		Date now = new Date();
		Predicate<ExampleProduct> predicate = Restrictions.lte("created", now);
		predicate = predicate.and(Restrictions.eq("salesman.name", "Petter"));
		List<ExampleProduct> result = Selector.from(products).filter(predicate).list();
		assertFalse(result.isEmpty());
		assertEquals(products.stream().filter(p -> !p.getCreated().after(now) && "Petter".equals(p.getSalesman().getName()))
				.collect(Collectors.toList()), result);
	}

	/**
	 * select location, max(price) as maxPrice, min(price) as minPrice, avg(freight) as avgFreight, sum(sales) as sumSales
	 * from Product group by location
	 */
	@Test
	public void test2() {
		List<Tuple> rows = Selector.from(products).groupBy("location", String.class).setTransformer(new View<ExampleProduct>() {
			protected void setAttributes(Tuple tuple, Group<ExampleProduct> group) {
				tuple.set("maxPrice", group.max("price", Float.class));
				tuple.set("minPrice", group.min("price", Float.class));
				tuple.set("avgFreight", group.avg("freight"));
				tuple.set("sumSales", group.sum("sales"));
			}
		}).list();
		Map<String, List<ExampleProduct>> expected = groupBy(products, ExampleProduct::getLocation);
		assertEquals(expected.size(), rows.size());
		for (Tuple tuple : rows) {
			assertSummary(tuple, expected.get(tuple.get("location")));
		}
	}

	/**
	 * select location, style, max(price) as maxPrice, min(price) as minPrice, avg(freight) as avgFreight, sum(sales) as
	 * sumSales from Product group by location, style having avg(freight) &gt; 55
	 */
	@Test
	public void test3() {
		List<Tuple> rows = Selector.from(products).groupBy("location", String.class).groupBy("style", ExampleProduct.Style.class)
				.having(group -> {
					return group.avg("freight").compareTo(BigDecimal.valueOf(55)) > 0;
				}).setTransformer(new View<ExampleProduct>() {
					protected void setAttributes(Tuple tuple, Group<ExampleProduct> group) {
						tuple.set("maxPrice", group.max("price", Float.class));
						tuple.set("minPrice", group.min("price", Float.class));
						tuple.set("avgFreight", group.avg("freight"));
						tuple.set("sumSales", group.sum("sales"));
					}
				}).list();
		// nested grouping: the groups of the second column inside the groups of the first one
		Map<String, List<ExampleProduct>> expected = new LinkedHashMap<String, List<ExampleProduct>>();
		for (List<ExampleProduct> byLocation : groupBy(products, ExampleProduct::getLocation).values()) {
			for (List<ExampleProduct> byStyle : groupBy(byLocation, ExampleProduct::getStyle).values()) {
				expected.put(byStyle.get(0).getLocation() + "/" + byStyle.get(0).getStyle(), byStyle);
			}
		}
		expected.values().removeIf(group -> avgFreight(group).compareTo(BigDecimal.valueOf(55)) <= 0);
		assertEquals(new ArrayList<String>(expected.keySet()),
				rows.stream().map(t -> t.get("location") + "/" + t.get("style")).collect(Collectors.toList()));
		for (Tuple tuple : rows) {
			assertTrue(((BigDecimal) tuple.get("avgFreight")).compareTo(BigDecimal.valueOf(55)) > 0);
			assertSummary(tuple, expected.get(tuple.get("location") + "/" + tuple.get("style")));
		}
	}

	/**
	 * select name, price, freight from Product order by price desc, freight asc limit 100
	 */
	@Test
	public void test4() {
		Sorter<ExampleProduct> sorter = Orders.descending("price", Float.class);
		sorter = sorter.ascending("freight", BigDecimal.class);
		List<ExampleProduct> result = Selector.from(products).orderBy(sorter).list(100);
		List<ExampleProduct> expected = products.stream()
				.sorted(Comparator.comparing(ExampleProduct::getPrice).reversed().thenComparing(ExampleProduct::getFreight)).limit(100)
				.collect(Collectors.toList());
		assertEquals(100, result.size());
		assertEquals(expected, result);
	}

}
