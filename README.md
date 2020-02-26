# Good Programmers need Good Toolkits
## How to write Java code more simply and efficiently?
### You need have a set of tools are really suitable for you.

### 1. devtools-lang
***For writing less code, there are many useful codes waiting for you to explore.***
#### Maven
```xml
    <dependency>
        <groupId>com.github.paganini2008</groupId>
        <artifactId>devtools-lang</artifactId>
        <version>${devtools.version}</version>
    </dependency>
```

### 2. devtools-objectpool
***Contains object pool and jdbc connection pool. No extra functions.***
#### Java Code
##### 1. Object Pool
```java
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
```
##### 2. DataSource
```java
        public static void main(String[] args) throws Exception {
            GenericDataSource dataSource = new GenericDataSource();
            dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
            dataSource.setJdbcUrl(
                    "jdbc:mysql://localhost:3306/test?userUnicode=true&characterEncoding=UTF8&useSSL=false&serverTimezone=UTC&autoReconnect=true&zeroDateTimeBehavior=convertToNull");
            dataSource.setUser("your name");
            dataSource.setPassword("your password");
            Executor executor = Executors.newFixedThreadPool(10);
            for (final int i : Sequence.forEach(0, 10000)) {
                executor.execute(() -> {
                    Connection connection = null;
                    Tuple tuple = null;
                    try {
                        connection = dataSource.getConnection();
                        tuple = JdbcUtils.fetchOne(connection, "select * from mec_area where level=? limit 1",
                                new Object[] { RandomUtils.randomInt(1, 4) });
                        System.out.println(tuple);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    } finally {
                        JdbcUtils.closeQuietly(connection);
                    }
                });
            }
            System.in.read();
            // Sql Query Summary
		    Map<String, QuerySpan> results = dataSource.getStatisticsResult("dd/MM/yyyy");
            System.out.println(results);
            dataSource.close();
            ExecutorUtils.gracefulShutdown(executor, 60000);
            System.out.println("TestDataSource.main()");
        }
```
#### Maven
```xml
    <dependency>
        <groupId>com.github.paganini2008</groupId>
        <artifactId>devtools-objectpool</artifactId>
        <version>${devtools.version}</version>
    </dependency>
```

### 3. devtools-beans-streaming
***To operate Java Collection Framework like using sql query. Is it cool? That's LINQ in Java!***
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
***As a lazy man, to use JDBC more easily.***
#### Java Code
```java
	   /**
		 * Insert, Update, Delete in a transaction
		 * 
		 * @throws SQLException
		 */
		public static void testUpdateInTransaction() throws SQLException {
			String driverClassName = "com.mysql.cj.jdbc.Driver";
			String jdbcUrl = "jdbc:mysql://localhost:3306/test?userUnicode=true&characterEncoding=UTF8&useSSL=false&serverTimezone=UTC&autoReconnect=true&zeroDateTimeBehavior=convertToNull";
			String user = "your_user";
			String password = "your_password";
			SqlPlus sqlPlus = new SqlPlus(driverClassName, jdbcUrl, user, password);
			Transaction transaction = null;
			try {
				transaction = sqlPlus.beginTransaction(); // Open transaction
				GeneratedKey generatedKey = GeneratedKey.forNames("id");

				Point point = new Point();// Pojo
				point.setUsername("tester-12");
				point.setPoints(100);
				point.setTag(5);
				point.setDate(new Date());
				int effectedRows = transaction.update(
						"insert into tb_point (username,points,tag,last_modified) values ({username},{points},{tag},{date})",
						new BeanPropertySqlParameter(point), generatedKey); // bean mapping
				System.out.println("EffectedRows: " + effectedRows);
				System.out.println("Added id: " + generatedKey.getKey());

				Map<String, Object> parameterMap = new HashMap<String, Object>();
				parameterMap.put("points", 10);
				parameterMap.put("username", "tester-12");
				effectedRows = transaction.update("update tb_point set points=points+{points} where username={username}",
						new MapSqlParameter(parameterMap)); // Map mapping
				System.out.println("EffectedRows: " + effectedRows);

				effectedRows = transaction.update("delete from tb_point where username!={0}", new ArraySqlParameter("tester-12")); // Array
																																	// mapping
				System.out.println("EffectedRows: " + effectedRows);
				transaction.commit();
			} catch (SQLException e) {
				transaction.rollback();
				throw e;
			} finally {
				transaction.close(); // Transaction end
			}
		}

		/**
		 * Query for List
		 * 
		 * @throws SQLException
		 */
		public static void testQuery() throws SQLException {
			String driverClassName = "com.mysql.cj.jdbc.Driver";
			String jdbcUrl = "jdbc:mysql://localhost:3306/test?userUnicode=true&characterEncoding=UTF8&useSSL=false&serverTimezone=UTC&autoReconnect=true&zeroDateTimeBehavior=convertToNull";
			String user = "your_user";
			String password = "your_password";
			SqlPlus sqlPlus = new SqlPlus(driverClassName, jdbcUrl, user, password);
			List<Tuple> dataList = sqlPlus.queryForList("select * from tb_point", new Object[0]);
			dataList.forEach(tuple -> {
				System.out.println(tuple);
			});

		}

		/**
		 * Pageable Query
		 * 
		 * @throws SQLException
		 */
		public static void testPageableQuery() throws SQLException {
			String driverClassName = "com.mysql.cj.jdbc.Driver";
			String jdbcUrl = "jdbc:mysql://localhost:3306/test?userUnicode=true&characterEncoding=UTF8&useSSL=false&serverTimezone=UTC&autoReconnect=true&zeroDateTimeBehavior=convertToNull";
			String user = "your_user";
			String password = "your_password";
			SqlPlus sqlPlus = new SqlPlus(driverClassName, jdbcUrl, user, password);
			PageableQuery<Tuple> pageableQuery = sqlPlus.queryForPage("select * from tb_point where points>{0}", new Object[] { 10 });
			for (PageResponse<Tuple> pageResponse : pageableQuery.forEach(1, 10)) {// Page start from 1
				System.out.println("Page: " + pageResponse.getPageNumber());
				pageResponse.getContent().forEach(tuple -> {
					System.out.println(tuple); // Iterator each record
				});
			}
		}
```
#### Maven
```xml
    <dependency>
      <groupId>com.github.paganini2008</groupId>
      <artifactId>devtools-db4j</artifactId>
      <version>${devtools.version}</version>
    </dependency>
```

## At last, you can also use all in your application:
```xml
    <dependency>
      <groupId>com.github.paganini2008</groupId>
      <artifactId>devtools</artifactId>
      <version>${devtools.version}</version>
    </dependency>
```
#### Latest Version: 2.0-RC1
