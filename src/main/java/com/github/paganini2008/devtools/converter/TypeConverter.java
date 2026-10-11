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
 * Registry of {@link BasicConverter}s keyed by target type, used to convert arbitrary values to a required type.
 *
 * @author Fred Feng
 * @date 2019-05-29
 * @since 2.0.1
 */
public interface TypeConverter {

	/**
	 * Registers (or replaces) the converter for the given target type.
	 */
	<T> void registerType(Class<T> javaType, BasicConverter<T> converter);

	/**
	 * Removes the converter registered for the given target type.
	 */
	void removeType(Class<?> javaType);

	/**
	 * Checks whether a converter is registered for the given target type.
	 */
	boolean hasType(Class<?> javaType);

	/**
	 * Returns the converter registered for the given target type, or null if none.
	 */
	<T> BasicConverter<T> lookupType(Class<T> javaType);

	/**
	 * Converts the value to the required type. The value is returned as is if it is already an instance of the required
	 * type.
	 * 
	 * @param value the value to convert, may be null
	 * @param requiredType the target type, primitive types are supported
	 * @param defaultValue returned when the value is null, no converter is registered or the conversion fails
	 * @return the converted value or the default value
	 */
	<T> T convertValue(Object value, Class<T> requiredType, T defaultValue);

}
