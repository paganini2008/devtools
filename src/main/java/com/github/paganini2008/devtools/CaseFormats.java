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

import java.util.function.Function;

/**
 * Built-in {@link CaseFormat} implementations for hyphen, underscore and camel
 * case naming conventions.
 *
 * @author Fred Feng
 * @date 2019-12-02
 * @since 2.0.1
 */
public abstract class CaseFormats {

	public static final char UNDERSCORE_CHAR = '_';

	public static final char HYPHEN_CHAR = '-';

	/**
	 * Converts camel case to lower hyphen case, e.g. "userName" to "user-name".
	 */
	public static final CaseFormat LOWER_HYPHEN = new CaseFormats.LowerHyphenCase(ch -> {
		return Character.isUpperCase(ch);
	}, HYPHEN_CHAR);

	/**
	 * Converts camel case to upper hyphen case, e.g. "userName" to "USER-NAME".
	 */
	public static final CaseFormat UPPER_HYPHEN = new CaseFormats.UpperHyphenCase(ch -> {
		return Character.isUpperCase(ch);
	}, HYPHEN_CHAR);

	/**
	 * Converts camel case to lower underscore case.
	 *
	 * <pre>
	 * LOWER_UNDERSCORE.toCase("userLoginName") = "user_login_name"
	 * LOWER_UNDERSCORE.toCase("USER_NAME")     = "user_name"
	 * LOWER_UNDERSCORE.toCase("NAME")          = "name"
	 * LOWER_UNDERSCORE.toCase("userID")        = "user_id"
	 * LOWER_UNDERSCORE.toCase("HTTPServer")    = "http_server"
	 * </pre>
	 */
	public static final CaseFormat LOWER_UNDERSCORE = new CaseFormats.LowerHyphenCase(ch -> {
		return Character.isUpperCase(ch);
	}, UNDERSCORE_CHAR);

	/**
	 * Converts camel case to upper underscore case, e.g. "userName" to "USER_NAME".
	 */
	public static final CaseFormat UPPER_UNDERSCORE = new CaseFormats.UpperHyphenCase(ch -> {
		return Character.isUpperCase(ch);
	}, UNDERSCORE_CHAR);

	/**
	 * Converts hyphen or underscore separated names to lower camel case. A name
	 * without separators is returned unchanged.
	 *
	 * <pre>
	 * LOWER_CAMEL.toCase("USER_NAME") = "userName"
	 * LOWER_CAMEL.toCase("user-name") = "userName"
	 * LOWER_CAMEL.toCase("userName")  = "userName"
	 * LOWER_CAMEL.toCase("COUNT(*)")  = "COUNT(*)"
	 * </pre>
	 */
	public static final CaseFormat LOWER_CAMEL = new CaseFormats.LowerCamelCase(ch -> {
		return ch == HYPHEN_CHAR || ch == UNDERSCORE_CHAR;
	});

	/**
	 * Converts hyphen or underscore separated names to upper camel case, e.g.
	 * "user_name" to "UserName". A name without separators is returned unchanged.
	 */
	public static final CaseFormat UPPER_CAMEL = new CaseFormats.UpperCamelCase(ch -> {
		return ch == HYPHEN_CHAR || ch == UNDERSCORE_CHAR;
	});

	/**
	 * Checks whether the char may start a name: a letter, '-' or '$'.
	 */
	public static boolean isWellFormatedStartWith(char firstChar) {
		return Character.isAlphabetic(firstChar) || firstChar == HYPHEN_CHAR || firstChar == '$';
	}

	/**
	 * Checks whether the text is a well-formed name: a valid first char followed by
	 * letters, digits or underscores, and not one of the excluded keywords.
	 */
	public static boolean isWellFormatedNaming(CharSequence varName, String[] excludedKeywords) {
		if (StringUtils.isBlank(varName)) {
			return false;
		}
		if (!isWellFormatedStartWith(varName.charAt(0))) {
			return false;
		}
		char c;
		for (int i = 1; i < varName.length(); i++) {
			c = varName.charAt(i);
			if (!((Character.isAlphabetic(c)) || (Character.isDigit(c)) || (c == '_'))) {
				return false;
			}
		}
		return !ArrayUtils.contains(excludedKeywords, varName.toString());
	}

	/**
	 * Checks the naming before converting it to camel case. Unlike
	 * {@link #isWellFormatedNaming(CharSequence, String[])}, hyphens are also
	 * accepted, because both camel case formats treat a hyphen as a word
	 * separator.
	 */
	private static boolean isCamelCaseConvertible(CharSequence str) {
		if (!isWellFormatedStartWith(str.charAt(0))) {
			return false;
		}
		char c;
		for (int i = 1; i < str.length(); i++) {
			c = str.charAt(i);
			if (!(Character.isAlphabetic(c) || Character.isDigit(c) || c == UNDERSCORE_CHAR || c == HYPHEN_CHAR)) {
				return false;
			}
		}
		return true;
	}

	private static boolean isSeparator(char c, char hyphen) {
		return c == hyphen || c == HYPHEN_CHAR || c == UNDERSCORE_CHAR;
	}

