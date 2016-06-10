package com.mgames.testproject.dao;

import com.mgames.testproject.db.handlers.LogbackDBHandler;
import com.mgames.testproject.db.handlers.StringsDBHandler;
import com.mgames.utils.db.MgDbPool;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.dbutils.handlers.ColumnListHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Андрей
 */
@Component
public class CommonsDao {

	private static final Logger log = LoggerFactory.getLogger(CommonsDao.class);

	@Autowired
	private transient MgDbPool dbPool;

	public void createVariable(String name, String value) {
		dbPool.update("INSERT INTO variables (name, variable) VALUES (?,?)", name, value);
	}

	public List<String> getLogbackErrorDescription(long eventId) {
		return dbPool.query("SELECT trace_line FROM logging_event_exception WHERE event_id = ? LIMIT 1", new ColumnListHandler<String>("trace_line"), eventId);
	}

	public JSONObject getLogbackPerDay() {
		long now = System.currentTimeMillis();
		return dbPool.query("SELECT event_id, formatted_message, timestmp, caller_class, caller_method, caller_line FROM logging_event "
				+ "WHERE timestmp >= ? AND timestmp <= ? ORDER BY timestmp DESC", new LogbackDBHandler(this), now - 1000 * 60 * 60 * 24, now);
	}

	public HashMap<String, String> getStrings() {
		return dbPool.query("SELECT * FROM strings", new StringsDBHandler());
	}

	public List<Map<String, Object>> getVariables() {
		return dbPool.query("SELECT * FROM variables", new MapListHandler());
	}

}
