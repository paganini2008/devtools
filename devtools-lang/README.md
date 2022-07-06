# devtools-lang

<code>devtools-lang</code> provides enrich utility method to manipulate Java core classes. It optimize operations about Basic data type, Collection, Date, IO, Reflection and other class libraries. It also provides high level APIs to do  programming about Multithreading, JDBC, Logger .etc,  which can help developer decrease repeat basic code and focus on business logical developing, making project code have more readability and maintainability

## Compatibility

Jdk1.8+

## Install

``` xml
<dependency>
    <groupId>com.github.paganini2008</groupId>
    <artifactId>devtools-lang</artifactId>
    <version>2.0.5</version>
</dependency>
```

## Core API

Utility class for <code>java.lang.*</code>

* StringUtils
* ObjectUtils
* ArrayUtils
* NumericUtils
* CharsetUtils
* ClassUtils

Utility class  for primitive data types

* Booleans
* Chars
* Bytes
* Shorts
* Ints
* Longs
* Floats
* Doubles

Utility class for mathematics

* BigDecimalUtils
* BigIntegerUtils

Utility class for <code>java.lang.Random</code>

* RandomUtils
* RandomStringUtils
* RandomDateUtils

Utility class for <code>java.util.Date</code>

* CalendarUtils
* DateUtils
* LocalDateUtils
* LocalDateTimeUtils
* LocalTimeUtils

Utility class for <code>java.util.collection</code>

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

Utility class for <code>java.io.*</code>

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

Utility class for <code>java.lang.Thread</code>

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

Utility class for <code>java.lang.reflect.*</code>

* ConstructorUtils
* FieldUtils
* MethodUtils
* BeanUtils
* PropertyUtils
* EqualsBuilder
* HashCodeBuilder
* ToStringBuilder

Utility class for data type conversion operations

* ConvertUtils
* StandardTypeConverter

Utility class for <code>java.sql.*</code>

* JdbcUtils
* ResultSetSlice
* ConnectionFactory
* DataSourceFactory
* PageableQuery
* Cursor
* JdbcDumpTemplate

Utility class for <code> java.util.logging.*</code>

* Log
* LogFactory
