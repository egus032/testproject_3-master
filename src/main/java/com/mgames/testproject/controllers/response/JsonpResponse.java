/*
 */
package com.mgames.testproject.controllers.response;

/**
 *
 * @author Constantine Tretyak
 */
public class JsonpResponse extends Response {
	public JsonpResponse(Response response, String callback) {
		super(";" + callback + "(" + response.getBody() + ");", response.getStatusCode());
	}

}
