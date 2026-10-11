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
package com.github.paganini2008.devtools.beans.streaming;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.github.paganini2008.devtools.beans.SerializableFunction;

/**
 * 
 * Summary
 * <p>
 * Aggregate operations (min, max, sum, avg) on a set of elements.
 * 
 * @author Fred Feng
 * @date 2019-07-11
 *
 * @since 2.0.1
 */
public interface Summary<E> {

	/**
	 * Applies the aggregation to the elements.
	 */
	<T> T summarize(Aggregation<E, T> aggregation);

	/**
	 * See {@link Functions#min(String, Class)}.
	 */
	default <T extends Comparable<T>> T min(String attributeName, Class<T> requiredType) {
		return summarize(Functions.min(attributeName, requiredType));
	}

	/**
	 * See {@link Functions#max(String, Class)}.
	 */
	default <T extends Comparable<T>> T max(String attributeName, Class<T> requiredType) {
		return summarize(Functions.max(attributeName, requiredType));
	}

	/**
	 * See {@link Functions#sum(String)}.
	 */
	default BigDecimal sum(String attributeName) {
		return summarize(Functions.sum(attributeName));
	}

	/**
	 * Average with scale 2 and {@link RoundingMode#HALF_UP}. See {@link Functions#avg(String, int,
	 * RoundingMode)}.
	 */
	default BigDecimal avg(String attributeName) {
		return avg(attributeName, 2, RoundingMode.HALF_UP);
	}

	/**
	 * See {@link Functions#avg(String, int, RoundingMode)}.
	 */
	default BigDecimal avg(String attributeName, int scale, RoundingMode roundingMode) {
		return summarize(Functions.avg(attributeName, scale, roundingMode));
	}

	/**
	 * Like {@link #min(String, Class)}, reading the values by a getter method reference (any function works).
	 * 
	 * <pre>
	 * group.min(Product::getPrice) // like group.min("price", Float.class)
	 * </pre>
	 * 
	 * @since 3.0.0
	 */
	default <T extends Comparable<T>> T min(SerializableFunction<E, T> getter) {
		return summarize(Functions.min(getter));
	}

	/**
	 * Like {@link #max(String, Class)}, reading the values by a getter.
	 * 
	 * @since 3.0.0
	 */
	default <T extends Comparable<T>> T max(SerializableFunction<E, T> getter) {
		return summarize(Functions.max(getter));
	}

	/**
	 * Like {@link #sum(String)}, reading the values by a getter; the values are converted to BigDecimal.
	 * 
	 * <pre>
	 * group.sum(Product::getSales) // like group.sum("sales")
	 * </pre>
	 * 
	 * @since 3.0.0
	 */
	default BigDecimal sum(SerializableFunction<E, ?> getter) {
		return summarize(Functions.sum(e -> Functions.toBigDecimal(getter.apply(e))));
	}

	/**
	 * Like {@link #avg(String)} (scale 2, {@link RoundingMode#HALF_UP}), reading the values by a getter.
	 * 
	 * @since 3.0.0
	 */
	default BigDecimal avg(SerializableFunction<E, ?> getter) {
		return avg(getter, 2, RoundingMode.HALF_UP);
	}

	/**
	 * Like {@link #avg(String, int, RoundingMode)}, reading the values by a getter.
	 * 
	 * @since 3.0.0
	 */
	default BigDecimal avg(SerializableFunction<E, ?> getter, int scale, RoundingMode roundingMode) {
		return summarize(Functions.avg(e -> Functions.toBigDecimal(getter.apply(e)), scale, roundingMode));
	}

}
