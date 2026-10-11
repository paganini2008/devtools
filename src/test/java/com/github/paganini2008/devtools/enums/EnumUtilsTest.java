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
package com.github.paganini2008.devtools.enums;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/**
 * EnumUtilsTest
 *
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class EnumUtilsTest {

	enum Color implements EnumConstant {

		RED(1, "Red", "warm"), YELLOW(2, "Yellow", "warm"), BLUE(3, "Blue", null), BLACK(4, "Black", DEFAULT_GROUP);

		private final int value;
		private final String repr;
		private final String group;

		Color(int value, String repr, String group) {
			this.value = value;
			this.repr = repr;
			this.group = group;
		}

		public int getValue() {
			return value;
		}

		public String getRepr() {
			return repr;
		}

		public String getGroup() {
			return group;
		}
	}

	enum Plain implements EnumConstant {

		ONE;

		public int getValue() {
			return 1;
		}

		public String getRepr() {
			return "one";
		}
	}

	static class NotAnEnum implements EnumConstant {

		public int getValue() {
			return 0;
		}

		public String getRepr() {
			return "";
		}
	}

	@Test
	public void testFindAll() {
		Color[] warm = EnumUtils.findAll(Color.class, "warm");
		assertArrayEquals(new Color[] { Color.RED, Color.YELLOW }, warm);
		assertArrayEquals(new Color[] { Color.BLACK }, EnumUtils.findAll(Color.class, EnumConstant.DEFAULT_GROUP));
		assertArrayEquals(new Color[] { Color.BLUE }, EnumUtils.findAll(Color.class, null));
		assertEquals(0, EnumUtils.findAll(Color.class, "cold").length);
		assertArrayEquals(new Plain[] { Plain.ONE }, EnumUtils.findAll(Plain.class, EnumConstant.DEFAULT_GROUP));
		assertNull(EnumUtils.findAll(null, "warm"));
		assertThrows(IllegalArgumentException.class, () -> EnumUtils.findAll(NotAnEnum.class, "warm"));
	}

	@Test
	public void testValueOf() {
		assertSame(Color.YELLOW, EnumUtils.valueOf(Color.class, 2));
		assertEquals("Blue", EnumUtils.valueOf(Color.class, 3).getRepr());
		assertThrows(IllegalArgumentException.class, () -> EnumUtils.valueOf(Color.class, 99));
		assertNull(EnumUtils.valueOf(null, 1));
		assertThrows(IllegalArgumentException.class, () -> EnumUtils.valueOf(NotAnEnum.class, 0));
	}

}
