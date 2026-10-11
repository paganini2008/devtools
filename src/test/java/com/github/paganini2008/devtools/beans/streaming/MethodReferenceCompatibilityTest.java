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
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import com.github.paganini2008.devtools.FunctionalPredicate;
import com.github.paganini2008.devtools.MatchMode;
import com.github.paganini2008.devtools.beans.PropertyFilter;
import com.github.paganini2008.devtools.collection.Tuple;

/**
 * MethodReferenceCompatibilityTest
 * <p>
 * Compile time compatibility of the overloads taking a getter method reference:
 * every call form that compiled before they were added (property names,
 * lambdas and method references passed to the {@link Function} overloads, the
 * no-argument and filter overloads) must still compile without "reference is
 * ambiguous" and keep its behavior. The class compiling is the main assertion;
 * the asserts check that the calls still work.
 * 
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class MethodReferenceCompatibilityTest {

	private static List<Integer> where(Predicate<Item> predicate) {
		return Selector.from(Item.samples()).filter(predicate).list().stream().map(Item::getId).collect(Collectors.toList());
	}

	@Test
	public void restrictionsWithPropertyNames() {
		String name = null;
		Predicate<Item> p;
		p = Restrictions.eq("qty", 10);
		p = p.and(Restrictions.eq("qty", Integer.class, 10)).and(Restrictions.ne("qty", 11)).and(Restrictions.ne("qty", Integer.class, 11));
		p = p.and(Restrictions.gt("qty", 1)).and(Restrictions.gt("qty", Integer.class, 1)).and(Restrictions.gte("qty", 10));
		p = p.and(Restrictions.gte("qty", Integer.class, 10)).and(Restrictions.lt("qty", 11)).and(Restrictions.lt("qty", Integer.class, 11));
		p = p.and(Restrictions.lte("qty", 10)).and(Restrictions.lte("qty", Integer.class, 10)).and(Restrictions.between("qty", 1, 10, false));
		p = p.and(Restrictions.nonNull("qty")).and(Restrictions.<Item>isNull("owner").negate()).and(Restrictions.matches("code", "1", MatchMode.START));
		p = p.and(Restrictions.eq("qty", (Integer) null).negate());
		assertEquals(Arrays.asList(1), where(p));
		// a null name in a variable still resolves to the property name overload
		final String nullName = name;
		assertThrowsIllegalArgument(() -> where(Restrictions.isNull(nullName)));
	}

	@Test
	public void restrictionChainWithFunctionsAndLambdas() {
		Restriction<Item> r = Restrictions.junction();
		// implicitly typed lambdas: resolved to the more specific getter overloads, same behavior as before
		r.gt(i -> i.getQty(), 5).lt(i -> i.getQty(), 30).nonNull(i -> i.getCode()).isNull(i -> i.getCreated());
		r.between(i -> i.getQty(), 0, 100, false).eqAny(i -> i.getQty(), Arrays.asList(10, 20)).matches(i -> i.getCode(), "1", MatchMode.START);
		assertEquals(Arrays.asList(1), where(r));

		// an explicit Function still uses the inherited FunctionalPredicate overload
		Function<Item, Integer> qty = Item::getQty;
		FunctionalPredicate<Item> f = Restrictions.<Item>junction().gt(qty, 10);
		assertEquals(Arrays.asList(2, 5), where(f));

		// the result of a method reference call is still assignable to FunctionalPredicate
		FunctionalPredicate<Item> g = Restrictions.<Item>junction().eq(Item::getQty, 10);
		assertEquals(Arrays.asList(1), where(g));
		// and the inherited Function overloads stay callable through FunctionalPredicate
		FunctionalPredicate<Item> h = Restrictions.junction();
		h.lte(Item::getQty, 10).matches(Item::getCode, "1", MatchMode.START);
		assertEquals(Arrays.asList(1), where(h));
	}

	@Test
	public void groupByFormsOfBefore() {
		List<Group<Product>> a = Selector.from(Product.samples()).groupBy("location", String.class).groupBy("style", Product.Style.class)
				.setTransformer(x -> x).list();
		List<Group<Product>> b = Selector.from(Product.samples()).groupBy(Product::getLocation, "location").groupBy(p -> p.getStyle(), "style")
				.setTransformer(x -> x).list();
		assertEquals(a.size(), b.size());
		for (int i = 0; i < a.size(); i++) {
			assertEquals(a.get(i).getAttributes(), b.get(i).getAttributes());
		}
	}

	@Test
	public void ordersFormsOfBefore() {
		Sorter<Product> s = Orders.ascending("location", String.class);
		s = s.descending("price", Float.class).ascending(Product::getId).descending(p -> p.getSales());
		s = Orders.descending(Product::getPrice);
		s = Orders.ascending(p -> p.getPrice());
		// implicitly typed lambdas and method references for the group sorters are not ambiguous
		List<Object> locations = Selector.from(Product.samples()).groupBy("location", String.class).orderBy(Orders.groupAscending(g -> g.count()))
				.setTransformer(g -> g.getAttributes().get("location")).list();
		assertEquals(Arrays.asList("Paris", "Tokyo", "London"), locations);
		locations = Selector.from(Product.samples()).groupBy("location", String.class).orderBy(Orders.groupDescending(Group::count))
				.setTransformer(g -> g.getAttributes().get("location")).list();
		assertEquals(Arrays.asList("London", "Tokyo", "Paris"), locations);
		locations = Selector.from(Product.samples()).groupBy("location", String.class).orderBy(Orders.groupAscending("location", String.class))
				.setTransformer(g -> g.getAttributes().get("location")).list();
		assertEquals(Arrays.asList("London", "Paris", "Tokyo"), locations);
		assertTrue(s.compare(Product.samples().get(0), Product.samples().get(1)) < 0);
	}

	@Test
	public void summaryAndFunctionsFormsOfBefore() {
		Group<Product> all = new Grouper<Product>(Product.samples()).setTransformer(g -> g).first();
		assertEquals(new BigDecimal("125"), all.sum("sales"));
		assertEquals(new BigDecimal("65.00"), all.avg("freight"));
		assertEquals(new BigDecimal("65.0"), all.avg("freight", 1, RoundingMode.HALF_UP));
		assertEquals(Float.valueOf(100f), all.min("price", Float.class));
		assertEquals(Float.valueOf(500f), all.max("price", Float.class));
		assertEquals(Integer.valueOf(6), all.summarize(Functions.count()));
		assertEquals(Float.valueOf(500f), all.summarize(Functions.max(Product::getPrice)));
		assertEquals(Float.valueOf(500f), all.summarize(Functions.<Product, Float>max(p -> p.getPrice())));
		assertEquals(new BigDecimal("325"), all.summarize(Functions.sum(Product::getFreight)));
		assertEquals(new BigDecimal("65.00"), all.summarize(Functions.<Product>avg(p -> p.getFreight(), 2, RoundingMode.HALF_UP)));
	}

	@Test
	public void transformersFormsOfBefore() {
		Item first = Item.samples().get(0);
		Map<String, Object> all = Transformers.<Item>asMap().transfer(first);
		assertEquals(8, all.size());
		assertEquals(2, Transformers.<Item>asMap(new String[] { "id", "qty" }).transfer(first).size());
		assertEquals(8, Transformers.<Item>asMap((PropertyFilter) null).transfer(first).size());
		assertEquals(Long.valueOf(10L), Transformers.<Item, TransformersSpecTest.ItemDto>asBean(TransformersSpecTest.ItemDto.class).transfer(first).getQty());
		assertEquals(Long.valueOf(10L), Transformers.<Item, TransformersSpecTest.ItemDto>asBean(TransformersSpecTest.ItemDto.class, new String[] { "qty" })
				.transfer(first).getQty());
		assertEquals(Long.valueOf(10L),
				Transformers.<Item, TransformersSpecTest.ItemDto>asBean(TransformersSpecTest.ItemDto.class, (PropertyFilter) null).transfer(first).getQty());
		// a View is still a plain transformer of groups
		Transformer<Group<Item>, Tuple> view = new View<Item>() {
		};
		assertTrue(view.transfer(new Grouper.GroupImpl<Item>(new java.util.LinkedHashMap<String, Object>(), Item.samples(), null)).isEmpty());
	}

	private static void assertThrowsIllegalArgument(Runnable runnable) {
		try {
			runnable.run();
		} catch (IllegalArgumentException e) {
			return;
		}
		throw new AssertionError("IllegalArgumentException expected");
	}

}
