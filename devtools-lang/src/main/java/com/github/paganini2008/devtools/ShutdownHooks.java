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
package com.github.paganini2008.devtools;

import java.util.Queue;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * ShutdownHooks
 * 
 * @author Fred Feng
 * @since 2.0.1
 */
public final class ShutdownHooks {

	private final static HookThread instance = new HookThread();

	static {
		Runtime.getRuntime().addShutdownHook(instance);
	}

	static class HookThread extends Thread {

		final Queue<ShutdownHook> queue;

		HookThread() {
			this.queue = new PriorityBlockingQueue<ShutdownHook>();
		}

		public void run() {
			ShutdownHook hook;
			while (!queue.isEmpty()) {
				hook = queue.poll();
				try {
					hook.process();
					System.out.println("Hook '" + hook.toString() + "'is executed.");
				} catch (Throwable e) {
					hook.ignoreException(e);
				}
			}
		}
	}

	public static void addHook(ShutdownHook hook) {
		if (hook != null) {
			instance.queue.add(hook);
		}
	}

	public static void removeHook(ShutdownHook hook) {
		if (hook != null) {
			instance.queue.remove(hook);
		}
	}

	public static void cleanHooks() {
		instance.queue.clear();
	}

	public static int countOfHooks() {
		return instance.queue.size();
	}

	private ShutdownHooks() {
	}

}
