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
package com.github.paganini2008.devtools.mock;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;

/**
 * 
 * RandomOperations
 *
 * @author Fred Feng
 *
 * @since 2.0.4
 */
public interface RandomOperations {

	boolean randomBoolean(Field field, MockContext context);

	char randomChar(Field field, MockContext context);

	byte randomByte(Field field, MockContext context);

	short randomShort(Field field, MockContext context);

	int randomInt(Field field, MockContext context);

	long randomLong(Field field, MockContext context);

	float randomFloat(Field field, MockContext context);

	double randomDouble(Field field, MockContext context);

	BigInteger randomBigInteger(Field field, MockContext context);

	BigDecimal randomBigDecimal(Field field, MockContext context);

	String randomString(Field field, MockContext context);

	Date randomDate(Field field, MockContext context);

	LocalDate randomLocalDate(Field field, MockContext context);

	LocalDateTime randomLocalDateTime(Field field, MockContext context);

	LocalTime randomLocalTime(Field field, MockContext context);

	<E extends Enum<E>> E randomEnum(Field field, MockContext context);

}
