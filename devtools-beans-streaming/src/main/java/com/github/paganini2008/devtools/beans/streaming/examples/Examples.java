/**
* Copyright 2017-2021 Fred Feng (paganini.fy@gmail.com)

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
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Predicate;

import com.github.paganini2008.devtools.RandomDateUtils;
import com.github.paganini2008.devtools.RandomUtils;
import com.github.paganini2008.devtools.beans.streaming.Group;
import com.github.paganini2008.devtools.beans.streaming.Orders;
import com.github.paganini2008.devtools.beans.streaming.Restrictions;
import com.github.paganini2008.devtools.beans.streaming.Selector;
import com.github.paganini2008.devtools.beans.streaming.Sorter;
import com.github.paganini2008.devtools.beans.streaming.View;
import com.github.paganini2008.devtools.beans.streaming.examples.Product.Style;
import com.github.paganini2008.devtools.collection.Tuple;
import com.github.paganini2008.devtools.date.DateUtils;

/**
 * 
 * TestBeanStreaming
 * 
 * @author Fred Feng
 * 
 * @since 2.0.1
 */
public abstract class Examples {

	private static final List<Product> products = new ArrayList<Product>();

	static {
		String[] users = new String[] { "Petter", "Jack", "Tony" };
		String[] locations = new String[] { "London", "NewYork", "Tokyo", "HongKong", "Paris" };
		for (int i = 0; i < 10000; i++) {
			Product product = new Product();
			product.setCreated(RandomDateUtils.randomDate(2021));
			product.setExpired(DateUtils.addDayOfMonth(product.getCreated(), 90));
			product.setExport(RandomUtils.randomBoolean());
			product.setFreight(BigDecimal.valueOf(RandomUtils.randomFloat(10, 100)).setScale(2, RoundingMode.HALF_UP));
			product.setId(i + 1);
			product.setLocation(RandomUtils.randomChoice(locations));
			product.setName("Product-" + product.getId());
			product.setNumber(RandomUtils.randomLong(1, 1000));
			product.setPrice(RandomUtils.randomFloat(10, 1000));
			product.setSales(BigInteger.valueOf(RandomUtils.randomLong(10000, 100000)));
			product.setStyle(Style.values()[RandomUtils.randomInt(0, 2)]);
			product.setSalesman(new Product.Salesman(RandomUtils.randomChoice(users), "123456"));
			products.add(product);
		}
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
			System.out.println("Name: " + product.getName() + ", Price: " + product.getPrice() + ", Freight: " + product.getFreight());
		});
	}

	public static void main(String[] args) {
		test4();
	}

}
