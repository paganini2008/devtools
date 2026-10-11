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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import com.github.paganini2008.devtools.MatchMode;
import com.github.paganini2008.devtools.beans.SerializableFunction;
import com.github.paganini2008.devtools.collection.Tuple;

/**
 * MethodReferenceSpecTest
 * <p>
 * Every overload taking a getter method reference
 * ({@link com.github.paganini2008.devtools.beans.SerializableFunction}) gives
 * the same result as the corresponding overload taking a property name: the
 * same query is written both ways and compared.
 * 
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class MethodReferenceSpecTest {

	private static final List<Product> RANDOM = Datasets.randomProducts(99L, 600);

	private static List<Integer> where(Predicate<Item> predicate) {
		return Selector.from(Item.samples()).filter(predicate).list().stream().map(Item::getId).collect(Collectors.toList());
	}

	private static List<Integer> whereProduct(Predicate<Product> predicate) {
		return Selector.from(RANDOM).filter(predicate).list().stream().map(Product::getId).collect(Collectors.toList());
	}

	private static void same(Predicate<Item> byName, Predicate<Item> byGetter) {
		assertEquals(where(byName), where(byGetter));
	}

	private static void sameProduct(Predicate<Product> byName, Predicate<Product> byGetter) {
		List<Integer> expected = whereProduct(byName);
		assertEquals(expected, whereProduct(byGetter));
	}

	// ---------------------------------------------------------------- Restrictions (static)

	@Test
	public void restrictions_comparisonsAreTheSameAsByName() {
		for (Integer v : Arrays.asList(null, 5, 10, 15, 30)) {
			same(Restrictions.eq("qty", v), Restrictions.eq(Item::getQty, v));
			same(Restrictions.ne("qty", v), Restrictions.ne(Item::getQty, v));
			same(Restrictions.gt("qty", v), Restrictions.gt(Item::getQty, v));
			same(Restrictions.gte("qty", v), Restrictions.gte(Item::getQty, v));
			same(Restrictions.lt("qty", v), Restrictions.lt(Item::getQty, v));
			same(Restrictions.lte("qty", v), Restrictions.lte(Item::getQty, v));
			same(Restrictions.between("qty", v, 20, false), Restrictions.between(Item::getQty, v, 20, false));
			same(Restrictions.between("qty", v, 20, true), Restrictions.between(Item::getQty, v, 20, true));
		}
		for (Float v : Arrays.asList(1f, 300f, 551f, 991f)) {
			sameProduct(Restrictions.eq("price", v), Restrictions.eq(Product::getPrice, v));
			sameProduct(Restrictions.ne("price", v), Restrictions.ne(Product::getPrice, v));
			sameProduct(Restrictions.gt("price", v), Restrictions.gt(Product::getPrice, v));
			sameProduct(Restrictions.gte("price", v), Restrictions.gte(Product::getPrice, v));
			sameProduct(Restrictions.lt("price", v), Restrictions.lt(Product::getPrice, v));
			sameProduct(Restrictions.lte("price", v), Restrictions.lte(Product::getPrice, v));
			sameProduct(Restrictions.between("price", v, 600f, true), Restrictions.between(Product::getPrice, v, 600f, true));
		}
	}

	@Test
	public void restrictions_nullChecksAcceptNonComparableValues() {
		same(Restrictions.isNull("qty"), Restrictions.isNull(Item::getQty));
		same(Restrictions.nonNull("qty"), Restrictions.nonNull(Item::getQty));
		// Owner is not Comparable
		same(Restrictions.isNull("owner"), Restrictions.isNull(Item::getOwner));
		same(Restrictions.nonNull("owner"), Restrictions.nonNull(Item::getOwner));
		assertThrows(IllegalArgumentException.class, () -> Restrictions.isNull((SerializableFunction<Item, ?>) null));
		assertThrows(IllegalArgumentException.class, () -> Restrictions.nonNull((SerializableFunction<Item, ?>) null));
	}

	@Test
	public void restrictions_matches() {
		for (MatchMode mode : MatchMode.values()) {
			sameProduct(Restrictions.matches("location", mode == MatchMode.WILDCARD ? "*o*" : "o", mode),
					Restrictions.matches(Product::getLocation, mode == MatchMode.WILDCARD ? "*o*" : "o", mode));
			sameProduct(Restrictions.matches("location", "London", mode), Restrictions.matches(Product::getLocation, "London", mode));
		}
	}

	@Test
	public void restrictions_combinedLikeSql() {
		// where location = 'London' and price > 700
		List<Integer> byGetter = whereProduct(Restrictions.eq(Product::getLocation, "London").and(Restrictions.gt(Product::getPrice, 700f)));
		List<Integer> byName = whereProduct(Restrictions.<Product, String>eq("location", "London").and(Restrictions.gt("price", 700f)));
		assertEquals(byName, byGetter);
		assertTrue(byGetter.size() > 0);
		List<Integer> expected = RANDOM.stream().filter(p -> "London".equals(p.getLocation()) && p.getPrice() != null && p.getPrice() > 700f)
				.map(Product::getId).collect(Collectors.toList());
		assertEquals(expected, byGetter);
	}

	@Test
	public void restrictions_anyLambdaWorksAsTheValueIsReadByCallingIt() {
		// the property name is not needed to filter, so a lambda (here reading a nested value) is accepted
		assertEquals(Arrays.asList(1, 5), where(Restrictions.eq((Item i) -> i.getOwner() != null && i.getOwner().getAddress() != null
				? i.getOwner().getAddress().getCity()
				: null, "Paris")));
	}

	// ---------------------------------------------------------------- Restriction (chained defaults)

	@Test
	public void restriction_chainedConditionsAreTheSameAsByName() {
		List<Integer> values = Arrays.asList(10, 30);
		same(Restrictions.<Item>junction().nonNull("qty"), Restrictions.<Item>junction().nonNull(Item::getQty));
		same(Restrictions.<Item>junction().isNull("qty"), Restrictions.<Item>junction().isNull(Item::getQty));
		same(Restrictions.<Item>junction().between("qty", 10, 30, true), Restrictions.<Item>junction().between(Item::getQty, 10, 30, true));
		same(Restrictions.<Item>junction().eq("qty", 10), Restrictions.<Item>junction().eq(Item::getQty, 10));
		same(Restrictions.<Item>junction().ne("qty", 10), Restrictions.<Item>junction().ne(Item::getQty, 10));
		same(Restrictions.<Item>junction().lt("qty", 10), Restrictions.<Item>junction().lt(Item::getQty, 10));
		same(Restrictions.<Item>junction().lte("qty", 10), Restrictions.<Item>junction().lte(Item::getQty, 10));
		same(Restrictions.<Item>junction().gt("qty", 10), Restrictions.<Item>junction().gt(Item::getQty, 10));
		same(Restrictions.<Item>junction().gte("qty", 10), Restrictions.<Item>junction().gte(Item::getQty, 10));
		same(Restrictions.<Item>junction().eqAny("qty", values), Restrictions.<Item>junction().eqAny(Item::getQty, values));
		same(Restrictions.<Item>junction().eqAll("qty", values), Restrictions.<Item>junction().eqAll(Item::getQty, values));
		same(Restrictions.<Item>junction().ltAny("qty", values), Restrictions.<Item>junction().ltAny(Item::getQty, values));
		same(Restrictions.<Item>junction().ltAll("qty", values), Restrictions.<Item>junction().ltAll(Item::getQty, values));
		same(Restrictions.<Item>junction().lteAny("qty", values), Restrictions.<Item>junction().lteAny(Item::getQty, values));
		same(Restrictions.<Item>junction().lteAll("qty", values), Restrictions.<Item>junction().lteAll(Item::getQty, values));
		same(Restrictions.<Item>junction().gtAny("qty", values), Restrictions.<Item>junction().gtAny(Item::getQty, values));
		same(Restrictions.<Item>junction().gtAll("qty", values), Restrictions.<Item>junction().gtAll(Item::getQty, values));
		same(Restrictions.<Item>junction().gteAny("qty", values), Restrictions.<Item>junction().gteAny(Item::getQty, values));
		same(Restrictions.<Item>junction().gteAll("qty", values), Restrictions.<Item>junction().gteAll(Item::getQty, values));
		same(Restrictions.<Item>junction().matches("code", "1", MatchMode.START),
				Restrictions.<Item>junction().matches(Item::getCode, "1", MatchMode.START));
	}

	@Test
	public void restriction_chainedGetterConditionsReturnTheSameRestriction() {
		Restriction<Item> r = Restrictions.junction();
		// the getter overloads return Restriction, so property name conditions can follow
		Restriction<Item> same = r.gte(Item::getQty, 10).lt("qty", 30).nonNull(Item::getCode);
		assertSame(r, same);
		assertEquals(Arrays.asList(1, 2), where(r));
		Collection<Integer> any = Arrays.asList(20, 30);
		assertSame(r, r.eqAny(Item::getQty, any));
		assertEquals(Arrays.asList(2), where(r));
	}

	// ---------------------------------------------------------------- group by / order by groups / Summary

	@Test
	public void groupBy_getterUsesTheResolvedPropertyNameAsAttribute() {
		List<Map<String, Object>> byName = Selector.from(RANDOM).groupBy("location", String.class).groupBy("style", Product.Style.class)
				.setTransformer(Group::getAttributes).list();
		List<Map<String, Object>> byGetter = Selector.from(RANDOM).groupBy(Product::getLocation).groupBy(Product::getStyle)
				.setTransformer(Group::getAttributes).list();
		assertEquals(byName, byGetter);
		assertEquals(Arrays.asList("location", "style"), Arrays.asList(byGetter.get(0).keySet().toArray()));
	}

	@Test
	public void groupBy_ordinaryLambdaIsRejected() {
		IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
				() -> Selector.from(RANDOM).groupBy((Product p) -> p.getLocation()));
		assertTrue(e.getMessage().contains("getter"));
		Groupable<Product> g = Selector.from(RANDOM).groupBy(Product::getLocation);
		assertThrows(IllegalArgumentException.class, () -> g.groupBy((Product p) -> p.getStyle()));
	}

	@Test
	public void orders_groupAscendingByAndGroupDescendingBy() {
		List<Object> byName = Selector.from(RANDOM).groupBy(Product::getLocation).orderBy(Orders.groupAscending("location", String.class))
				.setTransformer(g -> g.getAttributes().get("location")).list();
		List<Object> byGetter = Selector.from(RANDOM).groupBy(Product::getLocation).orderBy(Orders.groupAscendingBy(Product::getLocation))
				.setTransformer(g -> g.getAttributes().get("location")).list();
		assertEquals(byName, byGetter);
		// the null location comes first
		assertNull(byGetter.get(0));
		assertEquals(Arrays.asList(null, "HongKong", "London", "NewYork", "Paris", "Tokyo"), byGetter);

		List<Object> descending = Selector.from(RANDOM).groupBy(Product::getLocation).orderBy(Orders.groupDescendingBy(Product::getLocation))
				.setTransformer(g -> g.getAttributes().get("location")).list();
		assertEquals(Arrays.asList("Tokyo", "Paris", "NewYork", "London", "HongKong", null), descending);
		assertThrows(IllegalArgumentException.class, () -> Orders.groupAscendingBy((Product p) -> p.getLocation()));
		assertThrows(IllegalArgumentException.class, () -> Orders.groupDescendingBy((Product p) -> p.getLocation()));
	}

	@Test
	public void summary_getterOverloadsAreTheSameAsByName() {
		for (Group<Product> g : Selector.from(RANDOM).groupBy(Product::getLocation).setTransformer(g -> g).list()) {
			assertEquals(g.sum("sales"), g.sum(Product::getSales));
			assertEquals(g.sum("freight"), g.sum(Product::getFreight));
			assertEquals(g.avg("freight"), g.avg(Product::getFreight));
			assertEquals(g.avg("sales", 3, RoundingMode.DOWN), g.avg(Product::getSales, 3, RoundingMode.DOWN));
			assertEquals(g.min("price", Float.class), g.min(Product::getPrice));
			assertEquals(g.max("price", Float.class), g.max(Product::getPrice));
			assertEquals(g.max("location", String.class), g.max(Product::getLocation));
		}
		Group<Item> all = new Grouper<Item>(Item.samples()).setTransformer(g -> g).first();
		// numeric strings are converted like the property name version
		assertEquals(all.sum("code"), all.sum(Item::getCode));
		assertEquals(all.avg("big"), all.avg(Item::getBig));
	}

	@Test
	public void functionsAndOrders_acceptGetterReferencesDirectly() {
		assertEquals(Functions.<Product, Float>max("price", Float.class).getResult(RANDOM), Functions.max(Product::getPrice).getResult(RANDOM));
		assertEquals(Functions.<Product>sum("freight").getResult(RANDOM), Functions.sum(Product::getFreight).getResult(RANDOM));
		Sorter<Product> byName = Orders.descending("price", Float.class);
		byName = byName.ascending("id", Integer.class);
		Sorter<Product> byGetter = Orders.descending(Product::getPrice);
		byGetter = byGetter.ascending(Product::getId);
		assertEquals(Selector.from(RANDOM).orderBy(byName).list(), Selector.from(RANDOM).orderBy(byGetter).list());
	}

	// ---------------------------------------------------------------- Transformers

	@Test
	public void transformers_asMapKeepsTheArgumentOrder() {
		Item first = Item.samples().get(0);
		Map<String, Object> row = Transformers.asMap(Item::getQty, Item::getId, Item::getOwner).transfer(first);
		assertEquals(Arrays.asList("qty", "id", "owner"), Arrays.asList(row.keySet().toArray()));
		assertEquals(10, row.get("qty"));
		assertEquals(1, row.get("id"));
		Map<String, Object> byName = Transformers.<Item>asMap(new String[] { "qty", "id", "owner" }).transfer(first);
		assertEquals(byName, row);
		// no getter: an empty map
		@SuppressWarnings("unchecked")
		SerializableFunction<Item, ?>[] none = new SerializableFunction[0];
		assertTrue(Transformers.asMap(none).transfer(Item.samples().get(0)).isEmpty());
	}

	@Test
	public void transformers_asMapThroughSelector() {
		// select name, price from products where location = 'Paris' order by price desc limit 3
		List<Map<String, Object>> rows = Selector.from(RANDOM).filter(Restrictions.eq(Product::getLocation, "Paris"))
				.orderBy(Orders.descending(Product::getPrice)).setTransformer(Transformers.asMap(Product::getName, Product::getPrice)).list(3);
		List<Map<String, Object>> byName = Selector.from(RANDOM).filter(Restrictions.eq("location", "Paris"))
				.orderBy(Orders.descending("price", Float.class)).setTransformer(Transformers.<Product>asMap(new String[] { "name", "price" }))
				.list(3);
		assertEquals(byName, rows);
		assertEquals(3, rows.size());
	}

	@Test
	public void transformers_asBeanCopiesTheReferredPropertiesOnly() {
		TransformersSpecTest.ItemDto dto = Transformers.asBean(TransformersSpecTest.ItemDto.class, Item::getQty, Item::getCode)
				.transfer(Item.samples().get(0));
		assertEquals(Long.valueOf(10L), dto.getQty());
		assertEquals("10", dto.getCode());
		assertEquals(0, dto.getId());
	}

	@Test
	public void transformers_rejectOrdinaryLambdasAndNull() {
		assertThrows(IllegalArgumentException.class, () -> Transformers.asMap(Item::getQty, (Item i) -> i.getId()));
		assertThrows(IllegalArgumentException.class, () -> Transformers.asBean(TransformersSpecTest.ItemDto.class, (Item i) -> i.getId()));
		assertThrows(IllegalArgumentException.class,
				() -> Transformers.<Item>asMap((SerializableFunction<Item, ?>[]) null));
	}

	// ---------------------------------------------------------------- complete query

	@Test
	public void completeQuery_writtenWithGetterReferences() {
		/*
		 * select location, style, count(*) as count, max(price) as maxPrice, sum(sales) as sumSales
		 *   from products
		 *  where location = 'London' and price > 300
		 *  group by location, style
		 *  having count(*) >= 2
		 *  order by style desc
		 */
		List<Tuple> rows = Selector.from(RANDOM).filter(Restrictions.eq(Product::getLocation, "London").and(Restrictions.gt(Product::getPrice, 300f)))
				.groupBy(Product::getLocation).groupBy(Product::getStyle).having(Having.gte(Functions.count(), 2))
				.orderBy(Orders.groupDescendingBy(Product::getStyle)).setTransformer(new View<Product>() {
					protected void setAttributes(Tuple tuple, Group<Product> group) {
						tuple.set("count", group.count());
						tuple.set("maxPrice", group.max(Product::getPrice));
						tuple.set("sumSales", group.sum(Product::getSales));
					}
				}).list();

		Map<Product.Style, List<Product>> expected = Datasets.groupBy(
				RANDOM.stream().filter(p -> "London".equals(p.getLocation()) && p.getPrice() != null && p.getPrice() > 300f)
						.collect(Collectors.toList()),
				Product::getStyle);
		List<String> expectedRows = expected.entrySet().stream().filter(e -> e.getValue().size() >= 2)
				.sorted((a, b) -> Datasets.compare(b.getKey(), a.getKey()))
				.map(e -> "London|" + e.getKey() + "|" + e.getValue().size() + "|" + Datasets.maxPrice(e.getValue()) + "|"
						+ Datasets.sumSales(e.getValue()))
				.collect(Collectors.toList());
		assertEquals(expectedRows, rows.stream().map(t -> t.get("location") + "|" + t.get("style") + "|" + t.get("count") + "|" + t.get("maxPrice")
				+ "|" + t.get("sumSales")).collect(Collectors.toList()));
		assertTrue(rows.size() >= 2);
		assertEquals(new BigDecimal(rows.get(0).get("sumSales").toString()), rows.get(0).get("sumSales"));
	}

}
