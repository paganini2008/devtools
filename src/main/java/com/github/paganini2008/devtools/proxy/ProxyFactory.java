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

/**
 * 
 * ProxyFactory
 * 
 * Creates proxies that route method calls through an {@link Aspect}.
 * 
 * @author Fred Feng
 * @date 2020-08-01
 *
 * @since 2.0.1
 */
public interface ProxyFactory {

	/**
	 * Creates a proxy implementing all interfaces of the target.
	 */
	default Object getProxy(Object target, Aspect aspect) {
		return getProxy(target, aspect, target.getClass().getInterfaces());
	}

	/**
	 * Creates a proxy implementing the given interfaces.
	 */
	Object getProxy(Object target, Aspect aspect, Class<?>... interfaces);

	/**
	 * Returns the default, JDK dynamic proxy based, factory.
	 */
	static ProxyFactory getDefault() {
		return JdkProxyFactory.getInstance();
	}
}
