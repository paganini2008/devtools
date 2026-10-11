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
package com.github.paganini2008.devtools.multithreads;

/**
 * 
 * Action
 * <p>
 * A unit of work submitted to a {@link ThreadPool}. Its result can trigger a chain of reactions
 * ({@link #shouldReact(Object)} / {@link #onReaction(Object, ThreadPool)}).
 *
 * @author Fred Feng
 * @date 2019-07-24
 * 
 * 
 * @since 2.0.1
 */
public interface Action<R> {

	/**
	 * Executes the action.
	 *
	 * @return the result
	 * @throws Exception
	 *            if the action fails; {@link #onFailure(Exception, ThreadPool)} is then called
	 */
	R execute() throws Exception;

	/**
	 * Decides whether a result should be fed to {@link #onReaction(Object, ThreadPool)} to produce
	 * a further result. The default reacts to every non-null result.
	 *
	 * @param result
	 *            the latest result
	 * @return true to keep reacting
	 */
	default boolean shouldReact(R result) {
		return result != null;
	}

	/**
	 * Produces the next result from the previous one. The default returns null, which ends the
	 * chain.
	 *
	 * @param result
	 *            the previous result
	 * @param threadPool
	 *            the pool running the action
	 * @return the next result
	 */
	default R onReaction(R result, ThreadPool threadPool) {
		return null;
	}

	/**
	 * Called when {@link #execute()} or a reaction throws. The default prints the stack trace.
	 *
	 * @param e
	 *            the exception
	 * @param threadPool
	 *            the pool running the action
	 */
	default void onFailure(Exception e, ThreadPool threadPool) {
		e.printStackTrace();
	}
}
