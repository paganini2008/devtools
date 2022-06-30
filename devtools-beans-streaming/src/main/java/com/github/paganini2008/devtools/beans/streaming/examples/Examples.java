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
package com.github.paganini2008.devtools.beans.streaming.examples;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.function.Predicate;

import com.github.paganini2008.devtools.RandomUtils;
import com.github.paganini2008.devtools.beans.streaming.Group;
import com.github.paganini2008.devtools.beans.streaming.Orders;
import com.github.paganini2008.devtools.beans.streaming.Restrictions;
import com.github.paganini2008.devtools.beans.streaming.Selector;
import com.github.paganini2008.devtools.beans.streaming.Sorter;
import com.github.paganini2008.devtools.beans.streaming.View;
import com.github.paganini2008.devtools.collection.Tuple;
import com.github.paganini2008.devtools.mock.BeanMocker;
import com.github.paganini2008.devtools.mock.MockContext;

/**
 * 
 * TestBeanStreaming
 * 
 * @author Fred Feng
 * 
 * @since 2.0.1
 */
public abstract class Examples {

	private static final List<Product> products;

	static {
		String[] users = new String[] { "Petter", "Jack", "Tony" };
		String[] locations = new String[] { "London", "NewYork", "Tokyo", "HongKong", "Paris" };
		MockContext mc = new MockContext();
		mc.setStringSupplier("name", () -> {
			return "Product_" + RandomUtils.randomInt(1, 100000);
		});
		mc.setStringSupplier("location", () -> {
			return RandomUtils.randomChoice(locations);
		});
		mc.setStringSupplier("username", () -> {
			return RandomUtils.randomChoice(users);
		});
		products = BeanMocker.mockBeans(10000, Product.class, mc);
	}

	/**
	 * <pre>
	 * Equivalent to: select * from Product where location='London'
	 * </pre>
	 */
	public static void test() {
		Predicate<Product> predicate = Restrictions.eq("location", "London");
		Selector.from(products).filter(predicate).list().forEach(product -> {
			System.out.println(product);
		});
	}

	/**
	 * <pre>
	 *Equivalent to: select * from Product where created<= now() and salesman.name='Petter'
	 * </pre>
	 */
	public static void test1() {
		Predicate<Product> predicate = Restrictions.lte("created", new Date());
		predicate = predicate.and(Restrictions.eq("salesman.name", "Petter"));
		Selector.from(products).filter(predicate).list().forEach(product -> {
			System.out.println(product);
		});
	}

	/**
	 * <pre>
	 *Equivalent to: select location,max(price) as maxPrice, min(price) as minPrice,avg(freight) as avgFreight,sum(sales) as sumSales from Product group by location
	 * </pre>
	 */
	public static void test2() {
		Selector.from(products).groupBy("location", String.class).setTransformer(new View<Product>() {
			protected void setAttributes(Tuple tuple, Group<Product> group) {
				tuple.set("maxPrice", group.max("price", Float.class));
				tuple.set("minPrice", group.min("price", Float.class));
				tuple.set("avgFreight", group.avg("freight"));
				tuple.set("sumSales", group.sum("sales"));
			}
		}).list().forEach(tuple -> {
			System.out.println(tuple);
		});
	}

	/**
	 * <pre>
	 *Equivalent to: select location,style,max(price) as maxPrice, min(price) as minPrice,avg(freight) as avgFreight,sum(sales) as sumSales from Product group by location,style having avg(freight) > 55
	 * </pre>
	 */
	public static void test3() {
		Selector.from(products).groupBy("location", String.class).groupBy("style", Product.Style.class).having(group -> {
			return group.avg("freight").compareTo(BigDecimal.valueOf(55)) > 0;
		}).setTransformer(new View<Product>() {
			protected void setAttributes(Tuple tuple, Group<Product> group) {
				tuple.set("maxPrice", group.max("price", Float.class));
				tuple.set("minPrice", group.min("price", Float.class));
				tuple.set("avgFreight", group.avg("freight"));
				tuple.set("sumSales", group.sum("sales"));
			}
		}).list().forEach(tuple -> {
			System.out.println(tuple);
		});
	}

	/**
	 * <pre>
	 *Equivalent to: select name,price from Product order by price desc limit 100
	 * </pre>
	 */
	public static void test4() {
		Sorter<Product> sorter = Orders.descending("price", BigDecimal.class);
		Selector.from(products).orderBy(sorter).list(100).forEach(product -> {
			System.out.println(product);
		});
	}

	public static void main(String[] args) {
		test3();
	}

}
