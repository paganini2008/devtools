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
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.github.paganini2008.devtools.beans.PropertyFilters;
import com.github.paganini2008.devtools.collection.Tuple;

/**
 * TransformersSpecTest
 * <p>
 * Specification of the 'select' list: {@link Transformers#asMap()},
 * {@link Transformers#asBean(Class)}, {@link View}, {@link Mapping} and
 * {@link Property}.
 * 
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class TransformersSpecTest {

	public static class ItemDto {

		private int id;
		private Long qty;
		private String code;
		private String unknown = "untouched";

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public Long getQty() {
			return qty;
		}

		public void setQty(Long qty) {
			this.qty = qty;
		}

		public String getCode() {
			return code;
		}

		public void setCode(String code) {
			this.code = code;
		}

		public String getUnknown() {
			return unknown;
		}

		public void setUnknown(String unknown) {
			this.unknown = unknown;
		}
	}

	private static final Item FIRST = Item.samples().get(0);

	@Test
	public void asMap_allPropertiesInNameOrderIncludingNulls() {
		// select * from items
		Map<String, Object> row = Transformers.<Item>asMap().transfer(Item.samples().get(3));
		assertEquals(Arrays.asList("amount", "big", "code", "created", "id", "owner", "qty", "ratio"), Arrays.asList(row.keySet().toArray()));
		assertEquals(4, row.get("id"));
		assertNull(row.get("big"));
		assertTrue(row.containsKey("owner"));
	}

	@Test
	public void asMap_givenPropertiesOnly() {
		// select id, qty from items
		Map<String, Object> row = Transformers.<Item>asMap(new String[] { "qty", "id", "nothing" }).transfer(FIRST);
		assertEquals(2, row.size());
		assertEquals(1, row.get("id"));
		assertEquals(10, row.get("qty"));
	}

	@Test
	public void asMap_byFilter() {
		Map<String, Object> row = Transformers.<Item>asMap(PropertyFilters.excludedProperties(new String[] { "owner", "created" }))
				.transfer(FIRST);
		assertEquals(6, row.size());
		assertTrue(!row.containsKey("owner"));
	}

	@Test
	public void asBean_copiesAndConvertsMatchingProperties() {
		ItemDto dto = Transformers.<Item, ItemDto>asBean(ItemDto.class).transfer(FIRST);
		assertEquals(1, dto.getId());
		assertEquals(Long.valueOf(10L), dto.getQty());
		assertEquals("10", dto.getCode());
		assertEquals("untouched", dto.getUnknown());
	}

	@Test
	public void asBean_givenPropertiesOnly() {
		ItemDto dto = Transformers.<Item, ItemDto>asBean(ItemDto.class, new String[] { "qty" }).transfer(FIRST);
		assertEquals(0, dto.getId());
		assertEquals(Long.valueOf(10L), dto.getQty());
		assertNull(dto.getCode());
		dto = Transformers.<Item, ItemDto>asBean(ItemDto.class, PropertyFilters.includedProperties(new String[] { "id" })).transfer(FIRST);
		assertEquals(1, dto.getId());
		assertNull(dto.getQty());
	}

	@Test
	public void asBean_throughSelector() {
		List<ItemDto> dtos = Selector.from(Item.samples()).filter(Restrictions.gt("qty", 10))
				.setTransformer(Transformers.<Item, ItemDto>asBean(ItemDto.class)).list();
		assertEquals(2, dtos.size());
		assertEquals(Long.valueOf(20L), dtos.get(0).getQty());
		assertEquals(Long.valueOf(30L), dtos.get(1).getQty());
	}

	@Test
	public void view_withoutOverrideContainsTheGroupAttributesOnly() {
		Tuple tuple = new View<Item>() {
		}.transfer(new Grouper.GroupImpl<Item>(new java.util.LinkedHashMap<String, Object>(Collections.singletonMap("city", "Paris")),
				Item.samples(), null));
		assertEquals(1, tuple.size());
		assertEquals("Paris", tuple.get("city"));
	}

	@Test
	public void view_aggregatesPerGroup() {
		// select owner.address.city, count(*), sum(amount) from items group by owner.address.city
		List<Tuple> rows = Selector.from(Item.samples()).groupBy("owner.address.city", String.class).setTransformer(new View<Item>() {
			protected void setAttributes(Tuple tuple, Group<Item> group) {
				tuple.set("count", group.count());
				tuple.set("sumAmount", group.sum("amount"));
			}
		}).list();
		assertEquals(3, rows.size());
		assertEquals("Paris", rows.get(0).get("owner.address.city"));
		assertEquals(2, rows.get(0).get("count"));
		assertEquals(new BigDecimal("4.435"), rows.get(0).get("sumAmount"));
		assertNull(rows.get(2).get("owner.address.city"));
		assertEquals(new BigDecimal("0.30"), rows.get(2).get("sumAmount"));
	}

	@Test
	public void mapping_readsAndConvertsMapValues() {
		Map<String, Object> m = new HashMap<String, Object>();
		m.put("a", 12);
		m.put("b", "34");
		m.put("c", "x");
		assertEquals(Integer.valueOf(12), Mapping.forName("a", Integer.class).apply(m));
		assertEquals(Long.valueOf(12L), Mapping.forName("a", Long.class).apply(m));
		assertEquals(Integer.valueOf(34), Mapping.forName("b", Integer.class).apply(m));
		assertEquals("12", Mapping.forName("a", String.class).apply(m));
		assertNull(Mapping.forName("c", Integer.class).apply(m));
		assertNull(Mapping.forName("missing", Integer.class).apply(m));
		assertEquals(Integer.valueOf(12), Mapping.forName("a", int.class).apply(m));
		assertNull(Mapping.forName("missing", int.class).apply(m));
	}

	@Test
	public void property_readsNestedAndConvertedValues() {
		assertEquals("Paris", Property.<Item, String>forName("owner.address.city", String.class).apply(FIRST));
		assertEquals(Long.valueOf(75000L), Property.<Item, Long>forName("owner.address.zip", Long.class).apply(FIRST));
		assertEquals(Integer.valueOf(10), Property.<Item, Integer>forName("code", Integer.class).apply(FIRST));
		assertEquals(Integer.valueOf(10), Property.<Item, Integer>forName("qty", int.class).apply(FIRST));
		// null intermediate beans give null
		assertNull(Property.<Item, String>forName("owner.address.city", String.class).apply(Item.samples().get(2)));
		assertNull(Property.<Item, String>forName("owner.address.city", String.class).apply(Item.samples().get(3)));
		// a null bean, a blank name or an unknown property are rejected
		assertThrows(IllegalArgumentException.class, () -> Property.<Item, String>forName("code", String.class).apply(null));
		assertThrows(IllegalArgumentException.class, () -> Property.<Item, String>forName("", String.class).apply(FIRST));
		assertThrows(IllegalArgumentException.class, () -> Property.<Item, String>forName(null, String.class).apply(FIRST));
		assertThrows(IllegalArgumentException.class, () -> Property.<Item, String>forName("owner.nothing", String.class).apply(FIRST));
		assertThrows(IllegalArgumentException.class, () -> Property.<Item, String>forName("nothing.city", String.class).apply(FIRST));
	}

	@Test
	public void property_readRawValueWithoutRequiredType() {
		assertEquals(Integer.valueOf(10), Property.<Object>read(FIRST, "qty", null));
		assertEquals("Ann", Property.read(FIRST, "owner.name"));
	}

}
