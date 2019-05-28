package com.github.paganini2008.devtools.beans;

import java.beans.PropertyDescriptor;
import java.util.Collection;

import com.github.paganini2008.devtools.Assert;
import com.github.paganini2008.devtools.StringUtils;
import com.github.paganini2008.devtools.collection.CollectionUtils;




public class MultiLineToStringStyle implements ToStringStyle {

	private static final String NEWLINE = System.getProperty("line.separator");

	public String toString(Object bean, Collection<PropertyDescriptor> descriptors) {
		Assert.isNull(bean, "Source object must not be null.");
		StringBuilder str = new StringBuilder("");
		str.append(bean.getClass().getName());
		str.append("@").append(bean.hashCode());
		str.append(" ");
		str.append("{");
		if (CollectionUtils.isNotEmpty(descriptors)) {
			str.append(NEWLINE);
			for (PropertyDescriptor descriptor : descriptors) {
				str.append(StringUtils.padding(4));
				str.append(descriptor.getName()).append(": ").append(PropertyUtils.getProperty(bean, descriptor));
				str.append(NEWLINE);
			}
		}
		str.append("}");
		return str.toString();
	}

}
