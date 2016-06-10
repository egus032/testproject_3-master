package com.mgames.testproject.controllers;

import com.mgames.testproject.managers.UsersManager;
import com.mgames.testproject.models.User;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author Andrey Tertichnikov
 */
public abstract class BaseController {

	private static final Logger log = LoggerFactory.getLogger(BaseController.class.getName());

	@Autowired
	private UsersManager usersManager;

	@Autowired
	protected HttpServletRequest request;

	@Autowired
	protected HttpServletResponse response;

	@Autowired
	protected HttpSession session;

	protected String getUserAgent() {
		return request.getHeader("user-agent");
	}

	protected String getIp() {
		String receiverIp = request.getHeader("X-Real-IP");
		if (receiverIp == null || receiverIp.equals("") || receiverIp.equals("127.0.0.1")) {
			String forwardedFor = request.getHeader("x-forwarded-for");
			if (forwardedFor != null) {
				String[] ips = forwardedFor.split(",");
				if (ips.length > 0) {
					receiverIp = ips[0].trim();
				}
			}
		}
		if (receiverIp == null || receiverIp.equals("") || receiverIp.equals("127.0.0.1")) {
			receiverIp = request.getRemoteAddr();
		}
		return receiverIp;
	}

	protected User getUser() {
		if (hasUser()) {
			return usersManager.get(request);
		}
		return null;
	}

	protected boolean hasUser() {
		return usersManager.has(request);
	}

	protected Map<String, String> parseRequestBody(String requestBody) {
		Map<String, String> map = new HashMap<>();
		try {
			JSONObject body = new JSONObject(requestBody);

			Iterator<?> keys = body.keys();
			while (keys.hasNext()) {
				String key = (String) keys.next();
				map.put(key, body.get(key).toString());
			}
		} catch (JSONException ex) {
			log.error("parse arguments error", ex);
			return map;
		}
		return map;
	}

}
