package com.github.paganini2008.devtools.beans;

import java.beans.PropertyDescriptor;
import java.util.Collection;
import java.util.Iterator;

import com.github.paganini2008.devtools.Assert;
import com.github.paganini2008.devtools.collection.CollectionUtils;

public class PlainToStringStyle implements ToStringStyle {

	public String toString(Object bean, Collection<PropertyDescriptor> descriptors) {
		Assert.isNull(bean, "Source object must not be null.");
		StringBuilder str = new StringBuilder();
		str.append("{");
		if (CollectionUtils.isNotEmpty(descriptors)) {
			Iterator<PropertyDescriptor> it = descriptors.iterator();
			PropertyDescriptor descriptor;
			for (;;) {
				descriptor = it.next();
				str.append(PropertyUtils.getProperty(bean, descriptor));
				if (it.hasNext()) {
					str.append(",");
				} else {
					break;
				}
			}
		}
		str.append("}");
		return str.toString();
	}

}
