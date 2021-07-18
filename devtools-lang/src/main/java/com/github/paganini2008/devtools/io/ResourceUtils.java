/**
* Copyright 2018-2021 Fred Feng (paganini.fy@gmail.com)

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
package com.github.paganini2008.devtools.io;

import java.util.Map;

import com.github.paganini2008.devtools.MatchMode;
import com.github.paganini2008.devtools.collection.LruMap;
import com.github.paganini2008.devtools.collection.MapUtils;

/**
 * ResourceUtils
 * 
 * @author Fred Feng
 * @version 1.0
 */
public abstract class ResourceUtils {

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
