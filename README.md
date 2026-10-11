# devtools

**One jar. Zero dependencies. Every utility your Java 8 project keeps rewriting.**

devtools is an all-in-one, general-purpose Java utility library. It covers strings, collections, IO, dates, reflection, type conversion, concurrency, caching, JDBC and pooling, HTTP, codecs, validation and more, in a single jar with **no third-party dependencies**. It targets **JDK 8+** and keeps its public API backward compatible, so it can be dropped into long-lived enterprise systems without dependency conflicts.

[![Maven Central](https://img.shields.io/maven-central/v/com.github.paganini2008/devtools-lang.svg)](https://central.sonatype.com/artifact/com.github.paganini2008/devtools-lang)
[![JDK](https://img.shields.io/badge/JDK-8%2B-blue.svg)](#requirements)
[![Dependencies](https://img.shields.io/badge/dependencies-zero-brightgreen.svg)](#requirements)
[![Line coverage](https://img.shields.io/badge/line%20coverage-95%25%2B-brightgreen.svg)](#contributing)
[![License](https://img.shields.io/badge/license-Apache%202.0-blue.svg)](LICENSE)

---

## Features

- **Zero dependencies**: a single jar on top of the JDK, so it adds no version conflicts to an existing project.
- **JDK 8 compatible**: compiled with `--release 8` and runs on any later JDK.
- **Backward compatible**: public APIs are deprecated rather than removed or changed.
- **Null-safe**: utility methods accept `null` and return sensible defaults instead of throwing.
- **Thread-safe utilities**: stateless helpers can be shared freely; stateful components document their thread-safety.
- **Well tested**: more than 2,000 unit tests and over 95% line coverage.

## What's Covered

All classes live under `com.github.paganini2008.devtools`.

| Area | Main classes | Covers |
|---|---|---|
| Strings & text | `StringUtils`, `CaseFormats`, `EscapeUtils`, `RegexUtils`, `RandomStringUtils` | Blank checks, padding, splitting, templates with `{}` / `${}` placeholders, camel/snake/kebab case conversion, HTML/XML/JSON/CSV/Java escaping, regex helpers |
| Objects & arrays | `ObjectUtils`, `ArrayUtils`, `ClassUtils`, `Comparables`, `Assert` | Null-safe equals/toString/defaults, array set operations, slicing and searching, class and type helpers, argument checks |
| Primitives & math | `Ints`, `Longs`, `Shorts`, `Bytes`, `Chars`, `Floats`, `Doubles`, `Booleans`, `NumberUtils`, `BigDecimalUtils`, `BigIntegerUtils` | Parsing with defaults, overflow-checked casts, primitive array math, number formatting, exact decimal arithmetic and rounding |
| Type conversion | `ConvertUtils`, `TypeConverter` | String ↔ number, boolean, date, `java.time`, charset, locale, UUID and arrays, with defaults instead of exceptions |
| Date & time | `DateUtils`, `CalendarUtils`, `LocalDateUtils`, `LocalDateTimeUtils`, `LocalTimeUtils`, `StopWatch`, `TimeWindowMap` | Thread-safe parsing and formatting, month-end aware arithmetic, ranges and iteration, time zones, stopwatch, time-sliced statistics |
| Collections | `CollectionUtils`, `ListUtils`, `SetUtils`, `MapUtils`, `LruMap`, `LruList`, `LruSet`, `MultiMappedMap`, `MultiListMap`, `CaseInsensitiveMap`, `ConcurrentBoundedMap` | Set operations, paging and sorting helpers, LRU and size-bounded concurrent collections, multi-value and case-insensitive maps, reference maps |
| IO & files | `FileUtils`, `IOUtils`, `PathUtils`, `ZipUtils`, `SerializationUtils`, `ImageUtils`, `RecursiveDirectoryWalker`, `Filters`, `FileMonitor` | Read/write/copy/move/split files, stream copying, path handling, zip/unzip with Zip Slip protection, gzip, serialization, directory walking and filtering, file change monitoring, ini/properties files |
| Codecs & crypto | `HexUtils`, `Base64Utils`, `URLCodecUtils`, `DigestUtils`, `HmacUtils` | Hex, Base64 (standard/URL-safe/MIME), URL encoding and query strings, MD5, SHA-1/256/384/512, HMAC, CRC32 |
| IDs & validation | `SnowflakeIdGenerator`, `IdUtils`, `ValidationUtils` | Snowflake IDs, UUIDs, short IDs, ObjectIds; e-mail, mobile and landline numbers, Chinese ID card, unified social credit code, bank card (Luhn), IPv4/IPv6, URL, postal code |
| Beans & reflection | `BeanUtils`, `PropertyUtils`, `ToStringBuilder`, `EqualsBuilder`, `HashCodeBuilder`, `FieldUtils`, `MethodUtils`, `ConstructorUtils`, `JdkProxyFactory` | Bean ↔ Map, property copy, nested properties, reflective toString/equals/hashCode, private field and method access, overload resolution, dynamic proxies |
| In-memory queries | `Selector`, `Restrictions`, `Orders`, `Functions`, `Transformers` | `where` / `group by` / `having` / `order by` / `limit` and aggregates (count, sum, avg, min, max) over object lists |
| Concurrency | `ThreadUtils`, `ThreadPoolBuilder`, `ThreadPool`, `Promise`, `ExecutorUtils`, `CounterLatch`, `SemaphoreLatch`, `RetryableTimer`, `ConcurrentTimer`, `Clock` | Thread pools with promises, concurrency limiters, graceful shutdown, retryable and shared timers, scheduled tasks, atomic sequences |
| Events | `EventBus`, `Observable` | In-process publish/subscribe and topic-based observers |
| Caching | `LruCache`, `FifoCache`, `SortedCache`, `HashCache`, `SoftReferenceCache`, `WeakReferenceCache` | Bounded, expiring, reference-based and two-level (master/standby) caches that compose by decoration, with optional spill-over stores |
| JDBC | `JdbcUtils`, `PageableQuery`, `JdbcDumpTemplate`, `UnpooledDataSource` | Queries returning tuples or typed values, batch updates, cursors, paging without hand-written `LIMIT`/`COUNT`, table dumps, transactions |
| Pooling | `GenericObjectPool`, `GenericDataSource` | Generic object pool and JDBC connection pool with validation, idle trimming, expiry, statement cache and query statistics |
| HTTP & network | `HttpClient`, `HttpRequests`, `NetUtils`, `Urls` | GET/POST/PUT/DELETE with forms, uploads, redirects, retries and timeouts; local address, port and URL helpers |
| Logging | `LogFactory`, `Log` | Logging on `java.util.logging` with `{}` placeholders and per-logger levels |
| Progress | `ProgressBar`, `ProgressAdapters` | tqdm-style progress bars that wrap `for` loops, iterables, iterators, arrays, streams, byte streams and directory walks in one line; console or log output; adapters for table dumps and stream copies |
| Test data | `BeanMocker`, `RandomUtils`, `RandomDateUtils` | Beans filled with random values constrained by annotations, random numbers, dates and strings |

## Requirements

| | |
|---|---|
| Java | JDK 8 or later |
| Runtime dependencies | None |
| Build from source | Maven 3.6+ |

## Installation

**Maven**

```xml
<dependency>
    <groupId>com.github.paganini2008</groupId>
    <artifactId>devtools-lang</artifactId>
    <version>3.0.0</version>
</dependency>
```

**Gradle**

```groovy
implementation 'com.github.paganini2008:devtools-lang:3.0.0'
```

## Upgrading from 2.x

- **One artifact.** `devtools-lang`, `devtools-objectpool` and `devtools-beans-streaming` are merged into `devtools-lang` 3.0.0. Package names are unchanged, so replacing the dependency is enough.
- **Discontinued.** `devtools-cron4j` and `devtools-db4j` are no longer maintained; 2.0.5 remains available.
- **Compatibility.** No public class or method was removed or had its signature changed, apart from demo and debug code. Problematic methods are marked `@Deprecated` and point to their replacement.
- **Bug fixes.** A few fixes change results that were previously wrong, for example `BigDecimalUtils.round` used to truncate and integer parsing used to wrap silently on overflow. Test your upgrade against these fixes.

## Documentation

- [Javadoc](https://javadoc.io/doc/com.github.paganini2008/devtools-lang): public methods document their null handling, edge cases and exceptions; core utilities include input → output examples.
- The unit tests under `src/test/java` show typical usage of every package.

## Contributing

- Report bugs or request features in [GitHub Issues](https://github.com/paganini2008/devtools/issues), with the JDK version and a minimal reproduction.
- Pull requests are welcome. Please keep to the project rules:
  - no third-party runtime dependencies, JDK 8 API only;
  - no breaking changes to public APIs (deprecate instead of removing);
  - null-safe utility methods with Javadoc on public methods;
  - unit tests for every change. `mvn verify` fails if line coverage drops below 90%.

## License

[Apache License 2.0](LICENSE)
