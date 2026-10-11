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
package com.github.paganini2008.devtools.comparator;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.github.paganini2008.devtools.collection.MapUtils;

/**
 * MapComparatorTest
 * 
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class MapComparatorTest {

	@Test
	public void testSortByKeyAndValue() {
		Map<String, Integer> m = new LinkedHashMap<String, Integer>();
		m.put("b", 1);
		m.put("c", 3);
		m.put("a", 2);
		assertEquals("{a=2, b=1, c=3}", MapComparator.<String, Integer>byKey().sort(m).toString());
		assertEquals("{b=1, a=2, c=3}", MapComparator.<String, Integer>byValue().sort(m).toString());
		assertEquals("{c=3, b=1, a=2}", MapUtils.sort(m, MapComparator.<String, Integer>byKey().reverse()).toString());
		assertEquals("{c=3, a=2, b=1}", MapUtils.sort(m, MapComparator.<String, Integer>byValue().reverse()).toString());
	}

}
