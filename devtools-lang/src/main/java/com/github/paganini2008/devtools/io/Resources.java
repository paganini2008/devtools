/**
* Copyright 2017-2022 Fred Feng (paganini.fy@gmail.com)

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
 * Resources
 * 
 * @author Fred Feng
 * @since 2.0.1
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
