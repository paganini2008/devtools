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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Locale;

import org.junit.jupiter.api.Test;

/**
 * LocaleUtilsTest
 *
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class LocaleUtilsTest {

	@Test
	public void testToLocale() {
		assertEquals(new Locale("en"), LocaleUtils.toLocale("en"));
		assertEquals(Locale.US, LocaleUtils.toLocale("en_US"));
		assertEquals(Locale.US, LocaleUtils.toLocale("en_us"));
		assertEquals(new Locale("ja", "JP", "JP"), LocaleUtils.toLocale("ja_JP_JP"));
		assertThrows(IllegalArgumentException.class, () -> LocaleUtils.toLocale(null));
		assertThrows(IllegalArgumentException.class, () -> LocaleUtils.toLocale(" "));
		assertThrows(IllegalArgumentException.class, () -> LocaleUtils.toLocale("a_b_c_d"));
		assertThrows(IllegalArgumentException.class, () -> LocaleUtils.toLocale("en_U1S"));
		assertThrows(IllegalArgumentException.class, () -> LocaleUtils.toLocale("EN1_US"));
	}

	@Test
	public void testGetLocaleIsCached() {
		Locale locale = LocaleUtils.getLocale("zh_CN");
		assertEquals(Locale.CHINA, locale);
		assertSame(locale, LocaleUtils.getLocale("zh_CN"));
		assertThrows(IllegalArgumentException.class, () -> LocaleUtils.getLocale(""));
	}

}
