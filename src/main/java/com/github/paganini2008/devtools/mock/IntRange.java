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
 * IntRange
 * <p>
 * Restricts the random value of an int field to [from, to).
 *
 * @author Fred Feng
 * @date 2021-01-18
 *
 * @since 2.0.4
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD })
public @interface IntRange {

	/**
	 * Inclusive lower bound.
	 */
	int from() default Integer.MIN_VALUE;

	/**
	 * Exclusive upper bound.
	 */
	int to() default Integer.MAX_VALUE;
	
	/**
	 * The example name of the supplier in the context, defaults to the field name.
	 */
	String value() default "";

}
