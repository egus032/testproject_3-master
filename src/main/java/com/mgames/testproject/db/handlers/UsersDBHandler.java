/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mgames.testproject.db.handlers;

import com.mgames.testproject.models.User;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.dbutils.ResultSetHandler;

/**
 *
 * @author MalyanovDmitry
 */
public class UsersDBHandler implements ResultSetHandler<List<User>> {

	@Override
	public List<User> handle(ResultSet rs) throws SQLException {
		List<User> list = new ArrayList<>();
		while (rs.next()) {
			list.add(new User(rs.getInt("id"),
							  rs.getString("social_id"),
							  rs.getString("lname"),
							  rs.getString("fname"),
							  rs.getString("sname"),
                                                          rs.getString("email"),
                                                          rs.getString("phone"),
                                                          rs.getString("repeatPhone"),
                                                          rs.getString("password"),
							  rs.getTimestamp("bdate"),
                                                          rs.getInt("sex"),
							  rs.getString("region"),
							  rs.getString("city"),
							  rs.getString("salt"),
                                                          rs.getString("photo")));
		}
		return list;
	}

}
