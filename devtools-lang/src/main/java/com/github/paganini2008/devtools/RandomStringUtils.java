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

import java.util.ArrayList;
import java.util.List;

import com.github.paganini2008.devtools.primitives.Chars;

/**
 * RandomStringUtils
 * 
 * @author Fred Feng
 * @version 1.0
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

		for (int i : Sequence.forEach(97, 122)) {
			lettersLower.add((char) i);
		}
		for (int i : Sequence.forEach(65, 90)) {
			lettersUpper.add((char) i);
		}
		for (int i : Sequence.forEach(48, 57)) {
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

	public static void main(String[] args) throws Exception {
		for (String s : randomStrings(10000, 100, true, true, true)) {
			System.out.println(s);
		}
	}

	public static String randomString(int count) {
		return randomString(count, true, true, true);
	}

	public static String randomString(int count, boolean digits, boolean lowerCaseLetters, boolean upperCaseLetters) {
		return randomString(count, getExample(digits, lowerCaseLetters, upperCaseLetters));
	}

	public static String randomString(int count, String example) {
		return randomString(count, example.toCharArray());
	}

	public static String randomString(int count, char[] chars) {
		return randomString(count, chars, 0);
	}

	public static String randomString(int count, String example, int from) {
		return randomString(count, example.toCharArray(), from);
	}

	public static String randomString(int count, char[] chars, int from) {
		return randomString(count, chars, from, chars.length);
	}

	public static String randomString(int count, String example, int from, int to) {
		return randomString(count, example.toCharArray(), from, to);
	}

	public static String randomString(int count, char[] chars, int from, int to) {
		char[] buffer = new char[count];
		for (int i = 0; i < count; i++) {
			buffer[i] = chars[RandomUtils.randomInt(from, to)];
		} 
		return new String(buffer);
	}

	public static String[] randomStrings(int length, int count) {
		return randomStrings(length, count, true, true, true);
	}

	public static String[] randomStrings(int length, int count, boolean digits, boolean lowerCaseLetters, boolean upperCaseLetters) {
		return randomStrings(length, count, getExample(digits, lowerCaseLetters, upperCaseLetters));
	}

	public static String[] randomStrings(int length, int count, String example) {
		return randomStrings(length, count, example, 0);
	}

	public static String[] randomStrings(int length, int count, String example, int from) {
		return randomStrings(length, count, example.toCharArray(), from);
	}

	public static String[] randomStrings(int length, int count, String example, int from, int to) {
		return randomStrings(length, count, example.toCharArray(), from, to);
	}

	public static String[] randomStrings(int length, int count, char[] chars) {
		return randomStrings(length, count, chars, 0);
	}

	public static String[] randomStrings(int length, int count, char[] chars, int from) {
		return randomStrings(length, count, chars, from, chars.length);
	}

	public static String[] randomStrings(int length, int count, char[] chars, int from, int to) {
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
