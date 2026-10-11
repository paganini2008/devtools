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
package com.github.paganini2008.devtools.converter;

/**
 * Static facade of a shared {@link StandardTypeConverter} for converting values between common Java types.
 * 
 * @author Fred Feng
 * @date 2019-05-29
 * @since 2.0.1
 */
public abstract class ConvertUtils {

	private static final TypeConverter INSTANCE = new StandardTypeConverter();

	/**
	 * Registers (or replaces) the global converter for the given target type.
	 */
	public static <T> void registerType(Class<T> javaType, BasicConverter<T> converter) {
		INSTANCE.registerType(javaType, converter);
	}

	/**
	 * Removes the global converter registered for the given target type.
	 */
	public static void removeType(Class<?> javaType) {
		INSTANCE.removeType(javaType);
	}

	/**
	 * Checks whether a converter is registered for the given type. Note that the result is discarded.
	 * 
	 * @deprecated the result is lost because this method returns void, use {@link #lookupType(Class)} and compare
	 *             the result with null instead
	 */
	@Deprecated
	public static void hasType(Class<?> javaType) {
		INSTANCE.hasType(javaType);
	}

	/**
	 * Returns the global converter registered for the given target type, or null if none.
	 */
	public static <T> BasicConverter<T> lookupType(Class<T> javaType) {
		return INSTANCE.lookupType(javaType);
	}

	/**
	 * Converts the value to the given type, returning null when it cannot be converted.
	 * 
	 * <pre>
	 * ConvertUtils.convertValue(null, Integer.class)  = null
	 * ConvertUtils.convertValue("3", Integer.class)   = 3
	 * ConvertUtils.convertValue("3", String.class)    = "3"
	 * ConvertUtils.convertValue(12, String.class)     = "12"
	 * ConvertUtils.convertValue("1,2,3", int[].class) = [1, 2, 3]
	 * </pre>
	 */
	public static <T> T convertValue(Object value, Class<T> javaType) {
		return convertValue(value, javaType, null);
	}

	/**
	 * Converts the value to the given type, returning the default value when it is null or cannot be converted.
	 * 
	 * <pre>
	 * ConvertUtils.convertValue("abc", Integer.class, 9) = 9
	 * ConvertUtils.convertValue(null, String.class, "x") = "x"
	 * </pre>
	 */
	public static <T> T convertValue(Object value, Class<T> javaType, T defaultValue) {
		return INSTANCE.convertValue(value, javaType, defaultValue);
	}
}
