package com.github.paganini2008.devtools.db4j.examples;

import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.paganini2008.devtools.collection.Tuple;
import com.github.paganini2008.devtools.db4j.ArraySqlParameter;
import com.github.paganini2008.devtools.db4j.BeanPropertySqlParameter;
import com.github.paganini2008.devtools.db4j.GeneratedKey;
import com.github.paganini2008.devtools.db4j.MapSqlParameter;
import com.github.paganini2008.devtools.db4j.SqlPlus;
import com.github.paganini2008.devtools.db4j.Transaction;
import com.github.paganini2008.devtools.jdbc.PageResponse;
import com.github.paganini2008.devtools.jdbc.PageableQuery;

/**
 * 
 * SqlPlusTester
 *
 * @author Fred Feng
 * @version 1.0
 */
public class SqlPlusTester {

	public static class Point {
		private String username;
		private int points;
		private int tag;
		private Date date;

		public String getUsername() {
			return username;
		}

		public void setUsername(String username) {
			this.username = username;
		}

		public int getPoints() {
			return points;
		}

		public void setPoints(int points) {
			this.points = points;
		}

		public int getTag() {
			return tag;
		}

		public void setTag(int tag) {
			this.tag = tag;
		}

		public Date getDate() {
			return date;
		}

		public void setDate(Date date) {
			this.date = date;
		}

	}

	/**
	 * Insert, Update, Delete in a transaction
	 * 
	 * @throws SQLException
	 */
	public static void testUpdateInTransaction() throws SQLException {
		String driverClassName = "com.mysql.cj.jdbc.Driver";
		String jdbcUrl = "jdbc:mysql://localhost:3306/test?userUnicode=true&characterEncoding=UTF8&useSSL=false&serverTimezone=UTC&autoReconnect=true&zeroDateTimeBehavior=convertToNull";
		String user = "fengy";
		String password = "123456";
		SqlPlus sqlPlus = new SqlPlus(driverClassName, jdbcUrl, user, password);
		Transaction transaction = null;
		try {
			transaction = sqlPlus.beginTransaction(); // Open transaction
			GeneratedKey generatedKey = GeneratedKey.auto();

			Point point = new Point();// Pojo
			point.setUsername("tester-14");
			point.setPoints(100);
			point.setTag(5);
			point.setDate(new Date());
			int effectedRows = transaction.update(
					"insert into tb_point (username,points,tag,last_modified) values ({username},{points},{tag},{date})",
					new BeanPropertySqlParameter(point), generatedKey); // bean mapping
			System.out.println("EffectedRows: " + effectedRows);
			System.out.println("Added id: " + generatedKey.getKey());

			Map<String, Object> parameterMap = new HashMap<String, Object>();
			parameterMap.put("points", 10);
			parameterMap.put("username", "tester-12");
			effectedRows = transaction.update("update tb_point set points=points+{points} where username={username}",
					new MapSqlParameter(parameterMap)); // Map mapping
			System.out.println("EffectedRows: " + effectedRows);

			//effectedRows = transaction.update("delete from tb_point where username!={0}", new ArraySqlParameter("tester-12")); // Array
																																// mapping
			//System.out.println("EffectedRows: " + effectedRows);
			transaction.commit();
		} catch (SQLException e) {
			transaction.rollback();
			throw e;
		} finally {
			transaction.close(); // Transaction end
		}
	}

	/**
	 * Query for List
	 * 
	 * @throws SQLException
	 */
	public static void testQuery() throws SQLException {
		String driverClassName = "com.mysql.cj.jdbc.Driver";
		String jdbcUrl = "jdbc:mysql://localhost:3306/test?userUnicode=true&characterEncoding=UTF8&useSSL=false&serverTimezone=UTC&autoReconnect=true&zeroDateTimeBehavior=convertToNull";
		String user = "fengy";
		String password = "123456";
		SqlPlus sqlPlus = new SqlPlus(driverClassName, jdbcUrl, user, password);
		List<Tuple> dataList = sqlPlus.queryForList("select * from tb_point", new Object[0]);
		dataList.forEach(tuple -> {
			System.out.println(tuple);
		});

	}

	/**
	 * Pageable Query
	 * 
	 * @throws SQLException
	 */
	public static void testPageableQuery() throws SQLException {
		String driverClassName = "com.mysql.cj.jdbc.Driver";
		String jdbcUrl = "jdbc:mysql://localhost:3306/test?userUnicode=true&characterEncoding=UTF8&useSSL=false&serverTimezone=UTC&autoReconnect=true&zeroDateTimeBehavior=convertToNull";
		String user = "fengy";
		String password = "123456";
		SqlPlus sqlPlus = new SqlPlus(driverClassName, jdbcUrl, user, password);
		PageableQuery<Tuple> pageableQuery = sqlPlus.queryForPage("select * from tb_point where points>{0}", new Object[] { 10 });
		for (PageResponse<Tuple> pageResponse : pageableQuery.forEach(1, 10)) {// Page start from 1
			System.out.println("Page: " + pageResponse.getPageNumber());
			pageResponse.getContent().forEach(tuple -> {
				System.out.println(tuple); // Iterator each record
			});
		}
	}

	public static void main(String[] args) throws Exception {
		// testQuery();
		testPageableQuery();
		//testUpdateInTransaction();
	}
}
