# devtools-objectpool

<code>devtools-objectpool</code>  is a high performance implementation of  object pool, including a generic object pool and jdbc connection pool.

## Install

``` xml
<dependency>
   <groupId>com.github.paganini2008</groupId>
   <artifactId>devtools-objectpool</artifactId>
   <version>2.0.5</version>
</dependency>
```

## Core API

* ObjectPool
* ObjectFactory
* Jdk14ObjectPool
* GenericObjectPool
* ConnectionPool
* GenericDataSource

## Quick Start
#### Object Pool
1. Define a <code>ObjectPool</code> Object

``` java
  GenericObjectPool objectPool = new GenericObjectPool(new ResourceFactory());
  objectPool.setMaxPoolSize(10);
  objectPool.setMaxIdleSize(3);
```

2. Test it

``` java
        Executor executor = Executors.newFixedThreadPool(10);
		AtomicInteger counter = new AtomicInteger();
		for (final int i : Sequence.forEach(0, 10000)) {
			executor.execute(() -> {
				counter.incrementAndGet();
				Resource resource = null;
				try {
					resource = (Resource) objectPool.borrowObject();
					ThreadUtils.randomSleep(1000L);
					System.out.println(resource.doSomething(i) + " :: busySize: " + objectPool.getBusySize() + ", idleSize: " + objectPool.getIdleSize());
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
```





#### DataSource

1. Define a <code>DataSource</code> Object

``` java
public static void main(String[] args) throws Exception {
		GenericDataSource dataSource = new GenericDataSource();
		dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
		dataSource.setJdbcUrl(
				"jdbc:mysql://localhost:3306/test?userUnicode=true&characterEncoding=UTF8&useSSL=false&serverTimezone=UTC&autoReconnect=true&zeroDateTimeBehavior=convertToNull");
		dataSource.setUser("fengy");
		dataSource.setPassword("123456");
	}
```

2. Test it

``` java
        Executor executor = Executors.newFixedThreadPool(10);
		for (final int i : Sequence.forEach(0, 10000)) {
			executor.execute(() -> {
				Connection connection = null;
				Tuple tuple = null;
				try {
					connection = dataSource.getConnection();
					tuple = JdbcUtils.fetchOne(connection, "select * from tb_demo where level=? limit 1",
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
```

