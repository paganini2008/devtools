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
package com.github.paganini2008.devtools;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/**
 * TableViewTest
 *
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class TableViewTest {

	private static final String NL = System.getProperty("line.separator");

	@Test
	public void testRenderWithBorders() {
		TableView view = new TableView(2, 2, 3);
		view.setValue(0, 0, "a").setValue(0, 1, "bb");
		String expected = "+-------+" + NL + "| a | bb|" + NL + "+-------+" + NL + "|   |   |" + NL + "+-------+";
		assertEquals(expected, view.toString());
		assertEquals(expected, view.toString(true, true));
	}

	@Test
	public void testRenderWithoutBorders() {
		TableView view = new TableView(2, 2, 3);
		view.setValues(0, new String[] { "a", "bb" });
		assertArrayEquals(new String[] { "+------+", "| a  bb|", "|      |", "+------+" }, view.toStringArray(false, false));
		assertArrayEquals(new String[] { "+-------+", "| a | bb|", "|   |   |", "+-------+" }, view.toStringArray(false, true));
	}

	@Test
	public void testAlignment() {
		TableView view = new TableView(1, 2, 5);
		view.setValueOnLeft(0, 0, "ab", 1);
		view.setValueOnRight(0, 1, "ab", 1);
		assertArrayEquals(new String[] { " ab  ", "  ab " }, view.toArray()[0]);
	}

	@Test
	public void testColumnWidthIsStableAfterLongValue() {
		TableView view = new TableView(1, 1, 3);
		view.setValue(0, 0, "abcdef");
		assertEquals("abcdef", view.toArray()[0][0]);
		view.setValue(0, 0, "x");
		assertEquals(" x ", view.toArray()[0][0]);
	}

	@Test
	public void testSetWidth() {
		TableView view = new TableView(2, 1);
		view.setValue(0, 0, "x");
		view.setWidth(0, 4);
		assertEquals("    ", view.toArray()[0][0]);
		view.setValue(1, 0, "ab");
		assertEquals(" ab ", view.toArray()[1][0]);
	}

	@Test
	public void testEmptyAndInvalid() {
		assertEquals("", new TableView(0, 0).toString());
		assertThrows(IllegalArgumentException.class, () -> new TableView(-1, 1));
	}

}
