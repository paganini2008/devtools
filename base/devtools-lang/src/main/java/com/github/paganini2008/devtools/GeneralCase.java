package com.github.paganini2008.devtools;

/**
 * 
 * GeneralCase
 * 
 * @author Fred Feng
 * @created 2019-11
 * @version 1.0
 */
public abstract class GeneralCase {

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

}
