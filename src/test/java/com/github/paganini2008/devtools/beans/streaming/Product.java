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

/**
 * Product, a test bean for streaming queries
 * 
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class Product {

	public enum Style {
		SMALL, LARGE
	}

	public static class Salesman {

		private String name;

		public Salesman() {
		}

		public Salesman(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
	}

	private int id;
	private String name;
	private String location;
	private Style style;
	private Float price;
	private BigDecimal freight;
	private Integer sales;
	private Salesman salesman;

	public Product() {
	}

	public Product(int id, String name, String location, Style style, Float price, BigDecimal freight, Integer sales, String salesman) {
		this.id = id;
		this.name = name;
		this.location = location;
		this.style = style;
		this.price = price;
		this.freight = freight;
		this.sales = sales;
		this.salesman = salesman != null ? new Salesman(salesman) : null;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
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

	public Style getStyle() {
		return style;
	}

	public void setStyle(Style style) {
		this.style = style;
	}

	public Float getPrice() {
		return price;
	}

	public void setPrice(Float price) {
		this.price = price;
	}

	public BigDecimal getFreight() {
		return freight;
	}

	public void setFreight(BigDecimal freight) {
		this.freight = freight;
	}

	public Integer getSales() {
		return sales;
	}

	public void setSales(Integer sales) {
		this.sales = sales;
	}

	public Salesman getSalesman() {
		return salesman;
	}

	public void setSalesman(Salesman salesman) {
		this.salesman = salesman;
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof Product && ((Product) obj).id == id;
	}

	@Override
	public int hashCode() {
		return id;
	}

	@Override
	public String toString() {
		return "Product#" + id;
	}

	/**
	 * <pre>
	 * id  name  location style  price freight sales salesman
	 * 1   p1    London   SMALL  100   50      10    Petter
	 * 2   p2    London   LARGE  300   70      20    Jack
	 * 3   p3    Tokyo    SMALL  200   60      null  Petter
	 * 4   p4    Tokyo    SMALL  400   null    40    Tony
	 * 5   p5    Paris    LARGE  500   90      50    Jack
	 * 6   p6    London   SMALL  150   55      5     Tony
	 * </pre>
	 */
	public static java.util.List<Product> samples() {
		java.util.List<Product> list = new java.util.ArrayList<Product>();
		list.add(new Product(1, "p1", "London", Style.SMALL, 100f, new BigDecimal("50"), 10, "Petter"));
		list.add(new Product(2, "p2", "London", Style.LARGE, 300f, new BigDecimal("70"), 20, "Jack"));
		list.add(new Product(3, "p3", "Tokyo", Style.SMALL, 200f, new BigDecimal("60"), null, "Petter"));
		list.add(new Product(4, "p4", "Tokyo", Style.SMALL, 400f, null, 40, "Tony"));
		list.add(new Product(5, "p5", "Paris", Style.LARGE, 500f, new BigDecimal("90"), 50, "Jack"));
		list.add(new Product(6, "p6", "London", Style.SMALL, 150f, new BigDecimal("55"), 5, "Tony"));
		return list;
	}
}
