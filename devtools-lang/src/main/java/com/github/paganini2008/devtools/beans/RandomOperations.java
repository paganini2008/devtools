package com.github.paganini2008.devtools.beans;

import java.lang.reflect.Type;
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

	boolean randomBoolean(Type type, MockContext context);

	char randomChar(Type type, MockContext context);

	byte randomByte(Type type, MockContext context);

	short randomShort(Type type, MockContext context);

	int randomInt(Type type, MockContext context);

	long randomLong(Type type, MockContext context);

	float randomFloat(Type type, MockContext context);

	double randomDouble(Type type, MockContext context);

	BigInteger randomBigInteger(Type type, MockContext context);

	BigDecimal randomBigDecimal(Type type, MockContext context);

	String randomString(Type type, MockContext context);

	Date randomDate(Type type, MockContext context);

	LocalDate randomLocalDate(Type type, MockContext context);

	LocalDateTime randomLocalDateTime(Type type, MockContext context);

	LocalTime randomLocalTime(Type type, MockContext context);

	<E extends Enum<E>> E randomEnum(Class<E> enumClass, MockContext context);

}
