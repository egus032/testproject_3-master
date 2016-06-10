/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mgames.testproject.models.interfaces;

import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Constantine Tretyak
 */
public interface JsonConvertible {
	public static JSONArray toJSONArray(List<? extends JsonConvertible> list) throws JSONException {
		JSONArray result = new JSONArray();
		for (JsonConvertible object : list) {
			result.put(object.toJson());
		}
		return result;
	}

	public JSONObject toJson() throws JSONException;

}
