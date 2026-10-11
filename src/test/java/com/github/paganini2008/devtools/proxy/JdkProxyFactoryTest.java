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
package com.github.paganini2008.devtools.proxy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * JdkProxyFactoryTest
 *
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class JdkProxyFactoryTest {

	public interface Calculator {

		int add(int a, int b);

		String name();

		boolean ready();

		long big();

		double ratio();

		void fail();
	}

	public interface Primitives {

		byte b();

		short s();

		char c();

		float f();
	}

	public static class SimpleCalculator implements Calculator, Primitives {

		public int add(int a, int b) {
			return a + b;
		}

		public String name() {
			return "simple";
		}

		public boolean ready() {
			return true;
		}

		public long big() {
			return Long.MAX_VALUE;
		}

		public double ratio() {
			return 0.5D;
		}

		public void fail() {
			throw new IllegalStateException("failed");
		}

		public byte b() {
			return 1;
		}

		public short s() {
			return 2;
		}

		public char c() {
			return 'c';
		}

		public float f() {
			return 1.5F;
		}
	}

	@Test
	public void testDefaultAspectDelegates() {
		Calculator calc = (Calculator) JdkProxyFactory.getInstance().getProxy(new SimpleCalculator(), new Aspect() {
		});
		assertEquals(3, calc.add(1, 2));
		assertEquals("simple", calc.name());
		assertTrue(calc instanceof Primitives);
		assertEquals((byte) 1, ((Primitives) calc).b());
		assertSame(JdkProxyFactory.getInstance(), ProxyFactory.getDefault());
	}

	@Test
	public void testAspectCallbacksAndExceptionHandling() {
		List<String> log = new ArrayList<String>();
		List<Throwable> errors = new ArrayList<Throwable>();
		Aspect aspect = new Aspect() {

			public boolean beforeCall(Object target, Method method, Object[] args) {
				log.add("before:" + method.getName());
				return true;
			}

			public boolean afterCall(Object target, Method method, Object[] args) {
				log.add("after:" + method.getName());
				return true;
			}

			public void catchException(Object target, Method method, Object[] args, Throwable e) {
				errors.add(e);
			}
		};
		Calculator calc = (Calculator) ProxyFactory.getDefault().getProxy(new SimpleCalculator(), aspect, Calculator.class);
		assertEquals(5, calc.add(2, 3));
		calc.fail();
		assertEquals(Arrays.asList("before:add", "after:add", "before:fail", "after:fail"), log);
		assertEquals(1, errors.size());
		assertTrue(errors.get(0) instanceof IllegalStateException);
	}

	@Test
	public void testSkippedCallReturnsDefaultsForPrimitives() {
		Aspect skip = new Aspect() {
			public boolean beforeCall(Object target, Method method, Object[] args) {
				return false;
			}
		};
		SimpleCalculator target = new SimpleCalculator();
		Calculator calc = (Calculator) JdkProxyFactory.getInstance().getProxy(target, skip);
		assertEquals(0, calc.add(1, 2));
		assertFalse(calc.ready());
		assertEquals(0L, calc.big());
		assertEquals(0D, calc.ratio());
		assertNull(calc.name());
		calc.fail();
		Primitives p = (Primitives) calc;
		assertEquals((byte) 0, p.b());
		assertEquals((short) 0, p.s());
		assertEquals((char) 0, p.c());
		assertEquals(0F, p.f());
	}

	@Test
	public void testAfterCallFalseDiscardsResult() {
		Aspect discard = new Aspect() {
			public boolean afterCall(Object target, Method method, Object[] args) {
				return false;
			}
		};
		Calculator calc = (Calculator) JdkProxyFactory.getInstance().getProxy(new SimpleCalculator(), discard);
		assertEquals(0, calc.add(1, 2));
		assertNull(calc.name());
	}

	@Test
	public void testCustomCallThrowingInvocationTargetException() {
		List<Throwable> errors = new ArrayList<Throwable>();
		Aspect aspect = new Aspect() {
			public Object call(Object target, Method method, Object[] args) throws Throwable {
				return method.invoke(target, args);
			}

			public void catchException(Object target, Method method, Object[] args, Throwable e) {
				errors.add(e);
			}
		};
		Calculator calc = (Calculator) JdkProxyFactory.getInstance().getProxy(new SimpleCalculator(), aspect);
		calc.fail();
		assertTrue(errors.get(0) instanceof IllegalStateException);
	}

	@Test
	public void testOtherExceptionsPropagate() {
		Aspect aspect = new Aspect() {
			public Object call(Object target, Method method, Object[] args) throws Throwable {
				throw new UnsupportedOperationException("nope");
			}
		};
		Calculator calc = (Calculator) JdkProxyFactory.getInstance().getProxy(new SimpleCalculator(), aspect);
		assertThrows(UnsupportedOperationException.class, () -> calc.add(1, 2));
	}

}
