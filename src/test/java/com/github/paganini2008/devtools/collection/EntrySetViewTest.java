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
package com.github.paganini2008.devtools.collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;

/**
 * EntrySetViewTest
 * 
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class EntrySetViewTest {

	@Test
	public void testRemovalIsRoutedToCallback() {
		final Map<String, Integer> delegate = new LinkedHashMap<String, Integer>();
		delegate.put("a", 1);
		delegate.put("b", 2);
		delegate.put("c", 3);
		final List<String> removed = new ArrayList<String>();
		Set<Map.Entry<String, Integer>> view = new EntrySetView<String, Integer>(delegate, key -> {
			removed.add(key);
			delegate.remove(key);
		});
		assertEquals(3, view.size());
		assertTrue(view.contains(new SimpleEntry<String, Integer>("a", 1)));
		assertFalse(view.contains(new SimpleEntry<String, Integer>("a", 2)));
		assertFalse(view.remove(new SimpleEntry<String, Integer>("a", 2)));
		assertFalse(view.remove("a"));
		assertTrue(view.remove(new SimpleEntry<String, Integer>("a", 1)));
		view.clear();
		assertEquals("[a, b, c]", removed.toString());
		assertTrue(delegate.isEmpty());
	}

}