	/**
	 * Decides whether a separator is inserted before the char at index i. A
	 * separator is inserted before a matched char unless it is the first char or
	 * follows a separator. Within a run of matched chars (an acronym such as
	 * "HTTP" or "ID"), a separator is only inserted before the last matched char
	 * if a lower case char follows it, so "HTTPServer" becomes "http_server",
	 * "userID" becomes "user_id" and "USER_NAME" becomes "user_name".
	 */
	private static boolean needsSeparator(CharSequence str, int i, Function<Character, Boolean> f, char hyphen) {
		if (i == 0 || !f.apply(str.charAt(i))) {
			return false;
		}
		char prev = str.charAt(i - 1);
		if (isSeparator(prev, hyphen)) {
			return false;
		}
		if (!f.apply(prev)) {
			return true;
		}
		return i + 1 < str.length() && Character.isLowerCase(str.charAt(i + 1));
	}

	/**
	 * Inserts a separator before each char matched by the function and upper-cases
	 * all chars. Runs of matched chars (acronyms) are kept together, e.g.
	 * "userID" becomes "USER_ID" and "HTTPServer" becomes "HTTP_SERVER".
	 */
	public static class UpperHyphenCase implements CaseFormat {

		private final Function<Character, Boolean> f;
		private final char hyphen;

		public UpperHyphenCase(Function<Character, Boolean> f, char hyphen) {
			this.f = f;
			this.hyphen = hyphen;
		}

		public String toCase(CharSequence str) {
			Assert.hasNoText(str);
			StringBuilder content = new StringBuilder();
			char c;
			for (int i = 0, l = str.length(); i < l; i++) {
				c = str.charAt(i);
				if (needsSeparator(str, i, f, hyphen)) {
					content.append(hyphen);
				}
				content.append(Character.toUpperCase(c));
			}
			if (content.length() == 0) {
				content.append(str);
			}
			return content.toString();
		}

	}

	/**
	 * Inserts a separator before each char matched by the function and lower-cases
	 * all chars. Runs of matched chars (acronyms) are kept together, e.g.
	 * "userID" becomes "user_id", "HTTPServer" becomes "http_server" and
	 * "USER_NAME" becomes "user_name".
	 */
	public static class LowerHyphenCase implements CaseFormat {

		private final Function<Character, Boolean> f;
		private final char hyphen;

		public LowerHyphenCase(Function<Character, Boolean> f, char hyphen) {
			this.f = f;
			this.hyphen = hyphen;
		}

		public String toCase(CharSequence str) {
			Assert.hasNoText(str);
			StringBuilder content = new StringBuilder();
			char c;
			for (int i = 0, l = str.length(); i < l; i++) {
				c = str.charAt(i);
				if (needsSeparator(str, i, f, hyphen)) {
					content.append(hyphen);
				}
				content.append(Character.toLowerCase(c));
			}
			if (content.length() == 0) {
				content.append(str);
			}
			return content.toString();
		}

	}

	/**
	 * Removes the separator chars matched by the function and capitalizes the
	 * following word and the first char.
	 *
	 * Its {@code toCase} returns a name containing illegal chars (such as
	 * "COUNT(*)") unchanged, and throws IllegalArgumentException if it is blank.
	 */
	public static class UpperCamelCase implements CaseFormat {

		private final Function<Character, Boolean> f;

		public UpperCamelCase(Function<Character, Boolean> f) {
			this.f = f;
		}

		public String toCase(CharSequence str) {
			Assert.hasNoText(str);
			if (!isCamelCaseConvertible(str)) {
				return str.toString();
			}
			String copy = str.toString().toLowerCase();
			StringBuilder content = new StringBuilder();
			boolean upperCase = false, changed = false;
			char c;
			for (int i = 0, l = copy.length(); i < l; i++) {
				c = copy.charAt(i);
				if (f.apply(c)) {
					upperCase = true;
				} else {
					if (upperCase) {
						c = Character.toUpperCase(c);
						changed = true;
						upperCase = false;
					}
					content.append(c);
				}
			}
			if (!changed || content.length() == 0) {
				return str.toString();
			}
			char firstChar = content.charAt(0);
			if (!Character.isUpperCase(firstChar)) {
				content.setCharAt(0, Character.toUpperCase(firstChar));
			}
			return content.toString();
		}

	}

	/**
	 * Removes the separator chars matched by the function, capitalizes the following
	 * word and lower-cases the first char.
	 *
	 * Its {@code toCase} returns a name containing illegal chars (such as
	 * "COUNT(*)") unchanged, and throws IllegalArgumentException if it is blank.
	 */
	public static class LowerCamelCase implements CaseFormat {

		private final Function<Character, Boolean> f;

		public LowerCamelCase(Function<Character, Boolean> f) {
			this.f = f;
		}

		public String toCase(CharSequence str) {
			Assert.hasNoText(str);
			if (!isCamelCaseConvertible(str)) {
				return str.toString();
			}
			String copy = str.toString().toLowerCase();
			StringBuilder content = new StringBuilder();
			boolean upperCase = false, changed = false;
			char c;
			for (int i = 0, l = copy.length(); i < l; i++) {
				c = copy.charAt(i);
				if (f.apply(c)) {
					upperCase = true;
				} else {
					if (upperCase) {
						c = Character.toUpperCase(c);
						changed = true;
						upperCase = false;
					}
					content.append(c);
				}
			}
			if (!changed || content.length() == 0) {
				return str.toString();
			}
			char firstChar = content.charAt(0);
			if (!Character.isLowerCase(firstChar)) {
				content.setCharAt(0, Character.toLowerCase(firstChar));
			}
			return content.toString();
		}
	}
}
