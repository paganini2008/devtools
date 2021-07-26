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

import com.github.paganini2008.devtools.CaseFormats;

/**
 * 
 * CamelCaseInsensitiveMap
 *
 * @author Fred Feng
 * @since 2.0.1
 */
public class CamelCaseInsensitiveMap<V> extends KeyConversionMap<String, String, V> {

	private static final long serialVersionUID = 9123521793974589929L;

	public CamelCaseInsensitiveMap() {
		super(new LinkedHashMap<String, V>());
	}

	@Override
	protected String convertKey(Object key) {
		if (key != null) {
			return CaseFormats.LOWER_CAMEL.toCase(key.toString());
		}
		return "";
	}

}
