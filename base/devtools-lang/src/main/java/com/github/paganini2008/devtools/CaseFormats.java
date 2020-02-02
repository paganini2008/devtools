package com.github.paganini2008.devtools;

import java.util.function.Function;

/**
 * 
 * CaseFormats
 * 
 * @author Fred Feng
 * @revised 2019-07
 * @created 2018-03
 */
public abstract class CaseFormats {
	
	public static final CaseFormat LOWER_HYPHEN = new CaseFormats.LowerHyphenCase(ch -> {
		return Character.isUpperCase(ch);
	}, '-');

	public static final CaseFormat UPPER_HYPHEN = new CaseFormats.UpperHyphenCase(ch -> {
		return Character.isUpperCase(ch);
	}, '-');

	public static final CaseFormat LOWER_UNDERSCORE = new CaseFormats.LowerHyphenCase(ch -> {
		return Character.isUpperCase(ch);
	}, '_');

	public static final CaseFormat UPPER_UNDERSCORE = new CaseFormats.UpperHyphenCase(ch -> {
		return Character.isUpperCase(ch);
	}, '_');

	public static final CaseFormat LOWER_CAMEL = new CaseFormats.LowerCamelCase(ch -> {
		return ch == '-' || ch == '_';
	});

	public static final CaseFormat UPPER_CAMEL = new CaseFormats.UpperCamelCase(ch -> {
		return ch == '-' || ch == '_';
	});

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
			boolean start = false;
			for (int i = 0, l = str.length(); i < l; i++) {
				c = str.charAt(i);
				if (!start && Character.isAlphabetic(c)) {
					start = true;
				}
				if (start) {
					if (f.apply(c)) {
						content.append(hyphen);
					}
					content.append(Character.toUpperCase(c));
				}
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
			boolean start = false;
			for (int i = 0, l = str.length(); i < l; i++) {
				c = str.charAt(i);
				if (!start && Character.isAlphabetic(c)) {
					start = true;
				}
				if (start) {
					if (f.apply(c)) {
						content.append(hyphen);
					}
					content.append(Character.toLowerCase(c));
				}
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
			StringBuilder content = new StringBuilder();
			boolean start = false, upperCase = false;
			char c;
			for (int i = 0, l = str.length(); i < l; i++) {
				c = str.charAt(i);
				if (!start && Character.isAlphabetic(c)) {
					start = true;
					upperCase = true;
				}
				if (start) {
					if (f.apply(c)) {
						upperCase = true;
					} else {
						if (upperCase) {
							c = Character.toUpperCase(c);
							upperCase = false;
						}
						content.append(c);
					}
				}
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
			StringBuilder content = new StringBuilder();
			boolean start = false, upperCase = false;
			char c;
			for (int i = 0, l = str.length(); i < l; i++) {
				c = str.charAt(i);
				if (!start && Character.isAlphabetic(c)) {
					start = true;
				}
				if (start) {
					if (f.apply(c)) {
						upperCase = true;
					} else {
						if (upperCase) {
							c = Character.toUpperCase(c);
							upperCase = false;
						}
						content.append(c);
					}
				}
			}
			char firstChar = content.charAt(0);
			if (!Character.isLowerCase(firstChar)) {
				content.setCharAt(0, Character.toLowerCase(firstChar));
			}
			return content.toString();
		}
	}

}
