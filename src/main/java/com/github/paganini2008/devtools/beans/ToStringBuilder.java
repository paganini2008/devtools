package com.github.paganini2008.devtools.beans;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;

import com.github.paganini2008.devtools.Assert;
import com.github.paganini2008.devtools.collection.MapUtils;

/**
 * 
 * ToStringBuilder
 * 
 * @author Fred Feng
 * @created 2019-05
 */
public class ToStringBuilder {

	private static final ToStringStyle DEFAULT_STYLE = new DefaultToSringStyle();

	private static final ToStringStyle PLAIN_STYLE = new PlainToStringStyle();

	private static final ToStringStyle SINGLE_LINE_STYLE = new SingleLineToSrtingStyle();

	private static final ToStringStyle MULTI_LINE_STYLE = new MultiLineToStringStyle();

	public static enum PrintStyle {

		DEFAULT(DEFAULT_STYLE), PLAIN(PLAIN_STYLE), SINGLE_LINE(SINGLE_LINE_STYLE), MULTI_LINE(MULTI_LINE_STYLE);

		private final ToStringStyle style;

		private PrintStyle(ToStringStyle style) {
			this.style = style;
		}

		public ToStringStyle getStyle() {
			return style;
		}

		public static PrintStyle get(String name) {
			for (PrintStyle style : PrintStyle.values()) {
				if (style.name().equalsIgnoreCase(name)) {
					return style;
				}
			}
			return null;
		}
	}

	private ToStringBuilder() {
	}

	public static String reflectionToString(Object target) {
		return reflectionToString(target, (PrintStyle) null);
	}

	public static String reflectionToString(Object target, PrintStyle style) {
		return reflectionToString(target, (PropertyFilter) null, style);
	}

	public static String reflectionToString(Object target, String[] excludedProperties) {
		return reflectionToString(target, PropertyFilters.exclude(excludedProperties));
	}

	public static String reflectionToString(Object target, PropertyFilter filter) {
		return reflectionToString(target, filter, (PrintStyle) null);
	}

	public static String reflectionToString(Object bean, PropertyFilter filter, PrintStyle style) {
		if (style == null) {
			style = PrintStyle.DEFAULT;
		}
		return reflectionToString(bean, null, filter, style.getStyle());
	}

	public static String reflectionToString(Object bean, PropertyFilter filter, ToStringStyle style) {
		return reflectionToString(bean, null, filter, style);
	}

	public static String reflectionToString(Object bean, Class<?> stopClass, PropertyFilter filter, ToStringStyle style) {
		Assert.isNull(bean, "Source object must not be null.");
		PropertyFilter propertyFilter = new PublicPropertyFilter();
		if (filter != null) {
			propertyFilter = propertyFilter.and(filter);
		}
		Map<String, PropertyDescriptor> descriptors = PropertyUtils.getPropertyDescriptors(bean.getClass(), stopClass, propertyFilter);
		if (MapUtils.isEmpty(descriptors)) {
			return "";
		}
		if (style == null) {
			style = DEFAULT_STYLE;
		}
		return style.toString(bean, descriptors.values());
	}

	/**
	 * 
	 * PublicPropertyFilter
	 * 
	 * @author Fred Feng
	 * @revised 2019-05
	 * @version 1.0
	 */
	private static class PublicPropertyFilter implements PropertyFilter {

		public boolean accept(String name, PropertyDescriptor descriptor) {
			Method getter = descriptor.getReadMethod();
			if (getter != null) {
				return getter.getModifiers() == Modifier.PUBLIC || getter.getModifiers() == Modifier.PROTECTED
						|| getter.getModifiers() == 0;
			}
			return false;
		}

	}
}
