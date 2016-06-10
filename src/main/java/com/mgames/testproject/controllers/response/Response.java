package com.mgames.testproject.controllers.response;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 *
 * @author Constantine Tretyak
 */
abstract public class Response extends ResponseEntity<String> {

	public static HttpHeaders getCommonHeaders() {
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("Content-Type", "application/json; charset=utf-8");
		responseHeaders.add("X-XSS-Protection", "0");
		responseHeaders.set("P3P", "CP=\"IDC DSP COR ADM DEVi TAIi PSA` PSD IVAi IVDi CONi HIS OUR IND CNT\"");
		responseHeaders.set("Cache-Control", "no-cache, no-store, must-revalidate");
		responseHeaders.set("Pragma", "no-cache");
		responseHeaders.set("Expires", "0");
		return responseHeaders;
	}

	public Response(String body, HttpStatus statusCode) {
		this(body, statusCode, null);
	}

	public Response(String body, HttpStatus statusCode, HttpHeaders customHeaders) {
		super(body, customHeaders != null ? customHeaders : getCommonHeaders(), statusCode);
	}

}
