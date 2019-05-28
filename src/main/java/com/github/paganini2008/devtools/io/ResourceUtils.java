package com.github.paganini2008.devtools.io;

import java.util.Map;

import com.github.paganini2008.devtools.collection.LruMap;
import com.github.paganini2008.devtools.collection.MapUtils;
import com.github.paganini2008.devtools.collection.MatchMode;

/**
 * ResourceUtils
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class ResourceUtils {

	private ResourceUtils() {
	}

	private final static LruMap<String, Resource> cache = new LruMap<String, Resource>(128);

	public static Map<String, String> getResource(String name) throws Exception {
		return getResource(name, ResourceUtils.class.getClassLoader());
	}

	public static Map<String, String> getResource(String name, ClassLoader loader) throws Exception {
		Resource resource = lookup(name, loader);
		return resource.toMap();
	}

	public static Map<String, String> getResource(String name, String prefix) throws Exception {
		return getResource(name, ResourceUtils.class.getClassLoader(), prefix);
	}

	public static Map<String, String> getResource(String name, ClassLoader loader, String prefix) throws Exception {
		return getResource(name, loader, prefix, MatchMode.START);
	}

	public static Map<String, String> getResource(String name, String substr, MatchMode matchMode) throws Exception {
		return getResource(name, ResourceUtils.class.getClassLoader(), substr, matchMode);
	}

	public static Map<String, String> getResource(String name, ClassLoader loader, String substr, MatchMode matchMode) throws Exception {
		Map<String, String> m = getResource(name, loader);
		return MapUtils.toMatchedKeyMap(m, substr, matchMode);
	}

	private static Resource lookup(String name, ClassLoader loader) throws Exception {
		Resource resource = cache.get(name);
		if (resource == null) {
			cache.put(name, Resources.openFromClassPath(name));
			resource = cache.get(name);
		}
		return resource;
	}
}
