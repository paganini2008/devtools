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
package com.github.paganini2008.devtools.time;

import java.time.Instant;
import java.util.List;

/**
 * 
 * Callback of {@link TimeWindowMap} which receives the values of a time window when they are flushed.
 *
 * @author Fred Feng
 * @date 2019-07-11
 * @since 2.0.4
 */
@FunctionalInterface
public interface TimeWindowListener<V> {

	void saveCheckPoint(Instant ins, List<V> values);

}
