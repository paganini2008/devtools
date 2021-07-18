/**
* Copyright 2018-2021 Fred Feng (paganini.fy@gmail.com)

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
package com.github.paganini2008.devtools.io;

import java.util.Map;

import com.github.paganini2008.devtools.MatchMode;

/**
 * 
 * Resource
 * 
 * @author Fred Feng
 * @version 1.0
 */
public interface Resource {

	String getString(String name);

	String getString(String name, String defaultValue);

	Byte getByte(String name);

	Byte getByte(String name, Byte defaultValue);

	Short getShort(String name);

	Short getShort(String name, Short defaultValue);

	Integer getInteger(String name);

	Integer getInteger(String name, Integer defaultValue);

	Long getLong(String name);

	Long getLong(String name, Long defaultValue);

	Float getFloat(String name);

	Float getFloat(String name, Float defaultValue);

	Double getDouble(String name);

	Double getDouble(String name, Double defaultValue);

	Boolean getBoolean(String name);

	Boolean getBoolean(String name, Boolean defaultValue);

	Map<String, String> toMap();

	Map<String, String> toMap(String substr, MatchMode mode);

}
