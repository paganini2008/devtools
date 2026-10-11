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
package com.github.paganini2008.devtools.objectpool;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;

/**
 * GenericObjectPoolTest
 * 
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class GenericObjectPoolTest extends AbstractObjectPoolTest {

	@Override
	protected ObjectPool newPool(ObjectFactory objectFactory) {
		return new GenericObjectPool(objectFactory);
	}

	@Test
	public void constructor_rejectsNullFactory() {
		assertThrows(IllegalArgumentException.class, () -> new GenericObjectPool(null));
	}

	@Test
	public void borrowWithTimeUnit() throws Exception {
		GenericObjectPool genericPool = (GenericObjectPool) pool;
		Object item = genericPool.borrowObject(1, TimeUnit.SECONDS);
		genericPool.givebackObject(item);
		assertEquals(1, genericPool.getIdleSize());
		genericPool.setMaxPoolSize(1);
		genericPool.borrowObject(1, TimeUnit.SECONDS);
		assertThrows(PooledObjectException.class, () -> genericPool.borrowObject(10, TimeUnit.MILLISECONDS));
	}

}
