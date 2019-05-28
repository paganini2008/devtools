package com.github.paganini2008.devtools;

import java.util.Locale;

import com.github.paganini2008.devtools.collection.LruMap;

/**
 * 
 * Cases
 * 
 * @author Fred Feng
 * @created 2018-03
 */
public abstract class Cases {

	private static final int cacheSize = 1024;
	private static final char join = '_';
	
	public static final Case PLAIN = new Plain();

	public static final Case LOWER_UNDER_SCORE = new LowerUnderScoreCase();

	public static final Case UPPER_UNDER_SCORE = new UpperUnderScoreCase();

	public static final Case LOWER_CAMEL = new LowerCamelCase();

	public static final Case UPPER_CAMEL = new UpperCamelCase();

	public static final Case LOWER = new LowerCase();

	public static final Case UPPER = new UpperCase();

	static abstract class AbstractCase implements Case {

		private final LruMap<CharSequence, String> cache = new LruMap<CharSequence, String>(cacheSize);

		public String toCase(CharSequence str) {
			String result = cache.get(str);
			if (result == null) {
				cache.put(str, transfer(str));
				result = cache.get(str);
			}
			return result;
		}

		protected abstract String transfer(CharSequence str);

	}

	/**
	 * 
	 * Plain
	 * @author Fred Feng
	 * @revised 2019-05
	 * @version 1.0
	 */
	static class Plain implements Case {

		public String toCase(CharSequence str) {
			return str.toString();
		}

	}

	static class UpperCase implements Case {

		public String toCase(CharSequence str) {
			Assert.hasNoText(str);
			return str.toString().toUpperCase(Locale.ENGLISH);
		}

	}

	static class LowerCase implements Case {

		public String toCase(CharSequence str) {
			Assert.hasNoText(str);
			return str.toString().toLowerCase(Locale.ENGLISH);
		}

	}

	public static abstract class LowerCombinedCase extends AbstractCase {

		private final String combination;

		public LowerCombinedCase(String combination) {
			this.combination = combination;
		}

		public LowerCombinedCase(char combination) {
			this.combination = String.valueOf(combination);
		}

		protected final String transfer(CharSequence str) {
			Assert.hasNoText(str);
			StringBuilder content = new StringBuilder();
			char c;
			for (int i = 0, l = str.length(); i < l; i++) {
				c = str.charAt(i);
				if (matches(str, c)) {
					if (i != 0) {
						content.append(combination);
					}
				}
				content.append(Character.toLowerCase(c));
			}
			return content.toString();
		}

		protected abstract boolean matches(CharSequence str, char c);

	}

	public static abstract class UpperCombinedCase extends AbstractCase {

		private final String combination;

		public UpperCombinedCase(String combination) {
			this.combination = combination;
		}

		public UpperCombinedCase(char combination) {
			this.combination = String.valueOf(combination);
		}

		protected final String transfer(CharSequence str) {
			Assert.hasNoText(str);
			StringBuilder content = new StringBuilder();
			char c;
			for (int i = 0, l = str.length(); i < l; i++) {
				c = str.charAt(i);
				if (matches(str, c)) {
					if (i != 0) {
						content.append(combination);
					}
				}
				content.append(Character.toUpperCase(c));
			}
			return content.toString();
		}

		protected abstract boolean matches(CharSequence str, char c);

	}

	static class LowerUnderScoreCase extends LowerCombinedCase {

		LowerUnderScoreCase() {
			super('_');
		}

		protected boolean matches(CharSequence str, char c) {
			return Character.isUpperCase(c);
		}
	}

	static class UpperUnderScoreCase extends UpperCombinedCase {

		UpperUnderScoreCase() {
			super('_');
		}

		protected boolean matches(CharSequence str, char c) {
			return Character.isUpperCase(c);
		}

	}

	static class LowerCamelCase extends AbstractCase {

		public String transfer(CharSequence str) {
			Assert.hasNoText(str);
			StringBuilder content = new StringBuilder();
			boolean upperCase = false;
			char c;
			for (int i = 0, l = str.length(); i < l; i++) {
				c = str.charAt(i);
				if (c == join) {
					if (i == 0) {
						continue;
					}
					upperCase = true;
				} else {
					if (upperCase) {
						content.append(Character.toUpperCase(c));
						upperCase = false;
					} else {
						content.append(c);
					}
				}
			}
			return content.toString();
		}

	}

	static class UpperCamelCase extends AbstractCase {

		public String transfer(CharSequence str) {
			Assert.hasNoText(str);
			StringBuilder content = new StringBuilder();
			boolean upperCase = true;
			char c;
			for (int i = 0, l = str.length(); i < l; i++) {
				c = str.charAt(i);
				if (c == join) {
					if (i == 0) {
						continue;
					}
					upperCase = true;
				} else {
					if (upperCase) {
						content.append(Character.toUpperCase(c));
						upperCase = false;
					} else {
						content.append(c);
					}
				}
			}
			return content.toString();
		}
	}

	private Cases() {
	}

	public static String toUpperCase(CharSequence str) {
		return UPPER.toCase(str);
	}

	public static String toLowerCase(CharSequence str) {
		return LOWER.toCase(str);
	}

	public static String toUpperCamelCase(CharSequence str) {
		return UPPER_CAMEL.toCase(str);
	}

	public static String toLowerCamelCase(CharSequence str) {
		return LOWER_CAMEL.toCase(str);
	}

	public static String toUpperUnderScoreCase(CharSequence str) {
		return UPPER_UNDER_SCORE.toCase(str);
	}

	public static String toLowerUnderScoreCase(CharSequence str) {
		return LOWER_UNDER_SCORE.toCase(str);
	}

	public static void main(String[] args) {
		System.out.println(toUpperCamelCase("last_modified"));
	}

}
