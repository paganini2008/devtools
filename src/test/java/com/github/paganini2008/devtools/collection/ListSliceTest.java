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

import java.util.Arrays;
import java.util.LinkedHashSet;

import org.junit.jupiter.api.Test;

/**
 * ListSliceTest
 * 
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class ListSliceTest {

	@Test
	public void testSlice() {
		ListSlice<Integer> slice = new ListSlice<Integer>(Arrays.asList(1, 2, 3, 4, 5));
		assertEquals(5, slice.rowCount());
		assertEquals("[3, 4]", slice.list(2, 2).toString());
		assertEquals("[1, 2, 3, 4, 5]", slice.list().toString());
		assertEquals("[]", slice.list(2, 10).toString());
		ListSlice<String> wrapped = ListSlice.wrap(new LinkedHashSet<String>(Arrays.asList("a", "b")));
		assertEquals("[a, b]", wrapped.list(-1, 0).toString());
		assertEquals(2, wrapped.rowCount());
	}

}
