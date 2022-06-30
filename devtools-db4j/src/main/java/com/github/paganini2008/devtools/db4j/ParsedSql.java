/**
* Copyright 2017-2022 Fred Feng (paganini.fy@gmail.com)

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
package com.github.paganini2008.devtools.db4j;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.github.paganini2008.devtools.PlaceholderTokenParser;
import com.github.paganini2008.devtools.collection.LruMap;

/**
 * ParsedSql
 * 
 * @author Fred Feng
 * @since 2.0.1
 */
public final class ParsedSql implements Serializable {

	private static final long serialVersionUID = -7823481840053509465L;
	private static final LruMap<String, ParsedSql> parsedSqlCache = new LruMap<String, ParsedSql>(1024);
	private static final String DEFAULT_PLACEHOLDER_PREFIX = "{";
	private static final String DEFAULT_PLACEHOLDER_SUFFEX = "}";
	final StringBuilder rawSql = new StringBuilder();
	final List<String> parameterNames = new ArrayList<String>();
	final List<String> defaultValues = new ArrayList<String>();

	ParsedSql() {
	}

	public String[] getParameterNames() {
		return parameterNames.toArray(new String[0]);
	}

	public String[] getDefaultValues() {
		return defaultValues.toArray(new String[0]);
	}

	public StringBuilder getRawSql() {
		return rawSql;
	}

	public String toString() {
		return rawSql.toString();
	}

	public static ParsedSql parse(String sql) {
		return parsedSqlCache.getOrDefault(sql, doParse(sql));
	}

	private static ParsedSql doParse(String sql) {
		ParsedSql parsedSql = new ParsedSql();
		PlaceholderTokenParser placeholderTokenizer = new PlaceholderTokenParser(DEFAULT_PLACEHOLDER_PREFIX, DEFAULT_PLACEHOLDER_SUFFEX);
		parsedSql.rawSql.append(placeholderTokenizer.parse(sql, variable -> {
			String parameterName = variable;
			String defaultValue = null;
			int index;
			if ((index = variable.indexOf(':')) > 0) {
				parameterName = variable.substring(0, index);
				defaultValue = variable.substring(index + 1);
			}
			parsedSql.parameterNames.add(parameterName);
			parsedSql.defaultValues.add(defaultValue);
			return "?";
		}));
		return parsedSql;
	}

}
