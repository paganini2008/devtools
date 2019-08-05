package com.github.paganini2008.devtools.beans.streaming;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.github.paganini2008.devtools.RandomUtils;
import com.github.paganini2008.devtools.beans.Tuple;
import com.github.paganini2008.devtools.beans.streaming.Product.Sort;
import com.github.paganini2008.devtools.date.DateUtils;

/**
 * 
 * Tester
 * 
 * @author Fred Feng
 * @revised 2019-07
 * @version 1.0
 */
public class Tester {

	public static void main(String[] args) {
		String[] location = new String[] { "上海", "北京", "天津", "青岛", "黑龙江" };
		List<Product> products = new ArrayList<Product>();
		for (int i = 0; i < 1000; i++) {
			Product product = new Product();
			product.setCreated(DateUtils.valueOf(2019, RandomUtils.randomInt(1, 12), RandomUtils.randomInt(1, 28)));
			product.setExpired(DateUtils.addDays(product.getCreated(), 90));
			product.setExport(RandomUtils.randomBoolean());
			product.setFreight(BigDecimal.valueOf(RandomUtils.randomFloat(10, 100)).setScale(2, RoundingMode.HALF_UP));
			product.setId(i + 1);
			product.setLocation(RandomUtils.randomChoice(location));
			product.setName("产品-" + product.getId());
			product.setNumber(RandomUtils.randomLong(1, 1000));
			product.setPrice(RandomUtils.randomFloat(10, 1000, 2));
			product.setSales(BigInteger.valueOf(RandomUtils.randomLong(10000, 100000)));
			product.setSort(Sort.values()[RandomUtils.randomInt(0, 2)]);
			products.add(product);
		}
		System.out.println(products);
		com.github.paganini2008.devtools.beans.streaming.Sort<Product> order = Orders.descending("created", Date.class);
		List<Tuple> list = Select.from(products).filter(Restrictions.gt("created", new Date())).groupBy("location", String.class)
				.groupBy("export", Boolean.class).groupBy("sort", Sort.class).orderBy(Orders.groupAscending("location", String.class))
				.setTransformer(new View<Product>() {
					protected void setAttributes(Tuple m, Group<Product> group) {
						m.put("maxPrice", group.max("price", Float.class));
						m.put("minPrice", group.min("price", Float.class));
						m.put("avgFreight", group.avg("freight"));
						m.put("sumSales", group.sum("sales"));
					}

				}).list();
		for (Map<String, Object> p : list) {
			System.out.println(p);
		}
		System.out.println("-------------------------------------------------");
		List<Tuple> list2 = Select.from(products).filter(Restrictions.gt("created", new Date())).groupBy("location", String.class)
				.groupBy("export", Boolean.class).orderBy(Orders.groupAscending("location", String.class))
				.setTransformer(new View<Product>() {
					protected void setAttributes(Tuple m, Group<Product> group) {
						m.put("maxPrice", group.max("price", Float.class));
						m.put("minPrice", group.min("price", Float.class));
						m.put("avgFreight", group.avg("freight"));
						m.put("sumSales", group.sum("sales"));
					}

				}).list();
		for (Map<String, Object> p : list2) {
			System.out.println(p);
		}
		System.out.println("-------------------------------------------------");
		List<Tuple> list3 = Select.from(products).filter(Restrictions.gt("created", new Date())).groupBy("location", String.class).orderBy(Orders.groupAscending("location", String.class))
				.setTransformer(new View<Product>() {
					protected void setAttributes(Tuple m, Group<Product> group) {
						m.put("maxPrice", group.max("price", Float.class));
						m.put("minPrice", group.min("price", Float.class));
						m.put("avgFreight", group.avg("freight"));
						m.put("sumSales", group.sum("sales"));
					}

				}).list();
		for (Map<String, Object> p : list3) {
			System.out.println(p);
		}
	}

}
