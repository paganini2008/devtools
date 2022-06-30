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
package com.github.paganini2008.devtools.collection;

import java.util.EventObject;

/**
 * 
 * PropertyChangeEvent
 * 
 * @author Fred Feng
 * @create 2019-03
 */
public class PropertyChangeEvent<T> extends EventObject {

	private static final long serialVersionUID = -2624472937448392535L;

	public PropertyChangeEvent(T latest, T current) {
		super(latest);
		this.current = current;
	}

	private final T current;

	@SuppressWarnings("unchecked")
	public T getLatestVersion() {
		return (T) getSource();
	}

	public T getCurrentVersion() {
		return current;
	}

}
