/**
* Copyright 2021 Fred Feng (paganini.fy@gmail.com)

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

import java.util.Locale;

import com.github.paganini2008.devtools.collection.LruMap;

/**
 * 
 * LocaleUtils
 *
 * @author Fred Feng
 * @version 1.0
 */
public abstract class LocaleUtils {

	private static final LruMap<String, Locale> localeCache = new LruMap<String, Locale>(16);

	public static Locale getLocale(String str) {
		if (StringUtils.isBlank(str)) {
			throw new IllegalArgumentException("Locale format is required.");
		}
		Locale locale;
		if (null == (locale = localeCache.get(str))) {
			locale = toLocale(str);
			localeCache.put(str, locale);
		}
		return locale;
	}

	public static Locale toLocale(String str) {
		if (StringUtils.isBlank(str)) {
			throw new IllegalArgumentException("Locale format is required.");
		}
		String[] args = str.split("_");
		int length = args.length;
		if (length > 3) {
			throw new IllegalArgumentException("Invalid locale format: " + str);
		}
		switch (length) {
		case 3:
		case 2:
			if (args[1].length() != 2 && !args[1].matches("[A-Z]+")) {
				throw new IllegalArgumentException("Invalid locale format: " + str);
			}
		case 1:
			if (args[0].length() != 2 && !args[0].matches("[a-z]+")) {
				throw new IllegalArgumentException("Invalid locale format: " + str);
			}
			break;
		}
		args = ArrayUtils.copy(args, 0, 3, "");
		return new Locale(args[0], args[1], args[2]);
	}

}
