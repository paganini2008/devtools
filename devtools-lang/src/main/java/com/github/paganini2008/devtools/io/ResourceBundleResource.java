package com.github.paganini2008.devtools.io;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * 
 * ResourceBundleResource
 * 
 * @author Jimmy Hoff
 * @version 1.0
 */
public class ResourceBundleResource extends RefreshingResource {

	private static final long serialVersionUID = -5199315508871485666L;
	private final String[] names;
	private final Locale locale;
	private final ClassLoader loader;

	public ResourceBundleResource(String name, Locale locale) {
		this(new String[] { name }, locale);
	}

	public ResourceBundleResource(String[] names, Locale locale) {
		this(names, locale, ResourceBundleResource.class.getClassLoader());
	}

	public ResourceBundleResource(String[] names, Locale locale, ClassLoader loader) {
		this.names = names;
		this.locale = locale;
		this.loader = loader;
	}

	protected Map<String, String> getConfig() throws IOException {
		Map<String, String> kwargs = new HashMap<String, String>();
		if (names != null && names.length > 0) {
			for (String name : names) {
				ResourceBundle resourceBundle = ResourceBundle.getBundle(name, locale, loader);
				Enumeration<String> keys = resourceBundle.getKeys();
				if (keys != null) {
					while (keys.hasMoreElements()) {
						String key = keys.nextElement();
						kwargs.put(key, resourceBundle.getString(key));
					}
				}
			}
		}
		return kwargs;
	}

}
