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
import java.util.List;
import java.util.function.Function;

import com.github.paganini2008.devtools.Comparables;
import com.github.paganini2008.devtools.beans.BeanUtils;

/**
 * 
 * Functions
 * 
 * @author Fred Feng
 * 
 * @since 2.0.1
 */
public abstract class Functions {

	public static <E, T extends Comparable<T>> Aggregation<E, T> min(String attributeName, Class<T> requiredType) {
		return min(e -> BeanUtils.getProperty(e, attributeName, requiredType));
	}

	public static <E, T extends Comparable<T>> Aggregation<E, T> min(Function<E, T> supplier) {
		return new Min<E, T>(supplier);
	}

	public static <E, T extends Comparable<T>> Aggregation<E, T> max(String attributeName, Class<T> requiredType) {
		return max(e -> BeanUtils.getProperty(e, attributeName, requiredType));
	}

	public static <E, T extends Comparable<T>> Aggregation<E, T> max(Function<E, T> supplier) {
		return new Max<E, T>(supplier);
	}

	public static <E> Aggregation<E, BigDecimal> sum(String attributeName) {
		return sum(e -> BeanUtils.getProperty(e, attributeName, BigDecimal.class));
	}

	public static <E> Aggregation<E, BigDecimal> sum(Function<E, BigDecimal> supplier) {
		return new Sum<E>(supplier);
	}

	public static <E> Aggregation<E, BigDecimal> avg(String attributeName, int scale, RoundingMode roundingMode) {
		return avg(e -> BeanUtils.getProperty(e, attributeName, BigDecimal.class), scale, roundingMode);
	}

	public static <E> Aggregation<E, BigDecimal> avg(Function<E, BigDecimal> supplier, int scale, RoundingMode roundingMode) {
		return new Avg<E>(supplier, scale, roundingMode);
	}

	public static class Min<E, T extends Comparable<T>> implements Aggregation<E, T> {

		private final Function<E, T> supplier;

		Min(Function<E, T> supplier) {
			this.supplier = supplier;
		}

		public T getResult(List<E> elements) {
			T minValue = supplier.apply(elements.get(0));
			return elements.stream().map(e -> {
				return supplier.apply(e);
			}).reduce(minValue, (left, right) -> {
				return Comparables.min(left, right);
			});
		}
	}

	public static class Max<E, T extends Comparable<T>> implements Aggregation<E, T> {

		Max(Function<E, T> supplier) {
			this.supplier = supplier;
		}

		private final Function<E, T> supplier;

		public T getResult(List<E> elements) {
			return elements.stream().map(e -> {
				return supplier.apply(e);
			}).reduce(null, (left, right) -> {
				return Comparables.max(left, right);
			});
		}
	}

	public static class Sum<E> implements Aggregation<E, BigDecimal> {

		private final Function<E, BigDecimal> supplier;

		Sum(Function<E, BigDecimal> supplier) {
			this.supplier = supplier;
		}

		public BigDecimal getResult(List<E> elements) {
			return elements.stream().map(e -> {
				return supplier.apply(e);
			}).reduce(BigDecimal.ZERO, (left, right) -> {
				return left.add(right);
			});
		}
	}

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
			return elements.stream().map(e -> {
				return supplier.apply(e);
			}).reduce(BigDecimal.ZERO, (left, right) -> {
				return left.add(right);
			}).divide(BigDecimal.valueOf(elements.size()), scale, roundingMode);
		}
	}

}
