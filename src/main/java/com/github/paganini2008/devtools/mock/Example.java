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
package com.github.paganini2008.devtools.mock;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * Example
 * <p>
 * Configures the random value of a String field. 'value' names a string
 * supplier registered in the {@link MockContext} (defaults to the field name).
 *
 * @author Fred Feng
 * @date 2019-05-29
 *
 * @since 2.0.4
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD })
public @interface Example {

	/**
	 * Length of the random part.
	 */
	int length() default 32;

	/**
	 * Prepended to the value.
	 */
	String prefix() default "";

	/**
	 * Appended to the value.
	 */
	String suffix() default "";

	/**
	 * Whether the random part may contain digits.
	 */
	boolean digit() default true;

	/**
	 * Whether the random part may contain lower case letters.
	 */
	boolean lowerCaseLetter() default true;

	/**
	 * Whether the random part may contain upper case letters.
	 */
	boolean upperCaseLetter() default true;

	/**
	 * If not empty, the random part only consists of these characters.
	 */
	String style() default "";

	/**
	 * The example name of the string supplier in the context, defaults to the field name.
	 */
	String value() default "";
}
