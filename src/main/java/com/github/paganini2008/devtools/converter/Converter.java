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
 * Converts a source value of type {@code S} into a value of type {@code T}, falling back to a default value.
 * 
 * @author Fred Feng
 * @date 2019-05-29
 * @since 2.0.1
 */
public interface Converter<S, T> {

	/**
	 * Converts the source value.
	 * 
	 * @param source the value to convert, may be null
	 * @param defaultValue the value returned when the source is null or cannot be converted
	 * @return the converted value or the default value
	 */
	T convertValue(S source, T defaultValue);

}
