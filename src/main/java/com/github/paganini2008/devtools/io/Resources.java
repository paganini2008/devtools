package com.github.paganini2008.devtools.io;

import java.io.File;
import java.util.Locale;

/**
 * 
 * Resources
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class Resources {

	public static Resource openFromClassPath(String name) throws Exception {
		RefreshingResource resource = new ClassPathResource(name);
		resource.refresh();
		return resource;
	}

	public static Resource openFromClassPath(String name, int refreshInterval) throws Exception {
		RefreshingResource resource = new ClassPathResource(name);
		resource.refresh();
		return resource.refresh(refreshInterval);
	}

	public static Resource openBundle(String name, Locale locale) throws Exception {
		RefreshingResource resource = new ResourceBundleResource(name, locale);
		resource.refresh();
		return resource;
	}

	public static Resource openBundle(String name, Locale locale, int refreshInterval) throws Exception {
		RefreshingResource resource = new ResourceBundleResource(name, locale);
		resource.refresh();
		return resource.refresh(refreshInterval);
	}

	public static Resource openFromFileSystem(File file) throws Exception {
		RefreshingResource resource = new FileSystemResource(file);
		resource.refresh();
		return resource;
	}

	public static Resource openFromFileSystem(File file, int refreshInterval) throws Exception {
		RefreshingResource resource = new FileSystemResource(file);
		resource.refresh();
		return resource.refresh(refreshInterval);
	}

	private Resources() {
	}

}
