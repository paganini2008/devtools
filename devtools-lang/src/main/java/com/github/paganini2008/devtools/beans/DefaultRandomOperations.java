package com.github.paganini2008.devtools.beans;

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
import com.github.paganini2008.devtools.beans.MockContext.RandomDateConfig;
import com.github.paganini2008.devtools.beans.MockContext.RandomStringConfig;

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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LocalTime randomLocalTime(Field field, MockContext context) {
		// TODO Auto-generated method stub
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <E extends Enum<E>> E randomEnum(Field field, MockContext context) {
		return RandomUtils.randomEnum((Class<E>) field.getType());
	}

}
