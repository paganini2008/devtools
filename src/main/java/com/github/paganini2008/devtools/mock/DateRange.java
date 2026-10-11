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
 * DateRange
 * <p>
 * Restricts the random date of a Date/LocalDate/LocalDateTime field. An empty
 * 'to' means now.
 *
 * @author Fred Feng
 * @date 2019-05-29
 *
 * @since 2.0.4
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD })
public @interface DateRange {

	/**
	 * The earliest date, in the given format.
	 */
	String from() default "1970-01-01";

	/**
	 * The latest date, in the given format. Empty means now.
	 */
	String to() default "";

	/**
	 * The format of from and to.
	 */
	String format() default "yyyy-MM-dd";
	
	/**
	 * The example name of the supplier in the context, defaults to the field name.
	 */
	String value() default "";

}
