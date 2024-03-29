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

import com.github.paganini2008.devtools.RandomDateUtils;
import com.github.paganini2008.devtools.RandomStringUtils;
import com.github.paganini2008.devtools.RandomUtils;
import com.github.paganini2008.devtools.mock.MockContext.RandomDateConfig;
import com.github.paganini2008.devtools.mock.MockContext.RandomStringConfig;

/**
 * 
 * DefaultRandomOperations
 *
 * @author Fred Feng
 *
 * @since 2.0.4
 */
public class DefaultRandomOperations implements RandomOperations {

	@Override
	public boolean randomBoolean(Field field, MockContext context) {
		return RandomUtils.randomBoolean();
	}

	@Override
	public char randomChar(Field field, MockContext context) {
		return RandomUtils.randomChar(context.getRandomConfig().getMinCharValue(), context.getRandomConfig().getMaxCharValue());
	}

	@Override
	public byte randomByte(Field field, MockContext context) {
		return RandomUtils.randomByte(context.getRandomConfig().getMinByteValue(), context.getRandomConfig().getMaxByteValue());
	}

	@Override
	public short randomShort(Field field, MockContext context) {
		return RandomUtils.randomShort(context.getRandomConfig().getMinShortValue(), context.getRandomConfig().getMaxShortValue());
	}

	@Override
	public int randomInt(Field field, MockContext context) {
		return RandomUtils.randomInt(context.getRandomConfig().getMinIntValue(), context.getRandomConfig().getMaxIntValue());
	}

	@Override
	public long randomLong(Field field, MockContext context) {
		return RandomUtils.randomLong(context.getRandomConfig().getMinLongValue(), context.getRandomConfig().getMaxLongValue());
	}

	@Override
	public float randomFloat(Field field, MockContext context) {
		return RandomUtils.randomFloat(context.getRandomConfig().getMinFloatValue(), context.getRandomConfig().getMaxFloatValue(), 6);
	}

	@Override
	public double randomDouble(Field field, MockContext context) {
		return RandomUtils.randomDouble(context.getRandomConfig().getMinDoubleValue(), context.getRandomConfig().getMaxDoubleValue(), 16);
	}

	@Override
	public BigInteger randomBigInteger(Field field, MockContext context) {
		return RandomUtils.randomBigInteger(context.getRandomConfig().getMinLongValue(), context.getRandomConfig().getMaxLongValue());
	}

	@Override
	public BigDecimal randomBigDecimal(Field field, MockContext context) {
		return RandomUtils.randomBigDecimal(context.getRandomConfig().getMinDoubleValue(), context.getRandomConfig().getMaxDoubleValue(),
				0);
	}

	@Override
	public String randomString(Field field, MockContext context) {
		RandomStringConfig config = context.getRandomStringConfig();
		return RandomStringUtils.randomString(config.getLength(), config.isDigit(), config.isLowerCaseLetter(), config.isUpperCaseLetter());
	}

	@Override
	public Date randomDate(Field field, MockContext context) {
		RandomDateConfig config = context.getRandomDateConfig();
		return RandomDateUtils.randomDateTime(config.getFromYear(), config.getToYear(), config.getFromMonth(), config.getToMonth(),
				config.getFromDayOfMonth(), config.getToDayOfMonth(), config.getFromHourOfDay(), config.getToHourOfDay(),
				config.getFromMinute(), config.getToMinute(), config.getFromSecond(), config.getToSecond());
	}

	@Override
	public LocalDate randomLocalDate(Field field, MockContext context) {
		RandomDateConfig config = context.getRandomDateConfig();
		return RandomDateUtils.randomLocalDate(config.getFromYear(), config.getToYear(), config.getFromMonth(), config.getToMonth(),
				config.getFromDayOfMonth(), config.getToDayOfMonth());
	}

	@Override
	public LocalDateTime randomLocalDateTime(Field field, MockContext context) {
		RandomDateConfig config = context.getRandomDateConfig();
		return RandomDateUtils.randomLocalDateTime(config.getFromYear(), config.getToYear(), config.getFromMonth(), config.getToMonth(),
				config.getFromDayOfMonth(), config.getToDayOfMonth(), config.getFromHourOfDay(), config.getToHourOfDay(),
				config.getFromMinute(), config.getToMinute(), config.getFromSecond(), config.getToSecond());
	}

	@Override
	public LocalTime randomLocalTime(Field field, MockContext context) {
		RandomDateConfig config = context.getRandomDateConfig();
		return RandomDateUtils.randomLocalTime(config.getFromHourOfDay(), config.getToHourOfDay(), config.getFromMinute(),
				config.getToMinute(), config.getFromSecond(), config.getToSecond());
	}

	@SuppressWarnings("unchecked")
	@Override
	public <E extends Enum<E>> E randomEnum(Field field, MockContext context) {
		return RandomUtils.randomEnum((Class<E>) field.getType());
	}

}
