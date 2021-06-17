# devtools-lang
<code>devtools-lang</code> is a basic toolkit in <code>devtools series</code>. It encapsulates the basic data type, collection, date, IO, multithreading, JDBC, log and other common class libraries in JDK. <code>devtools-lang</code> toolkit provides utility methods and APIs with a higher level of encapsulation, which aims to significantly improve the development efficiency of developers, optimize the code style and maintainability.

### Install
``` xml
		<dependency>
			<groupId>com.github.paganini2008</groupId>
			<artifactId>devtools-lang</artifactId>
			<version>2.0.1</version>
		</dependency>
```

### Compatibility
Jdk1.8+

### Common Tools
<code>StringUtils</code>
<code>ObjectUtils</code>
<code>ArrayUtils</code>
<code>NumericUtils</code>
<code>RandomUtils</code>
<code>RandomStringUtils</code>
<code>ClassUtils</code>

**Common tool API of basic data type:**
1. Booleans
2. Chars
3. Bytes
4. Shorts
5. <code>Ints</code>
6. Longs
7. Floats
8. Doubles

**Common utility method and API for numerical calculation:**
1. <code>BigDecimalUtils</code>
2. <code>BigIntegerUtils</code>

**Common utility method and API for date processing:**

1. <code>CalendarUtils</code>
2. <code>DateUtils</code>
3. <code>LocalDateUtils</code>

**Common utility method and API for collection processing:**
1. <code>CollectionUtils</code>
2. <code>ListUtils</code>
3. <code>SetUtils</code>
4. <code>MapUtils</code>
5. <code>LruMap</code>
6. <code>LruList</code>
7. <code>LruSet</code>

**Common utility method and API for IO operations:**

1. <code>IOUtils</code>
2. <code>FileUtils</code>
3. <code>PropertiesUtils</code>
4. <code>ResourceUtils</code>
5. <code>ImageUtils</code>
6. <code>SerializationUtils</code>
7. <code>DirectoryWalker</code>
8. <code>FileMonitor</code>
9. <code>FileComparator</code>

**Common utility method and API for multithreading:**
1. <code>ExecutorUtils</code>
2. <code>ThreadsUtils</code>
3. <code>ThreadPool</code>
4. <code>ThreadFactoryBuilder</code>
5. <code>AtomicIntegerSequence</code>
6. <code>AtomicLongSequence</code>
7. <code>Latch</code>

**Common utility method and API for reflection:**
1. <code>ConstructorUtils</code>
2. <code>FieldUtils</code>
3. <code>MethodUtils</code>

**Common utility method and API for Bean Operations:**
1. BeanUtils
2. PropertyUtils
3. EqualsBuilder
4. HashCodeBuilder
5. ToStringBuilder

**Common utility method and API for data type conversion:**
1. <code>ConvertUtils</code>
2. <code>TypeConverter</code>

**Common utility method and API for JDBC:**
1. <code>JdbcUtils</code>
2. <code>ResultSetSlice</code>
3. <code>PageableQuery</code>

**Common utility method and API for log:**
1. Log
2. <code>LogFactory</code>
