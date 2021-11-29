/**
* Copyright 2017-2021 Fred Feng (paganini.fy@gmail.com)

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
package com.github.paganini2008.devtools.beans.streaming.examples;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

import com.github.paganini2008.devtools.beans.DateRange;
import com.github.paganini2008.devtools.beans.DoubleRange;
import com.github.paganini2008.devtools.beans.FloatRange;
import com.github.paganini2008.devtools.beans.IntRange;
import com.github.paganini2008.devtools.beans.LongRange;
import com.github.paganini2008.devtools.beans.Recur;
import com.github.paganini2008.devtools.beans.ToStringBuilder;

/**
 * 
 * Product
 * 
 * @author Fred Feng
 * 
 * @since 2.0.1
 */
public class Product {

	@IntRange(from = 100000000, to = 1000000000)
	private int id;
	private String name;
	private String location;
	@DateRange(from = "2020-01-01", to = "2021-03-08")
	private Date created;
	@DateRange(from = "2020-01-01", to = "2021-10-12")
	private Date expired;
	@FloatRange(from = 100, to = 1000)
	private Float price;
	@LongRange(from = 0, to = 10)
	private BigInteger sales;
	private boolean export;
	@LongRange(from = 1, to = 1000l)
	private Long number;
	@DoubleRange(from = 50, to = 200, scale = 2)
	private BigDecimal freight;
	private Style style;

	@Recur
	private Salesman salesman;

	public static class Salesman {

		private String name;
		private String password;

		public Salesman() {
		}

		public Salesman(String name, String password) {
			this.name = name;
			this.password = password;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}

		@Override
		public String toString() {
			return ToStringBuilder.reflectionToString(this);
		}

	}

	public Salesman getSalesman() {
		return salesman;
	}

	public void setSalesman(Salesman salesman) {
		this.salesman = salesman;
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

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public Date getExpired() {
		return expired;
	}

	public void setExpired(Date expired) {
		this.expired = expired;
	}

	public Float getPrice() {
		return price;
	}

	public void setPrice(Float price) {
		this.price = price;
	}

	public BigInteger getSales() {
		return sales;
	}

	public void setSales(BigInteger sales) {
		this.sales = sales;
	}

	public boolean isExport() {
		return export;
	}

	public void setExport(boolean export) {
		this.export = export;
	}

	public Long getNumber() {
		return number;
	}

	public void setNumber(Long number) {
		this.number = number;
	}

	public BigDecimal getFreight() {
		return freight;
	}

	public void setFreight(BigDecimal freight) {
		this.freight = freight;
	}

	public Style getStyle() {
		return style;
	}

	public void setStyle(Style style) {
		this.style = style;
	}

	public static enum Style {

		HARD, SOFT;

	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
