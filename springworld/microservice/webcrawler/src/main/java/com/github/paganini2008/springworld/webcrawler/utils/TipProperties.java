package com.github.paganini2008.springworld.webcrawler.utils;

import java.sql.Connection;
import java.util.Iterator;
import java.util.Properties;

import javax.sql.DataSource;

import com.github.paganini2008.devtools.Assert;
import com.github.paganini2008.devtools.collection.RefreshingProperties;
import com.github.paganini2008.devtools.collection.Tuple;
import com.github.paganini2008.devtools.jdbc.DBUtils;

/**
 * 
 * TipProperties
 * 
 * @author Fred Feng
 * @create 2019-03
 */
public class TipProperties extends RefreshingProperties {

	private static final long serialVersionUID = 4433481696751426557L;

	private static final String SELECT_SQL = "select t.tip_code as code, t.tip_msg as msg from tip_info t";
	private DataSource dataSource;

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	protected Properties createObject() throws Exception {
		Assert.isNull(dataSource, "DataSource must be required.");
		Properties p = new Properties();
		Connection connection = null;
		try {
			connection = dataSource.getConnection();
			Iterator<Tuple> iterator = DBUtils.executeQuery(connection, SELECT_SQL);
			while (iterator.hasNext()) {
				Tuple t = iterator.next();
				p.setProperty((String) t.get("code"), (String) t.get("msg"));
			}
		} catch (Exception ignored) {
		} finally {
			DBUtils.closeQuietly(connection);
		}
		return p;
	}

}
