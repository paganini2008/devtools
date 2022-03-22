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
package com.github.paganini2008.devtools.collection;

import java.util.LinkedHashMap;

import com.github.paganini2008.devtools.MatchMode;

/**
 * 
 * StringKeyMatchedMap
 *
 * @author Fred Feng
 * 
 * @since 2.0.1
 */
public class StringKeyMatchedMap<V> extends KeyMatchedMap<String, V> {

	private static final long serialVersionUID = 7256088542898472026L;

	public StringKeyMatchedMap(MatchMode matchMode) {
		this(matchMode, true);
	}

	public StringKeyMatchedMap(MatchMode matchMode, boolean matchFirst) {
		super(new LinkedHashMap<String, V>(), matchFirst);
		this.matchMode = matchMode;
	}

	private final MatchMode matchMode;

	@Override
	protected boolean match(String key, Object inputKey) {
		return matchMode.matches(key, (String) inputKey);
	}

}
