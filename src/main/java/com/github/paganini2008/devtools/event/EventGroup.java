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
package com.github.paganini2008.devtools.event;

/**
 * 
 * EventGroup
 * <p>
 * A group of subscribers of one event class, dispatching events according to its strategy.
 *
 * @author Fred Feng
 * @date 2019-05-29
 * @since 2.0.1
 */
public interface EventGroup<E extends Event<T>, T> extends EventSubscriber<E, T> {

	/**
	 * Adds a subscriber; duplicates are ignored.
	 *
	 * @param subscriber
	 *            the subscriber
	 */
	void subscribe(EventSubscriber<E, T> subscriber);

	/**
	 * Removes a subscriber.
	 *
	 * @param subscriber
	 *            the subscriber
	 */
	void unsubscribe(EventSubscriber<E, T> subscriber);

}
