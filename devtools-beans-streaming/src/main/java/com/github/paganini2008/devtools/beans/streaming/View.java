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
package com.github.paganini2008.devtools.beans.streaming;

import com.github.paganini2008.devtools.collection.Tuple;

/**
 * 
 * View
 * 
 * @author Fred Feng
 * @version 1.0
 */
public abstract class View<E> implements Transformer<Group<E>, Tuple> {

	public Tuple transfer(Group<E> group) {
		Tuple tuple = Tuple.newTuple();
		tuple.append(group.getAttributes());
		setAttributes(tuple, group);
		return tuple;
	}

	protected void setAttributes(Tuple tuple, Group<E> group) {
	}
}
