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
package com.github.paganini2008.devtools.collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Supplier;

import org.junit.jupiter.api.Test;

import com.github.paganini2008.devtools.MatchMode;
import com.github.paganini2008.devtools.MissingKeyException;

/**
 * MapUtilsTest
 * 
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class MapUtilsTest {

	private static Map<String, Integer> abc() {
		Map<String, Integer> m = new LinkedHashMap<String, Integer>();
		m.put("a", 1);
		m.put("b", 2);
		m.put("c", 3);
		return m;
	}

	@Test
	public void testFactories() {
		assertTrue(MapUtils.emptyMap().isEmpty());
		Map.Entry<String, Integer> e = MapUtils.singletonEntry("a", 1);
		assertEquals("a", e.getKey());
		assertEquals(Integer.valueOf(1), e.getValue());
		assertEquals(new HashMap<String, Integer>(Collections.singletonMap("a", 1)).entrySet().iterator().next(), e);
		assertEquals(e, MapUtils.singletonEntry("a", 1));
		assertEquals(e, e);
		assertNotEquals(e, "a=1");
		assertNotEquals(e, MapUtils.singletonEntry("a", 2));
		assertEquals(e.hashCode(), MapUtils.singletonEntry("a", 1).hashCode());
		assertEquals("a".hashCode(), MapUtils.singletonEntry("a", null).hashCode());
		assertEquals("a=1", e.toString());
		assertThrows(UnsupportedOperationException.class, () -> e.setValue(2));
		assertThrows(IllegalArgumentException.class, () -> MapUtils.singletonEntry(null, 1));
		Map<String, Integer> m = MapUtils.synchronizedHashMap(4, 0.5F);
		m.put("a", 1);
		assertTrue(MapUtils.synchronizedHashMap(4).isEmpty());
		assertTrue(MapUtils.synchronizedHashMap().isEmpty());
		assertEquals(m, MapUtils.synchronizedHashMap(m));
		assertTrue(MapUtils.synchronizedLinkedHashMap().isEmpty());
		assertEquals("{a=1, b=2, c=3}", MapUtils.synchronizedLinkedHashMap(abc()).toString());
		Map<String, Integer> access = MapUtils.synchronizedLinkedHashMap(4, 0.75F, true);
		access.putAll(abc());
		access.get("a");
		assertEquals("{b=2, c=3, a=1}", access.toString());
	}

	@Test
	public void testBoundedLinkedHashMap() {
		final Map<String, Integer> evicted = new LinkedHashMap<String, Integer>();
		Map<String, Integer> m = MapUtils.synchronizedLinkedHashMap(4, 2, (k, v) -> evicted.put(k, v));
		m.put("a", 1);
		m.put("b", 2);
		m.get("a");
		m.put("c", 3);
		assertEquals("{a=1, c=3}", m.toString());
		assertEquals("{b=2}", evicted.toString());
		Map<String, Integer> noListener = MapUtils.synchronizedLinkedHashMap(4, 1, null);
		noListener.put("a", 1);
		noListener.put("b", 2);
		assertEquals("{b=2}", noListener.toString());
		assertThrows(IllegalArgumentException.class, () -> MapUtils.synchronizedLinkedHashMap(4, 0, null));
	}

	@Test
	public void testSingletonMap() {
		assertEquals("{a=1}", MapUtils.singletonMap(abc()).toString());
		assertEquals("{c=3}", MapUtils.singletonMap(abc(), false).toString());
		assertThrows(IllegalArgumentException.class, () -> MapUtils.singletonMap(new HashMap<String, Integer>()));
		assertTrue(MapUtils.isMap(abc()));
		assertFalse(MapUtils.isMap(null));
		assertTrue(MapUtils.isNotMap("a"));
	}

	@Test
	public void testGetters() {
		Map<String, Object> m = new HashMap<String, Object>();
		m.put("a", 1);
		m.put("s", "12");
		assertEquals(1, MapUtils.getIfRequired(m, "a"));
		assertThrows(MissingKeyException.class, () -> MapUtils.getIfRequired(m, "z"));
		assertThrows(IllegalStateException.class, () -> MapUtils.getIfRequired(null, "z", new IllegalStateException()));
		assertEquals(1, MapUtils.get(m, "a"));
		assertNull(MapUtils.get(m, "z"));
		Map<String, String> sm = new HashMap<String, String>();
		sm.put("a", "1");
		Map<String, String> none = null;
		assertEquals("d", MapUtils.getOrDefault(sm, "z", "d"));
		assertEquals("d", MapUtils.getOrDefault(none, "z", "d"));
		assertEquals("1", MapUtils.getOrDefault(sm, "a", "d"));
		Supplier<String> supplier = () -> "s";
		assertEquals("s", MapUtils.getOrDefault(sm, "z", supplier));
		assertEquals("s", MapUtils.getOrDefault(none, "z", supplier));
		assertEquals("1", MapUtils.getOrDefault(sm, "a", supplier));
		Function<String, String> function = k -> k + k;
		assertEquals("zz", MapUtils.getOrDefault(sm, "z", function));
		assertEquals("zz", MapUtils.getOrDefault(none, "z", function));
		assertEquals("1", MapUtils.getOrDefault(sm, "a", function));
	}

	@Test
	public void testGetWithRequiredType() {
		Map<String, Object> m = new HashMap<String, Object>();
		m.put("i", 5);
		m.put("s", "12");
		assertEquals(Integer.valueOf(5), MapUtils.get(m, "i", Integer.class, 0));
		assertEquals(Integer.valueOf(12), MapUtils.get(m, "s", Integer.class, 0));
		assertEquals(Integer.valueOf(7), MapUtils.get(m, "missing", Integer.class, 7));
		assertEquals(Integer.valueOf(7), MapUtils.get(null, "missing", Integer.class, 7));
		assertNull(MapUtils.get(m, "missing", Integer.class, null));
	}

	@Test
	public void testGetOrCreate() throws Exception {
		Map<String, Integer> m = new HashMap<String, Integer>();
		assertEquals(Integer.valueOf(1), MapUtils.get(m, "a", (Supplier<Integer>) () -> 1));
		assertEquals(Integer.valueOf(1), MapUtils.get(m, "a", (Supplier<Integer>) () -> 2));
		assertEquals(Integer.valueOf(3), MapUtils.get(m, "abc", (Function<String, Integer>) String::length));
		assertEquals(Integer.valueOf(3), MapUtils.get(m, "abc", (Function<String, Integer>) k -> 0));
		assertNull(MapUtils.get((Map<String, Integer>) null, "a", (Supplier<Integer>) () -> 1));
		assertNull(MapUtils.get((Map<String, Integer>) null, "a", (Function<String, Integer>) k -> 1));

		// the supplier must be invoked once per key even when called concurrently
		for (final Map<String, Integer> map : Arrays.<Map<String, Integer>>asList(new ConcurrentHashMap<String, Integer>(),
				MapUtils.synchronizedHashMap())) {
			final AtomicInteger supplied = new AtomicInteger();
			final AtomicInteger applied = new AtomicInteger();
			final CountDownLatch start = new CountDownLatch(1);
			final CountDownLatch done = new CountDownLatch(8);
			for (int t = 0; t < 8; t++) {
				new Thread(() -> {
					try {
						start.await();
						for (int i = 0; i < 100; i++) {
							MapUtils.get(map, "k" + i, (Supplier<Integer>) () -> supplied.incrementAndGet());
							MapUtils.get(map, "f" + i, (Function<String, Integer>) k -> applied.incrementAndGet());
						}
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();
					} finally {
						done.countDown();
					}
				}).start();
			}
			start.countDown();
			assertTrue(done.await(10, TimeUnit.SECONDS));
			assertEquals(100, supplied.get());
			assertEquals(100, applied.get());
		}
	}

	@Test
	public void testConversions() {
		Map<String, String> pairs = new LinkedHashMap<String, String>();
		pairs.put("a", "1");
		assertEquals("[a, 1]", MapUtils.toList(pairs).toString());
		assertTrue(MapUtils.toList(null).isEmpty());
		assertEquals(3, MapUtils.toEntries(abc()).size());
		assertTrue(MapUtils.toEntries(null).isEmpty());
		assertEquals("a", MapUtils.getFirstEntry(abc()).getKey());
		assertEquals("c", MapUtils.getLastEntry(abc()).getKey());
		assertEquals("b", MapUtils.getEntry(abc(), 1).getKey());
		assertEquals("c", MapUtils.getEntry(abc(), -1).getKey());
		assertNull(MapUtils.getEntry(null, 0));
		assertEquals("b", MapUtils.getKey(abc(), 2));
		assertNull(MapUtils.getKey(abc(), 9));
		assertNull(MapUtils.getKey(null, 9));
	}

	@Test
	public void testPutRemoveRetain() {
		Map<String, Integer> m = new LinkedHashMap<String, Integer>();
		MapUtils.putAll(m, abc().entrySet());
		MapUtils.putAll(m, (java.util.Collection<Map.Entry<String, Integer>>) null);
		@SuppressWarnings("unchecked")
		Map.Entry<String, Integer>[] array = new Map.Entry[] { MapUtils.singletonEntry("d", 4) };
		MapUtils.putAll(m, array);
		assertEquals("{a=1, b=2, c=3, d=4}", m.toString());
		MapUtils.removeFirstEntry(m);
		MapUtils.removeLastEntry(m);
		assertEquals("{b=2, c=3}", m.toString());
		Map<String, Integer> empty = new HashMap<String, Integer>();
		MapUtils.removeFirstEntry(empty);
		MapUtils.removeLastEntry(empty);
		MapUtils.removeFirstEntry(null);
		Map<String, Integer> m2 = abc();
		MapUtils.removeKeys(m2, Arrays.asList("a"));
		MapUtils.removeKeys(m2, new Object[] { "b" });
		assertEquals("{c=3}", m2.toString());
		Map<String, Integer> m3 = abc();
		MapUtils.retainKeys(m3, Arrays.asList("a", "b"));
		MapUtils.retainKeys(m3, new Object[] { "a" });
		assertEquals("{a=1}", m3.toString());
		Map<String, Integer> m4 = abc();
		MapUtils.retainValues(m4, Arrays.asList(1, 2));
		MapUtils.retainValues(m4, new Object[] { 2 });
		assertEquals("{b=2}", m4.toString());
		assertThrows(IllegalArgumentException.class, () -> MapUtils.removeKeys(null, new Object[0]));
	}

	@Test
	public void testToMap() {
		assertEquals("{a=1, b=2}", MapUtils.toMap(new String[] { "a", "b" }, new Integer[] { 1, 2 }).toString());
		assertEquals("{a=1, b=null}", MapUtils.toMap(new String[] { "a", "b" }, new Integer[] { 1 }).toString());
		assertEquals("{a=1, null=2}", MapUtils.toMap(new String[] { "a" }, new Integer[] { 1, 2 }).toString());
		assertEquals("{a=1}", MapUtils.toMap("a", 1).toString());
		assertEquals("{a=1, b=2}", MapUtils.toMap("a", 1, "b", 2).toString());
		assertEquals("{a=1, b=2, c=3}", MapUtils.toMap("a", 1, "b", 2, "c", 3).toString());
		assertEquals("{a=b, c=null}", MapUtils.toMap("a", "b", "c").toString());
		assertEquals("{null=x, y=null, z=null}", MapUtils.toMap(null, "x", "y", null, "z").toString());
		assertTrue(MapUtils.toMap((Object[]) null).isEmpty());
		assertEquals("{a=b, c=null}", MapUtils.toMap(Arrays.asList("a", "b", "c")).toString());
		assertEquals("{null=x}", MapUtils.toMap(Arrays.asList(null, "x")).toString());
		assertTrue(MapUtils.toMap(Collections.<String>emptyList()).isEmpty());
		assertTrue(MapUtils.toMap((java.util.Iterator<String>) null).isEmpty());
		assertEquals("{a=b, null=d}", MapUtils.toMap(Collections.enumeration(Arrays.asList("a", "b", null, "d"))).toString());
		assertEquals("{x=null}", MapUtils.toMap(Collections.enumeration(Arrays.asList("x"))).toString());
		assertTrue(MapUtils.toMap((java.util.Enumeration<String>) null).isEmpty());
	}

	@Test
	public void testTransformations() {
		assertEquals("{c=3, b=2, a=1}", MapUtils.reverse(abc()).toString());
		assertTrue(MapUtils.reverse(null).isEmpty());
		assertEquals("{1=a, 2=b, 3=c}", MapUtils.exchange(abc()).toString());
		assertTrue(MapUtils.exchange(null).isEmpty());
		Map<String, Integer> other = MapUtils.toMap("c", 30, "d", 4);
		assertEquals("{a=1, b=2, c=30, d=4}", MapUtils.union(abc(), other).toString());
		assertSame(other, MapUtils.union(null, other));
		assertEquals(abc(), MapUtils.union(abc(), null));
		assertNull(MapUtils.union(null, null));
		assertEquals("{a=1, b=2}", MapUtils.minus(abc(), other).toString());
		assertSame(other, MapUtils.minus(null, other));
		assertEquals(abc(), MapUtils.minus(abc(), null));
		assertNull(MapUtils.minus(null, null));
		assertEquals("{c=3}", MapUtils.intersect(abc(), other).toString());
		assertNull(MapUtils.intersect(abc(), null));
		assertEquals("a=1,b=2,c=3", MapUtils.join(abc(), "=", ","));
		assertEquals("a,1,b,2,c,3", MapUtils.join(abc()));
		assertEquals("a;1;b;2;c;3", MapUtils.join(abc(), ";"));
		assertEquals("a1b2c3", MapUtils.join(abc(), null, null));
		assertEquals("", MapUtils.join(null));
		assertEquals("", MapUtils.join(new HashMap<String, String>()));
		assertEquals("{a=1,b=2,c=3}", MapUtils.toString(abc()));
		assertTrue(MapUtils.isEmpty(null));
		assertTrue(MapUtils.isNotEmpty(abc()));
		assertEquals("{a=<1>, b=<2>, c=<3>}", MapUtils.formats(abc(), "<%d>").toString());
	}

	@Test
	public void testCompareAndEquals() {
		Map<String, Integer> m = abc();
		assertNull(MapUtils.compareDifference(m, m));
		assertSame(m, MapUtils.compareDifference(null, m));
		assertSame(m, MapUtils.compareDifference(m, null));
		Map<String, Integer> changed = abc();
		changed.put("b", 20);
		assertEquals("{b=2}", MapUtils.compareDifference(m, changed).toString());
		assertTrue(MapUtils.deepEquals(m, m));
		assertTrue(MapUtils.deepEquals(m, abc()));
		assertFalse(MapUtils.deepEquals(m, changed));
		assertFalse(MapUtils.deepEquals(m, null));
		assertFalse(MapUtils.deepEquals(m, MapUtils.toMap("a", 1)));
		assertEquals(MapUtils.deepHashCode(m), MapUtils.deepHashCode(abc()));
		assertEquals(0, MapUtils.deepHashCode(null));
	}

	@Test
	public void testSort() {
		Map<String, Integer> m = MapUtils.toMap("b", 1, "a", 3, "c", 2);
		assertEquals("{a=3, b=1, c=2}", MapUtils.sortByKey(m).toString());
		assertEquals("{c=2, b=1, a=3}", MapUtils.sortByKey(m, true).toString());
		assertEquals("{b=1, c=2, a=3}", MapUtils.sortByValue(m).toString());
		assertEquals("{a=3, c=2, b=1}", MapUtils.sortByValue(m, true).toString());
		Map<Object, Object> mixed = new LinkedHashMap<Object, Object>();
		mixed.put(new Object(), new Object());
		mixed.put("x", "y");
		assertEquals(2, MapUtils.sortByKey(mixed).size());
		assertEquals(2, MapUtils.sortByValue(mixed).size());
		Map<String, Integer> empty = new HashMap<String, Integer>();
		assertSame(empty, MapUtils.sort(empty, MapUtils.<String, Integer>keyComparator()));
	}

	@Test
	public void testProperties() {
		Properties left = new Properties();
		left.setProperty("a", "1");
		left.setProperty("b", "2");
		Properties right = new Properties();
		right.setProperty("b", "20");
		right.setProperty("c", "3");
		Map<String, String> output = new HashMap<String, String>();
		Map<String, String> prevs = MapUtils.mergeProperties(left, right, output);
		assertEquals("{b=2}", prevs.toString());
		assertEquals("20", output.get("b"));
		assertEquals(3, output.size());
		Map<String, String> output2 = new HashMap<String, String>();
		assertTrue(MapUtils.mergeProperties(new Properties(), right, output2).isEmpty());
		assertEquals(2, output2.size());
		Map<String, String> output3 = new HashMap<String, String>();
		MapUtils.mergeProperties(left, null, output3);
		assertEquals(2, output3.size());

		assertTrue(MapUtils.toMap(new Properties()).isEmpty());
		assertEquals("1", MapUtils.toMap(left).get("a"));
		Properties db = new Properties();
		db.setProperty("db.url", "jdbc");
		db.setProperty("db.user", "sa");
		db.setProperty("db", "root");
		db.setProperty("other", "x");
		Map<String, String> sub = MapUtils.toMap(db, "db");
		assertEquals(2, sub.size());
		assertEquals("jdbc", sub.get("url"));
		Properties subProps = new Properties();
		MapUtils.copyProperties(db, "db", subProps);
		assertEquals("sa", subProps.getProperty("user"));
		assertEquals(2, subProps.size());
		assertEquals(3, MapUtils.toMap(db, "db", MatchMode.START).size());
		Properties matched = new Properties();
		MapUtils.copyProperties(db, "user", MatchMode.END, matched);
		assertEquals("{db.user=sa}", matched.toString());
		MapUtils.copyProperties(db, "user", null, matched);
		assertEquals(1, matched.size());
		Properties copy = new Properties();
		MapUtils.copyProperties(db, copy);
		assertEquals(db, copy);
		Map<String, String> plain = new LinkedHashMap<String, String>();
		plain.put("a", "1");
		plain.put("b", null);
		Properties props = MapUtils.toProperties(plain);
		assertEquals("{a=1}", props.toString());
	}

	@Test
	public void testMatchedMaps() {
		Map<String, String> m = MapUtils.toMap("apple", "red", "banana", "yellow", "apricot", "orange");
		assertEquals("{apple=red, apricot=orange}", MapUtils.toMatchedKeyMap(m, "ap", MatchMode.START).toString());
		assertEquals("{banana=yellow}", MapUtils.toMatchedValueMap(m, "yel", MatchMode.START).toString());
		assertTrue(MapUtils.toMatchedKeyMap(m, "ap", null).isEmpty());
		assertTrue(MapUtils.toMatchedValueMap(m, "ap", null).isEmpty());
	}

	@Test
	public void testPopulateAndSingleValueMap() {
		assertEquals("{a=1, b=null}", MapUtils.populate("a", 1, "b").toString());
		assertEquals("{a=1}", MapUtils.populate("a", 1).toString());
		Map<String, Integer[]> multi = new LinkedHashMap<String, Integer[]>();
		multi.put("a", new Integer[] { 1, 2 });
		multi.put("b", new Integer[0]);
		assertEquals("{a=1, b=null}", MapUtils.toSingleValueMap(multi).toString());
		assertEquals("{a=2, b=null}", MapUtils.toSingleValueMap(multi, false).toString());
		assertTrue(MapUtils.toSingleValueMap(null).isEmpty());
	}

}
