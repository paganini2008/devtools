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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import com.github.paganini2008.devtools.jdbc.PageRequest;
import com.github.paganini2008.devtools.jdbc.PageResponse;
import com.github.paganini2008.devtools.jdbc.ResultSetSlice;

/**
 * SelectorTest
 * 
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class SelectorTest {

	private static List<Integer> ids(List<Product> list) {
		return list.stream().map(Product::getId).collect(Collectors.toList());
	}

	@Test
	public void testSelectAll() {
		List<Product> products = Product.samples();
		Selector<Product> selector = Selector.from(products);
		assertEquals(6, selector.rowCount());
		assertEquals(Arrays.asList(1, 2, 3, 4, 5, 6), ids(selector.list()));
		assertEquals(1, selector.first().getId());
	}

	@Test
	public void testWhere() {
		// select * from Product where location = 'London'
		List<Product> result = Selector.from(Product.samples()).filter(Restrictions.eq("location", "London")).list();
		assertEquals(Arrays.asList(1, 2, 6), ids(result));

		// select * from Product where price >= 200 and salesman.name = 'Petter'
		result = Selector.from(Product.samples())
				.filter(Restrictions.<Product, Float>gte("price", 200f).and(Restrictions.eq("salesman.name", "Petter"))).list();
		assertEquals(Arrays.asList(3), ids(result));

		// chained filters are combined with 'and'
		Selectable<Product> s = Selector.from(Product.samples()).filter(Restrictions.eq("style", Product.Style.SMALL))
				.filter(Restrictions.lt("price", 200f));
		assertEquals(Arrays.asList(1, 6), ids(s.list()));
		assertEquals(2, s.rowCount());
	}

	@Test
	public void testEmptyResult() {
		Selectable<Product> s = Selector.from(Product.samples()).filter(Restrictions.eq("location", "Nowhere"));
		assertEquals(0, s.rowCount());
		assertTrue(s.list().isEmpty());
		assertNull(s.first());
		assertTrue(s.list(10, 5).isEmpty());
	}

	@Test
	public void testOrderBy() {
		// select * from Product order by location asc, price desc
		Sorter<Product> sorter = Orders.ascending("location", String.class);
		sorter = sorter.descending("price", Float.class);
		List<Product> result = Selector.from(Product.samples()).orderBy(sorter).list();
		assertEquals(Arrays.asList(2, 6, 1, 5, 4, 3), ids(result));

		// order by freight asc: nulls first
		result = Selector.from(Product.samples()).orderBy(Orders.ascending(Product::getFreight)).list();
		assertEquals(Arrays.asList(4, 1, 6, 3, 2, 5), ids(result));
		// order by freight desc: nulls last
		result = Selector.from(Product.samples()).orderBy(Orders.descending(Product::getFreight)).list();
		assertEquals(Arrays.asList(5, 2, 3, 6, 1, 4), ids(result));
	}

	@Test
	public void testSourceIsNotModified() {
		List<Product> products = Product.samples();
		Selector.from(products).orderBy(Orders.descending("id", Integer.class)).list();
		assertEquals(Arrays.asList(1, 2, 3, 4, 5, 6), ids(products));

		// works on unmodifiable lists and non-list collections
		List<Product> unmodifiable = Collections.unmodifiableList(Product.samples());
		assertEquals(6, ids(Selector.from(unmodifiable).orderBy(Orders.descending("id", Integer.class)).list()).get(0));
		assertEquals(6, Selector.from(new HashSet<Product>(products)).rowCount());
		assertThrows(IllegalArgumentException.class, () -> Selector.from(null));
	}

	@Test
	public void testLimitAndOffset() {
		Selector<Product> s = Selector.from(Product.samples());
		// limit 2
		assertEquals(Arrays.asList(1, 2), ids(s.list(2)));
		// limit 2 offset 2
		assertEquals(Arrays.asList(3, 4), ids(s.list(2, 2)));
		// limit 10 offset 4
		assertEquals(Arrays.asList(5, 6), ids(s.list(10, 4)));
		// no limit, offset 5
		assertEquals(Arrays.asList(6), ids(s.list(-1, 5)));
		// offset beyond the end
		assertTrue(s.list(2, 100).isEmpty());
		// limit 0
		assertTrue(s.list(0, 0).isEmpty());
		// huge limit does not overflow
		assertEquals(6, s.list(Integer.MAX_VALUE, 0).size());
		assertEquals(5, s.list(Integer.MAX_VALUE, 1).size());
		assertThrows(IllegalArgumentException.class, () -> s.list(1, -1));
		assertThrows(UnsupportedOperationException.class, () -> s.list().clear());
	}

	@Test
	public void testListIsSnapshot() {
		Selectable<Product> s = Selector.from(Product.samples());
		List<Product> page = s.list(3, 0);
		s.orderBy(Orders.descending("id", Integer.class));
		assertEquals(Arrays.asList(1, 2, 3), ids(page));
		assertEquals(Arrays.asList(6, 5, 4), ids(s.list(3, 0)));
	}

	@Test
	public void testPageRequest() {
		Selectable<Product> s = Selector.from(Product.samples()).orderBy(Orders.descending("price", Float.class));
		PageResponse<Product> page = s.list(PageRequest.of(1, 4));
		assertEquals(6, page.getTotalRecords());
		assertEquals(2, page.getTotalPages());
		assertEquals(Arrays.asList(5, 4, 2, 3), ids(page.getContent()));
		assertEquals(Arrays.asList(6, 1), ids(page.nextPage().getContent()));
	}

	@Test
	public void testDistinct() {
		List<Product> products = new ArrayList<Product>(Product.samples());
		products.addAll(Product.samples());
		Selectable<Product> s = Selector.from(products);
		assertEquals(12, s.rowCount());
		assertEquals(Arrays.asList(1, 2, 3, 4, 5, 6), ids(s.distinct().list()));
	}

	@Test
	public void testTransformer() {
		// select id, name from Product where location = 'Tokyo'
		ResultSetSlice<Map<String, Object>> slice = Selector.from(Product.samples()).filter(Restrictions.eq("location", "Tokyo"))
				.setTransformer(Transformers.asMap(new String[] { "id", "name" }));
		assertEquals(2, slice.rowCount());
		List<Map<String, Object>> rows = slice.list();
		assertEquals(2, rows.size());
		assertEquals(2, rows.get(0).size());
		assertEquals(3, rows.get(0).get("id"));
		assertEquals("p4", rows.get(1).get("name"));
		assertEquals(1, slice.list(1, 1).size());
		assertEquals("p4", slice.list(1, 1).get(0).get("name"));

		ResultSetSlice<String> names = Selector.from(Product.samples()).setTransformer(Product::getName);
		assertEquals(Arrays.asList("p1", "p2"), names.list(2));
	}

	@Test
	public void testGroupByFromSelector() {
		// select location, sum(freight) from Product group by location
		List<Map<String, Object>> rows = Selector.from(Product.samples()).groupBy("location", String.class)
				.setTransformer(group -> {
					Map<String, Object> row = new java.util.LinkedHashMap<String, Object>(group.getAttributes());
					row.put("sumFreight", group.sum("freight"));
					return row;
				}).list();
		assertEquals(3, rows.size());
		assertEquals("London", rows.get(0).get("location"));
		assertEquals(new BigDecimal("175"), rows.get(0).get("sumFreight"));
		assertEquals("Tokyo", rows.get(1).get("location"));
		assertEquals(new BigDecimal("60"), rows.get(1).get("sumFreight"));
		assertEquals("Paris", rows.get(2).get("location"));
	}

}
