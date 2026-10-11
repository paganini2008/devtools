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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import com.github.paganini2008.devtools.MatchMode;

/**
 * RestrictionsSpecTest
 * <p>
 * Specification of the 'where' clause, written against the sample data of
 * {@link Item#samples()}: every comparison operator in both the raw and the
 * 'requiredType' flavor, null handling, type conversion and nested property
 * paths with null intermediate beans.
 * 
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class RestrictionsSpecTest {

	private static List<Integer> where(Predicate<Item> predicate) {
		return Selector.from(Item.samples()).filter(predicate).list().stream().map(Item::getId).collect(Collectors.toList());
	}

	private static List<Integer> ids(Integer... ids) {
		return Arrays.asList(ids);
	}

	// ---------------------------------------------------------------- comparison operators (raw value)

	@Test
	public void eq_matchesEqualValuesOnly() {
		// where qty = 10
		assertEquals(ids(1), where(Restrictions.eq("qty", 10)));
		assertEquals(ids(), where(Restrictions.eq("qty", 11)));
	}

	@Test
	public void eq_withNullValueMatchesNullProperties() {
		// unlike sql ('= null' is unknown) a null value matches a null property, like 'is null'
		assertEquals(ids(3), where(Restrictions.eq("qty", (Integer) null)));
	}

	@Test
	public void ne_matchesNullPropertiesToo() {
		// where qty <> 10: a null property is regarded as different from any non-null value
		assertEquals(ids(2, 3, 4, 5), where(Restrictions.ne("qty", 10)));
		assertEquals(ids(1, 2, 4, 5), where(Restrictions.ne("qty", (Integer) null)));
	}

	@Test
	public void lt_andLte_treatNullPropertyAsSmallest() {
		// a null property is regarded as smaller than any value, so 'lt'/'lte' match it
		assertEquals(ids(3, 4), where(Restrictions.lt("qty", 10)));
		assertEquals(ids(1, 3, 4), where(Restrictions.lte("qty", 10)));
		assertEquals(ids(3), where(Restrictions.lt("qty", 5)));
		assertEquals(ids(3, 4), where(Restrictions.lte("qty", 5)));
	}

	@Test
	public void gt_andGte_neverMatchNullProperty() {
		assertEquals(ids(2, 5), where(Restrictions.gt("qty", 10)));
		assertEquals(ids(1, 2, 5), where(Restrictions.gte("qty", 10)));
		assertEquals(ids(), where(Restrictions.gt("qty", 30)));
		assertEquals(ids(5), where(Restrictions.gte("qty", 30)));
		// gte(null) matches everything, gt(null) every non-null property
		assertEquals(ids(1, 2, 3, 4, 5), where(Restrictions.gte("qty", (Integer) null)));
		assertEquals(ids(1, 2, 4, 5), where(Restrictions.gt("qty", (Integer) null)));
	}

	@Test
	public void between_isInclusiveByDefaultAndExclusiveOnRequest() {
		// where qty between 10 and 30
		assertEquals(ids(1, 2, 5), where(Restrictions.between("qty", 10, 30, false)));
		// where qty > 10 and qty < 30
		assertEquals(ids(2), where(Restrictions.between("qty", 10, 30, true)));
		// a single point range
		assertEquals(ids(2), where(Restrictions.between("qty", 20, 20, false)));
		assertEquals(ids(), where(Restrictions.between("qty", 20, 20, true)));
		// reversed bounds match nothing
		assertEquals(ids(), where(Restrictions.between("qty", 30, 10, false)));
		// a null lower bound includes the null property (null is the smallest)
		assertEquals(ids(3, 4), where(Restrictions.between("qty", null, 5, false)));
	}

	@Test
	public void bigDecimalComparisonIgnoresScale() {
		// 1.10 = 1.1 = 1.100 like sql numeric comparison
		assertEquals(ids(1), where(Restrictions.eq("amount", new BigDecimal("1.1"))));
		assertEquals(ids(1), where(Restrictions.eq("amount", new BigDecimal("1.100"))));
		assertEquals(ids(5), where(Restrictions.gt("amount", new BigDecimal("3.33"))));
		assertEquals(ids(), where(Restrictions.gt("amount", new BigDecimal("3.335"))));
	}

	// ---------------------------------------------------------------- isNull / nonNull

	@Test
	public void isNull_andNonNull_partitionTheRows() {
		assertEquals(ids(3), where(Restrictions.isNull("qty")));
		assertEquals(ids(1, 2, 4, 5), where(Restrictions.nonNull("qty")));
		assertEquals(ids(4), where(Restrictions.isNull("owner")));
		assertEquals(ids(1, 2, 3, 5), where(Restrictions.nonNull("owner")));
	}

	// ---------------------------------------------------------------- requiredType overloads and conversion

	@Test
	public void requiredType_convertsIntegerPropertyToLongValue() {
		assertEquals(ids(1), where(Restrictions.eq("qty", Long.class, 10L)));
		assertEquals(ids(2, 3, 4, 5), where(Restrictions.ne("qty", Long.class, 10L)));
		assertEquals(ids(3, 4), where(Restrictions.lt("qty", Long.class, 10L)));
		assertEquals(ids(1, 3, 4), where(Restrictions.lte("qty", Long.class, 10L)));
		assertEquals(ids(2, 5), where(Restrictions.gt("qty", Long.class, 10L)));
		assertEquals(ids(1, 2, 5), where(Restrictions.gte("qty", Long.class, 10L)));
		assertEquals(ids(2), where(Restrictions.<Item>junction().between("qty", Long.class, 10L, 30L, true)));
		assertEquals(ids(1, 2, 5), where(Restrictions.<Item>junction().between("qty", Long.class, 10L, 30L, false)));
	}

	@Test
	public void requiredType_convertsLongPropertyToInteger() {
		assertEquals(ids(2), where(Restrictions.eq("big", Integer.class, 20)));
		assertEquals(ids(4), where(Restrictions.isNull("big")));
		assertEquals(ids(3, 5), where(Restrictions.gt("big", Integer.class, 20)));
	}

	@Test
	public void requiredType_convertsNumericStringsAndTreatsUnconvertibleAsNull() {
		// code: "10", "9", "abc", null, "30" compared as integers
		assertEquals(ids(1, 5), where(Restrictions.gt("code", Integer.class, 9)));
		assertEquals(ids(2), where(Restrictions.eq("code", Integer.class, 9)));
		// "abc" cannot be converted and is regarded as null (the smallest) like the real null
		assertEquals(ids(1, 2, 3, 4), where(Restrictions.lt("code", Integer.class, 15)));
		// the raw flavor compares the strings lexicographically: "9" > "15"
		assertEquals(ids(1, 4), where(Restrictions.lt("code", "15")));
	}

	@Test
	public void requiredType_convertsDoubleToBigDecimal() {
		assertEquals(ids(2, 5), where(Restrictions.gte("ratio", BigDecimal.class, new BigDecimal("2.5"))));
		assertEquals(ids(2), where(Restrictions.eq("ratio", BigDecimal.class, new BigDecimal("2.50"))));
	}

	@Test
	public void requiredType_convertsNumberToStringForMatching() {
		// matches always reads the property as a String
		assertEquals(ids(1, 4), where(Restrictions.<Item>matches("qty", "*0", MatchMode.WILDCARD).and(Restrictions.lt("qty", 20))
				.or(Restrictions.eq("qty", 5))));
		assertEquals(ids(2), where(Restrictions.matches("qty", "2", MatchMode.START)));
	}

	@Test
	public void rawValueOfAnotherNumericTypeIsNotConverted() {
		// the raw flavor does not convert: Integer#compareTo(Long) fails, use the 'requiredType' flavor instead
		assertThrows(ClassCastException.class, () -> where(Restrictions.eq("qty", 10L)));
	}

	// ---------------------------------------------------------------- any / all with requiredType

	@Test
	public void anyAll_withRequiredTypeConvertTheProperty() {
		List<Long> values = Arrays.asList(10L, 30L);
		assertEquals(ids(1, 5), where(Restrictions.<Item>junction().eqAny("qty", Long.class, values)));
		assertEquals(ids(), where(Restrictions.<Item>junction().eqAll("qty", Long.class, values)));
		assertEquals(ids(1), where(Restrictions.<Item>junction().eqAll("qty", Long.class, Arrays.asList(10L, 10L))));
		assertEquals(ids(1, 2, 3, 4), where(Restrictions.<Item>junction().ltAny("qty", Long.class, values)));
		assertEquals(ids(3, 4), where(Restrictions.<Item>junction().ltAll("qty", Long.class, values)));
		assertEquals(ids(1, 2, 3, 4, 5), where(Restrictions.<Item>junction().lteAny("qty", Long.class, values)));
		assertEquals(ids(1, 3, 4), where(Restrictions.<Item>junction().lteAll("qty", Long.class, values)));
		assertEquals(ids(2, 5), where(Restrictions.<Item>junction().gtAny("qty", Long.class, values)));
		assertEquals(ids(), where(Restrictions.<Item>junction().gtAll("qty", Long.class, values)));
		assertEquals(ids(1, 2, 5), where(Restrictions.<Item>junction().gteAny("qty", Long.class, values)));
		assertEquals(ids(5), where(Restrictions.<Item>junction().gteAll("qty", Long.class, values)));
	}

	@Test
	public void anyAll_neverMatchAnEmptyCollection() {
		List<Integer> empty = Collections.emptyList();
		assertEquals(ids(), where(Restrictions.<Item>junction().eqAny("qty", empty)));
		assertEquals(ids(), where(Restrictions.<Item>junction().eqAll("qty", empty)));
		assertEquals(ids(), where(Restrictions.<Item>junction().ltAll("qty", empty)));
		assertEquals(ids(), where(Restrictions.<Item>junction().gteAll("qty", Integer.class, empty)));
		assertEquals(ids(), where(Restrictions.<Item>junction().gtAny("qty", (List<Integer>) null)));
	}

	@Test
	public void anyAll_rawFlavor() {
		List<Integer> values = Arrays.asList(10, 30);
		assertEquals(ids(1, 5), where(Restrictions.<Item>junction().eqAny("qty", values)));
		assertEquals(ids(3, 4), where(Restrictions.<Item>junction().ltAll("qty", values)));
		assertEquals(ids(5), where(Restrictions.<Item>junction().gteAll("qty", values)));
		assertEquals(ids(2, 5), where(Restrictions.<Item>junction().gtAny("qty", values)));
	}

	// ---------------------------------------------------------------- matches

	@Test
	public void matches_eachMatchMode() {
		// owner.name: Ann, Bob, Cat, (null), Dan
		assertEquals(ids(1), where(Restrictions.matches("owner.name", "An", MatchMode.START)));
		assertEquals(ids(), where(Restrictions.matches("owner.name", "nn", MatchMode.START)));
		assertEquals(ids(1, 5), where(Restrictions.matches("owner.name", "n", MatchMode.END)));
		assertEquals(ids(3, 5), where(Restrictions.matches("owner.name", "a", MatchMode.ANY_WHERE)));
		// regex must match the whole value
		assertEquals(ids(1, 2), where(Restrictions.matches("owner.name", "(Ann|Bob)", MatchMode.REGEX)));
		assertEquals(ids(), where(Restrictions.matches("owner.name", "nn", MatchMode.REGEX)));
		// wildcard must match the whole value
		assertEquals(ids(1, 5), where(Restrictions.matches("owner.name", "*n", MatchMode.WILDCARD)));
		assertEquals(ids(), where(Restrictions.matches("owner.name", "A", MatchMode.WILDCARD)));
	}

	@Test
	public void matches_neverMatchesNullOrBlankProperty() {
		List<Item> items = Item.samples();
		items.get(0).setCode(" ");
		List<Integer> result = Selector.from(items).filter(Restrictions.<Item>matches("code", "", MatchMode.ANY_WHERE)).list().stream()
				.map(Item::getId).collect(Collectors.toList());
		// "" is contained in every string, but " " (blank) and null never match
		assertEquals(ids(2, 3, 5), result);
	}

	@Test
	public void matches_isCaseSensitive() {
		assertEquals(ids(), where(Restrictions.matches("owner.name", "ann", MatchMode.START)));
	}

	// ---------------------------------------------------------------- nested properties

	@Test
	public void nestedProperty_threeLevels() {
		assertEquals(ids(1, 5), where(Restrictions.eq("owner.address.city", "Paris")));
		assertEquals(ids(1), where(Restrictions.eq("owner.address.zip", Long.class, 75000L)));
		assertEquals(ids(5), where(Restrictions.gt("owner.address.zip", 75000)));
	}

	@Test
	public void nestedProperty_nullIntermediateBeanIsReadAsNull() {
		// item 3 has no address, item 4 has no owner: like a sql outer join the value is null
		assertEquals(ids(1, 5), where(Restrictions.eq("owner.address.city", "Paris")));
		assertEquals(ids(2, 3, 4), where(Restrictions.ne("owner.address.city", "Paris")));
		assertEquals(ids(3, 4), where(Restrictions.isNull("owner.address.city")));
		assertEquals(ids(1, 2, 5), where(Restrictions.nonNull("owner.address.city")));
		assertEquals(ids(2, 3, 4), where(Restrictions.isNull("owner.address.zip")));
		assertEquals(ids(4), where(Restrictions.isNull("owner.name")));
		assertEquals(ids(2, 3, 4), where(Restrictions.lt("owner.address.zip", Long.class, 75000L)));
		assertEquals(ids(1, 5), where(Restrictions.matches("owner.address.city", "P", MatchMode.START)));
		assertEquals(ids(1, 5), where(Restrictions.<Item>junction().eqAny("owner.address.city", Arrays.asList("Paris", "Rome"))));
	}

	@Test
	public void unknownPropertyIsRejected() {
		IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> where(Restrictions.eq("nothing", 1)));
		assertTrue(e.getMessage().contains("nothing"));
		e = assertThrows(IllegalArgumentException.class, () -> where(Restrictions.eq("owner.nothing", 1)));
		assertTrue(e.getMessage().contains("nothing"));
	}

	@Test
	public void nullElementIsRejected() {
		List<Item> items = new ArrayList<Item>(Item.samples());
		items.add(null);
		assertThrows(IllegalArgumentException.class, () -> Selector.from(items).filter(Restrictions.eq("qty", 10)).list());
	}

	// ---------------------------------------------------------------- junction / and / or / not

	@Test
	public void junction_withoutConditionsMatchesEverything() {
		assertEquals(ids(1, 2, 3, 4, 5), where(Restrictions.junction()));
	}

	@Test
	public void junction_chainedConditionsAreCombinedWithAnd() {
		// where qty >= 10 and owner.address.city = 'Paris' and code is not null
		Restriction<Item> r = Restrictions.<Item>junction().gte("qty", 10).eq("owner.address.city", "Paris").nonNull("code");
		assertEquals(ids(1, 5), where(r));
	}

	@Test
	public void junction_chainingMutatesTheSameInstance() {
		Restriction<Item> r = Restrictions.eq("owner.address.city", "Paris");
		Restriction<Item> same = r.gt("qty", 10);
		assertSame(r, same);
		assertEquals(ids(5), where(r));
	}

	@Test
	public void andOrNot_followBooleanLogic() {
		Predicate<Item> paris = Restrictions.eq("owner.address.city", "Paris");
		Predicate<Item> small = Restrictions.lt("qty", 20);
		// where city = 'Paris' or qty < 20
		assertEquals(ids(1, 3, 4, 5), where(paris.or(small)));
		// where city = 'Paris' and qty < 20
		assertEquals(ids(1), where(paris.and(small)));
		// where not (city = 'Paris')
		assertEquals(ids(2, 3, 4), where(paris.negate()));
		// where (city = 'Paris' or qty < 20) and not (qty is null)
		assertEquals(ids(1, 4, 5), where(paris.or(small).and(Restrictions.<Item>isNull("qty").negate())));
		// de morgan: not (a or b) == not a and not b
		assertEquals(where(paris.or(small).negate()), where(paris.negate().and(small.negate())));
	}

	@Test
	public void staticFactories_returnIndependentRestrictions() {
		Predicate<Item> a = Restrictions.gt("qty", 10);
		Predicate<Item> b = Restrictions.gt("qty", 10);
		assertFalse(a == b);
		assertEquals(where(a), where(b));
		assertEquals(ids(2, 5), where(Restrictions.gte("qty", Integer.class, 20)));
		assertEquals(ids(1, 3, 4), where(Restrictions.lte("qty", Integer.class, 10)));
		assertEquals(ids(3, 4), where(Restrictions.lt("qty", Integer.class, 10)));
		assertEquals(ids(2), where(Restrictions.between("qty", 15, 25, true)));
	}

	@Test
	public void functionalConditions_canBeMixedWithPropertyConditions() {
		Restriction<Item> r = Restrictions.<Item>junction().eq("owner.address.city", "Paris");
		r.gt(Item::getQty, 10);
		assertEquals(ids(5), where(r));
	}

}
