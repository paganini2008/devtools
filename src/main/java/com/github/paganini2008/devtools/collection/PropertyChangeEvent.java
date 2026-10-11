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

import java.util.EventObject;

/**
 * 
 * PropertyChangeEvent
 * <p>
 * Event carrying the latest and the previous version of changed properties.
 * 
 * @author Fred Feng
 * @date 2019-05-29
 * @create 2019-03
 */
public class PropertyChangeEvent<T> extends EventObject {

	private static final long serialVersionUID = -2624472937448392535L;

	/**
	 * Creates an event.
	 *
	 * @param latest
	 *            the newly loaded version (also the event source)
	 * @param current
	 *            the version before the change
	 */
	public PropertyChangeEvent(T latest, T current) {
		super(latest);
		this.current = current;
	}

	private final T current;

	/**
	 * Returns the newly loaded version.
	 */
	@SuppressWarnings("unchecked")
	public T getLatestVersion() {
		return (T) getSource();
	}

	/**
	 * Returns the version before the change.
	 */
	public T getCurrentVersion() {
		return current;
	}

}
