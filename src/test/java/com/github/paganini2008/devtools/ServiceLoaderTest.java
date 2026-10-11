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
package com.github.paganini2008.devtools;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

/**
 * ServiceLoaderTest
 *
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class ServiceLoaderTest {

	public interface Greeter {
		String greet();
	}

	public interface WrongService {
	}

	public interface MissingService {
	}

	public interface UnconfiguredService {
	}

	public static class HelloGreeter implements Greeter {

		public String greet() {
			return "hello";
		}
	}

	public static class NamedGreeter implements Greeter {

		private final String name;

		public NamedGreeter(String name) {
			this.name = name;
		}

		public String greet() {
			return "hello " + name;
		}
	}

	@AfterEach
	public void resetLookupPath() {
		ServiceLoader.lookupPath(null);
	}

	@Test
	public void testLoadAndCache() {
		ServiceLoader<Greeter> loader = ServiceLoader.load(Greeter.class);
		Greeter first = loader.getFirst();
		assertEquals("hello", first.greet());
		assertSame(first, loader.getFirst());
		assertEquals("hello fred", loader.get("named", "fred").greet());
		assertThrows(ServiceNotFoundException.class, () -> loader.get("unknown"));
	}

	@Test
	public void testCustomLookupPathWithoutTrailingSlash() {
		ServiceLoader.lookupPath("META-INF/spi/com/github/paganini2008/devtools");
		ServiceLoader<Greeter> loader = ServiceLoader.load(Greeter.class, Thread.currentThread().getContextClassLoader());
		assertEquals("hello", loader.getFirst().greet());
	}

	@Test
	public void testExplicitLookupPath() {
		ServiceLoader.lookupPath("META-INF/spi/elsewhere");
		ServiceLoader<Greeter> loader = ServiceLoader.load(Greeter.class, null, "META-INF/spi/com/github/paganini2008/devtools/");
		assertEquals("hello", loader.getFirst().greet());
		// an explicit path does not fall back to the default one
		ServiceLoader<Greeter> missing = ServiceLoader.load(Greeter.class, null, "META-INF/spi/elsewhere");
		assertThrows(ServiceNotFoundException.class, () -> missing.getFirst());
	}

	@Test
	public void testChangedGlobalPathFallsBackToDefault() {
		ServiceLoader.lookupPath("META-INF/spi/elsewhere");
		assertEquals("hello", ServiceLoader.load(Greeter.class).getFirst().greet());
	}

	@Test
	public void testLoggingDoesNotAffectOtherServices() throws Exception {
		Class.forName("com.github.paganini2008.devtools.logging.LogFactory");
		com.github.paganini2008.devtools.logging.LogFactory.getLog(ServiceLoaderTest.class);
		assertEquals("hello", ServiceLoader.load(Greeter.class, null).getFirst().greet());
	}

	@Test
	public void testErrors() {
		assertThrows(ServiceNotFoundException.class, () -> ServiceLoader.load(String.class));
		assertThrows(ServiceNotFoundException.class, () -> ServiceLoader.load(null));
		assertThrows(ServiceNotFoundException.class, () -> ServiceLoader.load(WrongService.class));
		assertThrows(ServiceNotFoundException.class, () -> ServiceLoader.load(MissingService.class));
		ServiceLoader<UnconfiguredService> loader = ServiceLoader.load(UnconfiguredService.class);
		assertThrows(ServiceNotFoundException.class, () -> loader.getFirst());
		// constructor arguments do not match
		ServiceLoader<Greeter> greeters = ServiceLoader.load(Greeter.class);
		assertThrows(ServiceNotFoundException.class, () -> greeters.get("named"));
	}

}
