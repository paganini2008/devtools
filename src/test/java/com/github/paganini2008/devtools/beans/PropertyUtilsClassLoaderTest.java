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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.beans.PropertyDescriptor;
import java.io.File;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import com.github.paganini2008.devtools.beans.fixture.IsolatedBean;

/**
 * PropertyUtilsClassLoaderTest checks that the descriptor cache does not keep a bean class, and so its class loader,
 * alive.
 * 
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class PropertyUtilsClassLoaderTest {

	@Test
	@Timeout(30)
	public void cachedDescriptors_doNotPinTheClassLoader() throws Exception {
		WeakReference<ClassLoader> loaderRef = cacheDescriptorsInThrowawayLoader();
		List<byte[]> pressure = new ArrayList<byte[]>();
		for (int i = 0; i < 100 && loaderRef.get() != null; i++) {
			System.gc();
			pressure.add(new byte[1024 * 1024]);
			if (pressure.size() > 32) {
				pressure.clear();
			}
			Thread.sleep(20);
		}
		assertNull(loaderRef.get(), "the class loader of a cached bean class should be collectable");
	}

	private static WeakReference<ClassLoader> cacheDescriptorsInThrowawayLoader() throws Exception {
		URL classes = new File(IsolatedBean.class.getProtectionDomain().getCodeSource().getLocation().toURI()).toURI().toURL();
		URLClassLoader loader = new URLClassLoader(new URL[] { classes }, null);
		Class<?> isolated = loader.loadClass(IsolatedBean.class.getName());
		assertNotSame(IsolatedBean.class, isolated);
		Map<String, PropertyDescriptor> descriptors = PropertyUtils.getPropertyDescriptors(isolated);
		assertEquals(1, descriptors.size());
		assertEquals(String.class, descriptors.get("name").getPropertyType());
		loader.close();
		return new WeakReference<ClassLoader>(loader);
	}
}
