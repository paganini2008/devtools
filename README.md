# Devtools Series
*A Java language basic tool library, which provides a large amounts of useful and cool utility methods and APIs to optimize your code, making your code more efficient and elegant.*

### Install

``` xml
<dependency>
	 <groupId>com.github.paganini2008</groupId>
	 <artifactId>devtools</artifactId>
	 <version>2.0.5</version>
</dependency>
```

### Compatibility
Jdk1.8  (or later)

### Modules
* <code>devtools-lang</code>
* <code>devtools-objectpool</code>
* <code>devtools-cron4j</code>
* <code>devtools-beans-streaming</code>
* <code>devtools-db4j</code>



## 1. devtools-lang

<code>devtools-lang</code> provides enrich utility method to manipulate Java core classes. It optimize operations about Basic data type, Collection, Date, IO, Reflection and other class libraries. It also provides high level APIs to do  programming about Multithreading, JDBC, Logger .etc,  which can help developer decrease repeat basic code and focus on business logical developing, making project code have more readability and maintainability

#### Install
``` xml
<dependency>
    <groupId>com.github.paganini2008</groupId>
    <artifactId>devtools-lang</artifactId>
    <version>2.0.5</version>
</dependency>
```

#### Core API

**Utility class for java.lang.* **

* StringUtils
* ObjectUtils
* ArrayUtils
* NumericUtils
* CharsetUtils
* ClassUtils

**Utility class  for primitive data types **

* Booleans
* Chars
* Bytes
* Shorts
* Ints
* Longs
* Floats
* Doubles

**Utility class for mathematics**

* BigDecimalUtils
* BigIntegerUtils

**Utility class for java.lang.Random**

* RandomUtils
* RandomStringUtils
* RandomDateUtils

**Utility class for java.util.Date**

* CalendarUtils
* DateUtils
* LocalDateUtils
* LocalDateTimeUtils
* LocalTimeUtils

**Utility class for java.util.collection**

* CollectionUtils
* ListUtils
* SetUtils
* MapUtils
* LruMap 
* LruList
* LruSet
* MultiDequeMap
* MultiKeyMap
* MultiListMap
* MultiMappedMap
* MultiSetMap
* CaseInsensitiveMap
* CamelCaseInsensitiveMap

**Utility class for java.io.*  **

* IOUtils
* FileUtils
* PathUtils
* TreeUtils
* PropertiesUtils
* ResourceUtils
* ImageUtils
* SerializationUtils
* RecursiveDirectoryWalker
* ForkJoinDirectoryWalker
* FileMonitor
* FileComparator
* LogicalFileFilter

**Utility class for java.lang.Thread**

* ExecutorUtils
* ThreadsUtils
* Clock
* GenericThreadPool
* Jdk14ThreadPool
* ConcurrentTimer
* ThreadFactoryBuilder
* AtomicIntegerSequence
* AtomicLongSequence
* Latch

**Utility class for java.lang.reflect.* **

* ConstructorUtils
* FieldUtils
* MethodUtils
* BeanUtils
* PropertyUtils
* EqualsBuilder
* HashCodeBuilder
* ToStringBuilder

**Utility class for data type conversion operations **

* ConvertUtils
* StandardTypeConverter

**Utility class for java.sql.* **

* JdbcUtils
* ResultSetSlice
* ConnectionFactory
* DataSourceFactory
* PageableQuery
* Cursor
* JdbcDumpTemplate

**Utility class for java.util.logging.* **

* Log
* LogFactory




##  2. devtools-objectpool

<code>devtools-objectpool</code>  is a high performance implementation of  object pool, including a generic object pool and jdbc connection pool.

#### Install

``` xml
<dependency>
   <groupId>com.github.paganini2008</groupId>
   <artifactId>devtools-objectpool</artifactId>
   <version>2.0.5</version>
</dependency>
```

#### Core API

* ObjectPool
* ObjectFactory
* Jdk14ObjectPool
* GenericObjectPool
* ConnectionPool
* GenericDataSource



## 3. devtools-cron4j

<code>devtools-cron4j</code> is a small and practical Java scheduling toolkit that provides:

* Defining cron expressions by object-oriented way 
* Providing strong cron expression parser
* Executing job class at a certain time by multiple provided <code>TaskExecutor</code>
* Easily embedding any system in a lightweight way without relying on external components



#### Install
``` xml
<dependency>
    <groupId>com.github.paganini2008</groupId>
    <artifactId>devtools-cron4j</artifactId>
    <version>2.0.5</version>
</dependency>
```

#### Core API

* CronExpression
* ThreadPoolTaskExecutor
* TimerTaskExecutor
* ClockTaskExecutor



## 4. devtools-beans-streaming

<code>devtools-beans-streaming</code> provide a approach like SQL query  to tackle a Java object list. For example, searching specific attribute value, counting or aggregating some objects by attribute value   (Similar to LINQ in C#)


#### Install

``` xml
<dependency>
	<groupId>com.github.paganini2008</groupId>
	<artifactId>devtools-beans-streaming</artifactId>
	<version>2.0.5</version>
</dependency>
```

#### Core API

* Selector
* Restriction
* Group
* Sorter
* BeanSorter


## 5. devtools-db4j

<code>devtools-db4j</code> is a simple and practical JDBC tool library, which provides varied high level API to operation database, thereby increasing developing efficiency

#### Install

``` xml
<dependency>
	<groupId>com.github.paganini2008</groupId>
	<artifactId>devtools-db4j</artifactId>
	<version>2.0.5</version>
</dependency>
```

#### Core API

* SqlPlus
* SqlRunner
* ParsedSqlRunner
* TypeHandler
* TypeHandlerRegistry
* ResultSetExtractor
* RowMapper




