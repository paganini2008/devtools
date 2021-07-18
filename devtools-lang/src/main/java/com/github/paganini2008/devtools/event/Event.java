/**
* Copyright 2018-2021 Fred Feng (paganini.fy@gmail.com)

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

import java.util.EventObject;

/**
 * 
 * Event
 * 
 * @author Fred Feng
 * @since 1.0
 */
public abstract class Event<T> extends EventObject {

	private static final long serialVersionUID = 1L;

	public Event(Object source, T argument) {
		super(source);
		this.timestamp = System.currentTimeMillis();
		this.argument = argument;
	}

	private final long timestamp;
	private T argument;

	public T getArgument() {
		return argument;
	}

	public void setArgument(T argument) {
		this.argument = argument;
	}

	public long getTimestamp() {
		return timestamp;
	}

}
