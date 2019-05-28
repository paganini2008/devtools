package com.github.paganini2008.devtools.collection;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;

import com.github.paganini2008.devtools.Case;
import com.github.paganini2008.devtools.Cases;
import com.github.paganini2008.devtools.MissingRequiredPropertyException;
import com.github.paganini2008.devtools.beans.BeanUtils;

/**
 * 
 * Tuple
 * 
 * @author Fred Feng
 * @created 2019-03
 */
public class Tuple extends CaseFormatInsensitiveMap<Object> {

	private static final long serialVersionUID = 3578053139527863010L;

	public Tuple() {
		this(Cases.PLAIN);
	}

	public Tuple(Case format) {
		super(format);
	}

	public Object[] toArray() {
		return values().toArray();
	}

	public Boolean getBoolean(String key) {
		return getBoolean(key, null);
	}

	public Boolean getBoolean(String key, Boolean defaultValue) {
		Object o = get(key);
		if (o instanceof Boolean) {
			return (Boolean) o;
		}
		try {
			return Boolean.parseBoolean(o.toString());
		} catch (RuntimeException e) {
			return defaultValue;
		}
	}

	public Boolean getRequiredBoolean(String key) {
		Object o = get(key);
		if (o instanceof Boolean) {
			return (Boolean) o;
		}
		try {
			return Boolean.parseBoolean(o.toString());
		} catch (RuntimeException e) {
			throw new MissingRequiredPropertyException(key, e);
		}
	}

	public String getRequiredString(String key) {
		Object o = get(key);
		if (o instanceof String) {
			return (String) o;
		}
		try {
			return o.toString();
		} catch (RuntimeException e) {
			throw new MissingRequiredPropertyException(key, e);
		}
	}

	public String getString(String key) {
		return getString(key, null);
	}

	public String getString(String key, String defaultValue) {
		Object o = get(key);
		if (o instanceof String) {
			return (String) o;
		}
		try {
			return o.toString();
		} catch (RuntimeException e) {
			return defaultValue;
		}
	}

	public Byte getByte(String key) {
		return getByte(key, null);
	}

	public Byte getByte(String key, Byte defaultValue) {
		Object o = get(key);
		if (o instanceof Number) {
			return ((Number) o).byteValue();
		}
		try {
			return Byte.valueOf(o.toString());
		} catch (RuntimeException e) {
			return defaultValue;
		}
	}

	public Short getShort(String key) {
		return getShort(key, null);
	}

	public Short getShort(String key, Short defaultValue) {
		Object o = get(key);
		if (o instanceof Number) {
			return ((Number) o).shortValue();
		}
		try {
			return Short.valueOf(o.toString());
		} catch (RuntimeException e) {
			return defaultValue;
		}
	}

	public Integer getInteger(String key) {
		return getInteger(key, null);
	}

	public Integer getInteger(String key, Integer defaultValue) {
		Object o = get(key);
		if (o instanceof Number) {
			return ((Number) o).intValue();
		}
		try {
			return Integer.valueOf(o.toString());
		} catch (RuntimeException e) {
			return defaultValue;
		}
	}

	public Integer getRequiredInteger(String key) {
		Object o = get(key);
		if (o instanceof Number) {
			return ((Number) o).intValue();
		}
		try {
			return Integer.valueOf(o.toString());
		} catch (RuntimeException e) {
			throw new MissingRequiredPropertyException(key, e);
		}
	}

	public Long getLong(String key) {
		return getLong(key, null);
	}

	public Long getLong(String key, Long defaultValue) {
		Object o = get(key);
		if (o instanceof Number) {
			return ((Number) o).longValue();
		}
		try {
			return Long.valueOf(o.toString());
		} catch (RuntimeException e) {
			return defaultValue;
		}
	}

	public Long getRequiredLong(String key) {
		Object o = get(key);
		if (o instanceof Number) {
			return ((Number) o).longValue();
		}
		try {
			return Long.valueOf(o.toString());
		} catch (RuntimeException e) {
			throw new MissingRequiredPropertyException(key, e);
		}
	}

	public Float getFloat(String key) {
		return getFloat(key, null);
	}

	public Float getFloat(String key, Float defaultValue) {
		Object o = get(key);
		if (o instanceof Number) {
			return ((Number) o).floatValue();
		}
		try {
			return Float.valueOf(o.toString());
		} catch (RuntimeException e) {
			return defaultValue;
		}
	}

	public Float getRequiredFloat(String key) {
		Object o = get(key);
		if (o instanceof Number) {
			return ((Number) o).floatValue();
		}
		try {
			return Float.valueOf(o.toString());
		} catch (RuntimeException e) {
			throw new MissingRequiredPropertyException(key, e);
		}
	}

	public Double getDouble(String key) {
		return getDouble(key, null);
	}

	public Double getDouble(String key, Double defaultValue) {
		Object o = get(key);
		if (o instanceof Number) {
			return ((Number) o).doubleValue();
		}
		try {
			return Double.valueOf(o.toString());
		} catch (RuntimeException e) {
			return defaultValue;
		}
	}

	public Double getRequiredDouble(String key) {
		Object o = get(key);
		if (o instanceof Number) {
			return ((Number) o).doubleValue();
		}
		try {
			return Double.valueOf(o.toString());
		} catch (RuntimeException e) {
			throw new MissingRequiredPropertyException(key, e);
		}
	}

	public BigDecimal getBigDecimal(String key) {
		return getBigDecimal(key, null);
	}

	public BigDecimal getBigDecimal(String key, BigDecimal defaultValue) {
		Object o = get(key);
		if (o instanceof BigDecimal) {
			return (BigDecimal) o;
		} else if (o instanceof BigInteger) {
			return new BigDecimal((BigInteger) o);
		}
		try {
			return new BigDecimal(o.toString());
		} catch (RuntimeException e) {
			return defaultValue;
		}
	}

	public BigInteger getBigInteger(String key) {
		return getBigInteger(key, null);
	}

	public BigInteger getBigInteger(String key, BigInteger defaultValue) {
		Object o = get(key);
		if (o instanceof BigInteger) {
			return (BigInteger) o;
		} else if (o instanceof BigDecimal) {
			return ((BigDecimal) o).toBigInteger();
		}
		try {
			return new BigInteger(o.toString());
		} catch (RuntimeException e) {
			return defaultValue;
		}
	}

	public static Tuple createBy(Case caseFormat, Map<String, ?> kwargs) {
		Tuple tuple = new Tuple(caseFormat);
		tuple.putAll(kwargs);
		return tuple;
	}

	public static Tuple createBy(Map<String, ?> kwargs) {
		return createBy(Cases.PLAIN, kwargs);
	}

	public void fill(Object object) {
		for (String key : keySet()) {
			BeanUtils.setProperty(object, key, get(key));
		}
	}

	public <T> T toBean(Class<T> requiredType) {
		final T object = BeanUtils.instantiate(requiredType);
		fill(object);
		return object;
	}

}
