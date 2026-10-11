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
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.github.paganini2008.devtools.collection.Tuple;

/**
 * TransformersTest
 * 
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class TransformersTest {

	public static class ProductVo {

		private String name;
		private Float price;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public Float getPrice() {
			return price;
		}

		public void setPrice(Float price) {
			this.price = price;
		}
	}

	@Test
	public void testAsMap() {
		Product product = Product.samples().get(0);
		Map<String, Object> map = Transformers.<Product>asMap().transfer(product);
		assertEquals("p1", map.get("name"));
		assertEquals(100f, map.get("price"));
		assertEquals(new BigDecimal("50"), map.get("freight"));
		assertTrue(map.containsKey("salesman"));

		map = Transformers.<Product>asMap(new String[] { "name" }).transfer(product);
		assertEquals(Collections.singletonMap("name", "p1"), map);
	}

	@Test
	public void testAsBean() {
		Product product = Product.samples().get(1);
		ProductVo vo = Transformers.<Product, ProductVo>asBean(ProductVo.class).transfer(product);
		assertEquals("p2", vo.getName());
		assertEquals(Float.valueOf(300f), vo.getPrice());

		vo = Transformers.<Product, ProductVo>asBean(ProductVo.class, new String[] { "name" }).transfer(product);
		assertEquals("p2", vo.getName());
		assertNull(vo.getPrice());

		List<ProductVo> list = Selector.from(Product.samples()).filter(Restrictions.eq("location", "Paris"))
				.setTransformer(Transformers.<Product, ProductVo>asBean(ProductVo.class)).list();
		assertEquals(1, list.size());
		assertEquals("p5", list.get(0).getName());
	}

	@Test
	public void testView() {
		Grouper<Product> grouper = new Grouper<Product>(Product.samples());
		grouper.groupBy("style", Product.Style.class);
		// default view only contains the group attributes
		List<Tuple> rows = grouper.setTransformer(new View<Product>() {
		}).list();
		assertEquals(2, rows.size());
		assertEquals(1, rows.get(0).size());
		assertEquals(Product.Style.SMALL, rows.get(0).get("style"));
		assertEquals(Product.Style.LARGE, rows.get(1).get("style"));
	}

	@Test
	public void testMappingAndProperty() {
		Map<String, Object> m = new HashMap<String, Object>();
		m.put("a", "123");
		m.put("b", 5);
		assertEquals(Integer.valueOf(123), Mapping.forName("a", Integer.class).apply(m));
		assertEquals("123", Mapping.forName("a", String.class).apply(m));
		assertEquals(Integer.valueOf(5), Mapping.forName("b", Integer.class).apply(m));
		assertNull(Mapping.forName("c", Integer.class).apply(m));

		Product product = Product.samples().get(0);
		assertEquals("Petter", Property.<Product, String>forName("salesman.name", String.class).apply(product));
		assertEquals("1", Property.<Product, String>forName("id", String.class).apply(product));
	}

	@Test
	public void testBeanSorter() {
		BeanSorter<Product> sorter = new BeanSorter<Product>();
		List<Product> list = Product.samples();
		// identity comparator keeps the original order (stable sort)
		list.sort(sorter);
		assertEquals(1, list.get(0).getId());
		sorter.descending(Product::getPrice);
		list.sort(sorter);
		assertEquals(5, list.get(0).getId());
		sorter.reset().ascending(Product::getId);
		list.sort(sorter);
		assertEquals(1, list.get(0).getId());
		assertEquals(6, sorter.sort(Product.samples()).size());
		assertTrue(sorter.compare(list.get(0), list.get(1)) < 0);
	}

}
