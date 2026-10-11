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
package com.github.paganini2008.devtools.reflection;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * ReflectionFixtures
 * 
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class ReflectionFixtures {

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.FIELD, ElementType.METHOD })
	public @interface Marker {
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.FIELD, ElementType.METHOD })
	public @interface Other {
	}

	public interface Empty {
	}

	public interface Constants {
		String GREETING = "hello";
	}

	public interface Named {
		String name();
	}

	public static class Base implements Named {
		public static String staticValue = "base-static";
		private String secret = "base-secret";
		public int visible = 1;

		@Override
		public String name() {
			return "base";
		}

		protected String hidden() {
			return "hidden";
		}

		@Marker
		public String marked() {
			return "base-marked";
		}
	}

	public static class Child extends Base implements Empty, Constants {
		private static int counter = 5;
		@Marker
		private String title = "child";
		@Other
		private long number = 7L;
		private final String fixed = "fixed";

		public Child() {
		}

		public Child(String title) {
			this.title = title;
		}

		public Child(Object title) {
			this.title = "object:" + title;
		}

		private Child(int number) {
			this.number = number;
		}

		public Child(String title, long number) {
			this.title = title;
			this.number = number;
		}

		public String echo(Object o) {
			return "object:" + o;
		}

		public String echo(String s) {
			return "string:" + s;
		}

		public String echo(CharSequence s) {
			return "chars:" + s;
		}

		public long add(int a, long b) {
			return a + b;
		}

		private String privateMethod() {
			return "private";
		}

		@Override
		public String name() {
			return "child";
		}

		@Other
		public String other() {
			return "other";
		}

		public static String staticEcho(String s) {
			return "static:" + s;
		}

		public static int count() {
			return counter;
		}

		public void fail() {
			throw new IllegalStateException("fail");
		}

		public String getTitle() {
			return title;
		}

		public long getNumber() {
			return number;
		}
	}

}
