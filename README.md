# Good Java Programmer need Good Toolkit
## How to write Java code more simply and efficiently?
### You need have a set of tools are really suitable for you.

#### Latest Version: 2.0-RC1
### 1. devtools-lang
***For writing less code.***
```xml
    <dependency>
        <groupId>com.github.paganini2008</groupId>
        <artifactId>devtools-lang</artifactId>
        <version>${devtools.version}</version>
    </dependency>
```

### 2. devtools-objectpool
***Contains object pool and jdbc connection pool. No extra functions, just a DataSource.***
```xml
    <dependency>
        <groupId>com.github.paganini2008</groupId>
        <artifactId>devtools-objectpool</artifactId>
        <version>${devtools.version}</version>
    </dependency>
```

### 3. devtools-beans-streaming
***To operate Java Collection Framework like use sql. That's LINQ in Java!***
#### Java Code
```java
        /**
         * <pre>
         * 	 select * from Product where created<= now() and salesman.name='Petter'
         * </pre>
         */
        public static void test1() {
            Predicate<Product> predicate = Restrictions.lte("created", new Date());
            predicate = predicate.and(Restrictions.eq("salesman.name", "Petter"));
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
```
#### Maven
```xml
   <dependency>
      <groupId>com.github.paganini2008</groupId>
      <artifactId>devtools-beans-streaming</artifactId>
      <version>${devtools.version}</version>
   </dependency>
```

### 4. devtools-scheduler
***'0 0/5 12,18 * * ?' What's this?  Doesn't you think the cron expression is anti-humanistic? You may change another style.***
#### Java Code
```java
        // */5 * * * * ?
        public static CronExpression getCron1() {
            return CronBuilder.everySecond(5);
        }
    
        // 0 26,29,33 * * * ?
        public static CronExpression getCron2() {
            return CronBuilder.minute(26).andMinute(29).andMinute(33);
        }
    
        // 0 15 12 * * ?
        public static CronExpression getCron3() {
            return CronBuilder.everyDay().hour(12).minute(15);
        }
    
        // 0 15 10 ? * MON-FRI
        public static CronExpression getCron4() {
            return CronBuilder.everyWeek().Mon().toFri().at(10, 15, 0);
        }
    
        // 0 10 23 ? * 6#3
        public static CronExpression getCron5() {
            return CronBuilder.everyMonth().week(3).Fri().at(23, 10);
        }
    
        // 0 10,20,30 12 ? 3,4 5L 2002-2005
        public static CronExpression getCron6() {
            return CronBuilder.year(2020).toYear(2025).Mar().andApr().lastWeek().Thur().hour(12).minute(10).andMinute(20).andMinute(30);
        }
```
#### Maven
```xml
    <dependency>
        <groupId>com.github.paganini2008</groupId>
        <artifactId>devtools-scheduler</artifactId>
        <version>${devtools.version}</version>
    </dependency>
```

### 5. devtools-db4j
***To use JDBC more easily.***
```xml
    <dependency>
      <groupId>com.github.paganini2008</groupId>
      <artifactId>devtools-db4j</artifactId>
      <version>${devtools.version}</version>
    </dependency>
```
