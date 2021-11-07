package com.github.paganini2008.devtools.beans;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;

import com.github.paganini2008.devtools.RandomStringUtils;
import com.github.paganini2008.devtools.RandomUtils;
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
	public boolean randomBoolean(Type type, MockContext context) {
		return RandomUtils.randomBoolean();
	}

	@Override
	public char randomChar(Type type, MockContext context) {
		return RandomUtils.randomChar(context.getRandomConfig().getMinCharValue(), context.getRandomConfig().getMaxCharValue());
	}

	@Override
	public byte randomByte(Type type, MockContext context) {
		return RandomUtils.randomByte(context.getRandomConfig().getMinByteValue(), context.getRandomConfig().getMaxByteValue());
	}

	@Override
	public short randomShort(Type type, MockContext context) {
		return RandomUtils.randomShort(context.getRandomConfig().getMinShortValue(), context.getRandomConfig().getMaxShortValue());
	}

	@Override
	public int randomInt(Type type, MockContext context) {
		return RandomUtils.randomInt(context.getRandomConfig().getMinIntValue(), context.getRandomConfig().getMaxIntValue());
	}

	@Override
	public long randomLong(Type type, MockContext context) {
		return RandomUtils.randomLong(context.getRandomConfig().getMinLongValue(), context.getRandomConfig().getMaxLongValue());
	}

	@Override
	public float randomFloat(Type type, MockContext context) {
		return RandomUtils.randomFloat(context.getRandomConfig().getMinFloatValue(), context.getRandomConfig().getMaxFloatValue(), 6);
	}

	@Override
	public double randomDouble(Type type, MockContext context) {
		return RandomUtils.randomDouble(context.getRandomConfig().getMinDoubleValue(), context.getRandomConfig().getMaxDoubleValue(), 16);
	}

	@Override
	public BigInteger randomBigInteger(Type type, MockContext context) {
		return RandomUtils.randomBigInteger(context.getRandomConfig().getMinLongValue(), context.getRandomConfig().getMaxLongValue());
	}

	@Override
	public BigDecimal randomBigDecimal(Type type, MockContext context) {
		return RandomUtils.randomBigDecimal(context.getRandomConfig().getMinDoubleValue(), context.getRandomConfig().getMaxDoubleValue(),
				0);
	}

	@Override
	public String randomString(Type type, MockContext context) {
		RandomStringConfig config = context.getRandomStringConfig();
		return RandomStringUtils.randomString(config.getLength(), config.isDigit(), config.isLowerCaseLetter(), config.isUpperCaseLetter());
	}

	@Override
	public Date randomDate(Type type, MockContext context) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LocalDate randomLocalDate(Type type, MockContext context) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LocalDateTime randomLocalDateTime(Type type, MockContext context) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LocalTime randomLocalTime(Type type, MockContext context) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <E extends Enum<E>> E randomEnum(Class<E> enumClass, MockContext context) {
		return RandomUtils.randomEnum(enumClass);
	}

}
