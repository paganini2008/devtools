# Java LINQ Tool -- <code>devtools-beans-streaming</code>

<code>devtools-beans-streaming</code> package is one of <code>devtools</code> series toolkits. It is a solution to query or aggregate object list (or <code>pojo</code> list). It provides LINQ function similar to C#

### Install：
``` xml
		<dependency>
			<groupId>com.github.paganini2008</groupId>
			<artifactId>devtools-beans-streaming</artifactId>
			<version>2.0.1</version>
		</dependency>
```
### Compatibility
jdk1.8 (or later)

### Example
Here is a POJO

``` java
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
		HARD, SOFT;
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

**Demo1:**
``` java
		Predicate<Product> predicate = Restrictions.eq("location", "London");
		Selector.from(products).filter(predicate).list().forEach(product -> {
			System.out.println(product);
		});
// Equivalent to: select * from Product where location='London'
```

**Demo2:**
``` java
		Predicate<Product> predicate = Restrictions.lte("created", new Date());
		predicate = predicate.and(Restrictions.eq("salesman.name", "Petter"));
		Selector.from(products).filter(predicate).list().forEach(product -> {
			System.out.println(product);
		});
// select * from Product where created<= now() and salesman.name='Petter'
```
**Demo3:**
``` java
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
// Equivalent to: select location,max(price) as maxPrice, min(price) as minPrice,avg(freight) as avgFreight,sum(sales) as sumSales from Product group by location
```
**Demo4:**
``` java
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
// Equivalent to: select location,style,max(price) as maxPrice, min(price) as minPrice,avg(freight) as avgFreight,sum(sales) as sumSales from Product group by location,style having avg(freight) > 55
```

**Demo5:**
``` java
		Sorter<Product> sorter = Orders.descending("price", BigDecimal.class);
		Selector.from(products).orderBy(sorter).list(100).forEach(product -> {
			System.out.println("Name: " + product.getName() + ", Price: " + product.getPrice() + ", Freight: " + product.getFreight());
		});
// Equivalent to: select name,price from Product order by price desc limit 100
```



