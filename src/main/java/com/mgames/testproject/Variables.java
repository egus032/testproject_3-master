package com.mgames.testproject;

import com.mgames.testproject.dao.CommonsDao;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Andrey Tertichnikov
 */
@Component
public class Variables {

	private static final Logger log = LoggerFactory.getLogger(Variables.class);

	/**
	 * We bind only public fields*
	 */
	public String FB_APP_ID = "373108039550983";

	public String FB_APP_SECRET = "a8a217516aa17980570c0b70aecc6d41";

	public String FB_APP_URL = "https://apps.facebook.com/373108039550983/";

	public String FB_APP_PERMISSIONS = "public_profile";

	public String FB_API_VERSION = "v2.3";

	public String VK_APP_IFRAME_ID = "4859309";

	public String VK_APP_IFRAME_SECRET = "1UZ5LXS3lFh4vIimbKKy";

	public String VK_APP_IFRAME_URL = "http://vk.com/app4859309";

	public String VK_APP_IFRAME_PERMISSIONS = "FRIENDS";

	public String VK_APP_WEBSITE_ID = "4859279";

	public String VK_APP_WEBSITE_SECRET = "jO6yYOgfocwMjDxCb3Pn";

	public String VK_APP_WEBSITE_PERMISSIONS = "FRIENDS";

	public String VK_APP_API_VERISON = "5.29";

	public String OK_APP_ID = "1105742080";

	public String OK_APP_SECRET = "CB9468B4726BE7CE1312899D";

	public String OK_APP_URL = "http://ok.ru/game/1105742080";

	public String OK_APP_SCOPE = "";

	public String OK_APP_KEY = "CBAKEJOCEBABABABA";

	public String HOST = "//localhost/";

	public String RESOURCES_FOLDER = "/srv/testproject/resources/";

	public String RESOURCES_HTTP_PATH = "//localhost/resources/";

	public boolean RESOURCES_USE_AMAZON_S3 = false;

	public boolean THIRD_PARTY_COOKIE_FIX = true;

	public String LOGBACK_REPORT_EMAILS = "teran@m-games-ltd.com";

	// Email settings
	public boolean USE_MAILGUN = false;

	public String MAILGUN_DOMAIN = "";

	public String MAILGUN_APIKEY = "";

	public String SMTP_HOST = "localhost";

	public int SMTP_PORT = 25;

	public String SMTP_USER = "";

	public String SMTP_PASSWORD = "";

	public boolean SMTP_SSL = false;

	public int USER_AUTOLOGIN_EXPIRED_SECONDS = 60 * 60 * 24 * 7;

	private final List<ChangeVariableListener> changeVariableListeners = new ArrayList<>();

	@Autowired
	private CommonsDao commonsDao;

	@PostConstruct
	public void reload() {
		//List<Map<String, Object>> dbVars = commonsDao.getVariables();
		Map<String, String> dbVars = new HashMap<>();
		commonsDao.getVariables().stream().forEach((row) -> {
			dbVars.put(row.get("name").toString(), row.get("variable").toString());
		});

		Field[] fields = this.getClass().getDeclaredFields();
		for (Field field : fields) {
			int mods = field.getModifiers();
			if (Modifier.isStatic(mods) || !Modifier.isPublic(mods)) {
				continue;
			}
			try {
				String fieldName = field.getName();
				Object fieldValue = field.get(this);

				String strValue = dbVars.get(fieldName);
				if (strValue != null) {
					Object newValue = null;

					if (fieldValue instanceof String) {
						newValue = strValue;
					} else if (fieldValue instanceof Integer) {
						newValue = Integer.parseInt(strValue);
					} else if (fieldValue instanceof Double) {
						newValue = Double.parseDouble(strValue);
					} else if (fieldValue instanceof Float) {
						newValue = Float.parseFloat(strValue);
					} else if (fieldValue instanceof Boolean) {
						newValue = Boolean.parseBoolean(strValue);
					} else if (fieldValue instanceof Long) {
						newValue = Long.parseLong(strValue);
					} else if (fieldValue instanceof Timestamp) {
						newValue = Timestamp.valueOf(strValue);
					}

					log.info("VARIABLE LOAD; name: " + field.getName() + "; value: " + strValue);

					field.set(this, newValue);
					if (!changeVariableListeners.isEmpty() && !fieldValue.equals(newValue)) {
						for (ChangeVariableListener listener : changeVariableListeners) {
							listener.onChange(fieldName, newValue, fieldValue);
						}
					}
				} else {
					// Create variable in database if not exist
					commonsDao.createVariable(fieldName, fieldValue.toString());
					log.info("Create var " + fieldName + " in database with value " + fieldValue.toString());
				}

			} catch (IllegalAccessException ex) {
				log.error("", ex);
			}
		}
	}

	public void addChangeVariableListener(ChangeVariableListener listener) {
		changeVariableListeners.add(listener);
	}

	public void removeChangeVariableListener(ChangeVariableListener listener) {
		changeVariableListeners.remove(listener);
	}

	public interface ChangeVariableListener {

		public void onChange(String name, Object newValue, Object oldValue);

	}

}
