package com.github.paganini2008.devtools.beans.streaming;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

import com.github.paganini2008.devtools.date.DateUtils;

/**
 * 
 * Product
 * 
 * @author Fred Feng
 * 
 * @version 1.0
 */
public class Product {

	private int id;
	private String name;
	private String location;
	private Date created;
	private Date expired;
	private Float price;
	private BigInteger sales;
	private boolean export;
	private Long number;
	private BigDecimal freight;
	private Sort sort;
	private Admin admin = new Admin();

	public static class Admin {

		private String username;
		private String password;

		public String getUsername() {
			return username;
		}

		public void setUsername(String username) {
			this.username = username;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}

	}

	public Admin getAdmin() {
		return admin;
	}

	public void setAdmin(Admin admin) {
		this.admin = admin;
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

	public Sort getSort() {
		return sort;
	}

	public void setSort(Sort sort) {
		this.sort = sort;
	}

	public static enum Sort {

		HARD, SOFT;

	}

	@Override
	public String toString() {
		return "Product [id=" + id + ", name=" + name + ", location=" + location + ", created=" + DateUtils.format(created) + ", expired="
				+ DateUtils.format(expired) + ", price=" + price + ", sales=" + sales + ", export=" + export + ", number=" + number
				+ ", freight=" + freight + ", sort=" + sort + "]";
	}

}
