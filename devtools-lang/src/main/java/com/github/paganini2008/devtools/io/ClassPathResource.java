package com.github.paganini2008.devtools.io;

import java.io.IOException;
import java.util.Map;

import com.github.paganini2008.devtools.collection.MapUtils;

/**
 * 
 * ClassPathResource
 * 
 * @author Jimmy Hoff
 * @version 1.0
 */
public class ClassPathResource extends RefreshingResource {

	private static final long serialVersionUID = -4336490159044412458L;
	private final String[] names;
	private final ClassLoader loader;
	private String charset = "UTF-8";

	public ClassPathResource(String... names) {
		this(names, ClassPathResource.class.getClassLoader());
	}

	public ClassPathResource(String[] names, ClassLoader loader) {
		this.names = names;
		this.loader = loader;
	}

	protected Map<String, String> getConfig() throws IOException {
		BasicProperties p = new BasicProperties();
		if (names != null && names.length > 0) {
			for (String name : names) {
				p.load(name, charset, loader);
			}
		}
		return MapUtils.toMap(p);
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}

}
