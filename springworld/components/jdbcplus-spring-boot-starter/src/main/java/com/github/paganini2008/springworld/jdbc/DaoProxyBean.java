package com.github.paganini2008.springworld.jdbc;

import java.awt.List;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.github.paganini2008.devtools.ArrayUtils;
import com.github.paganini2008.devtools.NotImplementedException;
import com.github.paganini2008.devtools.StringUtils;
import com.github.paganini2008.devtools.beans.PropertyUtils;
import com.github.paganini2008.devtools.collection.MapUtils;
import com.github.paganini2008.devtools.jdbc.PageableSql;
import com.github.paganini2008.devtools.jdbc.ResultSetSlice;
import com.github.paganini2008.springworld.jdbc.annotations.Arg;
import com.github.paganini2008.springworld.jdbc.annotations.Example;
import com.github.paganini2008.springworld.jdbc.annotations.Get;
import com.github.paganini2008.springworld.jdbc.annotations.Insert;
import com.github.paganini2008.springworld.jdbc.annotations.Select;
import com.github.paganini2008.springworld.jdbc.annotations.Slice;
import com.github.paganini2008.springworld.jdbc.annotations.Update;

/**
 * 
 * DaoProxyBean
 *
 * @author Fred Feng
 * @created 2019-10
 * @revised 2020-01
 * @version 1.0
 */
@SuppressWarnings("all")
public class DaoProxyBean<T> implements InvocationHandler {

	private final Class<T> interfaceClass;
	protected final Logger log;

	public DaoProxyBean(Class<T> interfaceClass) {
		this.interfaceClass = interfaceClass;
		this.log = LoggerFactory.getLogger(interfaceClass);
	}

