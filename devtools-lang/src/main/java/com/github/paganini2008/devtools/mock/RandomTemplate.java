/**
* Copyright 2017-2021 Fred Feng (paganini.fy@gmail.com)

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
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
import java.util.function.Supplier;

import com.github.paganini2008.devtools.RandomDateUtils;
import com.github.paganini2008.devtools.RandomStringUtils;
import com.github.paganini2008.devtools.RandomUtils;
import com.github.paganini2008.devtools.StringUtils;
import com.github.paganini2008.devtools.beans.Example;
import com.github.paganini2008.devtools.time.DateUtils;

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
	public boolean randomBoolean(Field field, MockContext context) {
		return delegate.randomBoolean(field, context);
	}

	@Override
	public char randomChar(Field field, MockContext context) {
		return delegate.randomChar(field, context);
	}

	@Override
	public byte randomByte(Field field, MockContext context) {
		return delegate.randomByte(field, context);
	}

	@Override
	public short randomShort(Field field, MockContext context) {
		return delegate.randomShort(field, context);
	}

	@Override
	public int randomInt(Field field, MockContext context) {
		IntRange intRange = field.getAnnotation(IntRange.class);
		if (intRange != null) {
			String example = StringUtils.isNotBlank(intRange.value()) ? intRange.value() : field.getName();
			Supplier<Integer> supplier = context.getIntegerSupplier(example);
			if (supplier != null) {
				return supplier.get();
			}
			return RandomUtils.randomInt(intRange.from(), intRange.to());
		}
		return delegate.randomInt(field, context);
	}

	@Override
	public long randomLong(Field field, MockContext context) {
		LongRange longRange = field.getAnnotation(LongRange.class);
		if (longRange != null) {
			String example = StringUtils.isNotBlank(longRange.value()) ? longRange.value() : field.getName();
			Supplier<Long> supplier = context.getLongSupplier(example);
			if (supplier != null) {
				return supplier.get();
			}
			return RandomUtils.randomLong(longRange.from(), longRange.to());
		}
		return delegate.randomLong(field, context);
	}

	@Override
	public float randomFloat(Field field, MockContext context) {
		FloatRange floatRange = field.getAnnotation(FloatRange.class);
		if (floatRange != null) {
			String example = StringUtils.isNotBlank(floatRange.value()) ? floatRange.value() : field.getName();
			Supplier<Float> supplier = context.getFloatSupplier(example);
			if (supplier != null) {
				return supplier.get();
			} else if (floatRange.precision() > 0) {
				return RandomUtils.defineFloat(floatRange.precision(), floatRange.scale());
			}
			return RandomUtils.randomFloat(floatRange.from(), floatRange.to(), floatRange.scale());
		}
		return delegate.randomFloat(field, context);
	}

	@Override
	public double randomDouble(Field field, MockContext context) {
		DoubleRange doubleRange = field.getAnnotation(DoubleRange.class);
		if (doubleRange != null) {
			String example = StringUtils.isNotBlank(doubleRange.value()) ? doubleRange.value() : field.getName();
			Supplier<Double> supplier = context.getDoubleSupplier(example);
			if (supplier != null) {
				return supplier.get();
			} else if (doubleRange.precision() > 0) {
				return RandomUtils.defineDouble(doubleRange.precision(), doubleRange.scale());
			}
			return RandomUtils.randomDouble(doubleRange.from(), doubleRange.to(), doubleRange.scale());
		}
		return delegate.randomDouble(field, context);
	}

	@Override
	public BigInteger randomBigInteger(Field field, MockContext context) {
		LongRange longRange = field.getAnnotation(LongRange.class);
		if (longRange != null) {
			String example = StringUtils.isNotBlank(longRange.value()) ? longRange.value() : field.getName();
			Supplier<BigInteger> supplier = context.getBigIntegerSupplier(example);
			if (supplier != null) {
				return supplier.get();
			}
			return RandomUtils.randomBigInteger(longRange.from(), longRange.to());
		}
		return delegate.randomBigInteger(field, context);
	}

	@Override
	public BigDecimal randomBigDecimal(Field field, MockContext context) {
		DoubleRange doubleRange = field.getAnnotation(DoubleRange.class);
		if (doubleRange != null) {
			String example = StringUtils.isNotBlank(doubleRange.value()) ? doubleRange.value() : field.getName();
			Supplier<BigDecimal> supplier = context.getBigDecimalSupplier(example);
			if (supplier != null) {
				return supplier.get();
			} else if (doubleRange.precision() > 0) {
				return RandomUtils.defineBigDecimal(doubleRange.precision(), doubleRange.scale());
			}
			return RandomUtils.randomBigDecimal(doubleRange.from(), doubleRange.to(), doubleRange.scale());
		}
		return delegate.randomBigDecimal(field, context);
	}

	@Override
	public String randomString(Field field, MockContext context) {
		Example example = field.getAnnotation(Example.class);
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
		return delegate.randomString(field, context);
	}

	@Override
	public Date randomDate(Field field, MockContext context) {
		String example = null;
		Date date = null, time = null;
		DateRange dateRange = field.getAnnotation(DateRange.class);
		if (dateRange != null) {
			example = StringUtils.isNotBlank(dateRange.value()) ? dateRange.value() : field.getName();
			Supplier<Date> supplier = context.getDateSupplier(example);
			if (supplier != null) {
				date = supplier.get();
			} else {
				date = RandomDateUtils.randomDateTime(dateRange.from(), dateRange.to(), dateRange.format());
			}
		}
		TimeRange timeRange = field.getAnnotation(TimeRange.class);
		if (timeRange != null) {
			example = StringUtils.isNotBlank(timeRange.value()) ? timeRange.value() : field.getName();
			Supplier<Date> supplier = context.getDateSupplier(example);
			if (supplier != null) {
				time = supplier.get();
			} else {
				time = RandomDateUtils.randomDateTime(new Date(), timeRange.from(), timeRange.to(), timeRange.format());
			}
		}
		if (date == null && time == null) {
			return delegate.randomDate(field, context);
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
	public LocalDate randomLocalDate(Field field, MockContext context) {
		DateRange dateRange = field.getAnnotation(DateRange.class);
		if (dateRange != null) {
			String example = StringUtils.isNotBlank(dateRange.value()) ? dateRange.value() : field.getName();
			Supplier<LocalDate> supplier = context.getLocalDateSupplier(example);
			if (supplier != null) {
				return supplier.get();
			} else {
				return RandomDateUtils.randomLocalDate(dateRange.from(), dateRange.to(),
						DateTimeFormatter.ofPattern(dateRange.format(), Locale.ENGLISH));
			}
		}
		return delegate.randomLocalDate(field, context);
	}

	@Override
	public LocalDateTime randomLocalDateTime(Field field, MockContext context) {
		String example;
		LocalDate localDate = null;
		LocalTime localTime = null;
		DateRange dateRange = field.getAnnotation(DateRange.class);
		if (dateRange != null) {
			example = StringUtils.isNotBlank(dateRange.value()) ? dateRange.value() : field.getName();
			Supplier<LocalDate> supplier = context.getLocalDateSupplier(example);
			if (supplier != null) {
				localDate = supplier.get();
			} else {
				localDate = RandomDateUtils.randomLocalDate(dateRange.from(), dateRange.to(),
						DateTimeFormatter.ofPattern(dateRange.format(), Locale.ENGLISH));
			}
		}
		TimeRange timeRange = field.getAnnotation(TimeRange.class);
		if (timeRange != null) {
			example = StringUtils.isNotBlank(timeRange.value()) ? timeRange.value() : field.getName();
			Supplier<LocalTime> supplier = context.getLocalTimeSupplier(example);
			if (supplier != null) {
				localTime = supplier.get();
			} else {
				localTime = RandomDateUtils.randomLocalTime(timeRange.from(), timeRange.to(),
						DateTimeFormatter.ofPattern(timeRange.format(), Locale.ENGLISH));
			}
		}
		if (localDate == null && localTime == null) {
			return delegate.randomLocalDateTime(field, context);
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
	public LocalTime randomLocalTime(Field field, MockContext context) {
		TimeRange timeRange = field.getAnnotation(TimeRange.class);
		if (timeRange != null) {
			String example = StringUtils.isNotBlank(timeRange.value()) ? timeRange.value() : field.getName();
			Supplier<LocalTime> supplier = context.getLocalTimeSupplier(example);
			if (supplier != null) {
				return supplier.get();
			}
			return RandomDateUtils.randomLocalTime(timeRange.from(), timeRange.to(),
					DateTimeFormatter.ofPattern(timeRange.format(), Locale.ENGLISH));
		}
		return delegate.randomLocalTime(field, context);
	}

	@Override
	public <E extends Enum<E>> E randomEnum(Field field, MockContext context) {
		return delegate.randomEnum(field, context);
	}

}
