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
package com.github.paganini2008.devtools.converter;

/**
 * 
 * ConvertUtils
 * 
 * @author Fred Feng
 * @since 2.0.1
 */
public abstract class ConvertUtils {

	private static final TypeConverter INSTANCE = new StandardTypeConverter();

	public static <T> void registerType(Class<T> javaType, BasicConverter<T> converter) {
		INSTANCE.registerType(javaType, converter);
	}

	public static void removeType(Class<?> javaType) {
		INSTANCE.removeType(javaType);
	}

	public static void hasType(Class<?> javaType) {
		INSTANCE.hasType(javaType);
	}

	public static <T> BasicConverter<T> lookupType(Class<T> javaType) {
		return INSTANCE.lookupType(javaType);
	}

	public static <T> T convertValue(Object value, Class<T> javaType) {
		return convertValue(value, javaType, null);
	}

	public static <T> T convertValue(Object value, Class<T> javaType, T defaultValue) {
		return INSTANCE.convertValue(value, javaType, defaultValue);
	}
	
	public static void main(String[] args) {
		System.out.println(ConvertUtils.convertValue(38901.34554678f, String.class)) ;
	}

}
