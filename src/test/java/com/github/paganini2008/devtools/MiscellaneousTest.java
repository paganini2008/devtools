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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.management.ManagementFactory;
import java.util.concurrent.atomic.AtomicInteger;

import javax.tools.JavaFileObject;

import org.junit.jupiter.api.Test;

/**
 * MiscellaneousTest
 *
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class MiscellaneousTest {

	@Test
	public void testEnvPid() {
		String name = ManagementFactory.getRuntimeMXBean().getName();
		assertEquals(Integer.parseInt(name.split("@")[0]), Env.getPid());
		assertTrue(Env.getPid() > 0);
	}

	@Test
	public void testKeyVal() throws Exception {
		KeyVal<String, Integer> kv = KeyVal.of("a", 1);
		assertEquals("a", kv.getKey());
		assertEquals(Integer.valueOf(1), kv.getValue());
		assertEquals("key=a, value=1", kv.toString());
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try (ObjectOutputStream out = new ObjectOutputStream(bos)) {
			out.writeObject(kv);
		}
		try (ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bos.toByteArray()))) {
			assertEquals("key=a, value=1", in.readObject().toString());
		}
	}

	@Test
	public void testThreadSafeSupplierIsLazyAndMemoized() {
		AtomicInteger calls = new AtomicInteger();
		ThreadSafeSupplier<String> supplier = ThreadSafeSupplier.of(() -> "v" + calls.incrementAndGet());
		assertEquals(0, calls.get());
		assertEquals("v1", supplier.get());
		assertEquals("v1", supplier.get());
		assertEquals(1, calls.get());
	}

	@Test
	public void testJavaStringSource() {
		JavaStringSource source = new JavaStringSource("com.example.Foo", "class Foo {}");
		assertEquals("com.example.Foo", source.getName());
		assertEquals("class Foo {}", source.getCode());
		assertEquals("class Foo {}", source.getCharContent(true));
		assertEquals(JavaFileObject.Kind.SOURCE, source.getKind());
		assertEquals("string:///com/example/Foo.java", source.toUri().toString());
	}

	@Test
	public void testObtainableDefaults() {
		Obtainable<String> obtainable = () -> "x";
		assertEquals("x", obtainable.obtain());
		assertEquals(0, obtainable.retries());
		assertEquals(1000L, obtainable.retryingInterval());
		assertNull(obtainable.defaultValue());
		obtainable.exceptionCaught(new RuntimeException(), 1);
	}

	@Test
	public void testObserverDefaultCompareTo() {
		Observer a = (ob, arg) -> {
		};
		Observer b = (ob, arg) -> {
		};
		assertEquals(0, a.compareTo(b));
	}

}
