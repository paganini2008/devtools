/**
* Copyright 2017-2026 Fred Feng (paganini.fy@gmail.com)

* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.github.paganini2008.devtools.examples;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.io.TempDir;

import com.github.paganini2008.devtools.ArrayUtils;
import com.github.paganini2008.devtools.CaseFormats;
import com.github.paganini2008.devtools.MatchMode;
import com.github.paganini2008.devtools.NumberUtils;
import com.github.paganini2008.devtools.ObjectUtils;
import com.github.paganini2008.devtools.Observable;
import com.github.paganini2008.devtools.RandomStringUtils;
import com.github.paganini2008.devtools.StringUtils;
import com.github.paganini2008.devtools.beans.BeanUtils;
import com.github.paganini2008.devtools.beans.EqualsBuilder;
import com.github.paganini2008.devtools.beans.PropertyUtils;
import com.github.paganini2008.devtools.beans.ToStringBuilder;
import com.github.paganini2008.devtools.beans.streaming.Group;
import com.github.paganini2008.devtools.beans.streaming.Orders;
import com.github.paganini2008.devtools.beans.streaming.Restrictions;
import com.github.paganini2008.devtools.beans.streaming.Selector;
import com.github.paganini2008.devtools.beans.streaming.Transformers;
import com.github.paganini2008.devtools.beans.streaming.View;
import com.github.paganini2008.devtools.cache.Cache;
import com.github.paganini2008.devtools.cache.ExpiredCache;
import com.github.paganini2008.devtools.cache.HashCache;
import com.github.paganini2008.devtools.cache.LruCache;
import com.github.paganini2008.devtools.collection.CaseInsensitiveMap;
import com.github.paganini2008.devtools.collection.CollectionUtils;
import com.github.paganini2008.devtools.collection.ListUtils;
import com.github.paganini2008.devtools.collection.LruMap;
import com.github.paganini2008.devtools.collection.MapUtils;
import com.github.paganini2008.devtools.collection.MultiListMap;
import com.github.paganini2008.devtools.collection.MultiMappedMap;
import com.github.paganini2008.devtools.collection.Tuple;
import com.github.paganini2008.devtools.converter.ConvertUtils;
import com.github.paganini2008.devtools.event.Event;
import com.github.paganini2008.devtools.event.EventBus;
import com.github.paganini2008.devtools.event.EventSubscriber;
import com.github.paganini2008.devtools.http.HttpClient;
import com.github.paganini2008.devtools.http.HttpRequest;
import com.github.paganini2008.devtools.http.HttpRequests;
import com.github.paganini2008.devtools.http.HttpResponse;
import com.github.paganini2008.devtools.io.FileUtils;
import com.github.paganini2008.devtools.io.IOUtils;
import com.github.paganini2008.devtools.io.PathUtils;
import com.github.paganini2008.devtools.io.monitor.FileChangeListener;
import com.github.paganini2008.devtools.io.monitor.FileEntry;
import com.github.paganini2008.devtools.io.monitor.FileMonitor;
import com.github.paganini2008.devtools.io.monitor.FileWatcher;
import com.github.paganini2008.devtools.jdbc.JdbcUtils;
import com.github.paganini2008.devtools.jdbc.PageRequest;
import com.github.paganini2008.devtools.jdbc.PageResponse;
import com.github.paganini2008.devtools.jdbc.PageableQuery;
import com.github.paganini2008.devtools.logging.Log;
import com.github.paganini2008.devtools.logging.LogFactory;
import com.github.paganini2008.devtools.math.BigDecimalUtils;
import com.github.paganini2008.devtools.mock.BeanMocker;
import com.github.paganini2008.devtools.mock.DateRange;
import com.github.paganini2008.devtools.mock.IntRange;
import com.github.paganini2008.devtools.mock.MockContext;
import com.github.paganini2008.devtools.multithreads.ExecutorUtils;
import com.github.paganini2008.devtools.multithreads.Promise;
import com.github.paganini2008.devtools.multithreads.ThreadPool;
import com.github.paganini2008.devtools.multithreads.ThreadPoolBuilder;
import com.github.paganini2008.devtools.multithreads.ThreadUtils;
import com.github.paganini2008.devtools.multithreads.latch.CounterLatch;
import com.github.paganini2008.devtools.multithreads.latch.Latch;
import com.github.paganini2008.devtools.objectpool.GenericObjectPool;
import com.github.paganini2008.devtools.objectpool.ObjectFactory;
import com.github.paganini2008.devtools.objectpool.PooledObjectException;
import com.github.paganini2008.devtools.objectpool.dbpool.GenericDataSource;
import com.github.paganini2008.devtools.primitives.Booleans;
import com.github.paganini2008.devtools.primitives.Doubles;
import com.github.paganini2008.devtools.primitives.Ints;
import com.github.paganini2008.devtools.primitives.Longs;
import com.github.paganini2008.devtools.reflection.FieldUtils;
import com.github.paganini2008.devtools.reflection.MethodUtils;
import com.github.paganini2008.devtools.time.DateUtils;
import com.github.paganini2008.devtools.time.LocalDateTimeUtils;
import com.github.paganini2008.devtools.time.LocalDateUtils;
import com.sun.net.httpserver.HttpServer;

/**
 * UsageExamplesTest
 *
 * Every test method here is a typical usage example; the asserted values
 * document the expected output.
 *
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class UsageExamplesTest {

	// ------------------------------------------------------------------
	// 1. Strings, objects, arrays, case formats
	// ------------------------------------------------------------------

	@Test
	public void strings_nullSafeHelpersAndPadding() {
		assertTrue(StringUtils.isBlank(null));
		assertTrue(StringUtils.isBlank("   "));
		assertEquals("00042", StringUtils.textRight("42", '0', 5));
		assertEquals("ab...", StringUtils.textLeft("ab", '.', 5));
		assertEquals("The quick ...", StringUtils.thumb("The quick brown fox", 10));
		assertEquals("ab,ab,ab", StringUtils.repeat("ab", ",", 3));
		assertEquals(Arrays.asList("a", "b", "c"), StringUtils.split("a,b,,c", ","));
	}

	@Test
	public void strings_placeholders() {
		assertEquals("Hello Fred, today is Monday", StringUtils.format("Hello {}, today is {}", "{}", "Fred", "Monday"));

		Map<String, Object> vars = new HashMap<String, Object>();
		vars.put("name", "Fred");
		vars.put("age", 18);
		assertEquals("Hello Fred, you are 18", StringUtils.parseText("Hello ${name}, you are ${age}", "${", "}", vars));
	}

	@Test
	public void caseFormats_camelAndUnderscore() {
		assertEquals("userLoginName", StringUtils.toCamelCase("user_login_name"));
		assertEquals("user_login_name", StringUtils.toSegmentCase("userLoginName"));
		assertEquals("user-login-name", StringUtils.toSegmentCase("userLoginName", "-"));

		assertEquals("userLoginName", CaseFormats.LOWER_CAMEL.toCase("user_login_name"));
		assertEquals("UserLoginName", CaseFormats.UPPER_CAMEL.toCase("user_login_name"));
		assertEquals("user_login_name", CaseFormats.LOWER_UNDERSCORE.toCase("userLoginName"));
		assertEquals("USER_LOGIN_NAME", CaseFormats.UPPER_UNDERSCORE.toCase("userLoginName"));
		assertEquals("user-login-name", CaseFormats.LOWER_HYPHEN.toCase("userLoginName"));
	}

	@Test
	public void objectsAndArrays() {
		assertEquals("N/A", ObjectUtils.toString(null, "N/A"));
		assertTrue(ObjectUtils.equals(null, null));

		String[] a = { "a", "b" };
		String[] b = { "b", "c" };
		assertEquals("[a, b, c]", Arrays.toString(ArrayUtils.union(a, b)));
		assertEquals("[b]", Arrays.toString(ArrayUtils.intersect(a, b)));
		assertEquals("[a]", Arrays.toString(ArrayUtils.minus(a, b)));
		assertEquals("[a, b, c]", Arrays.toString(ArrayUtils.add(a, "c")));
		assertTrue(ArrayUtils.contains(a, "b"));
		assertEquals("a=1&b=2", ArrayUtils.join(new String[] { "a", "b" }, new Integer[] { 1, 2 }, "=", "&"));
	}

	// ------------------------------------------------------------------
	// 2. Primitives and numbers
	// ------------------------------------------------------------------

	@Test
	public void primitives_parseWithDefaultsAndArrayMath() {
		assertEquals(123, Ints.parse("123"));
		assertEquals(Integer.valueOf(0), Ints.valueOf("abc", 0));
		assertEquals(Long.valueOf(-1L), Longs.valueOf("n/a", -1L));
		assertTrue(Booleans.parse("yes"));
		assertEquals(3.14, Doubles.toFixed(3.14159, 2));

		int[] scores = Ints.parseMany(new String[] { "1", "2", "3", "4" });
		assertEquals(10, Ints.sum(scores));
		assertEquals(2.5, Ints.avg(scores));
		assertEquals(4, Ints.max(scores));
		assertEquals("1|2|3|4", Ints.join(scores, "|"));
		assertTrue(Ints.isSerial(scores));
	}

	@Test
	public void numbers_safeConversionAndFormatting() {
		assertEquals(Integer.valueOf(-1), NumberUtils.toInteger("12x", -1));
		assertEquals(Double.valueOf(3.5), NumberUtils.toDouble("3.5"));
		assertEquals("1,234,567.89", NumberUtils.format(1234567.891, "#,##0.00"));
		assertEquals("3.14", NumberUtils.format(3.14159, 2));
	}

	@Test
	public void bigDecimal_exactArithmetic() {
		// 0.1 + 0.2 = 0.30000000000000004 with double
		assertEquals(new BigDecimal("0.3"), BigDecimalUtils.add("0.1", "0.2"));
		assertEquals(new BigDecimal("3.33"), BigDecimalUtils.divide(10, 3, 2, RoundingMode.HALF_UP));
		assertEquals(new BigDecimal("6.75"), BigDecimalUtils.sum(new Number[] { 1, 2.5, new BigDecimal("3.25") }));
		assertEquals(new BigDecimal("2.33"), BigDecimalUtils.avg(new Number[] { 1, 2, 4 }, 2));
		assertEquals(new BigDecimal("2.68"), BigDecimalUtils.round(2.675, 2));
	}

	// ------------------------------------------------------------------
	// 3. Type conversion
	// ------------------------------------------------------------------

	@Test
	public void convertUtils_fromStrings() {
		assertEquals(Integer.valueOf(123), ConvertUtils.convertValue("123", Integer.class));
		assertEquals(Integer.valueOf(-1), ConvertUtils.convertValue("abc", Integer.class, -1));
		assertEquals(Boolean.TRUE, ConvertUtils.convertValue("yes", Boolean.class));
		assertEquals(new BigDecimal("3.14"), ConvertUtils.convertValue("3.14", BigDecimal.class));
		assertEquals(LocalDate.of(2024, 3, 15), ConvertUtils.convertValue("2024-03-15", LocalDate.class));
		assertEquals(LocalDateTime.of(2024, 3, 15, 10, 30), ConvertUtils.convertValue("2024-03-15T10:30:00", LocalDateTime.class));
		assertEquals("[1, 2, 3]", Arrays.toString(ConvertUtils.convertValue("1,2,3", int[].class)));

		Date date = ConvertUtils.convertValue("2024-03-15 10:30:00", Date.class);
		assertEquals("2024/03/15 10:30", DateUtils.format(date, "yyyy/MM/dd HH:mm"));
	}

	// ------------------------------------------------------------------
	// 4. Date and time
	// ------------------------------------------------------------------

	@Test
	public void dateUtils_parseFormatAndArithmetic() {
		Date date = DateUtils.parse("2024-01-31", "yyyy-MM-dd");
		assertEquals("2024-02-29", DateUtils.format(DateUtils.addMonths(date, 1), "yyyy-MM-dd"));
		assertEquals("2024-02-01", DateUtils.format(DateUtils.addDayOfMonth(date, 1), "yyyy-MM-dd"));

		// try several patterns, or fall back to a default value
		assertEquals(date, DateUtils.parse("2024/01/31", new String[] { "yyyy-MM-dd", "yyyy/MM/dd" }));
		assertNull(DateUtils.parse("not a date", "yyyy-MM-dd", null));
		assertEquals("2024-01-31", DateUtils.reformat("20240131", "yyyyMMdd", "yyyy-MM-dd"));

		assertEquals(60L, DateUtils.until(DateUtils.of(2024, 1, 1), DateUtils.of(2024, 3, 1), TimeUnit.DAYS));
		assertEquals(29, DateUtils.getLastDayOfMonth(DateUtils.of(2024, 2, 10)));
		assertEquals("2024-02-10 08:05:00", DateUtils.format(DateUtils.of(2024, 2, 10, 8, 5, 0)));
	}

	@Test
	public void localDateUtils_parseFormatAndIterate() {
		assertEquals(LocalDate.of(2024, 2, 28), LocalDateUtils.parseLocalDate("20240228", "yyyyMMdd"));
		assertEquals("2024/02/28", LocalDateUtils.format(LocalDate.of(2024, 2, 28), "yyyy/MM/dd"));
		assertEquals(LocalDate.of(2024, 3, 1), LocalDateUtils.addDays(LocalDate.of(2024, 2, 28), 2));

		Iterator<LocalDate> days = LocalDateUtils.toIterator(LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 4), ZoneId.systemDefault(), 1,
				ChronoUnit.DAYS);
		List<LocalDate> list = new ArrayList<LocalDate>();
		days.forEachRemaining(list::add);
		assertEquals("[2024-01-01, 2024-01-02, 2024-01-03]", list.toString());

		LocalDateTime ldt = LocalDateTimeUtils.parseLocalDateTime("2024-02-28 23:30:00", "yyyy-MM-dd HH:mm:ss");
		assertEquals("2024-02-29 00:30:00", LocalDateTimeUtils.format(LocalDateTimeUtils.addHours(ldt, 1), "yyyy-MM-dd HH:mm:ss"));
		assertEquals(3600000L, LocalDateTimeUtils.until(LocalDateTime.of(2024, 1, 1, 0, 0), LocalDateTime.of(2024, 1, 1, 1, 0)));
	}

	// ------------------------------------------------------------------
	// 5. Collections
	// ------------------------------------------------------------------

	@Test
	public void lruMap_evictsLeastRecentlyUsed() {
		LruMap<String, Integer> map = new LruMap<String, Integer>(2);
		map.put("a", 1);
		map.put("b", 2);
		map.get("a"); // "a" is now the most recently used
		map.put("c", 3); // evicts "b"
		assertEquals(2, map.size());
		assertTrue(map.containsKey("a"));
		assertFalse(map.containsKey("b"));
		assertTrue(map.containsKey("c"));
	}

	@Test
	public void multiValueAndCaseInsensitiveMaps() {
		MultiListMap<String, String> tags = new MultiListMap<String, String>();
		tags.add("fruit", "apple");
		tags.add("fruit", "banana");
		tags.add("veg", "carrot");
		assertEquals(Arrays.asList("apple", "banana"), tags.get("fruit"));

		MultiMappedMap<String, String, Integer> sales = new MultiMappedMap<String, String, Integer>();
		sales.put("2024", "Jan", 10);
		sales.put("2024", "Feb", 20);
		sales.put("2025", "Jan", 30);
		assertEquals(Integer.valueOf(20), sales.get("2024", "Feb"));
		assertEquals(2, sales.get("2024").size());

		CaseInsensitiveMap<Object> headers = new CaseInsensitiveMap<Object>();
		headers.put("Content-Type", "application/json");
		assertEquals("application/json", headers.get("content-type"));
		assertEquals("application/json", headers.get("CONTENT-TYPE"));
	}

	@Test
	public void collectionMapAndListUtils() {
		assertEquals(Arrays.asList(1, 2, 3, 4), new ArrayList<Integer>(CollectionUtils.union(Arrays.asList(1, 2, 3), Arrays.asList(3, 4))));
		assertEquals(Arrays.asList(3), new ArrayList<Integer>(CollectionUtils.intersect(Arrays.asList(1, 2, 3), Arrays.asList(3, 4))));
		assertEquals(Arrays.asList(1, 2), new ArrayList<Integer>(CollectionUtils.minus(Arrays.asList(1, 2, 3), Arrays.asList(3, 4))));
		assertEquals("1|2|3", CollectionUtils.join(Arrays.asList(1, 2, 3), "|"));
		assertTrue(CollectionUtils.isEmpty((List<?>) null));

		Map<String, Integer> scores = new HashMap<String, Integer>();
		scores.put("Tom", 80);
		scores.put("Jerry", 95);
		scores.put("Spike", 60);
		assertEquals("{Spike=60, Tom=80, Jerry=95}", MapUtils.sortByValue(scores).toString());
		assertEquals("{Jerry=95, Tom=80, Spike=60}", MapUtils.sortByValue(scores, true).toString());
		assertEquals("{a=1, b=2}", MapUtils.toMap(new String[] { "a", "b" }, new Integer[] { 1, 2 }).toString());

		List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7);
		assertEquals(Arrays.asList(4, 5, 6), ListUtils.slice(numbers, 3, 3)); // limit 3, offset 3
		assertEquals(Integer.valueOf(-1), ListUtils.getFirst(new ArrayList<Integer>(), -1));
		assertEquals(Integer.valueOf(7), ListUtils.getLast(numbers));
	}

	// ------------------------------------------------------------------
	// 6. IO
	// ------------------------------------------------------------------

	@Test
	public void fileUtils_writeReadCopy(@TempDir File dir) throws IOException {
		File file = new File(dir, "notes/hello.txt"); // parent directories are created automatically
		FileUtils.writeFile("line1\nline2\n中文", file, false, StandardCharsets.UTF_8);
		assertEquals(Arrays.asList("line1", "line2", "中文"), FileUtils.readLines(file, StandardCharsets.UTF_8));

		FileUtils.copyFile(file, new File(dir, "backup/hello.txt"));
		FileUtils.copyDirectory(new File(dir, "notes"), new File(dir, "archive/notes"));
		assertTrue(new File(dir, "backup/hello.txt").exists());
		assertTrue(new File(dir, "archive/notes/hello.txt").exists());

		assertEquals("txt", FileUtils.getExtension(file));
		assertEquals("hello", FileUtils.getBaseName(file));
		assertEquals("1.50 KB", FileUtils.formatSize(1536));
		assertEquals("5.00 MB", FileUtils.formatSize(5L * 1024 * 1024));
	}

	@Test
	public void fileUtils_recursiveScan(@TempDir File dir) throws IOException {
		FileUtils.writeFile("a", new File(dir, "a.txt"), false, StandardCharsets.UTF_8);
		FileUtils.writeFile("b", new File(dir, "sub/b.txt"), false, StandardCharsets.UTF_8);
		FileUtils.writeFile("c", new File(dir, "sub/deep/c.log"), false, StandardCharsets.UTF_8);

		List<String> found = new ArrayList<String>();
		FileUtils.scan(dir, null, (directory, depth, f) -> {
			if (f.getName().endsWith(".txt")) {
				found.add(f.getName());
			}
		});
		Collections.sort(found);
		assertEquals(Arrays.asList("a.txt", "b.txt"), found);
		assertEquals(3L, FileUtils.sizeOfDirectory(dir, null)); // total bytes of all files
	}

	@Test
	public void ioUtilsAndPathUtils() throws IOException {
		InputStream in = new ByteArrayInputStream("hello 世界".getBytes(StandardCharsets.UTF_8));
		assertEquals("hello 世界", IOUtils.toString(in, StandardCharsets.UTF_8));

		assertEquals("pdf", PathUtils.getExtension("/data/report.final.pdf"));
		assertEquals("report.final", PathUtils.getBaseName("/data/report.final.pdf"));
		assertEquals("report.final.pdf", PathUtils.getName("/data/report.final.pdf"));
	}

	// ------------------------------------------------------------------
	// 7. Beans and reflection
	// ------------------------------------------------------------------

	public static class Address {
		private String city;

		public Address() {
		}

		public Address(String city) {
			this.city = city;
		}

		public String getCity() {
			return city;
		}

		public void setCity(String city) {
			this.city = city;
		}
	}

	public static class User {
		private Long id;
		private String name;
		private Integer age;
		private Address address;
		private String secret = "s3cr3t";

		public User() {
		}

		public User(Long id, String name, Integer age) {
			this.id = id;
			this.name = name;
			this.age = age;
		}

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public Integer getAge() {
			return age;
		}

		public void setAge(Integer age) {
			this.age = age;
		}

		public Address getAddress() {
			return address;
		}

		public void setAddress(Address address) {
			this.address = address;
		}

		private String greet(String who) {
			return "Hi " + who + ", I am " + name;
		}
	}

	public static class UserDto {
		private String name;
		private int age;
		private String email;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public int getAge() {
			return age;
		}

		public void setAge(int age) {
			this.age = age;
		}

		public String getEmail() {
			return email;
		}

		public void setEmail(String email) {
			this.email = email;
		}
	}

	@Test
	public void beans_mapConversionAndPropertyCopy() {
		User user = new User(1L, "Alice", 30);
		user.setAddress(new Address("Paris"));

		Map<String, Object> map = PropertyUtils.convertToMap(user);
		assertEquals("[address, age, id, name]", map.keySet().toString());
		assertEquals("Paris", PropertyUtils.getProperty(user, "address.city")); // nested property

		// Map -> Bean, values are converted to the property types
		Map<String, Object> form = new HashMap<String, Object>();
		form.put("id", "7");
		form.put("name", "Bob");
		form.put("age", "25");
		User bob = BeanUtils.convertAsBean(form, User.class);
		assertEquals(Long.valueOf(7), bob.getId());
		assertEquals(Integer.valueOf(25), bob.getAge());

		// Bean -> Bean, properties with the same name are copied
		UserDto dto = new UserDto();
		BeanUtils.copyProperties(user, dto);
		assertEquals("Alice", dto.getName());
		assertEquals(30, dto.getAge());

		BeanUtils.setProperty(user, "age", "31");
		assertEquals(Integer.valueOf(31), user.getAge());
		assertEquals("31", BeanUtils.getProperty(user, "age", String.class));
	}

	@Test
	public void beans_reflectionToStringAndEquals() {
		assertEquals("{address: null, age: 30, id: 1, name: Alice}", ToStringBuilder.reflectionToString(new User(1L, "Alice", 30)));
		assertEquals("{age: 30, id: 1, name: Alice}", ToStringBuilder.reflectionToString(new User(1L, "Alice", 30), new String[] { "address" }));
		assertTrue(EqualsBuilder.reflectionEquals(new User(1L, "Alice", 30), new User(1L, "Alice", 30)));
	}

	@Test
	public void reflection_privateFieldsAndMethods() {
		User user = new User(1L, "Alice", 30);
		assertEquals("s3cr3t", FieldUtils.readField(user, "secret"));
		FieldUtils.writeField(user, "secret", "changed");
		assertEquals("changed", FieldUtils.readField(user, "secret"));

		assertEquals("Hi Bob, I am Alice", MethodUtils.invokeMethod(user, "greet", "Bob"));
		assertEquals(42, MethodUtils.invokeStaticMethod(Integer.class, "parseInt", "42"));
	}

	// ------------------------------------------------------------------
	// 8. SQL-like queries on object lists
	// ------------------------------------------------------------------

	public static class Product {
		private String name;
		private String location;
		private String category;
		private BigDecimal price;
		private int sales;

		public Product() {
		}

		public Product(String name, String location, String category, String price, int sales) {
			this.name = name;
			this.location = location;
			this.category = category;
			this.price = new BigDecimal(price);
			this.sales = sales;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getLocation() {
			return location;
		}

		public void setLocation(String location) {
			this.location = location;
		}

		public String getCategory() {
			return category;
		}

		public void setCategory(String category) {
			this.category = category;
		}

		public BigDecimal getPrice() {
			return price;
		}

		public void setPrice(BigDecimal price) {
			this.price = price;
		}

		public int getSales() {
			return sales;
		}

		public void setSales(int sales) {
			this.sales = sales;
		}

		@Override
		public String toString() {
			return name;
		}
	}

	private static List<Product> products() {
		return Arrays.asList(
				new Product("iPhone", "London", "Phone", "999.00", 120),
				new Product("Galaxy", "London", "Phone", "899.00", 80),
				new Product("MacBook", "London", "Laptop", "1999.00", 30),
				new Product("Pixel", "Tokyo", "Phone", "699.00", 60),
				new Product("ThinkPad", "Tokyo", "Laptop", "1299.00", 45),
				new Product("Surface", "Paris", "Laptop", "1199.00", 20));
	}

	@Test
	public void streaming_whereAndOrderBy() {
		List<Product> products = products();

		// select * from products where location = 'London'
		List<Product> london = Selector.from(products).filter(Restrictions.eq("location", "London")).list();
		assertEquals("[iPhone, Galaxy, MacBook]", london.toString());

		// select * from products where category = 'Phone' and price > 700
		List<Product> phones = Selector.from(products)
				.filter(Restrictions.<Product, String>eq("category", "Phone").and(Restrictions.gt("price", new BigDecimal("700")))).list();
		assertEquals("[iPhone, Galaxy]", phones.toString());

		// select * from products where name like '%Pad'
		assertEquals("[ThinkPad]", Selector.from(products).filter(Restrictions.matches("name", "Pad", MatchMode.END)).list().toString());

		// select * from products order by price desc limit 3
		List<Product> top3 = Selector.from(products).orderBy(Orders.descending("price", BigDecimal.class)).list(3);
		assertEquals("[MacBook, ThinkPad, Surface]", top3.toString());

		// select * from products order by location asc, sales desc
		List<Product> sorted = Selector.from(products)
				.orderBy(Orders.<Product, String>ascending("location", String.class).descending("sales", Integer.class)).list();
		assertEquals("[iPhone, Galaxy, MacBook, Surface, Pixel, ThinkPad]", sorted.toString());

		// select name, price from products where location = 'Tokyo'
		List<Map<String, Object>> rows = Selector.from(products).filter(Restrictions.eq("location", "Tokyo"))
				.setTransformer(Transformers.<Product>asMap(new String[] { "name", "price" })).list();
		assertEquals("[{name=Pixel, price=699.00}, {name=ThinkPad, price=1299.00}]", rows.toString());
	}

	@Test
	public void streaming_groupByWithAggregates() {
		// select location, count(*), max(price), sum(sales), avg(price) from products group by location
		List<Tuple> rows = Selector.from(products()).groupBy("location", String.class).setTransformer(new View<Product>() {
			@Override
			protected void setAttributes(Tuple tuple, Group<Product> group) {
				tuple.set("count", group.count());
				tuple.set("maxPrice", group.max("price", BigDecimal.class));
				tuple.set("totalSales", group.sum("sales"));
				tuple.set("avgPrice", group.avg("price"));
			}
		}).list();
		assertEquals("{location=London, count=3, maxPrice=1999.00, totalSales=230, avgPrice=1299.00}", rows.get(0).toString());
		assertEquals("{location=Tokyo, count=2, maxPrice=1299.00, totalSales=105, avgPrice=999.00}", rows.get(1).toString());
		assertEquals("{location=Paris, count=1, maxPrice=1199.00, totalSales=20, avgPrice=1199.00}", rows.get(2).toString());
	}

	@Test
	public void streaming_groupByHavingOrderBy() {
		// select location, category, sum(sales) as totalSales from products
		// group by location, category having count(*) >= 2 order by sum(sales) desc
		List<Tuple> rows = Selector.from(products())
				.groupBy("location", String.class)
				.groupBy("category", String.class)
				.having(group -> group.count() >= 2)
				.orderBy(Orders.groupDescending(group -> group.sum("sales")))
				.setTransformer(new View<Product>() {
					@Override
					protected void setAttributes(Tuple tuple, Group<Product> group) {
						tuple.set("totalSales", group.sum("sales"));
					}
				}).list();
		assertEquals(1, rows.size());
		assertEquals("{location=London, category=Phone, totalSales=200}", rows.get(0).toString());
	}

	// ------------------------------------------------------------------
	// 9. Multithreading
	// ------------------------------------------------------------------

	@Test
	@Timeout(10)
	public void threadPool_submitAndGetResult() throws Exception {
		ThreadPool threadPool = ThreadUtils.commonPool(4);
		try {
			Promise<Integer> promise = threadPool.submit(() -> 6 * 7);
			assertEquals(Integer.valueOf(42), promise.get());

			Promise<Integer> slow = threadPool.submit(() -> {
				Thread.sleep(500);
				return 1;
			});
			assertNull(slow.get(50)); // timed out, null is returned

			AtomicInteger counter = new AtomicInteger();
			CountDownLatch done = new CountDownLatch(100);
			for (int i = 0; i < 100; i++) {
				threadPool.execute(() -> {
					counter.incrementAndGet();
					done.countDown();
				});
			}
			done.await();
			assertEquals(100, counter.get());
		} finally {
			threadPool.shutdown();
		}
	}

	@Test
	@Timeout(10)
	public void threadPoolBuilderAndExecutorUtils() throws Exception {
		ThreadPool threadPool = ThreadPoolBuilder.common(2).setQueueSize(100).setTimeout(1000L).build();
		assertEquals(2, threadPool.getMaxPoolSize());
		threadPool.shutdown();
		assertTrue(threadPool.isShutdown());

		ExecutorService executor = Executors.newFixedThreadPool(2);
		assertEquals("hello", ExecutorUtils.callInBackground(executor, () -> "hello"));
		ExecutorUtils.gracefulShutdown(executor, 1000L);
		assertTrue(executor.isShutdown());
	}

	@Test
	@Timeout(10)
	public void latch_limitsConcurrency() {
		Latch latch = new CounterLatch(3); // at most 3 tasks at a time
		ExecutorService executor = Executors.newFixedThreadPool(10);
		AtomicInteger running = new AtomicInteger();
		AtomicInteger maxRunning = new AtomicInteger();
		List<Integer> jobs = new ArrayList<Integer>();
		for (int i = 0; i < 20; i++) {
			jobs.add(i);
		}
		latch.forEach(jobs, executor, job -> {
			maxRunning.accumulateAndGet(running.incrementAndGet(), Math::max);
			ThreadUtils.sleep(10);
			running.decrementAndGet();
		}); // returns when all jobs are done
		executor.shutdown();
		assertTrue(maxRunning.get() <= 3);
		assertEquals(3, latch.availablePermits());

		Latch single = new CounterLatch(1);
		assertTrue(single.tryAcquire());
		assertFalse(single.tryAcquire());
		single.release();
		assertTrue(single.tryAcquire());
	}

	// ------------------------------------------------------------------
	// 10. Cache
	// ------------------------------------------------------------------

	@Test
	public void cache_lruAndLoader() {
		LruCache cache = new LruCache(2);
		cache.putObject("a", 1);
		cache.putObject("b", 2);
		cache.getObject("a");
		cache.putObject("c", 3); // evicts "b"
		assertEquals(2, cache.getSize());
		assertFalse(cache.hasKey("b"));

		HashCache users = new HashCache();
		assertEquals("loaded:42", users.getObject("42", () -> "loaded:42")); // computed and cached on a miss
		assertEquals("loaded:42", users.getObject("42"));
	}

	@Test
	@Timeout(10)
	public void cache_expirationAndMasterStandby() throws Exception {
		ExpiredCache cache = new LruCache(100).expiredCache();
		cache.putObject("token", "abc", false, 1); // expires after 1 second
		cache.putObject("config", "x"); // never expires
		assertEquals("abc", cache.getObject("token"));
		Thread.sleep(1100);
		assertNull(cache.getObject("token"));
		assertEquals("x", cache.getObject("config"));

		LruCache master = new LruCache(1);
		HashCache standby = new HashCache();
		Cache twoLevel = master.masterStandbyCache(standby);
		twoLevel.putObject("a", 1);
		twoLevel.putObject("b", 2); // "a" is evicted from the master only
		assertFalse(master.hasKey("a"));
		assertEquals(1, twoLevel.getObject("a")); // read from the standby and put back into the master
		assertTrue(master.hasKey("a"));
	}

	// ------------------------------------------------------------------
	// 11. JDBC
	// ------------------------------------------------------------------

	@Test
	@Timeout(20)
	public void jdbc_queryAndPaging() throws Exception {
		GenericDataSource dataSource = new GenericDataSource("org.h2.Driver", "jdbc:h2:mem:readme;DB_CLOSE_DELAY=-1", "sa", "");
		dataSource.setMaxSize(5);
		try {
			Connection connection = dataSource.getConnection();
			try {
				JdbcUtils.update(connection, "create table users(id int primary key, name varchar(32), age int)");
				int[] inserted = JdbcUtils.batchUpdate(connection, "insert into users values(?, ?, ?)",
						Arrays.asList(new Object[] { 1, "Alice", 30 }, new Object[] { 2, "Bob", 25 }, new Object[] { 3, "Carol", 35 },
								new Object[] { 4, "Dave", 28 }, new Object[] { 5, "Eve", 22 }));
				assertEquals(5, inserted.length);

				List<Tuple> rows = JdbcUtils.fetchAll(connection, "select id, name from users where age > ? order by id", new Object[] { 26 });
				assertEquals("[{ID=1, NAME=Alice}, {ID=3, NAME=Carol}, {ID=4, NAME=Dave}]", rows.toString());
				assertEquals("Alice", rows.get(0).get("NAME"));

				assertEquals(Integer.valueOf(5), JdbcUtils.fetchOne(connection, "select count(*) from users", Integer.class));
				assertEquals(1, JdbcUtils.update(connection, "update users set age = age + 1 where id = ?", new Object[] { 1 }));
			} finally {
				connection.close(); // gives the connection back to the pool
			}

			PageableQuery<Tuple> query = JdbcUtils.pageableQuery(dataSource, "select id, name from users order by id", (Object[]) null);
			PageResponse<Tuple> page = query.list(PageRequest.of(2, 2)); // page 2, 2 rows per page
			assertEquals(5, page.getTotalRecords());
			assertEquals(3, page.getTotalPages());
			assertEquals("[{ID=3, NAME=Carol}, {ID=4, NAME=Dave}]", page.getContent().toString());
			assertTrue(page.hasNextPage());

			List<String> pages = new ArrayList<String>();
			for (PageResponse<Tuple> p : query.forEach(1, 2)) {
				pages.add(p.getPageNumber() + ": " + p.getContent());
			}
			assertEquals(Arrays.asList("1: [{ID=1, NAME=Alice}, {ID=2, NAME=Bob}]", "2: [{ID=3, NAME=Carol}, {ID=4, NAME=Dave}]",
					"3: [{ID=5, NAME=Eve}]"), pages);
		} finally {
			dataSource.close();
		}
	}

	// ------------------------------------------------------------------
	// 12. Object pool
	// ------------------------------------------------------------------

	@Test
	@Timeout(10)
	public void objectPool_borrowAndGiveback() throws Exception {
		AtomicInteger seq = new AtomicInteger();
		GenericObjectPool pool = new GenericObjectPool(new ObjectFactory() {

			@Override
			public Object createObject() {
				return "conn-" + seq.incrementAndGet();
			}

			@Override
			public boolean testObject(Object object) {
				return true;
			}

			@Override
			public void destroyObject(Object object) {
			}
		});
		pool.setMaxPoolSize(2);
		try {
			Object first = pool.borrowObject();
			Object second = pool.borrowObject();
			assertEquals("conn-1", first);
			assertEquals("conn-2", second);
			assertEquals(2, pool.getBusySize());

			// the pool is exhausted, wait at most 100 ms
			PooledObjectException e = assertThrows(PooledObjectException.class, () -> pool.borrowObject(100));
			assertEquals("Can not borrow any object now.", e.getMessage());

			pool.givebackObject(first);
			assertSame(first, pool.borrowObject()); // reused, not created again
			assertEquals(2, pool.getPoolSize());
			pool.givebackObject(first);
			pool.givebackObject(second);
		} finally {
			pool.close();
		}
	}

	// ------------------------------------------------------------------
	// 13. HTTP
	// ------------------------------------------------------------------

	@Test
	@Timeout(20)
	public void http_getAndPost() throws Exception {
		HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
		server.createContext("/echo", exchange -> {
			String query = exchange.getRequestURI().getRawQuery();
			String body = IOUtils.toString(exchange.getRequestBody(), StandardCharsets.UTF_8);
			byte[] bytes = (exchange.getRequestMethod() + " query=" + query + " body=" + body).getBytes(StandardCharsets.UTF_8);
			exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=UTF-8");
			exchange.sendResponseHeaders(200, bytes.length);
			try (OutputStream out = exchange.getResponseBody()) {
				out.write(bytes);
			}
		});
		server.start();
		String baseUrl = "http://127.0.0.1:" + server.getAddress().getPort();
		try {
			HttpClient client = new HttpClient();

			HttpRequest get = new HttpRequests.GetBuilder(baseUrl + "/echo").data("q", "devtools").timeout(5000).build();
			HttpResponse response = client.execute(get);
			assertEquals(200, response.statusCode());
			assertEquals("GET query=q=devtools body=", response.body());

			HttpRequest post = new HttpRequests.PostBuilder(baseUrl + "/echo").data("name", "Fred").data("lang", "java").build();
			assertEquals("POST query=null body=name=Fred&lang=java", client.execute(post).body());
		} finally {
			server.stop(0);
		}
	}

	// ------------------------------------------------------------------
	// 14. Logging
	// ------------------------------------------------------------------

	@Test
	public void logging_placeholders() {
		Log log = LogFactory.getLog(UsageExamplesTest.class);

		List<String> messages = new CopyOnWriteArrayList<String>();
		Handler capture = new Handler() {
			@Override
			public void publish(LogRecord record) {
				messages.add(record.getMessage());
			}

			@Override
			public void flush() {
			}

			@Override
			public void close() {
			}
		};
		Logger.getLogger(UsageExamplesTest.class.getName()).addHandler(capture);
		try {
			log.info("User {} logged in from {}", "alice", "10.0.0.1");
			assertEquals(Arrays.asList("User alice logged in from 10.0.0.1"), messages);
		} finally {
			Logger.getLogger(UsageExamplesTest.class.getName()).removeHandler(capture);
		}
	}

	// ------------------------------------------------------------------
	// 15. Other highlights
	// ------------------------------------------------------------------

	public static class OrderCreated extends Event<String> {

		private static final long serialVersionUID = 1L;

		public OrderCreated(Object source, String orderNo) {
			super(source, orderNo);
		}
	}

	public static class MailSender implements EventSubscriber<OrderCreated, String> {

		final List<String> sent = new CopyOnWriteArrayList<String>();

		@Override
		public void onEventFired(OrderCreated event) {
			sent.add("mail:" + event.getArgument());
		}
	}

	public static class StockUpdater implements EventSubscriber<OrderCreated, String> {

		final List<String> updated = new CopyOnWriteArrayList<String>();

		@Override
		public void onEventFired(OrderCreated event) {
			updated.add("stock:" + event.getArgument());
		}
	}

	@Test
	@Timeout(10)
	public void eventBus_publishToAllSubscribers() {
		EventBus<OrderCreated, String> eventBus = new EventBus<OrderCreated, String>(2, true);
		MailSender mailSender = new MailSender();
		StockUpdater stockUpdater = new StockUpdater();
		eventBus.subscribe(mailSender);
		eventBus.subscribe(stockUpdater);

		eventBus.publish(new OrderCreated(this, "A001"));
		eventBus.publish(new OrderCreated(this, "A002"));
		eventBus.close(); // waits for pending events, then shuts down

		List<String> mails = new ArrayList<String>(mailSender.sent);
		Collections.sort(mails);
		assertEquals(Arrays.asList("mail:A001", "mail:A002"), mails);
		assertEquals(2, stockUpdater.updated.size());
	}

	public static class Employee {
		@IntRange(from = 18, to = 60)
		private int age;
		@DateRange(from = "2020-01-01", to = "2020-12-31")
		private Date hired;
		private String name;
		private BigDecimal salary;

		public int getAge() {
			return age;
		}

		public void setAge(int age) {
			this.age = age;
		}

		public Date getHired() {
			return hired;
		}

		public void setHired(Date hired) {
			this.hired = hired;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public BigDecimal getSalary() {
			return salary;
		}

		public void setSalary(BigDecimal salary) {
			this.salary = salary;
		}
	}

	@Test
	public void beanMocker_fillsRandomTestData() {
		List<Employee> employees = BeanMocker.mockBeans(100, Employee.class, new MockContext());
		assertEquals(100, employees.size());
		for (Employee e : employees) {
			// random values, only the ranges are checked
			assertTrue(e.getAge() >= 18 && e.getAge() <= 60, "age: " + e.getAge());
			assertEquals(2020, DateUtils.getYear(e.getHired()));
			assertTrue(e.getName() != null && !e.getName().isEmpty());
			assertTrue(e.getSalary() != null);
		}
	}

	@Test
	public void randomStringAndObservable() {
		String code = RandomStringUtils.randomString(6, true, false, false); // random, e.g. "402917"
		assertTrue(code.matches("[0-9]{6}"), code);

		Observable observable = Observable.repeatable();
		List<Object> received = new ArrayList<Object>();
		observable.addObserver("order", (ob, arg) -> received.add(arg));
		observable.notifyObservers("order", "A001");
		observable.notifyObservers("order", "A002");
		assertEquals(Arrays.asList("A001", "A002"), received);

		Observable once = Observable.unrepeatable(); // observers are removed after the first notification
		List<Object> got = new ArrayList<Object>();
		once.addObserver((ob, arg) -> got.add(arg));
		once.notifyObservers("first");
		once.notifyObservers("second");
		assertEquals(Arrays.asList("first"), got);
	}

	@Test
	@Timeout(10)
	public void fileMonitor_detectsNewFiles(@TempDir File dir) throws Exception {
		CountDownLatch created = new CountDownLatch(1);
		List<String> names = new CopyOnWriteArrayList<String>();
		FileWatcher watcher = new FileWatcher(dir, -1, null);
		watcher.addListeners(new FileChangeListener() {

			@Override
			public void onFileCreate(FileEntry fileEntry) {
				names.add(fileEntry.getName());
				created.countDown();
			}

			@Override
			public void onFileDelete(FileEntry fileEntry) {
			}

			@Override
			public void onFileUpdate(FileEntry lastFileEntry, FileEntry fileEntry) {
			}

			@Override
			public void onDirectoryCreate(FileEntry fileEntry) {
			}

			@Override
			public void onDirectoryDelete(FileEntry fileEntry) {
			}

			@Override
			public void onDirectoryUpdate(FileEntry lastFileEntry, FileEntry fileEntry) {
			}
		});
		FileMonitor monitor = new FileMonitor(100, TimeUnit.MILLISECONDS); // check every 100 ms
		monitor.addWatchers(watcher);
		monitor.start();
		try {
			FileUtils.writeFile("hi", new File(dir, "new.txt"), false, StandardCharsets.UTF_8);
			assertTrue(created.await(5, TimeUnit.SECONDS));
			assertEquals(Arrays.asList("new.txt"), names);
		} finally {
			monitor.stop();
		}
	}
}
