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
package com.github.paganini2008.devtools.beans.streaming;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import com.github.paganini2008.devtools.Comparables;
import com.github.paganini2008.devtools.beans.BeanUtils;

/**
 * 
 * Functions
 * 
 * @author Fred Feng
 * 
 * @version 1.0
 */
public abstract class Functions {

	public static <E, T extends Comparable<T>> Calculation<E, T> min(String attributeName, Class<T> requiredType) {
		return new Min<E, T>(attributeName, requiredType);
	}

	public static <E, T extends Comparable<T>> Calculation<E, T> max(String attributeName, Class<T> requiredType) {
		return new Max<E, T>(attributeName, requiredType);
	}

	public static <E> Calculation<E, BigDecimal> sum(String attributeName) {
		return new Sum<E>(attributeName);
	}

	public static <E> Calculation<E, BigDecimal> avg(String attributeName, int scale, RoundingMode roundingMode) {
		return new Avg<E>(attributeName, scale, roundingMode);
	}

	public static class Min<E, T extends Comparable<T>> implements Calculation<E, T> {

		private final String attributeName;
		private final Class<T> requiredType;

		Min(String attributeName, Class<T> requiredType) {
			this.attributeName = attributeName;
			this.requiredType = requiredType;
		}

		public T getResult(List<E> elements) {
			T identity = BeanUtils.getProperty(elements.get(0), attributeName, requiredType);
			return elements.stream().map(e -> {
				return BeanUtils.getProperty(e, attributeName, requiredType);
			}).reduce(identity, (left, right) -> {
				return Comparables.min(left, right);
			});
		}
	}

	public static class Max<E, T extends Comparable<T>> implements Calculation<E, T> {

		private final String attributeName;
		private final Class<T> requiredType;

		Max(String attributeName, Class<T> requiredType) {
			this.attributeName = attributeName;
			this.requiredType = requiredType;
		}

		public T getResult(List<E> elements) {
			return elements.stream().map(e -> {
				return BeanUtils.getProperty(e, attributeName, requiredType);
			}).reduce(null, (left, right) -> {
				return Comparables.max(left, right);
			});
		}
	}

	public static class Sum<E> implements Calculation<E, BigDecimal> {

		private final String attributeName;

		Sum(String attributeName) {
			this.attributeName = attributeName;
		}

		public BigDecimal getResult(List<E> elements) {
			return elements.stream().map(e -> {
				return BeanUtils.getProperty(e, attributeName, BigDecimal.class);
			}).reduce(BigDecimal.ZERO, (left, right) -> {
				return left.add(right);
			});
		}
	}

	public static class Avg<E> implements Calculation<E, BigDecimal> {

		private final String attributeName;
		private final int scale;
		private final RoundingMode roundingMode;

		Avg(String attributeName, int scale, RoundingMode roundingMode) {
			this.attributeName = attributeName;
			this.scale = scale;
			this.roundingMode = roundingMode;
		}

		public BigDecimal getResult(List<E> elements) {
			return elements.stream().map(e -> {
				return BeanUtils.getProperty(e, attributeName, BigDecimal.class);
			}).reduce(BigDecimal.ZERO, (left, right) -> {
				return left.add(right);
			}).divide(BigDecimal.valueOf(elements.size()), scale, roundingMode);
		}
	}

}
