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
package com.github.paganini2008.devtools.enums;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 
 * EnumUtils
 * 
 * Lookup helpers for enums implementing {@link EnumConstant}.
 *
 * @author Fred Feng
 * @date 2019-08-06
 * @since 2.0.1
 */
public abstract class EnumUtils {

	/**
	 * Returns the constants of the given group, in declaration order.
	 *
	 * @return the matching constants, or null if the enum type is null
	 * @throws IllegalArgumentException if the type is not an enum
	 */
	@SuppressWarnings("unchecked")
	public static <T extends EnumConstant> T[] findAll(Class<T> enumType, String group) {
		if (enumType == null) {
			return null;
		}
		List<T> matches = new ArrayList<T>();
		for (T constant : getEnumConstants(enumType)) {
			if (Objects.equals(constant.getGroup(), group)) {
				matches.add(constant);
			}
		}
		return matches.toArray((T[]) Array.newInstance(enumType, matches.size()));
	}

	/**
	 * Returns the constant whose {@link EnumConstant#getValue()} equals the given value.
	 *
	 * @return the constant, or null if the enum type is null
	 * @throws IllegalArgumentException if no constant matches or the type is not an enum
	 */
	public static <T extends EnumConstant> T valueOf(Class<T> enumType, int ordinal) {
		if (enumType == null) {
			return null;
		}
		for (T constant : getEnumConstants(enumType)) {
			if (ordinal == constant.getValue()) {
				return constant;
			}
		}
		throw new IllegalArgumentException("No enum by value '" + ordinal + "' of " + enumType.getCanonicalName());
	}

	private static <T extends EnumConstant> T[] getEnumConstants(Class<T> enumType) {
		if (!enumType.isEnum()) {
			throw new IllegalArgumentException("Not an enum type: " + enumType);
		}
		return enumType.getEnumConstants();
	}

}
