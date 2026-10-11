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
package com.github.paganini2008.devtools.validation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * ValidationUtilsTest
 * 
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class ValidationUtilsTest {

	@Test
	void allMethods_returnFalseForNull() {
		assertFalse(ValidationUtils.isEmail(null));
		assertFalse(ValidationUtils.isMobile(null));
		assertFalse(ValidationUtils.isTelephone(null));
		assertFalse(ValidationUtils.isChineseIdCard(null));
		assertFalse(ValidationUtils.isIPv4(null));
		assertFalse(ValidationUtils.isIPv6(null));
		assertFalse(ValidationUtils.isUrl(null));
		assertFalse(ValidationUtils.isPostalCode(null));
		assertFalse(ValidationUtils.isBankCard(null));
		assertFalse(ValidationUtils.isUSCC(null));
		assertFalse(ValidationUtils.isChinese(null));
		assertFalse(ValidationUtils.isAlphanumeric(null));
	}

	@ParameterizedTest
	@ValueSource(strings = { "fred.feng@example.com", "a+b@mail.example.cn", "user_name-1@sub-domain.example.org", "x%y@a.io" })
	void isEmail_valid(String s) {
		assertTrue(ValidationUtils.isEmail(s));
	}

	@ParameterizedTest
	@ValueSource(strings = { "", "fred..feng@example.com", ".fred@example.com", "fred.@example.com", "fred@localhost", "fred@example.c",
			"fred@-example.com", "fred@example-.com", "fred@@example.com", "fred example@example.com", "fred@example..com", "@example.com",
			"fred" })
	void isEmail_invalid(String s) {
		assertFalse(ValidationUtils.isEmail(s));
	}

	@Test
	void isEmail_rejectsOverlongAddress() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 250; i++) {
			sb.append('a');
		}
		assertFalse(ValidationUtils.isEmail(sb.append("@example.com").toString()));
	}

	@ParameterizedTest
	@ValueSource(strings = { "13812345678", "19912345678", "15000000000", "16612345678" })
	void isMobile_valid(String s) {
		assertTrue(ValidationUtils.isMobile(s));
	}

	@ParameterizedTest
	@ValueSource(strings = { "", "12812345678", "1381234567", "138123456789", "23812345678", "1381234567a", "+8613812345678" })
	void isMobile_invalid(String s) {
		assertFalse(ValidationUtils.isMobile(s));
	}

	@ParameterizedTest
	@ValueSource(strings = { "010-62345678", "0755-8765432-123", "(021)62345678", "62345678", "02162345678", "8765432" })
	void isTelephone_valid(String s) {
		assertTrue(ValidationUtils.isTelephone(s));
	}

	@ParameterizedTest
	@ValueSource(strings = { "", "010-02345678", "010-123456", "10-12345678", "010-123456789", "010-62345678-", "010-12345678", "13812345678",
			"(021-62345678", "010-62345678-1234567" })
	void isTelephone_invalid(String s) {
		assertFalse(ValidationUtils.isTelephone(s));
	}

	@ParameterizedTest
	@ValueSource(strings = { "11010519491231002X", "11010519491231002x", "110105491231002", "110105800229001" })
	void isChineseIdCard_valid(String s) {
		assertTrue(ValidationUtils.isChineseIdCard(s));
	}

	@ParameterizedTest
	@ValueSource(strings = { "", "110105194912310021", // wrong check digit
			"110105194902300015", // 1949-02-30 does not exist
			"110105194913310018", // month 13
			"110105179912310015", // year before 1800
			"01010519491231002X", // leading zero
			"1101051949123100XX", // X in body
			"11010519491231002Y", "110105491301002", // 15 digits, month 13
			"110105490230002", // 15 digits, Feb 30
			"110105000229001", // 15 digits, 1900 is not a leap year
			"11010549123100", "1101051949123100211" })
	void isChineseIdCard_invalid(String s) {
		assertFalse(ValidationUtils.isChineseIdCard(s));
	}

	@Test
	void isChineseIdCard_rejectsFutureBirthDate() {
		String birth = LocalDate.now().plusDays(1).format(DateTimeFormatter.BASIC_ISO_DATE);
		assertFalse(ValidationUtils.isChineseIdCard(withCheckDigit("110105" + birth + "001")));
		String today = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
		assertTrue(ValidationUtils.isChineseIdCard(withCheckDigit("110105" + today + "001")));
	}

	private static String withCheckDigit(String body17) {
		int[] w = { 7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2 };
		int sum = 0;
		for (int i = 0; i < 17; i++) {
			sum += (body17.charAt(i) - '0') * w[i];
		}
		return body17 + "10X98765432".charAt(sum % 11);
	}

	@ParameterizedTest
	@ValueSource(strings = { "0.0.0.0", "192.168.1.1", "255.255.255.255", "10.0.0.255", "1.2.3.4" })
	void isIPv4_valid(String s) {
		assertTrue(ValidationUtils.isIPv4(s));
	}

	@ParameterizedTest
	@ValueSource(strings = { "", "256.1.1.1", "192.168.01.1", "1.2.3", "1.2.3.4.5", "1.2.3.-4", "a.b.c.d", "1.2.3.4 ", "300.300.300.300" })
	void isIPv4_invalid(String s) {
		assertFalse(ValidationUtils.isIPv4(s));
	}

	@ParameterizedTest
	@ValueSource(strings = { "::1", "::", "2001:db8::8a2e:370:7334", "2001:0db8:85a3:0000:0000:8a2e:0370:7334", "::ffff:192.0.2.128",
			"1::", "fe80::1", "1:2:3:4:5:6:7::", "::2:3:4:5:6:7:8", "1:2:3:4:5:6:1.2.3.4", "::1.2.3.4", "ABCD:EF01::1" })
	void isIPv6_valid(String s) {
		assertTrue(ValidationUtils.isIPv6(s));
	}

	@ParameterizedTest
	@ValueSource(strings = { "", "1.2.3.4", "2001:db8::1::1", "12345::1", ":::", ":1::2", "1::2:", "1:2:3:4:5:6:7", "1:2:3:4:5:6:7:8:9",
			"1:2:3:4:5:6:7::8", "g::1", "::ffff:192.0.2.256", "::ffff:1.2.3", "1.2.3.4::1", "1:2:3:4:5:6:7:1.2.3.4", "fe80::1%eth0",
			"[::1]", "１::1", "2001:db8:::1" })
	void isIPv6_invalid(String s) {
		assertFalse(ValidationUtils.isIPv6(s));
	}

	@ParameterizedTest
	@ValueSource(strings = { "https://www.example.com/path?q=1#top", "ftp://192.168.1.10:21/pub", "http://localhost:8080",
			"HTTP://EXAMPLE.COM", "https://user:pass@example.com/a", "http://[::1]:8080/x", "https://example.com?x=1", "http://a.b-c.io/" })
	void isUrl_valid(String s) {
		assertTrue(ValidationUtils.isUrl(s));
	}

	@ParameterizedTest
	@ValueSource(strings = { "", "mailto:fred@example.com", "www.example.com", "file:///etc/passwd", "http://", "http://exa mple.com",
			"http://example", "https://-example.com", "http://example.com:123456", "rtsp://example.com" })
	void isUrl_invalid(String s) {
		assertFalse(ValidationUtils.isUrl(s));
	}

	@Test
	void isPostalCode() {
		assertTrue(ValidationUtils.isPostalCode("100080"));
		assertTrue(ValidationUtils.isPostalCode("010000"));
		assertFalse(ValidationUtils.isPostalCode("10008"));
		assertFalse(ValidationUtils.isPostalCode("1000801"));
		assertFalse(ValidationUtils.isPostalCode("10008a"));
		assertFalse(ValidationUtils.isPostalCode(""));
	}

	@ParameterizedTest
	@ValueSource(strings = { "4111111111111111", "5500000000000004", "378282246310005", "6011111111111117", "4222222222222",
			"6212345678901234569" })
	void isBankCard_valid(String s) {
		assertTrue(ValidationUtils.isBankCard(s));
	}

	@ParameterizedTest
	@ValueSource(strings = { "", "4111111111111112", "411111111111", "41111111111111111111", "4111-1111-1111-1111", "411111111111111a",
			"6212345678901234560" })
	void isBankCard_invalid(String s) {
		assertFalse(ValidationUtils.isBankCard(s));
	}

	@Test
	void isUSCC() {
		assertTrue(ValidationUtils.isUSCC("91350100M000100Y43"));
		assertFalse(ValidationUtils.isUSCC("91350100M000100Y44")); // wrong check char
		assertFalse(ValidationUtils.isUSCC("91350100M000100Y4"));
		assertFalse(ValidationUtils.isUSCC("91350100M000100Y433"));
		assertFalse(ValidationUtils.isUSCC("91350100I000100Y43")); // I not allowed
		assertFalse(ValidationUtils.isUSCC("91350100m000100Y43")); // lowercase
		assertFalse(ValidationUtils.isUSCC(""));
		// check value 0 (31 - sum % 31 == 31)
		assertTrue(ValidationUtils.isUSCC("000000000000000000"));
	}

	@Test
	void isChinese() {
		assertTrue(ValidationUtils.isChinese("中文"));
		assertTrue(ValidationUtils.isChinese("𠀀")); // U+20000, CJK Extension B
		assertFalse(ValidationUtils.isChinese("中文a"));
		assertFalse(ValidationUtils.isChinese("中文，"));
		assertFalse(ValidationUtils.isChinese("にほん"));
		assertFalse(ValidationUtils.isChinese(""));
	}

	@Test
	void isAlphanumeric() {
		assertTrue(ValidationUtils.isAlphanumeric("abc123"));
		assertTrue(ValidationUtils.isAlphanumeric("ABC"));
		assertFalse(ValidationUtils.isAlphanumeric("ab-12"));
		assertFalse(ValidationUtils.isAlphanumeric("ab 12"));
		assertFalse(ValidationUtils.isAlphanumeric(""));
	}

	@Test
	void utilityClass_canBeSubclassed() {
		assertNotNull(new ValidationUtils() {
		});
	}

}
