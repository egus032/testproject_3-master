package com.mgames.testproject.db.handlers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import org.apache.commons.dbutils.ResultSetHandler;

/**
 *
 * @author MGames ltd
 */
public class StringsDBHandler implements ResultSetHandler<HashMap<String, String>> {
	@Override
	public HashMap<String, String> handle(ResultSet rs) throws SQLException {
		HashMap<String, String> map = new HashMap<>();
		while (rs.next()) {
			map.put(rs.getString("str_enum"), rs.getString("str"));
		}
		return map;
	}

}
