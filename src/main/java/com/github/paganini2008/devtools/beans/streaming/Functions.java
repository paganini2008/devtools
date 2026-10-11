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
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.github.paganini2008.devtools.Comparables;
import com.github.paganini2008.devtools.converter.ConvertUtils;

/**
 * 
 * Functions
 * <p>
 * Factory of the common {@link Aggregation aggregate functions}: min, max,
 * sum, avg and count. Besides property names, the functions accept any
 * {@link Function}, so getter method references work directly:
 * 
 * <pre>
 * Functions.max(Product::getPrice)                            // max(price)
 * Functions.sum(Product::getFreight)                          // sum(freight), a BigDecimal getter
 * Functions.avg(Product::getFreight, 2, RoundingMode.HALF_UP) // avg(freight)
 * Having.gt(Functions.max(Product::getPrice), 300f)           // having max(price) &gt; 300
 * </pre>
 * 
 * @author Fred Feng
 * @date 2019-07-11
 * 
 * @since 2.0.1
 */
public abstract class Functions {

	/**
	 * Like sql 'min(attributeName)', the property value is converted to the required type.
	 */
	public static <E, T extends Comparable<T>> Aggregation<E, T> min(String attributeName, Class<T> requiredType) {
		return min(e -> Property.read(e, attributeName, requiredType));
	}

	/**
	 * Like sql 'min'. A null value is regarded as the minimum, returns null for empty elements.
	 */
	public static <E, T extends Comparable<T>> Aggregation<E, T> min(Function<E, T> supplier) {
		return new Min<E, T>(supplier);
	}

	/**
	 * Like sql 'max(attributeName)', the property value is converted to the required type.
	 */
	public static <E, T extends Comparable<T>> Aggregation<E, T> max(String attributeName, Class<T> requiredType) {
		return max(e -> Property.read(e, attributeName, requiredType));
	}

	/**
	 * Like sql 'max'. Null values are ignored, returns null if there is no non-null value.
	 */
	public static <E, T extends Comparable<T>> Aggregation<E, T> max(Function<E, T> supplier) {
		return new Max<E, T>(supplier);
	}

	/**
	 * Like sql 'count(*)', the number of elements
	 */
	public static <E> Aggregation<E, Integer> count() {
		return elements -> elements.size();
	}

	/**
	 * Like sql 'sum(attributeName)', the property value is converted to BigDecimal.
	 */
	public static <E> Aggregation<E, BigDecimal> sum(String attributeName) {
		return sum(e -> Property.read(e, attributeName, BigDecimal.class));
	}

	/**
	 * Like sql 'sum'. Null values are ignored, returns zero if there is no non-null value.
	 */
	public static <E> Aggregation<E, BigDecimal> sum(Function<E, BigDecimal> supplier) {
		return new Sum<E>(supplier);
	}

	/**
	 * Like sql 'avg(attributeName)', the property value is converted to BigDecimal.
	 */
	public static <E> Aggregation<E, BigDecimal> avg(String attributeName, int scale, RoundingMode roundingMode) {
		return avg(e -> Property.read(e, attributeName, BigDecimal.class), scale, roundingMode);
	}

	/**
	 * Like sql 'avg'. Null values are neither summed nor counted, returns null if there is no non-
	 * null value.
	 */
	public static <E> Aggregation<E, BigDecimal> avg(Function<E, BigDecimal> supplier, int scale, RoundingMode roundingMode) {
		return new Avg<E>(supplier, scale, roundingMode);
	}

	/**
	 * Converts a value (usually a number) to BigDecimal for 'sum' and 'avg',
	 * returning null if it is null or cannot be converted.
	 */
	static BigDecimal toBigDecimal(Object value) {
		if (value == null || value instanceof BigDecimal) {
			return (BigDecimal) value;
		}
		return ConvertUtils.convertValue(value, BigDecimal.class);
	}

	/**
	 * 
	 * Min</br>
	 * Returns the minimum value. Note: a null value is regarded as the minimum (so
	 * the result is null if any value is null). Returns null for empty elements.
	 * 
	 * @author Fred Feng
	 * @since 2.0.1
	 */
	public static class Min<E, T extends Comparable<T>> implements Aggregation<E, T> {

		private final Function<E, T> supplier;

		Min(Function<E, T> supplier) {
			this.supplier = supplier;
		}

		public T getResult(List<E> elements) {
			// Keep the original semantics: a null value is regarded as the minimum
			T minValue = null;
			boolean first = true;
			for (E element : elements) {
				T value = supplier.apply(element);
				minValue = first ? value : Comparables.min(minValue, value);
				first = false;
			}
			return minValue;
		}
	}

	/**
	 * 
	 * Max</br>
	 * Like sql function 'max', null values are ignored. Returns null if there is
	 * no non-null value.
	 * 
	 * @author Fred Feng
	 * @since 2.0.1
	 */
	public static class Max<E, T extends Comparable<T>> implements Aggregation<E, T> {

		Max(Function<E, T> supplier) {
			this.supplier = supplier;
		}

		private final Function<E, T> supplier;

		public T getResult(List<E> elements) {
			return elements.stream().map(supplier).filter(Objects::nonNull).reduce(Comparables::max).orElse(null);
		}
	}

	/**
	 * 
	 * Sum</br>
	 * Null values are ignored, returns zero if there is no non-null value.
	 * 
	 * @author Fred Feng
	 * @since 2.0.1
	 */
	public static class Sum<E> implements Aggregation<E, BigDecimal> {

		private final Function<E, BigDecimal> supplier;

		Sum(Function<E, BigDecimal> supplier) {
			this.supplier = supplier;
		}

		public BigDecimal getResult(List<E> elements) {
			return elements.stream().map(supplier).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);
		}
	}

	/**
	 * 
	 * Avg</br>
	 * Like sql function 'avg', null values are ignored (neither summed nor
	 * counted). Returns null if there is no non-null value.
	 * 
	 * @author Fred Feng
	 * @since 2.0.1
	 */
	public static class Avg<E> implements Aggregation<E, BigDecimal> {

		private final Function<E, BigDecimal> supplier;
		private final int scale;
		private final RoundingMode roundingMode;

		Avg(Function<E, BigDecimal> supplier, int scale, RoundingMode roundingMode) {
			this.supplier = supplier;
			this.scale = scale;
			this.roundingMode = roundingMode;
		}

		public BigDecimal getResult(List<E> elements) {
			List<BigDecimal> values = elements.stream().map(supplier).filter(Objects::nonNull).collect(Collectors.toList());
			if (values.isEmpty()) {
				return null;
			}
			return values.stream().reduce(BigDecimal.ZERO, BigDecimal::add).divide(BigDecimal.valueOf(values.size()), scale, roundingMode);
		}
	}

}
