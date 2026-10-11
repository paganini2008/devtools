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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.github.paganini2008.devtools.primitives.Chars;

/**
 * RandomStringUtils
 * 
 * Random string generators. Character ranges given as {@code from}/{@code to} are indexes into the
 * supplied characters, {@code [from, to)}.
 * 
 * @author Fred Feng
 * @date 2019-05-29
 * @since 2.0.1
 */
public abstract class RandomStringUtils {

	private final static char[] LETTERS_LOWER;
	private final static char[] LETTERS_UPPER;
	private final static char[] LETTERS;
	private final static char[] DIGITS;
	private final static char[] DIGITS_LETTERS_LOWER;
	private final static char[] DIGITS_LETTERS_UPPER;
	private final static char[] DIGITS_LETTERS;

	static {
		List<Character> lettersLower = new ArrayList<Character>();
		List<Character> lettersUpper = new ArrayList<Character>();
		List<Character> letters = new ArrayList<Character>();
		List<Character> digits = new ArrayList<Character>();
		List<Character> digitsLettersLower = new ArrayList<Character>();
		List<Character> digitsLettersUpper = new ArrayList<Character>();
		List<Character> digitsLetters = new ArrayList<Character>();

		for (int i : Sequence.forEach(97, 123)) {
			lettersLower.add((char) i);
		}
		for (int i : Sequence.forEach(65, 91)) {
			lettersUpper.add((char) i);
		}
		for (int i : Sequence.forEach(48, 58)) {
			digits.add((char) i);
		}

		letters.addAll(lettersLower);
		letters.addAll(lettersUpper);
		digitsLettersLower.addAll(digits);
		digitsLettersLower.addAll(lettersLower);
		digitsLettersUpper.addAll(digits);
		digitsLettersUpper.addAll(lettersUpper);
		digitsLetters.addAll(letters);
		digitsLetters.addAll(digits);

		LETTERS_LOWER = Chars.toArray(lettersLower);
		LETTERS_UPPER = Chars.toArray(lettersUpper);
		LETTERS = Chars.toArray(letters);
		DIGITS = Chars.toArray(digits);
		DIGITS_LETTERS_LOWER = Chars.toArray(digitsLettersLower);
		DIGITS_LETTERS_UPPER = Chars.toArray(digitsLettersUpper);
		DIGITS_LETTERS = Chars.toArray(digitsLetters);
	}

	/**
	 * Returns a random string of {@code count} digits and letters.
	 */
	public static String randomString(int count) {
		return randomString(count, true, true, true);
	}

	/**
	 * Returns a random string of {@code count} characters chosen from the selected character classes.
	 *
	 * @throws IllegalArgumentException if no character class is selected
	 */
	public static String randomString(int count, boolean digits, boolean lowerCaseLetters, boolean upperCaseLetters) {
		return randomString(count, getExample(digits, lowerCaseLetters, upperCaseLetters));
	}

	/**
	 * Returns a random string of {@code count} characters chosen from the given characters.
	 *
	 * @throws IllegalArgumentException if the character range is empty or out of bounds
	 */
	public static String randomString(int count, String example) {
		return randomString(count, example.toCharArray());
	}

	/**
	 * Returns a random string of {@code count} characters chosen from the given characters.
	 *
	 * @throws IllegalArgumentException if the character range is empty or out of bounds
	 */
	public static String randomString(int count, char[] chars) {
		return randomString(count, chars, 0);
	}

	/**
	 * Returns a random string of {@code count} characters chosen from the given characters.
	 *
	 * @throws IllegalArgumentException if the character range is empty or out of bounds
	 */
	public static String randomString(int count, String example, int from) {
		return randomString(count, example.toCharArray(), from);
	}

	/**
	 * Returns a random string of {@code count} characters chosen from the given characters.
	 *
	 * @throws IllegalArgumentException if the character range is empty or out of bounds
	 */
	public static String randomString(int count, char[] chars, int from) {
		return randomString(count, chars, from, chars.length);
	}

	/**
	 * Returns a random string of {@code count} characters chosen from the given characters.
	 *
	 * @throws IllegalArgumentException if the character range is empty or out of bounds
	 */
	public static String randomString(int count, String example, int from, int to) {
		return randomString(count, example.toCharArray(), from, to);
	}

	/**
	 * Returns a random string of {@code count} characters chosen from the given characters.
	 *
	 * @throws IllegalArgumentException if the character range is empty or out of bounds
	 */
	public static String randomString(int count, char[] chars, int from, int to) {
		checkRange(chars, from, to);
		char[] buffer = new char[count];
		for (int i = 0; i < count; i++) {
			buffer[i] = chars[RandomUtils.randomInt(from, to)];
		}
		return new String(buffer);
	}

