package com.mgames.testproject.controllers.response;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

/**
 *
 * @author Constantine Tretyak
 */
public class ErrorResponse extends Response {

	private static final Logger log = LoggerFactory.getLogger(ErrorResponse.class);

	private static String getErrorBody(Error error, String description) {
		if (description == null || description.isEmpty()) {
			description = error.name();
		}
		try {
			return new JSONObject().put("error", new JSONObject().put("code", error.code).put("description", description)).toString();
		} catch (JSONException ex) {
			log.error("", ex);
			return null;
		}
	}

	public ErrorResponse(Error error) {
		this(error, null, null);
	}

	public ErrorResponse(Error error, HttpHeaders customHeaders) {
		this(error, null, customHeaders);
	}

	public ErrorResponse(Error error, String description) {
		this(error, description, null);
	}

	public ErrorResponse(Error error, String description, HttpHeaders customHeaders) {
		super(getErrorBody(error, description), HttpStatus.BAD_REQUEST, customHeaders);
	}

	public enum Error {

		INVALIDATE_SIGNATURE(0),
		NO_SUCH_USER(1),
		PARSE_ARGUMENTS_ERROR(2),
		JSON_EXCEPTION(3),
		FILE_IS_NOT_IMAGE(4),
		SAVING_FILE_ERROR(5),
		INTERNAL_ERROR(6),
		EXTERNAL_ERROR(7),
		NO_OAUTH_OBJECT(8),
		USER_ALREADY_EXISTS(9);

		private final int code;

		private Error(int code) {
			this.code = code;
		}

	}

}
