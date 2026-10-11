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
package com.github.paganini2008.devtools;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import org.junit.jupiter.api.Test;

/**
 * PredicatesTest
 *
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class PredicatesTest {

	static class Person {

		final String name;
		final Integer age;

		Person(String name, Integer age) {
			this.name = name;
			this.age = age;
		}
	}

	private static final Function<Person, Integer> AGE = p -> p.age;
	private static final Function<Person, String> NAME = p -> p.name;
	private static final Person TOM = new Person("Tom", 30);
	private static final Person NOBODY = new Person(null, null);
	private static final List<Integer> SMALL = Arrays.asList(10, 20);
	private static final List<Integer> MIXED = Arrays.asList(20, 30, 40);
	private static final List<Integer> SAME = Arrays.asList(30, 30);

	@Test
	public void testJunction() {
		assertTrue(Predicates.<Person>junction().test(TOM));
		FunctionalPredicate<Person> p = Predicates.<Person>junction().nonNull(AGE).gt(AGE, 18).lt(AGE, 60).matches(NAME, "T",
				MatchMode.START);
		assertTrue(p.test(TOM));
		assertFalse(p.test(new Person("Ann", 30)));
		assertFalse(p.test(new Person("Tim", 10)));
		assertTrue(Predicates.<Person>junction().isNull(AGE).test(NOBODY));
		assertFalse(Predicates.<Person>junction().isNull(AGE).test(TOM));
		assertFalse(Predicates.<Person>junction().nonNull(AGE).test(NOBODY));
	}

	@Test
	public void testSingleValueComparisons() {
		assertTrue(Predicates.eq(AGE, 30).test(TOM));
		assertFalse(Predicates.eq(AGE, 31).test(TOM));
		assertTrue(Predicates.ne(AGE, 31).test(TOM));
		assertFalse(Predicates.ne(AGE, 30).test(TOM));
		assertTrue(Predicates.lt(AGE, 31).test(TOM));
		assertFalse(Predicates.lt(AGE, 30).test(TOM));
		assertTrue(Predicates.lte(AGE, 30).test(TOM));
		assertFalse(Predicates.lte(AGE, 29).test(TOM));
		assertTrue(Predicates.gt(AGE, 29).test(TOM));
		assertFalse(Predicates.gt(AGE, 30).test(TOM));
		assertTrue(Predicates.gte(AGE, 30).test(TOM));
		assertFalse(Predicates.gte(AGE, 31).test(TOM));
		assertTrue(Predicates.between(AGE, 30, 40, false).test(TOM));
		assertFalse(Predicates.between(AGE, 30, 40, true).test(TOM));
		assertTrue(Predicates.between(AGE, 20, 40, true).test(TOM));
	}

	@Test
	public void testCollectionComparisons() {
		assertTrue(Predicates.eqAny(AGE, MIXED).test(TOM));
		assertFalse(Predicates.eqAny(AGE, SMALL).test(TOM));
		assertTrue(Predicates.eqAll(AGE, SAME).test(TOM));
		assertFalse(Predicates.eqAll(AGE, MIXED).test(TOM));
		assertTrue(Predicates.ltAny(AGE, MIXED).test(TOM));
		assertFalse(Predicates.ltAny(AGE, SMALL).test(TOM));
		assertTrue(Predicates.ltAll(AGE, Arrays.asList(31, 40)).test(TOM));
		assertFalse(Predicates.ltAll(AGE, MIXED).test(TOM));
		assertTrue(Predicates.lteAny(AGE, MIXED).test(TOM));
		assertFalse(Predicates.lteAny(AGE, SMALL).test(TOM));
		assertTrue(Predicates.lteAll(AGE, Arrays.asList(30, 40)).test(TOM));
		assertFalse(Predicates.lteAll(AGE, MIXED).test(TOM));
		assertTrue(Predicates.gtAny(AGE, MIXED).test(TOM));
		assertFalse(Predicates.gtAny(AGE, SAME).test(TOM));
		assertTrue(Predicates.gtAll(AGE, SMALL).test(TOM));
		assertFalse(Predicates.gtAll(AGE, MIXED).test(TOM));
		assertTrue(Predicates.gteAny(AGE, MIXED).test(TOM));
		assertFalse(Predicates.gteAny(AGE, Arrays.asList(31, 40)).test(TOM));
		assertTrue(Predicates.gteAll(AGE, Arrays.asList(20, 30)).test(TOM));
		assertFalse(Predicates.gteAll(AGE, MIXED).test(TOM));
	}

	@Test
	public void testChainedCollectionMethods() {
		FunctionalPredicate<Person> p = Predicates.<Person>junction().between(AGE, 0, 100, false).eq(AGE, 30).ne(AGE, 1).lte(AGE, 30)
				.gte(AGE, 30).eqAny(AGE, MIXED).eqAll(AGE, SAME).ltAny(AGE, MIXED).ltAll(AGE, Arrays.asList(40)).lteAny(AGE, MIXED)
				.lteAll(AGE, SAME).gtAny(AGE, MIXED).gtAll(AGE, SMALL).gteAny(AGE, MIXED).gteAll(AGE, SAME);
		assertTrue(p.test(TOM));
		assertFalse(p.test(new Person("Ann", 31)));
	}

}
