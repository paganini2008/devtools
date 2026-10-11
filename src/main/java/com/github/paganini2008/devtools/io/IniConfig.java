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
package com.github.paganini2008.devtools.io;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import com.github.paganini2008.devtools.MatchMode;

/**
 * 
 * Read and write access to INI style configuration organized in sections of {@code key=value} pairs.
 * 
 * @author Fred Feng
 * @date 2019-05-29
 * @since 2.0.1
 */
public interface IniConfig extends Iterable<String> {

	/**
	 * Replaces a whole section.
	 *
	 * @return the previous section, or {@code null}
	 */
	Map<String, String> set(String section, Map<String, String> kwargs);

	/**
	 * Sets a value, creating the section if necessary.
	 *
	 * @return the previous value, or {@code null}
	 */
	String set(String section, String name, String value);

	/**
	 * Writes the configuration in INI format to the stream. The stream is flushed but not closed.
	 */
	void store(OutputStream os, String charset) throws IOException;

	/**
	 * Returns the names of all sections.
	 */
	String[] sections();

	/**
	 * Returns a copy of the section, or an empty map if it does not exist.
	 */
	Map<String, String> get(String section);

	/**
	 * Returns the entries of the section whose keys match {@code substr} with the match mode.
	 */
	Map<String, String> get(String section, String substr, MatchMode mode);

	String getString(String section, String name);

	/**
	 * Returns the value, or the default value if the section or the key does not exist.
	 */
	String getString(String section, String name, String defaultValue);

	Byte getByte(String section, String name);

	Byte getByte(String section, String name, Byte defaultValue);

	Short getShort(String section, String name);

	Short getShort(String section, String name, Short defaultValue);

	Integer getInteger(String section, String name);

	/**
	 * Returns the value converted to an integer, or the default value if absent. The other typed getters behave the same.
	 */
	Integer getInteger(String section, String name, Integer defaultValue);

	Long getLong(String section, String name);

	Long getLong(String section, String name, Long defaultValue);

	Float getFloat(String section, String name);

	Float getFloat(String section, String name, Float defaultValue);

	Double getDouble(String section, String name);

	Double getDouble(String section, String name, Double defaultValue);

	Boolean getBoolean(String section, String name);

	Boolean getBoolean(String section, String name, Boolean defaultValue);

	/**
	 * Returns all sections.
	 */
	Map<String, Map<String, String>> toMap();

}
