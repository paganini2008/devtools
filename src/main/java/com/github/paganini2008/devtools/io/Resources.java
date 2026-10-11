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
package com.github.paganini2008.devtools.io;

import java.io.File;
import java.util.Locale;

/**
 * 
 * Factory methods for {@link Resource}s from the class path, the file system or resource bundles.
 * 
 * @author Fred Feng
 * @date 2019-05-29
 * @since 2.0.1
 */
public class Resources {

	/**
	 * Loads a resource from the class path properties files with the name.
	 */
	public static Resource openFromClassPath(String name) throws Exception {
		RefreshingResource resource = new ClassPathResource(name);
		resource.refresh();
		return resource;
	}

	/**
	 * Loads a resource from the class path; the returned resource reloads every {@code refreshInterval} seconds once
	 * {@link RealtimeRefreshingResource#start()} is called.
	 */
	public static Resource openFromClassPath(String name, int refreshInterval) throws Exception {
		RefreshingResource resource = new ClassPathResource(name);
		resource.refresh();
		return resource.refresh(refreshInterval);
	}

	/**
	 * Loads a resource from the resource bundle.
	 *
	 * @throws java.util.MissingResourceException if the bundle does not exist
	 */
	public static Resource openBundle(String name, Locale locale) throws Exception {
		RefreshingResource resource = new ResourceBundleResource(name, locale);
		resource.refresh();
		return resource;
	}

	/**
	 * Loads a resource from the resource bundle that can be reloaded periodically.
	 */
	public static Resource openBundle(String name, Locale locale, int refreshInterval) throws Exception {
		RefreshingResource resource = new ResourceBundleResource(name, locale);
		resource.refresh();
		return resource.refresh(refreshInterval);
	}

	/**
	 * Loads a resource from the properties file.
	 */
	public static Resource openFromFileSystem(File file) throws Exception {
		RefreshingResource resource = new FileSystemResource(file);
		resource.refresh();
		return resource;
	}

	/**
	 * Loads a resource from the properties file that can be reloaded periodically.
	 */
	public static Resource openFromFileSystem(File file, int refreshInterval) throws Exception {
		RefreshingResource resource = new FileSystemResource(file);
		resource.refresh();
		return resource.refresh(refreshInterval);
	}

	private Resources() {
	}

}
