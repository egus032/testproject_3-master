package com.mgames.testproject.controllers.response;

import java.util.Map;
import org.json.JSONObject;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

/**
 *
 * @author Constantine Tretyak
 */
public class OkResponse extends Response {

	public OkResponse() {
		this(new JSONObject());
	}

	public OkResponse(JSONObject answer) {
		super(answer.toString(), HttpStatus.OK);
	}

	public OkResponse(Map answer) {
		this(new JSONObject(answer));
	}

	public OkResponse(JSONObject answer, HttpHeaders customHeaders) {
		super(answer.toString(), HttpStatus.OK, customHeaders);
	}

	public OkResponse(Map answer, HttpHeaders customHeaders) {
		this(new JSONObject(answer), customHeaders);
	}

}
