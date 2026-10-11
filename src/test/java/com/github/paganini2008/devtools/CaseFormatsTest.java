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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * CaseFormatsTest
 * 
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class CaseFormatsTest {

	@Test
	public void testHyphenCase() {
		assertEquals("user-name", CaseFormats.LOWER_HYPHEN.toCase("userName"));
		assertEquals("USER-NAME", CaseFormats.UPPER_HYPHEN.toCase("userName"));
		assertEquals("user_name", CaseFormats.LOWER_UNDERSCORE.toCase("UserName"));
		assertEquals("USER_NAME", CaseFormats.UPPER_UNDERSCORE.toCase("userName"));
		assertEquals("abc", CaseFormats.LOWER_UNDERSCORE.toCase("abc"));
		assertEquals("user_login_name", CaseFormats.LOWER_UNDERSCORE.toCase("userLoginName"));
		assertEquals("user_name", CaseFormats.LOWER_UNDERSCORE.toCase("USER_NAME"));
		assertEquals("name", CaseFormats.LOWER_UNDERSCORE.toCase("NAME"));
		assertEquals("user", CaseFormats.LOWER_HYPHEN.toCase("USER"));
		assertEquals("user_id", CaseFormats.LOWER_UNDERSCORE.toCase("userID"));
		assertEquals("http_server", CaseFormats.LOWER_UNDERSCORE.toCase("HTTPServer"));
		assertEquals("get_url_path", CaseFormats.LOWER_UNDERSCORE.toCase("getURLPath"));
		assertEquals("USER_ID", CaseFormats.UPPER_UNDERSCORE.toCase("userID"));
		assertEquals("HTTP-SERVER", CaseFormats.UPPER_HYPHEN.toCase("HTTPServer"));
		assertEquals("USER_NAME", CaseFormats.UPPER_UNDERSCORE.toCase("USER_NAME"));
		assertEquals("user-name", CaseFormats.LOWER_HYPHEN.toCase("User-Name"));
		assertThrows(IllegalArgumentException.class, () -> CaseFormats.LOWER_HYPHEN.toCase(" "));
		assertThrows(IllegalArgumentException.class, () -> CaseFormats.UPPER_HYPHEN.toCase(null));
	}

	@Test
	public void testCamelCase() {
		assertEquals("userName", CaseFormats.LOWER_CAMEL.toCase("user_name"));
		assertEquals("userName", CaseFormats.LOWER_CAMEL.toCase("USER_NAME"));
		assertEquals("userName", CaseFormats.LOWER_CAMEL.toCase("user-name"));
		assertEquals("userNameId", CaseFormats.LOWER_CAMEL.toCase("user__name_id"));
		assertEquals("userName", CaseFormats.LOWER_CAMEL.toCase("userName"));
		assertEquals("user__", CaseFormats.LOWER_CAMEL.toCase("user__"));
		assertEquals("UserName", CaseFormats.UPPER_CAMEL.toCase("user_name"));
		assertEquals("UserName", CaseFormats.UPPER_CAMEL.toCase("USER-NAME"));
		assertEquals("user", CaseFormats.UPPER_CAMEL.toCase("user"));
		// illegal names are returned unchanged
		assertEquals("1abc", CaseFormats.LOWER_CAMEL.toCase("1abc"));
		assertEquals("a b", CaseFormats.LOWER_CAMEL.toCase("a b"));
		assertEquals("COUNT(*)", CaseFormats.LOWER_CAMEL.toCase("COUNT(*)"));
		assertEquals("a.b", CaseFormats.UPPER_CAMEL.toCase("a.b"));
		assertEquals("max(user_id)", CaseFormats.UPPER_CAMEL.toCase("max(user_id)"));
		// single upper case word is kept as it is (legacy behavior)
		assertEquals("AGE", CaseFormats.LOWER_CAMEL.toCase("AGE"));
		assertThrows(IllegalArgumentException.class, () -> CaseFormats.UPPER_CAMEL.toCase(""));
	}

	@Test
	public void testNaming() {
		assertTrue(CaseFormats.isWellFormatedNaming("abc1_", null));
		assertTrue(CaseFormats.isWellFormatedNaming("$a", null));
		assertFalse(CaseFormats.isWellFormatedNaming("1a", null));
		assertFalse(CaseFormats.isWellFormatedNaming(" ", null));
		assertFalse(CaseFormats.isWellFormatedNaming("a-b", null));
		assertFalse(CaseFormats.isWellFormatedNaming("class", new String[] { "class" }));
		assertTrue(CaseFormats.isWellFormatedStartWith('a'));
		assertFalse(CaseFormats.isWellFormatedStartWith('9'));
		assertEquals("Ab", CaseFormat.DEFAULT.toCase("Ab"));
	}

}
