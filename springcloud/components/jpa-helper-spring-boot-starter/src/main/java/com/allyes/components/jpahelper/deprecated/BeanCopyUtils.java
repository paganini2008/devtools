package com.allyes.components.jpahelper.deprecated;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.reflect.ConstructorUtils;

/**
 * 
 * BeanCopyUtils
 * 
 * @author Fred Feng
 * @created 2019-03
 */
@Deprecated
public class BeanCopyUtils {

	public static <S, T> void copyProperties(S from, T to) {
		try {
			BeanUtils.copyProperties(to, from);
		} catch (Exception e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
	}

	public static <T> T toBean(Map<String, ?> m, Class<T> resultClass) {
		T object = createInstance(resultClass);
		for (Map.Entry<String, ?> entry : m.entrySet()) {
			try {
				PropertyUtils.setProperty(object, entry.getKey(), entry.getValue());
			} catch (Exception ignored) {
			}
		}
		return object;
	}

	public static <T> List<T> copyFrom(List<Map<String, ?>> list, Class<T> resultClass) {
		List<T> results = new ArrayList<T>();
		T object;
		for (Map<String, ?> m : list) {
			object = toBean(m, resultClass);
			results.add(object);
		}
		return results;
	}

	@SuppressWarnings("unchecked")
	private static <T> T createInstance(Class<T> resultClass) {
		try {
			return (T) ConstructorUtils.invokeConstructor(resultClass, (Object[]) null);
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	private BeanCopyUtils() {
	}

	public static void main(String[] args) {
	}

}
