package com.github.paganini2008.devtools.beans;

import java.beans.PropertyDescriptor;
import java.util.Collection;

import com.github.paganini2008.devtools.Assert;
import com.github.paganini2008.devtools.collection.CollectionUtils;

/**
 * SingleLineToSrtingStyle
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class SingleLineToSrtingStyle implements ToStringStyle {

	public String toString(Object bean, Collection<PropertyDescriptor> descriptors) {
		Assert.isNull(bean, "Source object must not be null.");
		StringBuilder str = new StringBuilder();
		str.append(bean.getClass().getName());
		str.append("@").append(bean.hashCode());
		str.append("{");
		if (CollectionUtils.isNotEmpty(descriptors)) {
			int i = 0;
			for (PropertyDescriptor descriptor : descriptors) {
				str.append(descriptor.getName()).append(": ").append(PropertyUtils.getProperty(bean, descriptor));
				if (i++ != descriptors.size() - 1) {
					str.append(", ");
				}
			}
		}
		str.append("}");
		return str.toString();
	}

}
