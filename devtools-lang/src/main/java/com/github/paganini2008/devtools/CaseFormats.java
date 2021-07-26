/**
* Copyright 2017-2021 Fred Feng (paganini.fy@gmail.com)

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
 * 
 * CaseFormats
 *
 * @author Fred Feng
 * @since 2.0.1
 */
public abstract class CaseFormats {

	public static final char UNDERSCORE_CHAR = '_';

	public static final char HYPHEN_CHAR = '-';

	public static final CaseFormat LOWER_HYPHEN = new CaseFormats.LowerHyphenCase(ch -> {
		return Character.isUpperCase(ch);
	}, HYPHEN_CHAR);

	public static final CaseFormat UPPER_HYPHEN = new CaseFormats.UpperHyphenCase(ch -> {
		return Character.isUpperCase(ch);
	}, HYPHEN_CHAR);

	public static final CaseFormat LOWER_UNDERSCORE = new CaseFormats.LowerHyphenCase(ch -> {
		return Character.isUpperCase(ch);
	}, UNDERSCORE_CHAR);

	public static final CaseFormat UPPER_UNDERSCORE = new CaseFormats.UpperHyphenCase(ch -> {
		return Character.isUpperCase(ch);
	}, UNDERSCORE_CHAR);

	public static final CaseFormat LOWER_CAMEL = new CaseFormats.LowerCamelCase(ch -> {
		return ch == HYPHEN_CHAR || ch == UNDERSCORE_CHAR;
	});

	public static final CaseFormat UPPER_CAMEL = new CaseFormats.UpperCamelCase(ch -> {
		return ch == HYPHEN_CHAR || ch == UNDERSCORE_CHAR;
	});

	public static boolean isWellFormatedStartWith(char firstChar) {
		return Character.isAlphabetic(firstChar) || firstChar == HYPHEN_CHAR || firstChar == '$';
	}

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
				if (f.apply(c) && i != 0) {
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
				if (f.apply(c) && i != 0) {
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

	public static class UpperCamelCase implements CaseFormat {

		private final Function<Character, Boolean> f;

		public UpperCamelCase(Function<Character, Boolean> f) {
			this.f = f;
		}

		public String toCase(CharSequence str) {
			Assert.hasNoText(str);
			if (!isWellFormatedNaming(str, null)) {
				throw new IllegalArgumentException("Bad Formated naming: " + str);
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
			if (!changed) {
				return str.toString();
			}
			char firstChar = content.charAt(0);
			if (!Character.isUpperCase(firstChar)) {
				content.setCharAt(0, Character.toUpperCase(firstChar));
			}
			return content.toString();
		}

	}

	public static class LowerCamelCase implements CaseFormat {

		private final Function<Character, Boolean> f;

		public LowerCamelCase(Function<Character, Boolean> f) {
			this.f = f;
		}

		public String toCase(CharSequence str) {
			Assert.hasNoText(str);
			if (!isWellFormatedNaming(str, null)) {
				throw new IllegalArgumentException("Bad Formated naming: " + str);
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
			if (!changed) {
				return str.toString();
			}
			char firstChar = content.charAt(0);
			if (!Character.isLowerCase(firstChar)) {
				content.setCharAt(0, Character.toLowerCase(firstChar));
			}
			return content.toString();
		}
	}

	public static void main(String[] args) {
		System.out.println(isWellFormatedNaming("COLUMN_NAME", null));
		System.out.println(UPPER_UNDERSCORE.toCase("columnName"));
	}

}
