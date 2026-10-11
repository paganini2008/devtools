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
package com.github.paganini2008.devtools.beans;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.github.paganini2008.devtools.MatchMode;
import com.github.paganini2008.devtools.beans.TestBeans.Address;
import com.github.paganini2008.devtools.beans.TestBeans.Person;
import com.github.paganini2008.devtools.beans.TestBeans.PersonDto;

/**
 * PropertyFiltersTest
 * 
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class PropertyFiltersTest {

	private static List<String> names(Class<?> type, PropertyFilter filter) {
		Map<String, PropertyDescriptor> map = PropertyUtils.getPropertyDescriptors(type, null, filter);
		return new ArrayList<String>(map.keySet());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void propertyFilters_selectByAnnotationTypeAndName() {
		assertEquals("[city, email]", names(PersonDto.class,
				PropertyFilters.isAnnotationPresent(new Class[] { PropertyMapper.class, ExcludedProperty.class })).toString());
		assertEquals("[city]", names(PersonDto.class, PropertyFilters.isAnnotationPresent(PropertyMapper.class)).toString());
		// "id" has no backing field
		assertFalse(names(PersonDto.class, PropertyFilters.isAnnotationPresent(new Class[] { PropertyMapper.class })).contains("id"));
		assertEquals("[address]", names(Person.class, PropertyFilters.isAssignable(Address.class)).toString());
		assertEquals("[address, age, level]",
				names(Person.class, PropertyFilters.isAssignable(new Class<?>[] { Address.class, int.class })).toString());
		assertEquals("[age, name]", names(Person.class, PropertyFilters.includedProperties(new String[] { "name", "age" })).toString());
		assertEquals("[address, email, id, level]",
				names(Person.class, PropertyFilters.excludedProperties(new String[] { "name", "age" })).toString());
		assertEquals("[email]", names(Person.class, PropertyFilters.matches("mail", MatchMode.END)).toString());
		assertEquals("[age, level]", names(Person.class, PropertyFilters.propertyTypeContains(new Class<?>[] { int.class })).toString());
	}

	@Test
	public void propertyFilter_combinatorsComposeResults() {
		PropertyFilter none = PropertyFilter.disjunction();
		PropertyFilter all = PropertyFilter.conjunction();
		assertFalse(none.accept(Person.class, "name", null));
		assertTrue(all.accept(Person.class, "name", null));
		assertFalse(all.and(none).accept(Person.class, "name", null));
		assertTrue(all.and(all).accept(Person.class, "name", null));
		assertTrue(none.or(all).accept(Person.class, "name", null));
		assertFalse(none.or(none).accept(Person.class, "name", null));
		// not(filter) only negates the given filter
		assertTrue(none.not(none).accept(Person.class, "name", null));
		assertFalse(all.not(all).accept(Person.class, "name", null));
	}

}
