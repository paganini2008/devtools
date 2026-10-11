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
package com.github.paganini2008.devtools.beans;

/**
 * TestBeans
 * 
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class TestBeans {

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

	public static class Person {
		private String name;
		private int age;
		private String email;
		private Address address;
		private int level;

		public Person() {
		}

		public Person(String name, Integer age) {
			this.name = name;
			this.age = age;
		}

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

		public Address getAddress() {
			return address;
		}

		public void setAddress(Address address) {
			this.address = address;
		}

		public String getId() {
			return "ID-" + name;
		}

		public int getLevel() {
			return level;
		}

		// not a standard setter because the type does not match the getter
		public void setLevel(String level) {
			this.level = Integer.parseInt(level) * 10;
		}
	}

	public static class Employee extends Person {
		private double salary;

		public double getSalary() {
			return salary;
		}

		public void setSalary(double salary) {
			this.salary = salary;
		}
	}

	public static class PersonDto {
		private String name;
		private String age;
		private String nickname = "keep";

		@PropertyMapper("address.city")
		private String city;

		@ExcludedProperty
		private String email = "untouched";

		private String phone;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getAge() {
			return age;
		}

		public void setAge(String age) {
			this.age = age;
		}

		public String getNickname() {
			return nickname;
		}

		public void setNickname(String nickname) {
			this.nickname = nickname;
		}

		public String getCity() {
			return city;
		}

		public void setCity(String city) {
			this.city = city;
		}

		public String getEmail() {
			return email;
		}

		public void setEmail(String email) {
			this.email = email;
		}

		public String getPhone() {
			return phone;
		}

		@PropertyMapper("email")
		public void setPhone(String phone) {
			this.phone = phone;
		}

		public String getId() {
			return "dto";
		}
	}

	public static class Failing {
		public String getValue() {
			throw new IllegalStateException("boom");
		}

		public void setValue(String value) {
			throw new IllegalStateException("boom");
		}
	}

	public static class NoProperty {
		private final int v;

		public NoProperty(int v) {
			this.v = v;
		}

		@Override
		public boolean equals(Object obj) {
			return obj instanceof NoProperty && ((NoProperty) obj).v == v;
		}

		@Override
		public int hashCode() {
			return v;
		}
	}

	public static class Exploding {
		public Exploding() {
			throw new IllegalStateException("cannot create");
		}
	}

}
