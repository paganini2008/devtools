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
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import com.github.paganini2008.devtools.jdbc.ResultSetSlice;

/**
 * SelectorSpecTest
 * <p>
 * Specification of 'order by', 'limit/offset' and 'distinct', and of the
 * behavior on empty, single element, unmodifiable and large inputs.
 * 
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class SelectorSpecTest {

	private static List<Integer> ids(List<Product> list) {
		return list.stream().map(Product::getId).collect(Collectors.toList());
	}

	private static List<Integer> itemIds(List<Item> list) {
		return list.stream().map(Item::getId).collect(Collectors.toList());
	}

	// ---------------------------------------------------------------- order by

	@Test
	public void orderBy_propertyNameAndFunctionAreEquivalent() {
		List<Product> products = Datasets.randomProducts(11L, 300);
		Sorter<Product> byName = Orders.ascending("location", String.class);
		byName = byName.descending("price", Float.class).ascending("id", Integer.class);
		Sorter<Product> byFunction = Orders.ascending(Product::getLocation);
		byFunction = byFunction.descending(Product::getPrice).ascending(Product::getId);
		assertEquals(ids(Selector.from(products).orderBy(byName).list()), ids(Selector.from(products).orderBy(byFunction).list()));
	}

	@Test
	public void orderBy_multipleColumnsMatchesJdkSort() {
		// order by location asc (nulls first), price desc (nulls last)
		List<Product> products = Datasets.randomProducts(12L, 500);
		Sorter<Product> sorter = Orders.ascending("location", String.class);
		sorter = sorter.descending("price", Float.class);
		List<Product> expected = new ArrayList<Product>(products);
		expected.sort(Comparator.comparing(Product::getLocation, Comparator.nullsFirst(Comparator.<String>naturalOrder()))
				.thenComparing(Product::getPrice, Comparator.nullsLast(Comparator.<Float>reverseOrder())));
		assertEquals(ids(expected), ids(Selector.from(products).orderBy(sorter).list()));
	}

	@Test
	public void orderBy_ascendingPutsNullsFirstAndDescendingNullsLast() {
		// qty: 10, 20, null, 5, 30
		assertEquals(Arrays.asList(3, 4, 1, 2, 5), itemIds(Selector.from(Item.samples()).orderBy(Orders.ascending("qty", Integer.class)).list()));
		assertEquals(Arrays.asList(5, 2, 1, 4, 3), itemIds(Selector.from(Item.samples()).orderBy(Orders.descending(Item::getQty)).list()));
	}

	@Test
	public void orderBy_isStable() {
		// rows with the same location keep their original order
		List<Product> products = Datasets.randomProducts(13L, 400);
		List<Product> sorted = Selector.from(products).orderBy(Orders.ascending(Product::getLocation)).list();
		for (int i = 1; i < sorted.size(); i++) {
			Product a = sorted.get(i - 1), b = sorted.get(i);
			if (Datasets.compare(a.getLocation(), b.getLocation()) == 0) {
				assertTrue(a.getId() < b.getId(), "Not stable at " + i);
			}
		}
	}

	@Test
	public void orderBy_calledTwiceSortsByTheLastKeyFirst() {
		// a stable sort: orderBy(a) then orderBy(b) is like 'order by b, a'
		List<Product> products = Datasets.randomProducts(14L, 300);
		List<Product> twice = Selector.from(products).orderBy(Orders.ascending(Product::getPrice)).orderBy(Orders.ascending(Product::getLocation))
				.list();
		Sorter<Product> once = Orders.ascending(Product::getLocation);
		once = once.ascending(Product::getPrice);
		assertEquals(ids(Selector.from(products).orderBy(once).list()), ids(twice));
	}

	@Test
	public void orderBy_convertsByRequiredType() {
		// code: "10", "9", "abc", null, "30": numeric order, the unconvertible "abc" is regarded as null
		assertEquals(Arrays.asList(3, 4, 2, 1, 5), itemIds(Selector.from(Item.samples()).orderBy(Orders.ascending("code", Integer.class)).list()));
		// string order
		assertEquals(Arrays.asList(4, 1, 5, 2, 3), itemIds(Selector.from(Item.samples()).orderBy(Orders.ascending("code", String.class)).list()));
	}

	@Test
	public void orderBy_nestedPropertyWithNullIntermediate() {
		// owner.address.city: Paris, London, (no address), (no owner), Paris
		assertEquals(Arrays.asList(3, 4, 2, 1, 5),
				itemIds(Selector.from(Item.samples()).orderBy(Orders.ascending("owner.address.city", String.class)).list()));
		assertEquals(Arrays.asList(1, 5, 2, 3, 4),
				itemIds(Selector.from(Item.samples()).orderBy(Orders.descending("owner.address.city", String.class)).list()));
	}

	@Test
	public void orderBy_rejectsNullSorter() {
		assertThrows(IllegalArgumentException.class, () -> Selector.from(Item.samples()).orderBy(null));
	}

	@Test
	public void beanSorter_resetRemovesAllKeys() {
		BeanSorter<Item> sorter = new BeanSorter<Item>();
		assertSame(sorter, sorter.descending(Item::getId));
		assertEquals(Arrays.asList(5, 4, 3, 2, 1), itemIds(Selector.from(Item.samples()).orderBy(sorter).list()));
		assertSame(sorter, sorter.reset());
		assertEquals(0, sorter.compare(Item.samples().get(0), Item.samples().get(4)));
		// without keys the (stable) sort keeps the order
		assertEquals(Arrays.asList(1, 2, 3, 4, 5), itemIds(Selector.from(Item.samples()).orderBy(sorter).list()));
	}

	// ---------------------------------------------------------------- limit / offset

	@Test
	public void limitOffset_boundaries() {
		Selector<Item> s = Selector.from(Item.samples());
		assertEquals(Arrays.asList(), itemIds(s.list(0)));
		assertEquals(Arrays.asList(), itemIds(s.list(0, 2)));
		assertEquals(Arrays.asList(1), itemIds(s.list(1)));
		assertEquals(Arrays.asList(5), itemIds(s.list(1, 4)));
		assertEquals(Arrays.asList(), itemIds(s.list(1, 5)));
		assertEquals(Arrays.asList(4, 5), itemIds(s.list(5, 3)));
		// any negative limit means no limit
		assertEquals(Arrays.asList(1, 2, 3, 4, 5), itemIds(s.list(-1)));
		assertEquals(Arrays.asList(3, 4, 5), itemIds(s.list(-5, 2)));
		assertEquals(Arrays.asList(3, 4, 5), itemIds(s.list(Integer.MIN_VALUE, 2)));
		// extreme values never overflow
		assertEquals(Arrays.asList(5), itemIds(s.list(Integer.MAX_VALUE, 4)));
		assertEquals(Arrays.asList(), itemIds(s.list(Integer.MAX_VALUE, Integer.MAX_VALUE)));
		// a negative offset is rejected
		IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> s.list(1, -1));
		assertTrue(e.getMessage().contains("-1"));
		assertThrows(IllegalArgumentException.class, () -> s.list(1, Integer.MIN_VALUE));
	}

	@Test
	public void limitOffset_sameSemanticsAfterTransformer() {
		ResultSetSlice<Integer> slice = Selector.from(Item.samples()).setTransformer(Item::getId);
		assertEquals(5, slice.rowCount());
		assertEquals(Arrays.asList(), slice.list(0));
		assertEquals(Arrays.asList(4, 5), slice.list(5, 3));
		assertEquals(Arrays.asList(3, 4, 5), slice.list(-7, 2));
		assertEquals(Arrays.asList(), slice.list(Integer.MAX_VALUE, Integer.MAX_VALUE));
		assertEquals(Integer.valueOf(1), slice.first());
		assertThrows(IllegalArgumentException.class, () -> slice.list(1, -1));
	}

	@Test
	public void limitOffset_transformerIsOnlyAppliedToTheRequestedPage() {
		List<Integer> transformed = new ArrayList<Integer>();
		ResultSetSlice<Integer> slice = Selector.from(Item.samples()).setTransformer(i -> {
			transformed.add(i.getId());
			return i.getId();
		});
		slice.list(2, 1);
		assertEquals(Arrays.asList(2, 3), transformed);
	}

	// ---------------------------------------------------------------- distinct

	@Test
	public void distinct_keepsTheFirstOccurrenceInOrder() {
		List<Product> products = Product.samples();
		List<Product> source = new ArrayList<Product>();
		source.add(products.get(2));
		source.add(products.get(0));
		source.add(products.get(2));
		source.add(products.get(1));
		source.add(products.get(0));
		List<Product> result = Selector.from(source).distinct().list();
		assertEquals(Arrays.asList(3, 1, 2), ids(result));
		// the first instance is kept
		assertSame(source.get(0), result.get(0));
	}

	@Test
	public void distinct_afterProjectionLikeSelectDistinct() {
		// select distinct location from products order by location
		List<Product> products = Datasets.randomProducts(15L, 200);
		List<String> locations = Selector.from(products).orderBy(Orders.ascending(Product::getLocation)).setTransformer(Product::getLocation)
				.list().stream().distinct().collect(Collectors.toList());
		List<String> expected = products.stream().map(Product::getLocation).distinct()
				.sorted(Comparator.nullsFirst(Comparator.<String>naturalOrder())).collect(Collectors.toList());
		assertEquals(expected, locations);
	}

	@Test
	public void distinct_onIdentityEqualsObjects() {
		Item a = new Item(), b = new Item();
		// without an equals override the instances are compared by identity
		List<Item> result = Selector.from(Arrays.asList(a, b, a, b, a)).distinct().list();
		assertEquals(2, result.size());
		assertSame(a, result.get(0));
		assertSame(b, result.get(1));
	}

	// ---------------------------------------------------------------- inputs

	@Test
	public void emptyInput() {
		Selectable<Item> s = Selector.from(Collections.<Item>emptyList()).filter(Restrictions.gt("qty", 1))
				.orderBy(Orders.ascending(Item::getId)).distinct();
		assertEquals(0, s.rowCount());
		assertTrue(s.list().isEmpty());
		assertTrue(s.list(10, 0).isEmpty());
		assertTrue(s.list(10, 10).isEmpty());
		assertNull(s.first());
		assertTrue(Selector.from(Collections.<Item>emptyList()).groupBy("qty", Integer.class).setTransformer(g -> g).list().isEmpty());
	}

	@Test
	public void singleElementInput() {
		List<Item> one = Collections.singletonList(Item.samples().get(0));
		Selectable<Item> s = Selector.from(one).filter(Restrictions.eq("qty", 10)).orderBy(Orders.descending(Item::getId)).distinct();
		assertEquals(Arrays.asList(1), itemIds(s.list()));
		assertEquals(Arrays.asList(), itemIds(s.list(1, 1)));
		List<Group<Item>> groups = Selector.from(one).groupBy("qty", Integer.class).setTransformer(g -> g).list();
		assertEquals(1, groups.size());
		assertEquals(10, groups.get(0).getAttributes().get("qty"));
	}

	@Test
	public void unmodifiableInputIsNeverModified() {
		List<Item> source = Collections.unmodifiableList(Item.samples());
		List<Integer> before = itemIds(source);
		Selectable<Item> s = Selector.from(source).orderBy(Orders.descending(Item::getId)).filter(Restrictions.gt("id", 1)).distinct();
		assertEquals(Arrays.asList(5, 4, 3, 2), itemIds(s.list()));
		assertEquals(before, itemIds(source));
		assertThrows(UnsupportedOperationException.class, () -> s.list().add(new Item()));
	}

	@Test
	public void resultListsAreSnapshotsIndependentOfLaterOperations() {
		Selectable<Item> s = Selector.from(Item.samples());
		List<Item> all = s.list();
		s.filter(Restrictions.gt("id", 3));
		assertEquals(5, all.size());
		assertEquals(2, s.rowCount());
	}

	@Test
	@Timeout(value = 20, unit = TimeUnit.SECONDS)
	public void largeInput_hundredThousandRowsInLinearithmicTime() {
		List<Product> products = Datasets.randomProducts(16L, 100000);
		long begin = System.nanoTime();
		// select * from products where price >= 500 and location is not null order by location, price desc limit 100 offset 1000
		Sorter<Product> sorter = Orders.ascending("location", String.class);
		sorter = sorter.descending("price", Float.class);
		List<Product> page = Selector.from(products).filter(Restrictions.<Product, Float>gte("price", 500f).and(Restrictions.nonNull("location")))
				.orderBy(sorter).list(100, 1000);
		// select location, count(*) from products group by location
		List<Group<Product>> groups = Selector.from(products).groupBy("location", String.class).setTransformer(g -> g).list();
		Selectable<Product> distinct = Selector.from(products).distinct();
		long elapsed = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - begin);

		List<Product> expected = products.stream().filter(p -> p.getPrice() != null && p.getPrice() >= 500f && p.getLocation() != null)
				.sorted(Comparator.comparing(Product::getLocation).thenComparing(Product::getPrice, Comparator.reverseOrder())).skip(1000)
				.limit(100).collect(Collectors.toList());
		assertEquals(ids(expected), ids(page));
		Map<String, List<Product>> expectedGroups = Datasets.groupBy(products, Product::getLocation);
		assertEquals(expectedGroups.size(), groups.size());
		for (Group<Product> g : groups) {
			assertEquals(expectedGroups.get(g.getAttributes().get("location")).size(), g.count());
		}
		assertEquals(100000, distinct.rowCount());
		// generous bound, a quadratic implementation would take minutes
		assertTrue(elapsed < 15000, "Took " + elapsed + " ms");
	}

}
