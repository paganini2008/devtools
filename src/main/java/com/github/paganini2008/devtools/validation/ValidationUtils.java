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

import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Null-safe format validators for common data such as e-mail addresses, Chinese mobile and
 * telephone numbers, ID card numbers, IP addresses, URLs, bank cards and unified social
 * credit codes. Every method returns {@code false} for {@code null}.
 *
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public abstract class ValidationUtils {

	private static final String DOMAIN_LABEL = "[A-Za-z0-9](?:[A-Za-z0-9-]{0,61}[A-Za-z0-9])?";

	private static final String IPV4 = "(?:25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(?:\\.(?:25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}";

	private static final Pattern EMAIL_PATTERN = Pattern
			.compile("^[A-Za-z0-9_%+-]+(?:\\.[A-Za-z0-9_%+-]+)*@" + DOMAIN_LABEL + "(?:\\." + DOMAIN_LABEL + ")*\\.[A-Za-z]{2,63}$");

	private static final Pattern MOBILE_PATTERN = Pattern.compile("^1[3-9]\\d{9}$");

	private static final Pattern TELEPHONE_PATTERN = Pattern.compile("^(?:\\(0\\d{2,3}\\)|0\\d{2,3}-?)?[2-9]\\d{6,7}(?:-\\d{1,6})?$");

	private static final Pattern ID_CARD_18_PATTERN = Pattern.compile("^[1-9]\\d{5}(\\d{4})(\\d{2})(\\d{2})\\d{3}[0-9Xx]$");

	private static final Pattern ID_CARD_15_PATTERN = Pattern.compile("^[1-9]\\d{5}(\\d{2})(\\d{2})(\\d{2})\\d{3}$");

	private static final Pattern IPV4_PATTERN = Pattern.compile("^" + IPV4 + "$");

	private static final Pattern URL_PATTERN = Pattern.compile("^(?:[Hh][Tt][Tt][Pp][Ss]?|[Ff][Tt][Pp])://(?:[^\\s/?#@]+@)?(?:localhost|" + IPV4
			+ "|\\[[0-9A-Fa-f:.]+\\]|(?:" + DOMAIN_LABEL + "\\.)+[A-Za-z]{2,63})(?::\\d{1,5})?(?:[/?#]\\S*)?$");

	private static final Pattern POSTAL_CODE_PATTERN = Pattern.compile("^\\d{6}$");

	private static final Pattern BANK_CARD_PATTERN = Pattern.compile("^\\d{13,19}$");

	private static final int[] ID_CARD_WEIGHTS = { 7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2 };

	private static final char[] ID_CARD_CHECK_CODES = "10X98765432".toCharArray();

	private static final String USCC_CHARS = "0123456789ABCDEFGHJKLMNPQRTUWXY";

	private static final int[] USCC_WEIGHTS = { 1, 3, 9, 27, 19, 26, 16, 17, 20, 29, 25, 13, 8, 24, 10, 30, 28 };

	/**
	 * Checks if the text is a syntactically valid e-mail address (ASCII local part without
	 * leading, trailing or consecutive dots; domain with a top-level label of at least 2
	 * letters; at most 254 characters).
	 *
	 * <pre>
	 * ValidationUtils.isEmail(null)                  = false
	 * ValidationUtils.isEmail("fred.feng@example.com") = true
	 * ValidationUtils.isEmail("a+b@mail.example.cn")   = true
	 * ValidationUtils.isEmail("fred..feng@example.com") = false
	 * ValidationUtils.isEmail("fred@localhost")        = false
	 * </pre>
	 */
	public static boolean isEmail(String str) {
		return str != null && str.length() <= 254 && EMAIL_PATTERN.matcher(str).matches();
	}

	/**
	 * Checks if the text is a mainland China mobile number: 11 digits starting with 13-19.
	 *
	 * <pre>
	 * ValidationUtils.isMobile("13812345678")  = true
	 * ValidationUtils.isMobile("12812345678")  = false
	 * ValidationUtils.isMobile("1381234567")   = false
	 * </pre>
	 */
	public static boolean isMobile(String str) {
		return str != null && MOBILE_PATTERN.matcher(str).matches();
	}

	/**
	 * Checks if the text is a mainland China landline number: an optional area code (0 plus 2-3
	 * digits, optionally followed by '-' or written in parentheses), a 7-8 digit local number
	 * not starting with 0 or 1, and an optional extension "-NNNN" (1-6 digits).
	 *
	 * <pre>
	 * ValidationUtils.isTelephone("010-62345678")      = true
	 * ValidationUtils.isTelephone("0755-8765432-123")  = true
	 * ValidationUtils.isTelephone("(021)62345678")     = true
	 * ValidationUtils.isTelephone("62345678")          = true
	 * ValidationUtils.isTelephone("010-02345678")      = false
	 * </pre>
	 */
	public static boolean isTelephone(String str) {
		return str != null && TELEPHONE_PATTERN.matcher(str).matches();
	}

	/**
	 * Checks if the text is a valid mainland China resident ID card number. 18-digit numbers
	 * must have a real birth date (year 1800 or later and not in the future) and a correct
	 * GB 11643-1999 check digit ('X' or 'x'). Legacy 15-digit numbers are checked for a real
	 * birth date in 19YY.
	 *
	 * <pre>
	 * ValidationUtils.isChineseIdCard("11010519491231002X") = true
	 * ValidationUtils.isChineseIdCard("11010519491231002x") = true
	 * ValidationUtils.isChineseIdCard("110105194912310021") = false  // wrong check digit
	 * ValidationUtils.isChineseIdCard("110105491231002")    = true   // 15 digits
	 * </pre>
	 */
	public static boolean isChineseIdCard(String str) {
		if (str == null) {
			return false;
		}
		if (str.length() == 18) {
			Matcher m = ID_CARD_18_PATTERN.matcher(str);
			if (!m.matches()) {
				return false;
			}
			int year = Integer.parseInt(m.group(1));
			if (year < 1800 || !isValidBirthDate(year, Integer.parseInt(m.group(2)), Integer.parseInt(m.group(3)))) {
				return false;
			}
			int sum = 0;
			for (int i = 0; i < 17; i++) {
				sum += (str.charAt(i) - '0') * ID_CARD_WEIGHTS[i];
			}
			return ID_CARD_CHECK_CODES[sum % 11] == Character.toUpperCase(str.charAt(17));
		}
		if (str.length() == 15) {
			Matcher m = ID_CARD_15_PATTERN.matcher(str);
			return m.matches()
					&& isValidBirthDate(1900 + Integer.parseInt(m.group(1)), Integer.parseInt(m.group(2)), Integer.parseInt(m.group(3)));
		}
		return false;
	}

	private static boolean isValidBirthDate(int year, int month, int day) {
		try {
			return !LocalDate.of(year, month, day).isAfter(LocalDate.now());
		} catch (DateTimeException e) {
			return false;
		}
	}

	/**
	 * Checks if the text is a dotted-decimal IPv4 address; leading zeros are rejected.
	 *
	 * <pre>
	 * ValidationUtils.isIPv4("192.168.1.1")  = true
	 * ValidationUtils.isIPv4("256.1.1.1")    = false
	 * ValidationUtils.isIPv4("192.168.01.1") = false
	 * </pre>
	 */
	public static boolean isIPv4(String str) {
		return str != null && IPV4_PATTERN.matcher(str).matches();
	}

	/**
	 * Checks if the text is an IPv6 address in full, compressed ("::") or IPv4-embedded form
	 * (e.g. "::ffff:192.0.2.128"). Zone ids ("%eth0") and brackets are not accepted.
	 *
	 * <pre>
	 * ValidationUtils.isIPv6("::1")                     = true
	 * ValidationUtils.isIPv6("2001:db8::8a2e:370:7334") = true
	 * ValidationUtils.isIPv6("::ffff:192.0.2.128")      = true
	 * ValidationUtils.isIPv6("2001:db8::1::1")          = false
	 * ValidationUtils.isIPv6("12345::1")                = false
	 * </pre>
	 */
	public static boolean isIPv6(String str) {
		if (str == null) {
			return false;
		}
		int lastColon = str.lastIndexOf(':');
		if (lastColon < 0) {
			return false;
		}
		String s = str;
		if (s.indexOf('.') >= 0) {
			if (!isIPv4(s.substring(lastColon + 1))) {
				return false;
			}
			s = s.substring(0, lastColon + 1) + "0:0";
		}
		int doubleColon = s.indexOf("::");
		if (doubleColon < 0) {
			return countHexGroups(s) == 8;
		}
		if (s.indexOf("::", doubleColon + 1) >= 0) {
			return false;
		}
		int left = countHexGroups(s.substring(0, doubleColon));
		int right = countHexGroups(s.substring(doubleColon + 2));
		return left >= 0 && right >= 0 && left + right <= 7;
	}

	/**
	 * Returns the number of colon-separated 1-4 digit hex groups, 0 for an empty string, or -1
	 * if any group is invalid.
	 */
	private static int countHexGroups(String s) {
		if (s.isEmpty()) {
			return 0;
		}
		String[] groups = s.split(":", -1);
		for (String group : groups) {
			if (group.isEmpty() || group.length() > 4) {
				return -1;
			}
			for (int i = 0; i < group.length(); i++) {
				char c = group.charAt(i);
				if (!((c >= '0' && c <= '9') || (c >= 'a' && c <= 'f') || (c >= 'A' && c <= 'F'))) {
					return -1;
				}
			}
		}
		return groups.length;
	}

	/**
	 * Checks if the text is an absolute http, https or ftp URL with a domain name, "localhost",
	 * an IPv4 address or a bracketed IPv6 host, an optional port, path, query and fragment.
	 *
	 * <pre>
	 * ValidationUtils.isUrl("https://www.example.com/path?q=1#top") = true
	 * ValidationUtils.isUrl("ftp://192.168.1.10:21/pub")            = true
	 * ValidationUtils.isUrl("mailto:fred@example.com")              = false
	 * ValidationUtils.isUrl("www.example.com")                      = false
	 * </pre>
	 */
	public static boolean isUrl(String str) {
		return str != null && URL_PATTERN.matcher(str).matches();
	}

	/**
	 * Checks if the text is a 6-digit mainland China postal code.
	 *
	 * <pre>
	 * ValidationUtils.isPostalCode("100080") = true
	 * ValidationUtils.isPostalCode("10008")  = false
	 * </pre>
	 */
	public static boolean isPostalCode(String str) {
		return str != null && POSTAL_CODE_PATTERN.matcher(str).matches();
	}

	/**
	 * Checks if the text is a 13-19 digit card number passing the Luhn (mod 10) check.
	 *
	 * <pre>
	 * ValidationUtils.isBankCard("4111111111111111") = true
	 * ValidationUtils.isBankCard("4111111111111112") = false
	 * </pre>
	 */
	public static boolean isBankCard(String str) {
		if (str == null || !BANK_CARD_PATTERN.matcher(str).matches()) {
			return false;
		}
		int sum = 0;
		boolean doubled = false;
		for (int i = str.length() - 1; i >= 0; i--) {
			int digit = str.charAt(i) - '0';
			if (doubled) {
				digit *= 2;
				if (digit > 9) {
					digit -= 9;
				}
			}
			sum += digit;
			doubled = !doubled;
		}
		return sum % 10 == 0;
	}

	/**
	 * Checks if the text is an 18-character unified social credit code (uppercase, excluding
	 * I, O, Z, S and V) with a correct GB 32100-2015 check character.
	 *
	 * <pre>
	 * ValidationUtils.isUSCC("91350100M000100Y43") = true
	 * ValidationUtils.isUSCC("91350100M000100Y44") = false
	 * </pre>
	 */
	public static boolean isUSCC(String str) {
		if (str == null || str.length() != 18) {
			return false;
		}
		int sum = 0;
		for (int i = 0; i < 17; i++) {
			int value = USCC_CHARS.indexOf(str.charAt(i));
			if (value < 0) {
				return false;
			}
			sum += value * USCC_WEIGHTS[i];
		}
		int check = (31 - sum % 31) % 31;
		return str.charAt(17) == USCC_CHARS.charAt(check);
	}

	/**
	 * Checks if the text is non-empty and consists only of Han (Chinese) characters, including
	 * supplementary-plane ideographs.
	 *
	 * <pre>
	 * ValidationUtils.isChinese("中文")   = true
	 * ValidationUtils.isChinese("中文a")  = false
	 * ValidationUtils.isChinese("")      = false
	 * </pre>
	 */
	public static boolean isChinese(String str) {
		if (str == null || str.isEmpty()) {
			return false;
		}
		for (int i = 0; i < str.length();) {
			int cp = str.codePointAt(i);
			if (Character.UnicodeScript.of(cp) != Character.UnicodeScript.HAN) {
				return false;
			}
			i += Character.charCount(cp);
		}
		return true;
	}

	/**
	 * Checks if the text is non-empty and contains only Unicode letters or digits (same
	 * definition as {@code StringUtils.isAlpha}/{@code StringUtils.isNumeric}).
	 *
	 * <pre>
	 * ValidationUtils.isAlphanumeric("abc123") = true
	 * ValidationUtils.isAlphanumeric("ab-12")  = false
	 * ValidationUtils.isAlphanumeric("")       = false
	 * </pre>
	 */
	public static boolean isAlphanumeric(String str) {
		if (str == null || str.isEmpty()) {
			return false;
		}
		for (int i = 0; i < str.length(); i++) {
			if (!Character.isLetterOrDigit(str.charAt(i))) {
				return false;
			}
		}
		return true;
	}

}
