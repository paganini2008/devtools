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
package com.github.paganini2008.devtools.beans.streaming;

import java.util.Collection;
import java.util.function.Predicate;

import com.github.paganini2008.devtools.Comparables;
import com.github.paganini2008.devtools.FunctionalPredicateImpl;
import com.github.paganini2008.devtools.MatchMode;
import com.github.paganini2008.devtools.beans.BeanUtils;
import com.github.paganini2008.devtools.beans.PropertyUtils;

/**
 * 
 * RestrictionImpl
 * 
 * @author Fred Feng
 *
 * @version 2.0.5
 */
@SuppressWarnings("unchecked")
public class RestrictionImpl<T> extends FunctionalPredicateImpl<T> implements Restriction<T> {

	public RestrictionImpl() {
		super();
	}

	protected RestrictionImpl(Predicate<T> junction) {
		super(junction);
	}

	@Override
	public <R extends Comparable<R>> Restriction<T> nonNull(String attributeName) {
		junction = junction.and(t -> PropertyUtils.getProperty(t, attributeName) != null);
		return this;
	}

	@Override
	public <R extends Comparable<R>> Restriction<T> isNull(String attributeName) {
		junction = junction.and(t -> PropertyUtils.getProperty(t, attributeName) == null);
		return this;
	}

	@Override
	public <R extends Comparable<R>> Restriction<T> between(String attributeName, R minValue, R maxValue, boolean exclusive) {
		junction = junction.and(t -> Comparables.between((R) PropertyUtils.getProperty(t, attributeName), minValue, maxValue, exclusive));
		return this;
	}

	@Override
	public <R extends Comparable<R>> Restriction<T> between(String attributeName, Class<R> requiredType, R minValue, R maxValue,
			boolean exclusive) {
		junction = junction
				.and(t -> Comparables.between(BeanUtils.getProperty(t, attributeName, requiredType), minValue, maxValue, exclusive));
		return this;
	}

	@Override
	public <R extends Comparable<R>> Restriction<T> eq(String attributeName, R value) {
		junction = junction.and(t -> Comparables.eq((R) PropertyUtils.getProperty(t, attributeName), value));
		return this;
	}

	@Override
	public <R extends Comparable<R>> Restriction<T> eq(String attributeName, Class<R> requiredType, R value) {
		junction = junction.and(t -> Comparables.eq(BeanUtils.getProperty(t, attributeName, requiredType), value));
		return this;
	}

	@Override
	public <R extends Comparable<R>> Restriction<T> ne(String attributeName, R value) {
		junction = junction.and(t -> Comparables.ne((R) PropertyUtils.getProperty(t, attributeName), value));
		return this;
	}

	@Override
	public <R extends Comparable<R>> Restriction<T> ne(String attributeName, Class<R> requiredType, R value) {
		junction = junction.and(t -> Comparables.ne(BeanUtils.getProperty(t, attributeName, requiredType), value));
		return this;
	}

	@Override
	public <R extends Comparable<R>> Restriction<T> lt(String attributeName, R value) {
		junction = junction.and(t -> Comparables.lt((R) PropertyUtils.getProperty(t, attributeName), value));
		return this;
	}

	@Override
	public <R extends Comparable<R>> Restriction<T> lt(String attributeName, Class<R> requiredType, R value) {
		junction = junction.and(t -> Comparables.lt(BeanUtils.getProperty(t, attributeName, requiredType), value));
		return this;
	}

	@Override
	public <R extends Comparable<R>> Restriction<T> lte(String attributeName, R value) {
		junction = junction.and(t -> Comparables.lte((R) PropertyUtils.getProperty(t, attributeName), value));
		return this;
	}

	@Override
	public <R extends Comparable<R>> Restriction<T> lte(String attributeName, Class<R> requiredType, R value) {
		junction = junction.and(t -> Comparables.lte(BeanUtils.getProperty(t, attributeName, requiredType), value));
		return this;
	}

	@Override
	public <R extends Comparable<R>> Restriction<T> gt(String attributeName, Class<R> requiredType, R value) {
		junction = junction.and(t -> Comparables.gt(BeanUtils.getProperty(t, attributeName, requiredType), value));
		return this;
	}

	@Override
	public <R extends Comparable<R>> Restriction<T> gt(String attributeName, R value) {
		junction = junction.and(t -> Comparables.gt((R) PropertyUtils.getProperty(t, attributeName), value));
		return this;
	}

	@Override
	public <R extends Comparable<R>> Restriction<T> gte(String attributeName, Class<R> requiredType, R value) {
		junction = junction.and(t -> Comparables.gte(BeanUtils.getProperty(t, attributeName, requiredType), value));
		return this;
	}

	@Override
	public <R extends Comparable<R>> Restriction<T> gte(String attributeName, R value) {
		junction = junction.and(t -> Comparables.gte((R) PropertyUtils.getProperty(t, attributeName), value));
		return this;
	}

	@Override
	public <R extends Comparable<R>> Restriction<T> eqAny(String attributeName, Class<R> requiredType, Collection<R> values) {
		junction = junction.and(t -> Comparables.eqAny(BeanUtils.getProperty(t, attributeName, requiredType), values));
		return this;
	}

