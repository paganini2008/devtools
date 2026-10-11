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
package com.github.paganini2008.devtools.beans.streaming;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Datasets, randomly generated (with a fixed seed) products and the plain JDK
 * implementations of the sql semantics, used as the oracle of the property
 * style tests.
 * 
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
final class Datasets {

	static final String[] LOCATIONS = { "London", "NewYork", "Tokyo", "HongKong", "Paris" };
	static final String[] SALESMEN = { "Petter", "Jack", "Tony" };

	private Datasets() {
	}

	/**
	 * Products with about 10% null locations, prices, freights, sales and
	 * salesmen. Prices are whole numbers so that ties are frequent.
	 */
	static List<Product> randomProducts(long seed, int count) {
		Random random = new Random(seed);
		List<Product> list = new ArrayList<Product>(count);
		for (int i = 0; i < count; i++) {
			String location = random.nextInt(10) == 0 ? null : LOCATIONS[random.nextInt(LOCATIONS.length)];
			Product.Style style = random.nextInt(10) == 0 ? null : Product.Style.values()[random.nextInt(2)];
			Float price = random.nextInt(10) == 0 ? null : Float.valueOf(1 + random.nextInt(100) * 10);
			BigDecimal freight = random.nextInt(10) == 0 ? null : BigDecimal.valueOf(5000 + random.nextInt(15000), 2);
			Integer sales = random.nextInt(10) == 0 ? null : random.nextInt(51);
			String salesman = random.nextInt(10) == 0 ? null : SALESMEN[random.nextInt(SALESMEN.length)];
			list.add(new Product(i + 1, "p" + (i + 1), location, style, price, freight, sales, salesman));
		}
		return list;
	}

	/**
	 * Groups like sql 'group by', keeping the encounter order of the keys and
	 * grouping the null keys together.
	 */
	static <K> Map<K, List<Product>> groupBy(List<Product> products, Function<Product, K> key) {
		Map<K, List<Product>> map = new LinkedHashMap<K, List<Product>>();
		for (Product p : products) {
			map.computeIfAbsent(key.apply(p), k -> new ArrayList<Product>()).add(p);
		}
		return map;
	}

	/** sum(sales), nulls ignored, zero for no value */
	static BigDecimal sumSales(List<Product> list) {
		return list.stream().map(Product::getSales).filter(Objects::nonNull).map(BigDecimal::valueOf).reduce(BigDecimal.ZERO,
				BigDecimal::add);
	}

	/** avg(freight), nulls ignored, null for no value */
	static BigDecimal avgFreight(List<Product> list, int scale, RoundingMode roundingMode) {
		List<BigDecimal> values = list.stream().map(Product::getFreight).filter(Objects::nonNull).collect(Collectors.toList());
		if (values.isEmpty()) {
			return null;
		}
		return values.stream().reduce(BigDecimal.ZERO, BigDecimal::add).divide(BigDecimal.valueOf(values.size()), scale, roundingMode);
	}

	/** max(price), nulls ignored, null for no value */
	static Float maxPrice(List<Product> list) {
		return list.stream().map(Product::getPrice).filter(Objects::nonNull).max(Comparator.naturalOrder()).orElse(null);
	}

	/** min(price), note: a null value is regarded as the minimum */
	static Float minPrice(List<Product> list) {
		if (list.stream().anyMatch(p -> p.getPrice() == null)) {
			return null;
		}
		return list.stream().map(Product::getPrice).min(Comparator.naturalOrder()).orElse(null);
	}

	/** compares like the library does: null is the smallest */
	static <T extends Comparable<T>> int compare(T a, T b) {
		if (a == null || b == null) {
			return a == b ? 0 : (a == null ? -1 : 1);
		}
		return Integer.signum(a.compareTo(b));
	}

	static <T extends Comparable<T>> Comparator<T> nullsFirst() {
		return Datasets::compare;
	}
}
