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
package com.github.paganini2008.devtools;

/**
 * 
 * Observer
 * 
 * Receives notifications from an {@link Observable}. Observers of the same topic are notified
 * in ascending order of {@link #compareTo(Observer)}; by default all observers are equal.
 * 
 * @author Fred Feng
 * @date 2019-05-29
 * @since 2.0.1
 */
public interface Observer extends Comparable<Observer> {

	/**
	 * Called when the observed object publishes an event.
	 *
	 * @param ob the observable that published the event
	 * @param arg the event argument, may be null
	 */
	void update(Observable ob, Object arg);

	default int compareTo(Observer other) {
		return 0;
	}
}
