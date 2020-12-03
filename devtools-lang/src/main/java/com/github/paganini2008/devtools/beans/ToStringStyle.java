package com.github.paganini2008.devtools.beans;

import java.beans.PropertyDescriptor;
import java.util.Collection;

/**
 * ToStringStyle
 * 
 * @author Jimmy Hoff
 * @version 1.0
 */
public interface ToStringStyle {

	String toString(Object bean, Collection<PropertyDescriptor> descriptors);

}
