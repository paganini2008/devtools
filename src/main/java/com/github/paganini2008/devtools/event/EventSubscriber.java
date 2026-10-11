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
 * EventSubscriber
 * <p>
 * Receives events of type {@code E} from an {@link EventBus}.
 * 
 * @author Fred Feng
 * @date 2019-05-29
 * 
 * @since 2.0.1
 */
public interface EventSubscriber<E extends Event<T>, T> extends Comparable<EventSubscriber<E, T>> {

	/**
	 * Handles an event.
	 *
	 * @param event
	 *            the event
	 */
	void onEventFired(E event);

	/**
	 * Returns whether this subscriber joins a pub/sub group (true, default) or a queue group. The
	 * first subscriber of an event class decides the group type.
	 *
	 * @return true for pub/sub
	 */
	default boolean isPubSub() {
		return true;
	}

	/**
	 * Orders subscribers within a pub/sub group; all subscribers are equal by default.
	 */
	default int compareTo(EventSubscriber<E, T> subscriber) {
		return 0;
	}

}
