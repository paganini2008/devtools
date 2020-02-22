package com.github.paganini2008.devtools.beans.streaming.examples;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Predicate;

import com.github.paganini2008.devtools.RandomUtils;
import com.github.paganini2008.devtools.beans.streaming.Group;
import com.github.paganini2008.devtools.beans.streaming.Orders;
import com.github.paganini2008.devtools.beans.streaming.Restrictions;
import com.github.paganini2008.devtools.beans.streaming.Select;
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
 * @version 1.0
 */
public class TestBeanStreaming {

	private static final List<Product> products = new ArrayList<Product>();

	static {
		String[] users = new String[] { "Petter", "Jack", "Tony" };
		String[] locations = new String[] { "London", "NewYork", "Tokyo", "HongKong", "Paris" };
		for (int i = 0; i < 10000; i++) {
			Product product = new Product();
			product.setCreated(DateUtils.valueOf(2020, RandomUtils.randomInt(1, 12), RandomUtils.randomInt(1, 28)));
			product.setExpired(DateUtils.addDays(product.getCreated(), 90));
			product.setExport(RandomUtils.randomBoolean());
			product.setFreight(BigDecimal.valueOf(RandomUtils.randomFloat(10, 100)).setScale(2, RoundingMode.HALF_UP));
			product.setId(i + 1);
			product.setLocation(RandomUtils.randomChoice(locations));
			product.setName("Product-" + product.getId());
			product.setNumber(RandomUtils.randomLong(1, 1000));
			product.setPrice(RandomUtils.randomFloat(10, 1000, 2));
			product.setSales(BigInteger.valueOf(RandomUtils.randomLong(10000, 100000)));
			product.setStyle(Style.values()[RandomUtils.randomInt(0, 2)]);
			product.setAdmin(new Product.Admin(RandomUtils.randomChoice(users), "123456"));
			products.add(product);
		}
	}

	/**
	 * <pre>
	 * 	 select * from Product where created<= now() and admin.username='Petter'
	 * </pre>
	 */
	public static void test1() {
		Predicate<Product> predicate = Restrictions.lte("created", new Date());
		predicate = predicate.and(Restrictions.eq("admin.username", "Petter"));
		Select.from(products).filter(predicate).list().forEach(product -> {
			System.out.println(product);
		});
	}

	/**
	 * <pre>
	 * 	  select location,max(price) as maxPrice, min(price) as minPrice,avg(freight) as avgFreight,sum(sales) as sumSales from Product group by location
	 * </pre>
	 */
	public static void test2() {
		Select.from(products).groupBy("location", String.class).setTransformer(new View<Product>() {
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
	 * 	 select location,style,max(price) as maxPrice, min(price) as minPrice,avg(freight) as avgFreight,sum(sales) as sumSales from Product group by location,style having avg(freight) > 55
	 * </pre>
	 */
	public static void test3() {
		Select.from(products).groupBy("location", String.class).groupBy("style", Product.Style.class).having(group -> {
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
	 * 	 select name,price from Product order by price desc limit 100
	 * </pre>
	 */
	public static void test4() {
		Select.from(products).orderBy(Orders.descending("price", Float.class)).list(100).forEach(product -> {
			System.out.println("Name: " + product.getName() + ", Price: " + product.getPrice());
		});
	}

	public static void main(String[] args) {
		test4();
	}

}
