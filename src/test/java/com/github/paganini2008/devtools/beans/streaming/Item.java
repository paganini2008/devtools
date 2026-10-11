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
import java.util.Date;

/**
 * Item, a test bean with properties of various types and a three-level nested
 * property path (owner.address.city) for the type conversion and nested
 * property specifications.
 * 
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class Item {

	public static class Address {

		private String city;
		private Integer zip;

		public Address() {
		}

		public Address(String city, Integer zip) {
			this.city = city;
			this.zip = zip;
		}

		public String getCity() {
			return city;
		}

		public void setCity(String city) {
			this.city = city;
		}

		public Integer getZip() {
			return zip;
		}

		public void setZip(Integer zip) {
			this.zip = zip;
		}
	}

	public static class Owner {

		private String name;
		private Address address;

		public Owner() {
		}

		public Owner(String name, Address address) {
			this.name = name;
			this.address = address;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public Address getAddress() {
			return address;
		}

		public void setAddress(Address address) {
			this.address = address;
		}
	}

	private int id;
	private Integer qty;
	private Long big;
	private String code;
	private Double ratio;
	private BigDecimal amount;
	private Date created;
	private Owner owner;

	public Item() {
	}

	public Item(int id, Integer qty, Long big, String code, Double ratio, BigDecimal amount, Owner owner) {
		this.id = id;
		this.qty = qty;
		this.big = big;
		this.code = code;
		this.ratio = ratio;
		this.amount = amount;
		this.owner = owner;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Integer getQty() {
		return qty;
	}

	public void setQty(Integer qty) {
		this.qty = qty;
	}

	public Long getBig() {
		return big;
	}

	public void setBig(Long big) {
		this.big = big;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Double getRatio() {
		return ratio;
	}

	public void setRatio(Double ratio) {
		this.ratio = ratio;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public Owner getOwner() {
		return owner;
	}

	public void setOwner(Owner owner) {
		this.owner = owner;
	}

	@Override
	public String toString() {
		return "Item#" + id;
	}

	/**
	 * <pre>
	 * id qty  big   code   ratio amount owner.name owner.address.city owner.address.zip
	 * 1  10   10    "10"   1.5   1.10   Ann        Paris              75000
	 * 2  20   20    "9"    2.5   2.20   Bob        London             null
	 * 3  null 30    "abc"  null  null   Cat        (address null)
	 * 4  5    null  null   0.5   0.30   (owner null)
	 * 5  30   40    "30"   3.0   3.335  Dan        Paris              75001
	 * </pre>
	 */
	public static java.util.List<Item> samples() {
		java.util.List<Item> list = new java.util.ArrayList<Item>();
		list.add(new Item(1, 10, 10L, "10", 1.5, new BigDecimal("1.10"), new Owner("Ann", new Address("Paris", 75000))));
		list.add(new Item(2, 20, 20L, "9", 2.5, new BigDecimal("2.20"), new Owner("Bob", new Address("London", null))));
		list.add(new Item(3, null, 30L, "abc", null, null, new Owner("Cat", null)));
		list.add(new Item(4, 5, null, null, 0.5, new BigDecimal("0.30"), null));
		list.add(new Item(5, 30, 40L, "30", 3.0, new BigDecimal("3.335"), new Owner("Dan", new Address("Paris", 75001))));
		return list;
	}
}
