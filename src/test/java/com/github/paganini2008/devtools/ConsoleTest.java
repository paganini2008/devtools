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

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Vector;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * ConsoleTest
 *
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class ConsoleTest {

	private static final String NL = System.getProperty("line.separator");

	private PrintStream originalOut;
	private PrintStream originalErr;
	private ByteArrayOutputStream out;
	private ByteArrayOutputStream err;

	@BeforeEach
	public void redirect() throws Exception {
		originalOut = System.out;
		originalErr = System.err;
		out = new ByteArrayOutputStream();
		err = new ByteArrayOutputStream();
		System.setOut(new PrintStream(out, true, "UTF-8"));
		System.setErr(new PrintStream(err, true, "UTF-8"));
	}

	@AfterEach
	public void restore() {
		System.setOut(originalOut);
		System.setErr(originalErr);
	}

	@Test
	public void testLog() throws Exception {
		Console.log("hello");
		Console.log("  ");
		Console.log((String) null);
		Console.logf("%s-%d", "a", 1);
		Console.logf(null);
		Console.log(new Vector<String>(Arrays.asList("e1")).elements());
		Console.log(Arrays.asList("i1").iterator());
		Console.log(Arrays.asList("it1"));
		Console.log(new String[] { "arr1" });
		Map<String, Integer> map = new LinkedHashMap<String, Integer>();
		map.put("k", 1);
		Console.log(map);
		Console.log((Iterable<String>) null);
		assertEquals(String.join(NL, "hello", "a-1", "e1", "i1", "it1", "arr1", "k=1") + NL, out.toString("UTF-8"));
		assertEquals("", err.toString("UTF-8"));
	}

	@Test
	public void testErr() throws Exception {
		Console.err("oops");
		Console.err("");
		Console.errf("%s!", "x");
		Console.errf(" ");
		Console.err(Collections.enumeration(Arrays.asList("e1")));
		Console.err(Arrays.asList("i1").iterator());
		Console.err(Arrays.asList("it1"));
		Console.err(new Integer[] { 2 });
		Console.err(Collections.singletonMap("k", "v"));
		Console.err((Map<String, String>) null);
		assertEquals(String.join(NL, "oops", "x!", "e1", "i1", "it1", "2", "k=v") + NL, err.toString("UTF-8"));
		assertEquals("", out.toString("UTF-8"));
	}

}
