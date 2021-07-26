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
package com.github.paganini2008.devtools.beans.streaming;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 
 * Summary
 * 
 * @author Fred Feng
 *
 * @since 2.0.1
 */
public interface Summary<E> {

	<T> T summarize(Calculation<E, T> calculation);

	default <T extends Comparable<T>> T min(String attributeName, Class<T> requiredType) {
		return summarize(Functions.min(attributeName, requiredType));
	}

	default <T extends Comparable<T>> T max(String attributeName, Class<T> requiredType) {
		return summarize(Functions.max(attributeName, requiredType));
	}

	default BigDecimal sum(String attributeName) {
		return summarize(Functions.sum(attributeName));
	}

	default BigDecimal avg(String attributeName) {
		return avg(attributeName, 2, RoundingMode.HALF_UP);
	}

	default BigDecimal avg(String attributeName, int scale, RoundingMode roundingMode) {
		return summarize(Functions.avg(attributeName, scale, roundingMode));
	}

}
