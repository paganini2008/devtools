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
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * ExceptionsTest
 * 
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class ExceptionsTest {

	@Test
	public void testBooleanFormatException() {
		assertEquals("Invalid format for input: 'yes'.", new BooleanFormatException("yes").getMessage());
		assertEquals("Invalid format for input: 'x'.", new BooleanFormatException('x').getMessage());
		assertTrue(new BooleanFormatException("a") instanceof IllegalArgumentException);
	}

	@Test
	public void testCharValueOverflowException() {
		assertEquals("msg", new CharValueOverflowException("msg").getMessage());
		assertEquals("Out of range [0,65535] for input: 70000", CharValueOverflowException.causedByChar(70000).getMessage());
		assertEquals("Out of range [0,65535] for input: 65", CharValueOverflowException.causedByChar('A').getMessage());
	}

	@Test
	public void testCauseExceptions() {
		Throwable cause = new RuntimeException("cause");
		assertEquals("m", new ComparableException("m").getMessage());
		assertSame(cause, new ComparableException("m", cause).getCause());
		assertSame(cause, new ComparableException(cause).getCause());
		assertEquals("m", new ServiceNotFoundException("m").getMessage());
		assertSame(cause, new ServiceNotFoundException("m", cause).getCause());
		assertSame(cause, new ServiceNotFoundException(cause).getCause());
		assertEquals("key", new MissingKeyException("key").getMessage());
		assertSame(cause, new MissingKeyException("key", cause).getCause());
		assertTrue(new MissingKeyException("key") instanceof IllegalStateException);
	}

	@Test
	public void testSimpleExceptions() {
		assertEquals("m", new MismatchedTypeException("m").getMessage());
		assertNull(new NotImplementedException().getMessage());
		assertEquals("m", new NotImplementedException("m").getMessage());
		assertTrue(new NotImplementedException() instanceof UnsupportedOperationException);
		assertNull(new NumberOverflowException().getMessage());
		assertEquals("128", new NumberOverflowException(128).getMessage());
		assertNull(new UnreachableCodeException().getMessage());
		assertEquals("m", new UnreachableCodeException("m").getMessage());
	}

}
