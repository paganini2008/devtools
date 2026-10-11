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
package com.github.paganini2008.devtools.collection;

import java.util.LinkedHashMap;

import com.github.paganini2008.devtools.MatchMode;

/**
 * 
 * StringKeyMatchedMap
 * <p>
 * A {@link KeyMatchedMap} with string keys, which uses a {@link MatchMode} to
 * test a stored key (as the pattern) against the input key. A null input key
 * matches nothing.
 *
 * @author Fred Feng
 * @date 2020-12-18
 * 
 * @since 2.0.1
 */
public class StringKeyMatchedMap<V> extends KeyMatchedMap<String, V> {

	private static final long serialVersionUID = 7256088542898472026L;

	/**
	 * Creates a StringKeyMatchedMap trying the stored keys in ascending order.
	 * 
	 * @param matchMode
	 *            the way to match keys
	 */
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
		return inputKey != null && matchMode.matches(key, inputKey.toString());
	}

}
