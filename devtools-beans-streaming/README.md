# devtools-beans-streaming
<code>devtools-beans-streaming</code> provide a approach like SQL query  to tackle a Java object list. For example, searching specific attribute value, counting or aggregating some objects by attribute value   (Similar to LINQ in C#)

## Install：
``` xml
<dependency>
	<groupId>com.github.paganini2008</groupId>
	<artifactId>devtools-beans-streaming</artifactId>
	<version>2.0.5</version>
</dependency>
```
## Compatibility
jdk1.8 (or later)

## Core API

* Selector
* Restriction
* Group
* Sorter
* BeanSorter

## Quick Start

Define a POJO

``` java

/**
 * 
 * This is a POJO
 *
 * @author Fred Feng
 * @version 2.0.5
 */
@Getter
@Setter
@ToString
public class Product {

	private int id;
	private String name;
	private String location;
	private Date created;
	private Date expired;
	private Float price;
	private BigInteger sales;
	private boolean export;
	private Long number;
	private BigDecimal freight;
	private Style style;
	private Salesman salesman;

	public static enum Style {
		HARD, SOFT, Random;
	}

	@Getter
	@Setter
	@ToString
	public static class Salesman {

		private String name;
		private String password;

		public Salesman(String name, String password) {
			this.name = name;
			this.password = password;
		}

	}

}
```



Test Code:

``` java
/**
	 * Equivalent to:
	 * 
	 * <pre>
	 *   select * 
	 *     from Product 
	 *   where location='London'
	 * </pre>
	 */
	public static void test() {
		Predicate<Product> predicate = Restrictions.eq("location", "London");
		Selector.from(products).filter(predicate).list().forEach(product -> {
			System.out.println(product);
		});
	}

	/**
	 * Equivalent to: 
	 * 
	 * <pre>
	 *   select * 
	 *     from Product 
	 *   where created<= now() 
	 *     and salesman.name='Petter'
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
	 * Equivalent to:
	 * 
	 * <pre>
	 *    select 
	 *      location,
	 *      max(price) as maxPrice, 
	 *      min(price) as minPrice,
	 *      avg(freight) as avgFreight,
	 *      sum(sales) as sumSales 
	 *    from Product 
	 *      group by 
	 *        location
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
	 * Equivalent to:
	 * 
	 * <pre>
	 *    select location,
	 *           style,max(price) as maxPrice, 
	 *           min(price) as minPrice,
	 *           avg(freight) as avgFreight,
	 *           sum(sales) as sumSales 
	 *    from Product 
	 *           group by 
	 *             location,
	 *             style
	 *    having avg(freight) > 55
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
	 * Equivalent to:
	 * 
	 * <pre>
	 *   select 
	 *      name,
	 *      price,
	 *      freight
	 *   from Product 
	 *      order by 
	 *        price desc,
	 *        freight asc
	 *   limit 100
	 * </pre>
	 */
	public static void test4() {
		Sorter<Product> sorter = Orders.descending("price", Float.class);
		sorter = sorter.ascending("freight", BigDecimal.class);
		Selector.from(products).orderBy(sorter).list(100).forEach(product -> {
			System.out.printf("Name: %s, Price: %f, Freight: %f \n", product.getName(), product.getPrice(), product.getFreight());
		});
	}

	public static void main(String[] args) {
		test1();
	}
```





