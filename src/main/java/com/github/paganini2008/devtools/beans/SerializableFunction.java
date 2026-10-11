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

import java.io.Serializable;
import java.util.function.Function;

/**
 * A serializable {@link Function}. A getter method reference assigned to it
 * (e.g. {@code User::getName}) carries the name of the referenced method, from
 * which {@link LambdaUtils#getPropertyName(SerializableFunction)} resolves the
 * property name. This makes it possible to refer to bean properties in a type
 * safe way instead of by string names.
 * 
 * <pre>
 * SerializableFunction&lt;User, String&gt; getter = User::getName;
 * LambdaUtils.getPropertyName(getter) = "name"
 * getter.apply(user)                  = user.getName()
 * </pre>
 * 
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
@FunctionalInterface
public interface SerializableFunction<T, R> extends Function<T, R>, Serializable {

}
