package com.github.paganini2008.devtools.beans;

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
