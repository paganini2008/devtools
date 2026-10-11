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
package com.github.paganini2008.devtools.io.filter;

import java.io.File;

/**
 * A filter which compares the last modified time of a file with a given timestamp.
 * 
 * @author Fred Feng
 * @date 2019-05-29
 * @since 2.0.1
 */
public class LastModifiedFileFilter extends LogicalFileFilter {

	private final long lastModified;
	private final Operator operator;

	public LastModifiedFileFilter(long lastModified, Operator operator) {
		this.lastModified = lastModified;
		this.operator = operator;
	}

	public boolean accept(File file) {
		final long l = file.lastModified();
		switch (operator) {
		case LT:
			return l < lastModified;
		case GT:
			return l > lastModified;
		case LTE:
			return l <= lastModified;
		case GTE:
			return l >= lastModified;
		case EQ:
			return l == lastModified;
		case NE:
			return l!= lastModified;
		}
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Accepts files whose last modified time is equal to the given value.
	 */
	public static LastModifiedFileFilter eq(long lastModified) {
		return new LastModifiedFileFilter(lastModified, Operator.EQ);
	}

	/**
	 * Accepts files whose last modified time is not equal to the given value.
	 */
	public static LastModifiedFileFilter ne(long lastModified) {
		return new LastModifiedFileFilter(lastModified, Operator.NE);
	}

	/**
	 * Accepts files whose last modified time is greater than or equal to the given value.
	 */
	public static LastModifiedFileFilter gte(long lastModified) {
		return new LastModifiedFileFilter(lastModified, Operator.GTE);
	}

	/**
	 * Accepts files whose last modified time is greater than the given value.
	 */
	public static LastModifiedFileFilter gt(long lastModified) {
		return new LastModifiedFileFilter(lastModified, Operator.GT);
	}

	/**
	 * Accepts files whose last modified time is less than or equal to the given value.
	 */
	public static LastModifiedFileFilter lte(long lastModified) {
		return new LastModifiedFileFilter(lastModified, Operator.LTE);
	}

	/**
	 * Accepts files whose last modified time is less than the given value.
	 */
	public static LastModifiedFileFilter lt(long lastModified) {
		return new LastModifiedFileFilter(lastModified, Operator.LT);
	}

}
