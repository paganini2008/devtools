package com.github.paganini2008.devtools;

import java.util.Locale;

/**
 * 
 * Cases
 * 
 * @author Fred Feng
 * @revised 2019-07
 * @created 2018-03
 */
public abstract class Cases {

	public static final Case PLAIN = new PlainCase();

	public static final Case UNDER_SCORE = new UnderScoreCase('_');

	public static final Case CAMEL = new CamelCase();

	public static final Case LOWER = new LowerCase();

	static class PlainCase implements Case {

		public String toCase(CharSequence str) {
			return str != null ? str.toString().trim() : null;
		}

	}

	static class LowerCase implements Case {

		public String toCase(CharSequence str) {
			return str != null ? str.toString().toLowerCase(Locale.ENGLISH) : null;
		}

	}

	static class UnderScoreCase implements Case {

		UnderScoreCase(char combination) {
			this.combination = combination;
		}

		private final char combination;

		public String toCase(CharSequence str) {
			Assert.hasNoText(str);
			StringBuilder content = new StringBuilder();
			char c;
			for (int i = 0, l = str.length(); i < l; i++) {
				c = str.charAt(i);
				if (Character.isUpperCase(c)) {
					if (i != 0) {
						content.append(combination);
					}
				}
				content.append(Character.toLowerCase(c));
			}
			return content.toString();
		}

	}

	static class CamelCase implements Case {

		public String toCase(CharSequence str) {
			Assert.hasNoText(str);
			StringBuilder content = new StringBuilder();
			boolean upperCase = false;
			char c;
			for (int i = 0, l = str.length(); i < l; i++) {
				c = str.charAt(i);
				if (c == '_') {
					if (i == 0) {
						continue;
					}
					upperCase = true;
				} else {
					if (upperCase) {
						content.append(Character.toUpperCase(c));
						upperCase = false;
					} else {
						content.append(Character.toLowerCase(c));
					}
				}
			}
			return content.toString();
		}

	}

	public static String toLowerCase(CharSequence str) {
		return LOWER.toCase(str);
	}

	public static String toCamelCase(CharSequence str) {
		return CAMEL.toCase(str);
	}

	public static String toUnderScoreCase(CharSequence str) {
		return UNDER_SCORE.toCase(str);
	}

	public static void main(String[] args) {
		System.out.println(toLowerCase("lastModified"));
	}

}
