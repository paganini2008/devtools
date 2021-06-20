/**
* Copyright 2021 Fred Feng (paganini.fy@gmail.com)

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
import java.util.List;

import com.github.paganini2008.devtools.ArrayUtils;
import com.github.paganini2008.devtools.collection.ListUtils;

/**
 * AbstractComparator
 * 
 * @author Fred Feng
 * @version 1.0
 */
public abstract class AbstractComparator<T> implements Comparator<T> {

	public T[] sort(T[] args) {
		if (ArrayUtils.isNotEmpty(args)) {
			ArrayUtils.sort(args, this);
		}
		return args;
	}

	public List<T> sort(List<T> args) {
		if (ListUtils.isNotEmpty(args)) {
			ListUtils.sort(args, this);
		}
		return args;
	}

	public Comparator<T> reverse() {
		return new ReverseComparator<T>(this);
	}

	public String toString() {
		return "[Comparator] " + getClass().getName();
	}

}
