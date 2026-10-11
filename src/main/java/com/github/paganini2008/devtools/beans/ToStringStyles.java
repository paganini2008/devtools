/**
* Copyright 2017-2026 Fred Feng (paganini.fy@gmail.com)

* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.github.paganini2008.devtools.beans;

import java.beans.PropertyDescriptor;
import java.util.Collection;
import java.util.Iterator;

import com.github.paganini2008.devtools.Assert;
import com.github.paganini2008.devtools.StringUtils;
import com.github.paganini2008.devtools.collection.CollectionUtils;

/**
 * Built-in {@link ToStringStyle} implementations.
 * 
 * @author Fred Feng
 * @date 2019-08-08
 *
 * @since 2.0.1
 */
public abstract class ToStringStyles {

	private static final String NEWLINE = System.getProperty("line.separator");

	/**
	 * Renders property values only, e.g. {@code {30,Tom}}.
	 */
	public static class PlainToStringStyle implements ToStringStyle {

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

	/**
	 * Renders name/value pairs, e.g. {@code {age: 30, name: Tom}}.
	 */
	public static class DefaultToSringStyle implements ToStringStyle {

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

	/**
	 * Renders the class name, hash code and one name/value pair per line.
	 */
	public static class MultiLineToStringStyle implements ToStringStyle {

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
					str.append(StringUtils.repeat(' ', 4));
					str.append(descriptor.getName()).append(": ").append(PropertyUtils.getProperty(bean, descriptor));
					str.append(NEWLINE);
				}
			}
			str.append("}");
			return str.toString();
		}

	}

	/**
	 * Renders the class name, hash code and name/value pairs on a single line.
	 */
	public static class SingleLineToSrtingStyle implements ToStringStyle {

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

}