	@Override
	public <R extends Comparable<R>> Restriction<T> eqAny(String attributeName, Collection<R> values) {
		junction = junction.and(t -> Comparables.eqAny((R) PropertyUtils.getProperty(t, attributeName), values));
		return this;
	}

	@Override
	public <R extends Comparable<R>> Restriction<T> eqAll(String attributeName, Class<R> requiredType, Collection<R> values) {
		junction = junction.and(t -> Comparables.eqAll(BeanUtils.getProperty(t, attributeName, requiredType), values));
		return this;
	}

	@Override
	public <R extends Comparable<R>> Restriction<T> eqAll(String attributeName, Collection<R> values) {
		junction = junction.and(t -> Comparables.eqAll((R) PropertyUtils.getProperty(t, attributeName), values));
		return this;
	}

	@Override
	public <R extends Comparable<R>> Restriction<T> ltAny(String attributeName, Class<R> requiredType, Collection<R> values) {
		junction = junction.and(t -> Comparables.ltAny(BeanUtils.getProperty(t, attributeName, requiredType), values));
		return this;
	}

	@Override
	public <R extends Comparable<R>> Restriction<T> ltAny(String attributeName, Collection<R> values) {
		junction = junction.and(t -> Comparables.ltAny((R) PropertyUtils.getProperty(t, attributeName), values));
		return this;
	}

	@Override
	public <R extends Comparable<R>> Restriction<T> ltAll(String attributeName, Class<R> requiredType, Collection<R> values) {
		junction = junction.and(t -> Comparables.ltAll(BeanUtils.getProperty(t, attributeName, requiredType), values));
		return this;
	}

	@Override
	public <R extends Comparable<R>> Restriction<T> ltAll(String attributeName, Collection<R> values) {
		junction = junction.and(t -> Comparables.ltAll((R) PropertyUtils.getProperty(t, attributeName), values));
		return this;
	}

	@Override
	public <R extends Comparable<R>> Restriction<T> lteAny(String attributeName, Class<R> requiredType, Collection<R> values) {
		junction = junction.and(t -> Comparables.lteAny(BeanUtils.getProperty(t, attributeName, requiredType), values));
		return this;
	}

	@Override
	public <R extends Comparable<R>> Restriction<T> lteAny(String attributeName, Collection<R> values) {
		junction = junction.and(t -> Comparables.lteAny((R) PropertyUtils.getProperty(t, attributeName), values));
		return this;
	}

	@Override
	public <R extends Comparable<R>> Restriction<T> lteAll(String attributeName, Class<R> requiredType, Collection<R> values) {
		junction = junction.and(t -> Comparables.lteAll(BeanUtils.getProperty(t, attributeName, requiredType), values));
		return this;
	}

	@Override
	public <R extends Comparable<R>> Restriction<T> lteAll(String attributeName, Collection<R> values) {
		junction = junction.and(t -> Comparables.lteAll((R) PropertyUtils.getProperty(t, attributeName), values));
		return this;
	}

	@Override
	public <R extends Comparable<R>> Restriction<T> gtAny(String attributeName, Class<R> requiredType, Collection<R> values) {
		junction = junction.and(t -> Comparables.gtAny(BeanUtils.getProperty(t, attributeName, requiredType), values));
		return this;
	}

	@Override
	public <R extends Comparable<R>> Restriction<T> gtAny(String attributeName, Collection<R> values) {
		junction = junction.and(t -> Comparables.gtAny((R) PropertyUtils.getProperty(t, attributeName), values));
		return this;
	}

	@Override
	public <R extends Comparable<R>> Restriction<T> gtAll(String attributeName, Class<R> requiredType, Collection<R> values) {
		junction = junction.and(t -> Comparables.gtAll(BeanUtils.getProperty(t, attributeName, requiredType), values));
		return this;
	}

	@Override
	public <R extends Comparable<R>> Restriction<T> gtAll(String attributeName, Collection<R> values) {
		junction = junction.and(t -> Comparables.gtAll((R) PropertyUtils.getProperty(t, attributeName), values));
		return this;
	}

	@Override
	public <R extends Comparable<R>> Restriction<T> gteAny(String attributeName, Class<R> requiredType, Collection<R> values) {
		junction = junction.and(t -> Comparables.gteAny(BeanUtils.getProperty(t, attributeName, requiredType), values));
		return this;
	}

	@Override
	public <R extends Comparable<R>> Restriction<T> gteAny(String attributeName, Collection<R> values) {
		junction = junction.and(t -> Comparables.gteAny((R) PropertyUtils.getProperty(t, attributeName), values));
		return this;
	}

	@Override
	public <R extends Comparable<R>> Restriction<T> gteAll(String attributeName, Class<R> requiredType, Collection<R> values) {
		junction = junction.and(t -> Comparables.gteAll(BeanUtils.getProperty(t, attributeName, requiredType), values));
		return this;
	}

	@Override
	public <R extends Comparable<R>> Restriction<T> gteAll(String attributeName, Collection<R> values) {
		junction = junction.and(t -> Comparables.gteAll((R) PropertyUtils.getProperty(t, attributeName), values));
		return this;
	}

	@Override
	public Restriction<T> matches(String attributeName, String substr, MatchMode matchMode) {
		junction = junction.and(t -> matchMode.matches(BeanUtils.getProperty(t, attributeName, String.class), substr));
		return this;
	}

}