	@Autowired
	private EnhancedJdbcTemplate jdbcTemplate;

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		if (!acceptedMethod(method)) {
			throw new IllegalArgumentException("All calling is only based in Dao operations.");
		}
		if (method.isAnnotationPresent(Insert.class)) {
			return doInsert(method, args);
		} else if (method.isAnnotationPresent(Update.class)) {
			return doUpdate(method, args);
		} else if (method.isAnnotationPresent(Get.class)) {
			return doGet(method, args);
		} else if (method.isAnnotationPresent(Select.class)) {
			return doSelect(method, args);
		} else if (method.isAnnotationPresent(Slice.class)) {
			return doSlice(method, args);
		}
		throw new NotImplementedException("Unkown operation in interface: " + interfaceClass.getName());
	}

	private Object doSelect(Method method, Object[] args) {
		Select select = method.getAnnotation(Select.class);
		String sql = select.value();
		if (log.isTraceEnabled()) {
			log.trace("Execute sql: " + sql);
		}

		Class<?> resultClass = method.getReturnType();
		if (!List.class.isAssignableFrom(resultClass)) {
			throw new UnsupportedOperationException("Only for List Type");
		}
		SqlParameterSource sqlParameterSource = getSqlParameterSource(method, args);
		Class<?> elementType = select.elementType();
		if (select.javaType()) {
			return jdbcTemplate.queryForList(sql, sqlParameterSource, elementType);
		} else {
			if (Map.class.isAssignableFrom(elementType)) {
				return jdbcTemplate.queryForList(sql, sqlParameterSource);
			} else {
				return jdbcTemplate.query(sql, sqlParameterSource, new BeanPropertyRowMapper<>(elementType));
			}
		}
	}

	private Object doSlice(Method method, Object[] args) {
		final Slice slice = method.getAnnotation(Slice.class);
		PageableSql pageableSql;
		if (StringUtils.isNotBlank(slice.name())) {
			pageableSql = ApplicationContextUtils.getBean(slice.name(), PageableSql.class);
		} else {
			pageableSql = new PageableSql() {

				public String countableSql() {
					if (log.isTraceEnabled()) {
						log.trace("Execute sql: " + slice.countableSql());
					}
					return slice.countableSql();
				}

				public String pageableSql(int maxResults, int firstResult) {
					if (log.isTraceEnabled()) {
						log.trace("Execute sql: " + slice.pageableSql());
					}
					return slice.pageableSql();
				}

			};
		}

		Class<?> resultClass = method.getReturnType();
		if (!ResultSetSlice.class.isAssignableFrom(resultClass)) {
			throw new UnsupportedOperationException("Only for ResultSetSlice Type");
		}
		SqlParameterSource sqlParameterSource = getSqlParameterSource(method, args);
		Class<?> elementType = slice.elementType();
		if (slice.javaType()) {
			return jdbcTemplate.slice(pageableSql, sqlParameterSource, elementType);
		} else {
			if (Map.class.isAssignableFrom(elementType)) {
				return jdbcTemplate.slice(pageableSql, sqlParameterSource);
			} else {
				return jdbcTemplate.slice(pageableSql, sqlParameterSource, new BeanPropertyRowMapper<>(elementType));
			}
		}
	}

	private Object doGet(Method method, Object[] args) {
		Get getter = method.getAnnotation(Get.class);
		String sql = getter.value();
		if (log.isTraceEnabled()) {
			log.trace("Execute sql: " + sql);
		}

		Class<?> resultClass = method.getReturnType();
		SqlParameterSource sqlParameterSource = getSqlParameterSource(method, args);
		if (getter.javaType()) {
			return jdbcTemplate.queryForObject(sql, sqlParameterSource, resultClass);
		} else {
			if (Map.class.isAssignableFrom(resultClass)) {
				return jdbcTemplate.queryForObject(sql, sqlParameterSource, new ColumnMapRowMapper());
			} else {
				return jdbcTemplate.queryForObject(sql, sqlParameterSource, new BeanPropertyRowMapper<>(resultClass));
			}
		}
	}

	private Object doInsert(Method method, Object[] args) {
		Insert insert = method.getAnnotation(Insert.class);
		String sql = insert.value();
		if (log.isTraceEnabled()) {
			log.trace("Execute sql: " + sql);
		}

		String keyProperty = insert.keyProperty();
		SqlParameterSource sqlParameterSource = getSqlParameterSource(method, args);
		KeyHolder keyHolder = new GeneratedKeyHolder();
		jdbcTemplate.update(sql, sqlParameterSource, keyHolder);
		Map<String, Object> keys = keyHolder.getKeys();
		return keys.get(keyProperty);
	}

	private Object doUpdate(Method method, Object[] args) {
		Update update = method.getAnnotation(Update.class);
		String sql = update.value();
		if (log.isTraceEnabled()) {
			log.trace("Execute sql: " + sql);
		}

		SqlParameterSource sqlParameterSource = getSqlParameterSource(method, args);
		return jdbcTemplate.update(sql, sqlParameterSource);
	}

	private boolean acceptedMethod(Method method) {
		Annotation[] annotations = method.getAnnotations();
		if (annotations != null) {
			for (Annotation annotation : annotations) {
				if (acceptedAnnotation(annotation)) {
					return true;
				}
			}
		}
		return false;
	}

	protected boolean acceptedAnnotation(Annotation annotation) {
		Class<?> annotationClass = annotation.annotationType();
		return annotationClass == Insert.class || annotationClass == Update.class || annotationClass == Get.class
				|| annotationClass == Select.class || annotationClass == Slice.class;
	}

	private SqlParameterSource getSqlParameterSource(Method method, Object[] args) {
		Annotation[][] annotations = method.getParameterAnnotations();
		Annotation[] requiredAnnotations = new Annotation[annotations.length];
		int i = 0;
		for (Annotation[] annotation : annotations) {
			requiredAnnotations[i++] = annotation[0];
		}
		int length = requiredAnnotations.length;
		Map<String, Object> parameters = new HashMap<String, Object>();
		if (length > 1) {
			Annotation annotation;
			for (i = 0; i < length; i++) {
				annotation = requiredAnnotations[i];
				if (annotation instanceof Arg) {
					parameters.put(((Arg) annotation).value(), args[i]);
				} else if (annotation instanceof Example) {
					if (args[i] instanceof Map) {
						parameters.putAll((Map<String, Object>) args[i]);
					} else {
						parameters.putAll(PropertyUtils.convertToMap(args[i]));
					}
					String[] excludedProperties = ((Example) annotation).excludedProperties();
					if (ArrayUtils.isNotEmpty(excludedProperties)) {
						MapUtils.removeKeys(parameters, excludedProperties);
					}
				}
			}
		} else if (length == 1) {
			final Annotation annotation = requiredAnnotations[0];
			if (annotation instanceof Arg) {
				parameters.put(((Arg) annotation).value(), args[0]);
			} else if (annotation instanceof Example) {
				if (args[i] instanceof Map) {
					parameters.putAll((Map<String, Object>) args[i]);
				} else {
					parameters.putAll(PropertyUtils.convertToMap(args[i]));
				}
				String[] excludedProperties = ((Example) annotation).excludedProperties();
				if (ArrayUtils.isNotEmpty(excludedProperties)) {
					MapUtils.removeKeys(parameters, excludedProperties);
				}
			}
		}
		return new MapSqlParameterSource(parameters);
	}

	public Class<T> getInterfaceClass() {
		return interfaceClass;
	}

}
