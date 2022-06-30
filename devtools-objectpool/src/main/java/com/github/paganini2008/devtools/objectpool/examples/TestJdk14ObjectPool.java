/**
* Copyright 2017-2022 Fred Feng (paganini.fy@gmail.com)

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
package com.github.paganini2008.devtools.objectpool.examples;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import com.github.paganini2008.devtools.Sequence;
import com.github.paganini2008.devtools.multithreads.ExecutorUtils;
import com.github.paganini2008.devtools.multithreads.ThreadUtils;
import com.github.paganini2008.devtools.objectpool.GenericObjectPool;
import com.github.paganini2008.devtools.objectpool.ObjectFactory;

/**
 * 
 * TestJdk14ObjectPool
 *
 * @author Fred Feng
 * @since 2.0.1
 */
public class TestJdk14ObjectPool {

	public static class Resource {

		private final int id;

		public Resource(int id) {
			this.id = id;
		}

		public String doSomething(int i) {
			return ThreadUtils.currentThreadName() + " do something: " + i;
		}

		public String toString() {
			return "ID: " + id;
		}

	}

	public static class ResourceFactory implements ObjectFactory {

		private static final AtomicInteger seq = new AtomicInteger(0);

		public Object createObject() throws Exception {
			return new Resource(seq.incrementAndGet());
		}

		public void destroyObject(Object o) throws Exception {
			System.out.println("Destory: " + o);
		}

	}

	public static void main(String[] args) throws Exception {
		GenericObjectPool objectPool = new GenericObjectPool(new ResourceFactory());
		objectPool.setMaxPoolSize(10);
		objectPool.setMaxIdleSize(3);
		Executor executor = Executors.newFixedThreadPool(10);
		AtomicInteger counter = new AtomicInteger();
		for (final int i : Sequence.forEach(0, 10000)) {
			executor.execute(() -> {
				counter.incrementAndGet();
				Resource resource = null;
				try {
					resource = (Resource) objectPool.borrowObject();
					ThreadUtils.randomSleep(1000L);
					System.out.println(resource.doSomething(i) + " :: busySize: " + objectPool.getBusySize() + ", idleSize: "
							+ objectPool.getIdleSize());
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					try {
						objectPool.givebackObject(resource);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		}
		System.in.read();
		System.out.println(counter);
		objectPool.close();
		ExecutorUtils.gracefulShutdown(executor, 60000);
		System.out.println("Done.");
	}

}
