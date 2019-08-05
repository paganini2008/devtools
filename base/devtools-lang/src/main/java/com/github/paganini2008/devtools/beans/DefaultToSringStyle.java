package com.github.paganini2008.devtools.beans;

import java.beans.PropertyDescriptor;
import java.util.Collection;

import com.github.paganini2008.devtools.Assert;
import com.github.paganini2008.devtools.collection.CollectionUtils;

/**
 * DefaultToSringStyle
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class DefaultToSringStyle implements ToStringStyle {

	public String toString(Object bean, Collection<PropertyDescriptor> descriptors) {
		Assert.isNull(bean, "Source object must not be null.");
		StringBuilder str = new StringBuilder();
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