	/**
	 * Returns {@code length} distinct random strings of {@code count} digits and letters.
	 */
	public static String[] randomStrings(int length, int count) {
		return randomStrings(length, count, true, true, true);
	}

	/**
	 * Returns {@code length} distinct random strings of {@code count} characters chosen from the selected
	 * character classes.
	 */
	public static String[] randomStrings(int length, int count, boolean digits, boolean lowerCaseLetters, boolean upperCaseLetters) {
		return randomStrings(length, count, getExample(digits, lowerCaseLetters, upperCaseLetters));
	}

	/**
	 * Returns {@code length} distinct random strings of {@code count} characters chosen from the given characters.
	 *
	 * @throws IllegalArgumentException if the characters cannot form {@code length} distinct strings
	 */
	public static String[] randomStrings(int length, int count, String example) {
		return randomStrings(length, count, example, 0);
	}

	/**
	 * Returns {@code length} distinct random strings of {@code count} characters chosen from the given characters.
	 *
	 * @throws IllegalArgumentException if the characters cannot form {@code length} distinct strings
	 */
	public static String[] randomStrings(int length, int count, String example, int from) {
		return randomStrings(length, count, example.toCharArray(), from);
	}

	/**
	 * Returns {@code length} distinct random strings of {@code count} characters chosen from the given characters.
	 *
	 * @throws IllegalArgumentException if the characters cannot form {@code length} distinct strings
	 */
	public static String[] randomStrings(int length, int count, String example, int from, int to) {
		return randomStrings(length, count, example.toCharArray(), from, to);
	}

	/**
	 * Returns {@code length} distinct random strings of {@code count} characters chosen from the given characters.
	 *
	 * @throws IllegalArgumentException if the characters cannot form {@code length} distinct strings
	 */
	public static String[] randomStrings(int length, int count, char[] chars) {
		return randomStrings(length, count, chars, 0);
	}

	/**
	 * Returns {@code length} distinct random strings of {@code count} characters chosen from the given characters.
	 *
	 * @throws IllegalArgumentException if the characters cannot form {@code length} distinct strings
	 */
	public static String[] randomStrings(int length, int count, char[] chars, int from) {
		return randomStrings(length, count, chars, from, chars.length);
	}

	/**
	 * Returns {@code length} distinct random strings of {@code count} characters chosen from the given characters.
	 *
	 * @throws IllegalArgumentException if the characters cannot form {@code length} distinct strings
	 */
	public static String[] randomStrings(int length, int count, char[] chars, int from, int to) {
		checkRange(chars, from, to);
		Set<Character> distinct = new HashSet<Character>();
		for (int i = from; i < to; i++) {
			distinct.add(chars[i]);
		}
		double combinations = Math.pow(distinct.size(), count);
		if (combinations < length) {
			throw new IllegalArgumentException(
					"Cannot generate " + length + " distinct strings with only " + (long) combinations + " possible combinations.");
		}
		String[] results = new String[length];
		String rs;
		for (int i = 0; i < length; i++) {
			do {
				rs = randomString(count, chars, from, to);
			} while (ArrayUtils.contains(results, rs));
			results[i] = rs;
		}
		return results;
	}

	private static void checkRange(char[] chars, int from, int to) {
		if (chars == null || from < 0 || to > chars.length || from >= to) {
			throw new IllegalArgumentException("Invalid character range: [" + from + ", " + to + ")");
		}
	}

	private static char[] getExample(boolean digits, boolean lowerCaseLetters, boolean upperCaseLetters) {
		char[] ch = Chars.EMPTY_ARRAY;
		if (digits && lowerCaseLetters && upperCaseLetters) {
			ch = DIGITS_LETTERS;
		} else if (digits && lowerCaseLetters && !upperCaseLetters) {
			ch = DIGITS_LETTERS_LOWER;
		} else if (digits && !lowerCaseLetters && upperCaseLetters) {
			ch = DIGITS_LETTERS_UPPER;
		} else if (!digits && lowerCaseLetters && upperCaseLetters) {
			ch = LETTERS;
		} else if (digits && !lowerCaseLetters && !upperCaseLetters) {
			ch = DIGITS;
		} else if (!digits && lowerCaseLetters && !upperCaseLetters) {
			ch = LETTERS_LOWER;
		} else if (!digits && !lowerCaseLetters && upperCaseLetters) {
			ch = LETTERS_UPPER;
		}
		return ch.clone();
	}
}
