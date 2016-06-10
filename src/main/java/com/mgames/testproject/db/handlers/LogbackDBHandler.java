package com.mgames.testproject.db.handlers;

import com.mgames.testproject.dao.CommonsDao;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.commons.dbutils.ResultSetHandler;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Constantine Tretyak
 */
public class LogbackDBHandler implements ResultSetHandler<JSONObject> {

	private static final Logger log = LoggerFactory.getLogger(LogbackDBHandler.class);

	private final CommonsDao commonsDao;

	public LogbackDBHandler(CommonsDao commonsDao) {
		this.commonsDao = commonsDao;
	}

	@Override
	public JSONObject handle(ResultSet rs) throws SQLException {
		try {

			JSONArray errorsArray = new JSONArray();
			while (rs.next()) {
				JSONArray descriptionJSONArray = new JSONArray();
				for (String error : commonsDao.getLogbackErrorDescription(rs.getLong("event_id"))) {
					descriptionJSONArray.put(error);
				}
				errorsArray.put(new JSONObject()
						.put("event_id", rs.getLong("event_id"))
						.put("caller_class", rs.getString("caller_class"))
						.put("caller_method", rs.getString("caller_method"))
						.put("caller_line", rs.getString("caller_line"))
						.put("formatted_message", rs.getString("formatted_message"))
						.put("timestmp", rs.getLong("timestmp"))
						.put("description", descriptionJSONArray)
				);
			}
			return new JSONObject().put("errors", errorsArray);
		} catch (JSONException ex) {
			log.error("", ex);
			return null;
		}
	}

}
