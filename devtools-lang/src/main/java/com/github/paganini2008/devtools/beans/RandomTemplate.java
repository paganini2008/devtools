package com.github.paganini2008.devtools.beans;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
import java.util.function.Supplier;

import com.github.paganini2008.devtools.RandomDateUtils;
import com.github.paganini2008.devtools.RandomStringUtils;
import com.github.paganini2008.devtools.RandomUtils;
import com.github.paganini2008.devtools.StringUtils;
import com.github.paganini2008.devtools.date.DateUtils;

/**
 * 
 * RandomTemplate
 *
 * @author Fred Feng
 *
 * @since 2.0.4
 */
public class RandomTemplate implements RandomOperations {

	private final RandomOperations delegate;

	public RandomTemplate() {
		this(new DefaultRandomOperations());
	}

	public RandomTemplate(RandomOperations randomOperations) {
		this.delegate = randomOperations;
	}

	@Override
	public boolean randomBoolean(Type type, MockContext context) {
		return delegate.randomBoolean(type, context);
	}

	@Override
	public char randomChar(Type type, MockContext context) {
		return delegate.randomChar(type, context);
	}

	@Override
	public byte randomByte(Type type, MockContext context) {
		return delegate.randomByte(type, context);
	}

	@Override
	public short randomShort(Type type, MockContext context) {
		return delegate.randomShort(type, context);
	}

	@Override
	public int randomInt(Type type, MockContext context) {
		IntRange intRange = ((AnnotatedElement) type).getAnnotation(IntRange.class);
		if (intRange != null) {
			return RandomUtils.randomInt(intRange.from(), intRange.to());
		}
		return delegate.randomInt(type, context);
	}

	@Override
	public long randomLong(Type type, MockContext context) {
		LongRange longRange = ((AnnotatedElement) type).getAnnotation(LongRange.class);
		if (longRange != null) {
			return RandomUtils.randomLong(longRange.from(), longRange.to());
		}
		return delegate.randomLong(type, context);
	}

	@Override
	public float randomFloat(Type type, MockContext context) {
		FloatRange floatRange = ((AnnotatedElement) type).getAnnotation(FloatRange.class);
		if (floatRange != null) {
			if (floatRange.precision() > 0) {
				return RandomUtils.defineFloat(floatRange.precision(), floatRange.scale());
			}
			return RandomUtils.randomFloat(floatRange.from(), floatRange.to(), floatRange.scale());
		}
		return delegate.randomFloat(type, context);
	}

	@Override
	public double randomDouble(Type type, MockContext context) {
		DoubleRange doubleRange = ((AnnotatedElement) type).getAnnotation(DoubleRange.class);
		if (doubleRange != null) {
			if (doubleRange.precision() > 0) {
				return RandomUtils.defineDouble(doubleRange.precision(), doubleRange.scale());
			}
			return RandomUtils.randomDouble(doubleRange.from(), doubleRange.to(), doubleRange.scale());
		}
		return delegate.randomDouble(type, context);
	}

	@Override
	public BigInteger randomBigInteger(Type type, MockContext context) {
		LongRange longRange = ((AnnotatedElement) type).getAnnotation(LongRange.class);
		if (longRange != null) {
			return RandomUtils.randomBigInteger(longRange.from(), longRange.to());
		}
		return delegate.randomBigInteger(type, context);
	}

	@Override
	public BigDecimal randomBigDecimal(Type type, MockContext context) {
		DoubleRange doubleRange = ((AnnotatedElement) type).getAnnotation(DoubleRange.class);
		if (doubleRange != null) {
			if (doubleRange.precision() > 0) {
				return RandomUtils.defineBigDecimal(doubleRange.precision(), doubleRange.scale());
			}
			return RandomUtils.randomBigDecimal(doubleRange.from(), doubleRange.to(), doubleRange.scale());
		}
		return delegate.randomBigDecimal(type, context);
	}

	@Override
	public String randomString(Type type, MockContext context) {
		Example example = ((AnnotatedElement) type).getAnnotation(Example.class);
		if (example != null) {
			String rand = "";
			Supplier<String> supplier = context.getStringSupplier(example.value());
			if (supplier != null) {
				rand = supplier.get();
			} else if (StringUtils.isNotBlank(example.style())) {
				rand = RandomStringUtils.randomString(example.length(), example.style());
			} else {
				rand = RandomStringUtils.randomString(example.length(), example.digit(), example.lowerCaseLetter(),
						example.upperCaseLetter());
			}
			return example.prefix() + rand + example.suffix();
		}
		return delegate.randomString(type, context);
	}

	@Override
	public Date randomDate(Type type, MockContext context) {
		Date date = null, time = null;
		DateRange dateRange = ((AnnotatedElement) type).getAnnotation(DateRange.class);
		if (dateRange != null) {
			date = RandomDateUtils.randomDateTime(dateRange.from(), dateRange.to(), dateRange.format());
		}
		TimeRange timeRange = ((AnnotatedElement) type).getAnnotation(TimeRange.class);
		if (timeRange != null) {
			time = RandomDateUtils.randomDateTime(new Date(), timeRange.from(), timeRange.to(), dateRange.format());
		}
		if (date == null && time == null) {
			return delegate.randomDate(type, context);
		}
		if (date == null) {
			date = new Date();
		}
		if (time == null) {
			time = DateUtils.today();
		}
		return DateUtils.setTime(date, time);
	}

	@Override
	public LocalDate randomLocalDate(Type type, MockContext context) {
		DateRange dateRange = ((AnnotatedElement) type).getAnnotation(DateRange.class);
		if (dateRange != null) {
			return RandomDateUtils.randomLocalDate(dateRange.from(), dateRange.to(),
					DateTimeFormatter.ofPattern(dateRange.format(), Locale.ENGLISH));
		}
		return delegate.randomLocalDate(type, context);
	}

	@Override
	public LocalDateTime randomLocalDateTime(Type type, MockContext context) {
		LocalDate localDate = null;
		LocalTime localTime = null;
		DateRange dateRange = ((AnnotatedElement) type).getAnnotation(DateRange.class);
		if (dateRange != null) {
			localDate = RandomDateUtils.randomLocalDate(dateRange.from(), dateRange.to(),
					DateTimeFormatter.ofPattern(dateRange.format(), Locale.ENGLISH));
		}
		TimeRange timeRange = ((AnnotatedElement) type).getAnnotation(TimeRange.class);
		if (timeRange != null) {
			localTime = RandomDateUtils.randomLocalTime(timeRange.from(), timeRange.to(),
					DateTimeFormatter.ofPattern(timeRange.format(), Locale.ENGLISH));
		}
		if (localDate == null && localTime == null) {
			return delegate.randomLocalDateTime(type, context);
		}
		if (localDate == null) {
			localDate = LocalDate.now();
		}
		if (localTime == null) {
			localTime = LocalTime.of(0, 0, 0);
		}
		return localDate.atTime(localTime);
	}

	@Override
	public LocalTime randomLocalTime(Type type, MockContext context) {
		TimeRange timeRange = ((AnnotatedElement) type).getAnnotation(TimeRange.class);
		if (timeRange != null) {
			return RandomDateUtils.randomLocalTime(timeRange.from(), timeRange.to(),
					DateTimeFormatter.ofPattern(timeRange.format(), Locale.ENGLISH));
		}
		return delegate.randomLocalTime(type, context);
	}

	@Override
	public <E extends Enum<E>> E randomEnum(Class<E> enumClass, MockContext context) {
		return delegate.randomEnum(enumClass, context);
	}

}
