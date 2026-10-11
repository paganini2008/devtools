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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.github.paganini2008.devtools.collection.Tuple;
import com.github.paganini2008.devtools.jdbc.ResultSetSlice;

/**
 * HavingSpecTest
 * <p>
 * Specification of 'having': every {@link Having} factory method combined with
 * every aggregate function of {@link Functions} (matching, not matching and
 * exactly on the boundary), custom predicates and their combinations,
 * 'having' with single and multiple 'group by' columns, 'order by' and paging
 * after 'having', and null aggregation results.
 * <p>
 * Groups of {@link Product#samples()} by location:
 * 
 * <pre>
 * location count sum(sales) avg(freight) min(price) max(price) min(freight) max(freight)
 * London   3     35         58.33        100        300        50           70
 * Tokyo    2     40         60.00        200        400        null         60
 * Paris    1     50         90.00        500        500        90           90
 * </pre>
 * 
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class HavingSpecTest {

	private static final String LONDON = "London", TOKYO = "Tokyo", PARIS = "Paris";

	private static List<String> locations(String... locations) {
		return Arrays.asList(locations);
	}

	/**
	 * select location from products group by location having ...
	 */
	private static List<String> having(Predicate<Group<Product>> predicate) {
		return having(Product.samples(), predicate);
	}

	private static List<String> having(List<Product> products, Predicate<Group<Product>> predicate) {
		return Selector.from(products).groupBy("location", String.class).having(predicate)
				.setTransformer(g -> (String) g.getAttributes().get("location")).list();
	}

	private static BigDecimal dec(String s) {
		return new BigDecimal(s);
	}

	// ---------------------------------------------------------------- count(*)

	@Test
	public void count_eachOperator() {
		Aggregation<Product, Integer> count = Functions.count();
		assertEquals(locations(TOKYO), having(Having.eq(count, 2)));
		assertEquals(locations(), having(Having.eq(count, 4)));
		assertEquals(locations(LONDON, PARIS), having(Having.ne(count, 2)));
		assertEquals(locations(PARIS), having(Having.lt(count, 2)));
		assertEquals(locations(TOKYO, PARIS), having(Having.lte(count, 2)));
		assertEquals(locations(LONDON), having(Having.gt(count, 2)));
		assertEquals(locations(LONDON, TOKYO), having(Having.gte(count, 2)));
		assertEquals(locations(), having(Having.gt(count, 3)));
		assertEquals(locations(LONDON), having(Having.gte(count, 3)));
	}

	@Test
	public void count_betweenIncludesBothBounds() {
		Aggregation<Product, Integer> count = Functions.count();
		assertEquals(locations(TOKYO, PARIS), having(Having.between(count, 1, 2)));
		assertEquals(locations(LONDON, TOKYO), having(Having.between(count, 2, 3)));
		assertEquals(locations(TOKYO), having(Having.between(count, 2, 2)));
		assertEquals(locations(), having(Having.between(count, 4, 9)));
		assertEquals(locations(), having(Having.between(count, 3, 1)));
	}

	// ---------------------------------------------------------------- sum

	@Test
	public void sum_eachOperatorIgnoringScale() {
		Aggregation<Product, BigDecimal> sum = Functions.sum("sales");
		assertEquals(locations(LONDON), having(Having.eq(sum, dec("35"))));
		assertEquals(locations(LONDON), having(Having.eq(sum, dec("35.00"))));
		assertEquals(locations(LONDON, PARIS), having(Having.ne(sum, dec("40"))));
		assertEquals(locations(LONDON), having(Having.lt(sum, dec("40"))));
		assertEquals(locations(LONDON, TOKYO), having(Having.lte(sum, dec("40"))));
		assertEquals(locations(PARIS), having(Having.gt(sum, dec("40"))));
		assertEquals(locations(TOKYO, PARIS), having(Having.gte(sum, dec("40"))));
		assertEquals(locations(LONDON, TOKYO), having(Having.between(sum, dec("35"), dec("40"))));
		assertEquals(locations(), having(Having.between(sum, dec("35.01"), dec("39.99"))));
	}

	// ---------------------------------------------------------------- avg

	@Test
	public void avg_eachOperatorOnTheRoundedValue() {
		Aggregation<Product, BigDecimal> avg = Functions.avg("freight", 2, RoundingMode.HALF_UP);
		// London: 175 / 3 = 58.333.. rounded to 58.33
		assertEquals(locations(LONDON), having(Having.eq(avg, dec("58.33"))));
		assertEquals(locations(TOKYO), having(Having.eq(avg, dec("60"))));
		assertEquals(locations(LONDON, PARIS), having(Having.ne(avg, dec("60"))));
		assertEquals(locations(LONDON), having(Having.lt(avg, dec("60"))));
		assertEquals(locations(LONDON, TOKYO), having(Having.lte(avg, dec("60"))));
		assertEquals(locations(TOKYO, PARIS), having(Having.gt(avg, dec("58.33"))));
		assertEquals(locations(LONDON, TOKYO, PARIS), having(Having.gte(avg, dec("58.33"))));
		assertEquals(locations(LONDON, TOKYO), having(Having.between(avg, dec("58.33"), dec("60.00"))));
		// with another scale the boundary moves
		Aggregation<Product, BigDecimal> avg0 = Functions.avg("freight", 0, RoundingMode.HALF_UP);
		assertEquals(locations(LONDON), having(Having.eq(avg0, dec("58"))));
		Aggregation<Product, BigDecimal> avgUp = Functions.avg("freight", 1, RoundingMode.UP);
		assertEquals(locations(LONDON), having(Having.eq(avgUp, dec("58.4"))));
	}

	// ---------------------------------------------------------------- min / max

	@Test
	public void min_eachOperator() {
		Aggregation<Product, Float> min = Functions.min("price", Float.class);
		assertEquals(locations(TOKYO), having(Having.eq(min, 200f)));
		assertEquals(locations(LONDON, PARIS), having(Having.ne(min, 200f)));
		assertEquals(locations(LONDON), having(Having.lt(min, 200f)));
		assertEquals(locations(LONDON, TOKYO), having(Having.lte(min, 200f)));
		assertEquals(locations(PARIS), having(Having.gt(min, 200f)));
		assertEquals(locations(TOKYO, PARIS), having(Having.gte(min, 200f)));
		assertEquals(locations(LONDON, TOKYO), having(Having.between(min, 100f, 200f)));
	}

	@Test
	public void max_eachOperatorWithMethodReference() {
		Aggregation<Product, Float> max = Functions.max(Product::getPrice);
		assertEquals(locations(TOKYO), having(Having.eq(max, 400f)));
		assertEquals(locations(LONDON, PARIS), having(Having.ne(max, 400f)));
		assertEquals(locations(LONDON), having(Having.lt(max, 400f)));
		assertEquals(locations(LONDON, TOKYO), having(Having.lte(max, 400f)));
		assertEquals(locations(PARIS), having(Having.gt(max, 400f)));
		assertEquals(locations(TOKYO, PARIS), having(Having.gte(max, 400f)));
		assertEquals(locations(TOKYO, PARIS), having(Having.between(max, 400f, 500f)));
		assertEquals(locations(), having(Having.gt(max, 500f)));
	}

	// ---------------------------------------------------------------- null aggregation results

	@Test
	public void nullMin_isRegardedAsSmallestAndNeverThrows() {
		// Tokyo contains a null freight, so min(freight) is null
		Aggregation<Product, BigDecimal> minFreight = Functions.min("freight", BigDecimal.class);
		assertEquals(locations(LONDON, TOKYO), having(Having.lt(minFreight, dec("51"))));
		assertEquals(locations(LONDON, TOKYO), having(Having.lte(minFreight, dec("50"))));
		assertEquals(locations(LONDON, PARIS), having(Having.gt(minFreight, dec("0"))));
		assertEquals(locations(LONDON, PARIS), having(Having.gte(minFreight, dec("0"))));
		assertEquals(locations(LONDON, PARIS), having(Having.between(minFreight, dec("0"), dec("100"))));
		assertEquals(locations(TOKYO), having(Having.eq(minFreight, null)));
		assertEquals(locations(LONDON, PARIS), having(Having.ne(minFreight, null)));
		assertEquals(locations(LONDON, TOKYO), having(Having.between(minFreight, null, dec("50"))));
	}

	@Test
	public void allNullColumn_avgAndMaxAreNullButSumIsZero() {
		// Paris freight set to null: avg(freight) and max(freight) are null, sum(freight) is 0 (sql would give null)
		List<Product> products = Product.samples();
		products.get(4).setFreight(null);
		Aggregation<Product, BigDecimal> avg = Functions.avg("freight", 2, RoundingMode.HALF_UP);
		Aggregation<Product, BigDecimal> max = Functions.max("freight", BigDecimal.class);
		Aggregation<Product, BigDecimal> sum = Functions.sum("freight");
		assertEquals(locations(LONDON, TOKYO), having(products, Having.gt(avg, dec("0"))));
		assertEquals(locations(LONDON, TOKYO, PARIS), having(products, Having.lt(avg, dec("100"))));
		assertEquals(locations(PARIS), having(products, Having.eq(max, null)));
		assertEquals(locations(LONDON, TOKYO), having(products, Having.gte(max, dec("60"))));
		assertEquals(locations(PARIS), having(products, Having.eq(sum, dec("0"))));
		assertEquals(locations(), having(products, Having.between(avg, dec("90"), dec("90"))));
	}

	@Test
	public void customPredicateOnNullAggregationIsTheCallersResponsibility() {
		List<Product> products = Product.samples();
		products.get(4).setFreight(null);
		// a hand written condition must check for null itself: avg(freight) of Paris is null
		assertThrows(NullPointerException.class,
				() -> having(products, g -> g.avg("freight").compareTo(BigDecimal.ZERO) > 0));
		assertEquals(locations(LONDON, TOKYO),
				having(products, g -> g.avg("freight") != null && g.avg("freight").compareTo(BigDecimal.ZERO) > 0));
	}

	// ---------------------------------------------------------------- custom predicates and combinations

	@Test
	public void customLambda() {
		// having max(price) - min(price) >= 200
		assertEquals(locations(LONDON, TOKYO), having(g -> g.max(Product::getPrice) - g.min(Product::getPrice) >= 200));
	}

	@Test
	public void andOrNegate() {
		Predicate<Group<Product>> atLeastTwo = Having.gte(Functions.count(), 2);
		Predicate<Group<Product>> bigSales = Having.gt(Functions.sum("sales"), dec("36"));
		// having count(*) >= 2 and sum(sales) > 36
		assertEquals(locations(TOKYO), having(atLeastTwo.and(bigSales)));
		// having count(*) >= 2 or sum(sales) > 36
		assertEquals(locations(LONDON, TOKYO, PARIS), having(atLeastTwo.or(bigSales)));
		// having not (count(*) >= 2)
		assertEquals(locations(PARIS), having(atLeastTwo.negate()));
		// having not (count(*) >= 2 or sum(sales) > 36)
		assertEquals(locations(), having(atLeastTwo.or(bigSales).negate()));
		// having count(*) >= 2 and not sum(sales) > 36
		assertEquals(locations(LONDON), having(atLeastTwo.and(bigSales.negate())));
	}

	@Test
	public void successiveHavingCallsAreCombinedWithAnd() {
		Predicate<Group<Product>> atLeastTwo = Having.gte(Functions.count(), 2);
		Predicate<Group<Product>> bigSales = Having.gt(Functions.sum("sales"), dec("36"));
		List<String> chained = Selector.from(Product.samples()).groupBy("location", String.class).having(atLeastTwo).having(bigSales)
				.setTransformer(g -> (String) g.getAttributes().get("location")).list();
		assertEquals(locations(TOKYO), chained);
		// the order does not matter
		List<String> reversed = Selector.from(Product.samples()).groupBy("location", String.class).having(bigSales).having(atLeastTwo)
				.setTransformer(g -> (String) g.getAttributes().get("location")).list();
		assertEquals(chained, reversed);
		// a later 'having' never brings back a group removed by an earlier one
		List<String> widened = Selector.from(Product.samples()).groupBy("location", String.class).having(atLeastTwo)
				.having(g -> true).setTransformer(g -> (String) g.getAttributes().get("location")).list();
		assertEquals(locations(LONDON, TOKYO), widened);
	}

	@Test
	public void havingRejectsNullPredicate() {
		Groupable<Product> g = Selector.from(Product.samples()).groupBy("location", String.class);
		assertThrows(IllegalArgumentException.class, () -> g.having(null));
	}

	// ---------------------------------------------------------------- with group by / order by / paging

	@Test
	public void havingAfterMultipleColumnGroupBy() {
		// select location, style, count(*) from products group by location, style having count(*) >= 2
		List<Tuple> rows = Selector.from(Product.samples()).groupBy("location", String.class).groupBy("style", Product.Style.class)
				.having(Having.gte(Functions.count(), 2)).setTransformer(new View<Product>() {
					protected void setAttributes(Tuple tuple, Group<Product> group) {
						tuple.set("count", group.count());
					}
				}).list();
		assertEquals(2, rows.size());
		assertEquals(LONDON, rows.get(0).get("location"));
		assertEquals(Product.Style.SMALL, rows.get(0).get("style"));
		assertEquals(2, rows.get(0).get("count"));
		assertEquals(TOKYO, rows.get(1).get("location"));
		assertEquals(Product.Style.SMALL, rows.get(1).get("style"));
	}

	@Test
	public void havingBetweenGroupByLevelsOnlyDividesTheRemainingGroups() {
		// keep the locations having count(*) >= 2, then group them further by style
		List<Group<Product>> groups = Selector.from(Product.samples()).groupBy("location", String.class)
				.having(Having.gte(Functions.count(), 2)).groupBy("style", Product.Style.class).setTransformer(g -> g).list();
		assertEquals(Arrays.asList("London/SMALL", "London/LARGE", "Tokyo/SMALL"),
				groups.stream().map(g -> g.getAttributes().get("location") + "/" + g.getAttributes().get("style"))
						.collect(Collectors.toList()));
	}

	@Test
	public void havingThenOrderByAggregationThenPaging() {
		/*
		 * select location, sum(sales) from products group by location
		 *   having count(*) >= 1 and max(price) >= 300
		 *   order by sum(sales) desc limit 1 offset 1
		 */
		ResultSetSlice<String> slice = Selector.from(Product.samples()).groupBy("location", String.class)
				.having(Having.gte(Functions.count(), 1)).having(Having.gte(Functions.max(Product::getPrice), 300f))
				.orderBy(Orders.groupDescending((GroupFunction<Product, BigDecimal>) g -> g.sum("sales")))
				.setTransformer(g -> (String) g.getAttributes().get("location"));
		assertEquals(3, slice.rowCount());
		assertEquals(locations(PARIS, TOKYO, LONDON), slice.list());
		assertEquals(locations(TOKYO), slice.list(1, 1));
		assertEquals(locations(LONDON), slice.list(5, 2));
		assertEquals(locations(), slice.list(5, 3));
	}

	@Test
	public void havingFilteringOutAllGroupsResultsInEmptyList() {
		ResultSetSlice<Group<Product>> slice = Selector.from(Product.samples()).groupBy("location", String.class)
				.having(Having.gt(Functions.count(), 100)).orderBy(Orders.groupAscending("location", String.class))
				.setTransformer(g -> g);
		assertTrue(slice.list().isEmpty());
		assertEquals(0, slice.rowCount());
		assertNull(slice.first());
		assertTrue(slice.list(10, 0).isEmpty());
	}

	@Test
	public void havingOnEmptySource() {
		assertEquals(locations(), having(Collections.<Product>emptyList(), Having.gte(Functions.count(), 0)));
	}

	@Test
	public void havingOnNullGroupKey() {
		List<Product> products = Product.samples();
		products.get(4).setLocation(null);
		products.get(1).setLocation(null);
		// the null location group (products 2 and 5) has count 2
		assertEquals(Arrays.asList(LONDON, null, TOKYO), having(products, Having.eq(Functions.count(), 2)));
	}

	// ---------------------------------------------------------------- compared with plain JDK code

	@Test
	public void sqlExample_countAtLeastTwo_matchesStreamComputation() {
		// select location, count(*) from products group by location having count(*) >= 2
		List<Product> products = Datasets.randomProducts(7L, 500);
		List<Tuple> rows = Selector.from(products).groupBy("location", String.class).having(Having.gte(Functions.count(), 2))
				.setTransformer(new View<Product>() {
					protected void setAttributes(Tuple tuple, Group<Product> group) {
						tuple.set("count", group.count());
					}
				}).list();
		List<String> expected = new ArrayList<String>();
		for (Map.Entry<String, List<Product>> e : Datasets.groupBy(products, Product::getLocation).entrySet()) {
			if (e.getValue().size() >= 2) {
				expected.add(e.getKey() + "=" + e.getValue().size());
			}
		}
		assertEquals(expected, rows.stream().map(t -> t.get("location") + "=" + t.get("count")).collect(Collectors.toList()));
	}

	/**
	 * For every operator, aggregate and threshold (every distinct group value,
	 * its neighbors and null), the groups kept by {@link Having} are the same as
	 * the ones computed by plain JDK code.
	 */
	@ParameterizedTest
	@ValueSource(longs = { 1L, 2L, 3L })
	public void everyOperatorAndAggregate_matchesStreamComputation(long seed) {
		List<Product> products = Datasets.randomProducts(seed, 300);
		// grouped once, every 'having' predicate is applied to the same groups
		List<Group<Product>> grouped = Selector.from(products).groupBy("location", String.class).setTransformer(g -> g).list();
		Map<String, List<Product>> expectedGroups = Datasets.groupBy(products, Product::getLocation);
		checkAggregate(grouped, expectedGroups, Functions.count(), List::size, v -> v + 1);
		checkAggregate(grouped, expectedGroups, Functions.sum("sales"), Datasets::sumSales, v -> v.add(BigDecimal.ONE));
		checkAggregate(grouped, expectedGroups, Functions.avg("freight", 2, RoundingMode.HALF_UP),
				l -> Datasets.avgFreight(l, 2, RoundingMode.HALF_UP), v -> v.add(dec("0.01")));
		checkAggregate(grouped, expectedGroups, Functions.min("price", Float.class), Datasets::minPrice, v -> v + 1f);
		checkAggregate(grouped, expectedGroups, Functions.max(Product::getPrice), Datasets::maxPrice, v -> v + 1f);
	}

	private static <T extends Comparable<T>> void checkAggregate(List<Group<Product>> products, Map<String, List<Product>> groups,
			Aggregation<Product, T> aggregation, Function<List<Product>, T> oracle, Function<T, T> next) {
		TreeSet<T> thresholds = new TreeSet<T>(Datasets.<T>nullsFirst());
		for (List<Product> list : groups.values()) {
			T value = oracle.apply(list);
			if (value != null) {
				thresholds.add(value);
				thresholds.add(next.apply(value));
			}
		}
		List<T> values = new ArrayList<T>(thresholds);
		values.add(0, null);
		for (T v : values) {
			check(products, groups, Having.eq(aggregation, v), oracle, c -> Datasets.compare(c, v) == 0);
			check(products, groups, Having.ne(aggregation, v), oracle, c -> Datasets.compare(c, v) != 0);
			check(products, groups, Having.lt(aggregation, v), oracle, c -> Datasets.compare(c, v) < 0);
			check(products, groups, Having.lte(aggregation, v), oracle, c -> Datasets.compare(c, v) <= 0);
			check(products, groups, Having.gt(aggregation, v), oracle, c -> Datasets.compare(c, v) > 0);
			check(products, groups, Having.gte(aggregation, v), oracle, c -> Datasets.compare(c, v) >= 0);
		}
		for (int i = 0; i < values.size(); i += 3) {
			for (int j = 0; j < values.size(); j += 2) {
				T lo = values.get(i), hi = values.get(j);
				check(products, groups, Having.between(aggregation, lo, hi), oracle,
						c -> Datasets.compare(c, lo) >= 0 && Datasets.compare(c, hi) <= 0);
			}
		}
	}

	private static <T extends Comparable<T>> void check(List<Group<Product>> products, Map<String, List<Product>> groups,
			Predicate<Group<Product>> having, Function<List<Product>, T> oracle, Predicate<T> condition) {
		List<String> expected = groups.entrySet().stream().filter(e -> condition.test(oracle.apply(e.getValue()))).map(Map.Entry::getKey)
				.collect(Collectors.toList());
		assertEquals(expected, products.stream().filter(having).map(g -> (String) g.getAttributes().get("location"))
				.collect(Collectors.toList()));
	}

}
