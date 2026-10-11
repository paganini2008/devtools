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
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.Serializable;
import java.lang.invoke.MethodHandleInfo;
import java.lang.invoke.SerializedLambda;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

/**
 * LambdaUtilsTest
 * 
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class LambdaUtilsTest {

	public interface Named {

		default String getTitle() {
			return "title";
		}
	}

	public static class Base {

		public String getBaseName() {
			return "base";
		}
	}

	public static class Bean extends Base implements Named {

		public String getName() {
			return "name";
		}

		public boolean isActive() {
			return true;
		}

		public Boolean isEnabled() {
			return Boolean.TRUE;
		}

		public Boolean getVisible() {
			return Boolean.FALSE;
		}

		public String getURL() {
			return "url";
		}

		public String getUrlPath() {
			return "path";
		}

		public int getX() {
			return 1;
		}

		public String doSomething() {
			return "x";
		}

		public String get() {
			return "get";
		}

		public boolean is() {
			return false;
		}
	}

	public static String getStaticLabel(Bean bean) {
		return "label";
	}

	public String getBoundLabel(Bean bean) {
		return "bound";
	}

	/**
	 * Not a lambda, but provides its own serialized form.
	 */
	static class FakeLambda implements SerializableFunction<Bean, Object> {

		private static final long serialVersionUID = 1L;
		final AtomicInteger calls = new AtomicInteger();
		private final Object replacement;

		FakeLambda(Object replacement) {
			this.replacement = replacement;
		}

		@Override
		public Object apply(Bean t) {
			return null;
		}

		@SuppressWarnings("unused")
		private Object writeReplace() {
			calls.incrementAndGet();
			if (replacement instanceof RuntimeException) {
				throw (RuntimeException) replacement;
			}
			return replacement;
		}
	}

	static class NotALambda implements SerializableFunction<Bean, Object> {

		private static final long serialVersionUID = 1L;

		@Override
		public Object apply(Bean t) {
			return null;
		}
	}

	private static SerializedLambda serializedLambda(String implClass, String implMethodName) {
		return new SerializedLambda(LambdaUtilsTest.class, "com/github/paganini2008/devtools/beans/SerializableFunction", "apply",
				"(Ljava/lang/Object;)Ljava/lang/Object;", MethodHandleInfo.REF_invokeVirtual, implClass, implMethodName,
				"()Ljava/lang/String;", "(Lcom/github/paganini2008/devtools/beans/LambdaUtilsTest$Bean;)Ljava/lang/Object;", new Object[0]);
	}

	private static <T, R> String name(SerializableFunction<T, R> getter) {
		return LambdaUtils.getPropertyName(getter);
	}

	@Test
	public void getPrefix() {
		assertEquals("name", name(Bean::getName));
		assertEquals("visible", name(Bean::getVisible));
		assertEquals("x", name(Bean::getX));
		assertEquals("urlPath", name(Bean::getUrlPath));
	}

	@Test
	public void isPrefix_forBooleanAndBooleanWrapper() {
		assertEquals("active", name(Bean::isActive));
		// only the method name is checked, so a Boolean returning 'is' method is accepted too
		assertEquals("enabled", name(Bean::isEnabled));
	}

	@Test
	public void upperCaseAbbreviationIsKeptLikeIntrospector() {
		assertEquals("URL", name(Bean::getURL));
		assertEquals(java.beans.Introspector.decapitalize("URL"), name(Bean::getURL));
	}

	@Test
	public void inheritedAndInterfaceDefaultGetters() {
		assertEquals("baseName", name(Bean::getBaseName));
		assertEquals("title", name(Bean::getTitle));
		assertEquals("title", name(Named::getTitle));
		assertTrue(LambdaUtils.getImplClass((SerializableFunction<Bean, String>) Bean::getBaseName).isAssignableFrom(Bean.class));
		assertEquals(Named.class, LambdaUtils.getImplClass((SerializableFunction<Named, String>) Named::getTitle));
	}

	@Test
	public void staticAndBoundMethodReferences() {
		assertEquals("staticLabel", name(LambdaUtilsTest::getStaticLabel));
		assertEquals("boundLabel", name(new LambdaUtilsTest()::getBoundLabel));
		assertEquals(LambdaUtilsTest.class, LambdaUtils.getImplClass((SerializableFunction<Bean, String>) LambdaUtilsTest::getStaticLabel));
	}

	@Test
	public void ordinaryLambdaIsRejected() {
		IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> name((Bean b) -> b.getName()));
		assertTrue(e.getMessage().contains("User::getName"), e.getMessage());
		assertTrue(e.getMessage().contains("lambda"), e.getMessage());
	}

	@Test
	public void nonGetterMethodReferenceIsRejected() {
		IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> name(String::length));
		assertTrue(e.getMessage().contains("User::getName"), e.getMessage());
		assertTrue(e.getMessage().contains("'length'"), e.getMessage());
		assertThrows(IllegalArgumentException.class, () -> name(Bean::doSomething));
		assertThrows(IllegalArgumentException.class, () -> name(Bean::get));
		assertThrows(IllegalArgumentException.class, () -> name(Bean::is));
	}

	@Test
	public void nullIsRejected() {
		IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> LambdaUtils.getPropertyName(null));
		assertTrue(e.getMessage().contains("User::getName"));
		assertThrows(IllegalArgumentException.class, () -> LambdaUtils.getSerializedLambda(null));
		assertThrows(IllegalArgumentException.class, () -> LambdaUtils.getImplClass(null));
	}

	@Test
	public void nonLambdaImplementationIsRejected() {
		IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> LambdaUtils.getPropertyName(new NotALambda()));
		assertTrue(e.getMessage().contains("Not a serializable lambda"));
		e = assertThrows(IllegalArgumentException.class, () -> LambdaUtils.getSerializedLambda(new FakeLambda("not a SerializedLambda")));
		assertTrue(e.getMessage().contains("Not a serializable lambda"));
		IllegalStateException failure = new IllegalStateException("boom");
		e = assertThrows(IllegalArgumentException.class, () -> LambdaUtils.getSerializedLambda(new FakeLambda(failure)));
		assertSame(failure, e.getCause());
	}

	@Test
	public void serializedLambdaOfAnyKindOfSerializableLambda() {
		SerializedLambda sl = LambdaUtils.getSerializedLambda((SerializableFunction<Bean, String>) Bean::getName);
		assertEquals("getName", sl.getImplMethodName());
		assertEquals("com/github/paganini2008/devtools/beans/LambdaUtilsTest$Bean", sl.getImplClass());
		Runnable r = (Runnable & Serializable) () -> {
		};
		assertTrue(LambdaUtils.getSerializedLambda((Serializable) r).getImplMethodName().startsWith("lambda$"));
	}

	@Test
	public void implClassThatCannotBeLoaded() {
		IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
				() -> LambdaUtils.getImplClass(new FakeLambda(serializedLambda("no/such/Clazz", "getName"))));
		assertTrue(e.getMessage().contains("no.such.Clazz"));
	}

	@Test
	public void resolvedNameIsCachedPerLambdaClass() {
		FakeLambda fake = new FakeLambda(serializedLambda("com/github/paganini2008/devtools/beans/LambdaUtilsTest$Bean", "getCached"));
		assertEquals("cached", LambdaUtils.getPropertyName(fake));
		assertEquals("cached", LambdaUtils.getPropertyName(fake));
		assertEquals("cached", LambdaUtils.getPropertyName(new FakeLambda(null)));
		assertEquals(1, fake.calls.get());
	}

	@Test
	@Timeout(10)
	public void concurrentResolution() throws Exception {
		final int threads = 16;
		ExecutorService executor = Executors.newFixedThreadPool(threads);
		try {
			CountDownLatch start = new CountDownLatch(1);
			List<Future<String>> futures = new ArrayList<Future<String>>();
			for (int i = 0; i < threads * 4; i++) {
				final int n = i;
				futures.add(executor.submit(() -> {
					start.await();
					return n % 2 == 0 ? name(Bean::getUrlPath) : name(Bean::isActive);
				}));
			}
			start.countDown();
			for (int i = 0; i < futures.size(); i++) {
				assertEquals(i % 2 == 0 ? "urlPath" : "active", futures.get(i).get(5, TimeUnit.SECONDS));
			}
		} finally {
			executor.shutdownNow();
			assertTrue(executor.awaitTermination(5, TimeUnit.SECONDS));
		}
	}

	@Test
	public void serializableFunctionIsAFunction() {
		SerializableFunction<Bean, String> f = Bean::getName;
		assertEquals("name", f.apply(new Bean()));
		assertEquals("name!", f.andThen(s -> s + "!").apply(new Bean()));
		assertTrue(f instanceof Serializable);
	}

}
