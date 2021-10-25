package com.github.paganini2008.devtools.beans;

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

	boolean randomBoolean();
	
	char randomChar();
	
	byte randomByte();
	
	short randomShort();
	
	int randomInt();
	
	long randomLong();
	
	float randomFloat();
	
	double randomDouble();
	
	BigInteger randomBigInteger();
	
	BigDecimal randomBigDecimal();
	
	String randomString();
	
	Date randomDate();
	
	LocalDate randomLocalDate();
	
	LocalDateTime randomLocalDateTime();
	
	LocalTime randomLocalTime();
	
}
