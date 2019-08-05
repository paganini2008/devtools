package com.github.paganini2008.springcloud.fastjpa;

import java.util.LinkedHashMap;
import java.util.Map;

import com.github.paganini2008.springcloud.fastjpa.support.BeanReflection;

/**
 * 
 * Transformers
 *
 * @author Fred Feng
 * @revised 2019-07
 * @created 2019-02
 * @version 1.0
 */
public abstract class Transformers {

	public static <E> Transformer<E, Map<String, Object>> asMap() {
		return new MapTransformer<E>();
	}

	public static <E, T> Transformer<E, T> asBean(Class<T> resultClass) {
		return asBean(resultClass, null);
	}

	public static <E, T> Transformer<E, T> asBean(Class<T> resultClass, String[] includedProperties) {
		return new BeanTransformer<E, T>(resultClass, includedProperties);
	}

	/**
	 * 
	 * MapTransformer
	 *
	 * @author Fred Feng
	 * @revised 2019-07
	 * @created 2019-02
	 * @version 1.0
	 */
	public static class MapTransformer<E> extends TransformerSupport<E, Map<String, Object>> {

		MapTransformer() {
		}

		protected Map<String, Object> createObject() {
			return new LinkedHashMap<String, Object>();
		}

		protected void setAsValue(String attributeName, Object attributeValue, Map<String, Object> data) {
			data.put(attributeName, attributeValue);
		}

	}

	/**
	 * 
	 * BeanTransformer
	 *
	 * @author Fred Feng
	 * @revised 2019-07
	 * @created 2019-02
	 * @version 1.0
	 */
	public static class BeanTransformer<E, T> extends TransformerSupport<E, T> {

		private final BeanReflection<T> beanReflection;

		BeanTransformer(Class<T> resultClass, String... includedProperties) {
			this.beanReflection = new BeanReflection<T>(resultClass, includedProperties);
		}

		protected void setAsValue(String attributeName, Object attributeValue, T object) {
			beanReflection.setProperty(object, attributeName, attributeValue);
		}

		protected T createObject() {
			return beanReflection.instantiateBean();
		}

	}

}
