/**
* Copyright 2018-2021 Fred Feng (paganini.fy@gmail.com)

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
package com.github.paganini2008.devtools.comparator;

import java.util.Comparator;

import com.github.paganini2008.devtools.Assert;

/**
 * ReverseComparator
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class ReverseComparator<T> extends AbstractComparator<T> {

	public ReverseComparator(Comparator<T> delegate) {
		Assert.isNull(delegate, "Delegate comparator is missing.");
		this.delegate = delegate;
	}

	private final Comparator<T> delegate;

	public int compare(T left, T right) {
		return delegate.compare(right, left);
	}

	public String toString() {
		return super.toString() + "[" + delegate.toString() + "]";
	}

}
