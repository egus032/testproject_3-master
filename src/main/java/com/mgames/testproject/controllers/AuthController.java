package com.mgames.testproject.controllers;

import com.mashape.unirest.http.HttpMethod;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mgames.testproject.Variables;
import com.mgames.testproject.controllers.response.ErrorResponse;
import com.mgames.testproject.controllers.response.OkResponse;
import com.mgames.testproject.controllers.response.Response;
import com.mgames.testproject.managers.UsersManager;
import com.mgames.testproject.models.User;
import com.mgames.testproject.models.UserInfo;
import com.mgames.social.Network;
import com.mgames.social.OAuth2;
import com.mgames.social.fb.FbApi;
import com.mgames.social.ok.OkApi;
import com.mgames.social.vk.VkApi;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.PathParam;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 * <p>
 * @author Constantine Tretyak
 */
@Controller
@RequestMapping("api/auth")
public class AuthController extends BaseController {

	private static final Logger log = LoggerFactory.getLogger(AuthController.class);

	@Autowired
	private UsersManager usersManager;

	@Autowired
	private Variables vars;

	@RequestMapping(value = "get_user", method = RequestMethod.GET)
	public Response getCurrentUser() {
		if (!hasUser()) {
			return new ErrorResponse(ErrorResponse.Error.NO_SUCH_USER);
		}
		return new OkResponse(getUser().toJson());
	}

	@RequestMapping("oauth2.html")
	public String oauth2(HttpServletRequest request, @PathParam("state") String state, @PathParam("code") String code) {
		OAuth2 oAuth2 = null;
		try {
			String redirectUri = request.getScheme() + ":" + vars.HOST + "api/auth/oauth2.html";
			switch (state) {
				case "ok": {
					oAuth2 = new OkApi(vars.OK_APP_ID, vars.OK_APP_SECRET, redirectUri, code, vars.OK_APP_KEY);
				}
				break;
				case "vk": {
					oAuth2 = new VkApi(vars.VK_APP_WEBSITE_ID, vars.VK_APP_WEBSITE_SECRET, redirectUri, code);
				}
				break;
				case "fb": {
					oAuth2 = new FbApi(vars.FB_APP_ID, vars.FB_APP_SECRET, redirectUri, code);
				}
				break;
				case "fb_fix_openapi": {
					return "oauth2";
				}
			}
		} catch (UnirestException ex) {
			log.error("", ex);
		}

		if (oAuth2 != null) {
			session.setAttribute("oauth2_" + oAuth2.getNetwork(), oAuth2);
		}

		return "oauth2";
	}

	@RequestMapping(value = "oauth2/call/{net}/{method}", method = RequestMethod.POST)
	public Response oauth2Call(@PathVariable String net, @PathVariable String method, @RequestBody String requestBody) {
		OAuth2 oauth2 = OAuth2.valueOf(session.getAttribute("oauth2_" + net));
		if (oauth2 == null || !oauth2.isAuthorized()) {
			return new ErrorResponse(ErrorResponse.Error.NO_OAUTH_OBJECT);
		}
		try {
			return new OkResponse(new JSONObject(oauth2.call(method, new HashMap<>(parseRequestBody(requestBody)))));
		} catch (UnirestException ex) {
			log.error("", ex);
			return new ErrorResponse(ErrorResponse.Error.PARSE_ARGUMENTS_ERROR);
		}
	}

	@RequestMapping(value = "oauth2/is_authorized/{net}", method = RequestMethod.GET)
	public Response oauth2IsAuthorized(@PathVariable String net) {
		OAuth2 oauth2 = OAuth2.valueOf(session.getAttribute("oauth2_" + net));
		if (oauth2 == null || !oauth2.isAuthorized()) {
			return new OkResponse(new JSONObject().put("is_authorized", false));
		}
		return new OkResponse(new JSONObject().put("is_authorized", true));
	}

	@RequestMapping(value = "oauth2/sign_in/{net}", method = RequestMethod.PUT)
	public Response oauth2SignIn(@PathVariable String net) {
		OAuth2 oauth2 = OAuth2.valueOf(session.getAttribute("oauth2_" + net));
		if (oauth2 == null || !oauth2.isAuthorized()) {
			return new ErrorResponse(ErrorResponse.Error.NO_OAUTH_OBJECT);
		}
		JSONObject userInfo;
		try {
			userInfo = oauth2.getCurrentUser();
		} catch (UnirestException ex) {
			log.error("", ex);
			return new ErrorResponse(ErrorResponse.Error.INTERNAL_ERROR);
		}
		UserInfo.UserInfoBuilder userInfoBuilder = null;
		switch (Network.getNetwork(net)) {
			case VK:
				userInfoBuilder = UserInfo.UserInfoBuilder.vk(userInfo.get("uid").toString())
						.withLastName(userInfo.optString("last_name"))
						.withFirstName(userInfo.optString("first_name"))
						.withSex(userInfo.optInt("sex"))
						.withPhoto(userInfo.optString("photo_100"))
						.withBirthday(userInfo.optString("bdate"), "d.M.yyyy");
				break;
			case Facebook:
				userInfoBuilder = UserInfo.UserInfoBuilder.fb(userInfo.getString("id"))
						.withLastName(userInfo.optString("last_name"))
						.withFirstName(userInfo.optString("first_name"))
						.withSexMaleFemale(userInfo.optString("gender"))
						.withPhoto("https://graph.facebook.com/" + userInfo.getString("id") + "/picture?type=large")
						.withBirthday(userInfo.optString("birthday"), "M/d/yyyy");
				break;
			case OK:
				userInfoBuilder = UserInfo.UserInfoBuilder.ok(userInfo.get("uid").toString())
						.withLastName(userInfo.optString("last_name"))
						.withFirstName(userInfo.optString("first_name"))
						.withSexMaleFemale(userInfo.optString("gender"))
						.withPhoto(userInfo.optString("pic128x128"))
						.withBirthday(userInfo.optString("birthday"), "yyyy-M-d");
				break;
			default:
				return new ErrorResponse(ErrorResponse.Error.PARSE_ARGUMENTS_ERROR);
		}

		User user = usersManager.registerOrUpdate(session, response, userInfoBuilder.build(), false);
		return new OkResponse(new JSONObject().put("user", user.toJson()));
	}

	@RequestMapping(value = "openapi/sign_in/{net}", method = RequestMethod.PUT)
	public Response openapiSignIn(@RequestBody final String requestBody, @PathVariable String net) {
		JSONObject params = new JSONObject(requestBody);
		boolean success = false;
		switch (net) {
			case "vk":
				success = vkAuthOpenapi(params);
				break;
			case "fb":
				try {
					fbAuthOpenapi(params);
					success = true;
				} catch (JSONException | UnirestException ex) {
					log.error("", ex);
					return new ErrorResponse(ErrorResponse.Error.PARSE_ARGUMENTS_ERROR);
				}
				break;
			case "ok":
				success = okAuthIframe(params);
				break;
		}
		if (!success) {
			return new ErrorResponse(ErrorResponse.Error.PARSE_ARGUMENTS_ERROR);
		}

		return new OkResponse(new JSONObject().put("user", getUser().toJson()));
	}

	@RequestMapping(value = "sign_in", method = RequestMethod.PUT)
	public Response signIn(@RequestBody final String requestBody) {
		try {
			JSONObject params = new JSONObject(requestBody);

			String email = params.getString("email").trim().toLowerCase();
			User user = usersManager.get("email_" + email);
			if (user == null || !user.checkPassword(params.getString("password"))) {
				return new ErrorResponse(ErrorResponse.Error.NO_SUCH_USER);
			}
			usersManager.signIn(session, response, user, params.optBoolean("autologin"));

			return new OkResponse(new JSONObject().put("user", user.toJson()));
		} catch (JSONException ex) {
			log.error("", ex);
			return new ErrorResponse(ErrorResponse.Error.JSON_EXCEPTION);
		}
	}

	@RequestMapping(value = "sign_out", method = RequestMethod.DELETE)
	public Response signOut() {
		usersManager.signOut(session, response);
		return new OkResponse();
	}

	@RequestMapping(value = "sign_up", method = RequestMethod.POST)
	public Response signUp(@RequestBody final String requestBody) throws ParseException {
		try {
			JSONObject params = new JSONObject(requestBody);
			
			String lname = params.getString("lname").trim().toLowerCase();
			String fname = params.getString("fname").trim().toLowerCase();
			String sname = params.getString("sname").trim().toLowerCase();

			String email = params.getString("email").trim().toLowerCase();
			if (!email.matches("^\\S+@\\S+\\.\\S+[A-zА-я]$")) {
				return new ErrorResponse(ErrorResponse.Error.INTERNAL_ERROR);
			}
                        
                        String phone = params.getString("phone").trim().toLowerCase();
                        String repeatPhone = params.getString("repeatPhone").trim().toLowerCase();
			
			String password = params.getString("password");
			if (password.length() < 3) {
				return new ErrorResponse(ErrorResponse.Error.INTERNAL_ERROR);
			}
			
			final String OLD_FORMAT = "dd.MM.yyyy";
			final String NEW_FORMAT = "yyyy-MM-dd";
			String bdate = params.getString("bdate").trim().toLowerCase();
			SimpleDateFormat sdf = new SimpleDateFormat(OLD_FORMAT);
			Date d = sdf.parse(bdate);
			sdf.applyPattern(NEW_FORMAT);
			String bdateNew = sdf.format(d) + " 00:00:00.000000000";
			Timestamp birthday = Timestamp.valueOf(bdateNew);
                        
                        String sex = params.getString("sex").trim().toLowerCase();
                        Integer sexInt = Integer.valueOf(sex);
                        
                        String region = params.getString("region").trim().toLowerCase();
                        String city = params.getString("city").trim().toLowerCase();

			UserInfo userInfo = UserInfo.UserInfoBuilder.email(fname, lname, sname, email,  phone,
					   repeatPhone, password, birthday, sexInt,
					   region,
					   city)
					.build();

			if (usersManager.get(userInfo.getSocialId()) != null) {
				return new ErrorResponse(ErrorResponse.Error.USER_ALREADY_EXISTS);
			}

			User user = usersManager.registerOrUpdate(session, response, userInfo, params.optBoolean("autologin"));
			if (user == null) {
				return new ErrorResponse(ErrorResponse.Error.INTERNAL_ERROR);
			}

			return new OkResponse(new JSONObject()
					.put("user", user.toJson())
					
			);
		} catch (JSONException ex) {
			log.error("", ex);
			return new ErrorResponse(ErrorResponse.Error.JSON_EXCEPTION);
		}
	}

	@RequestMapping("/third_party_cookie_fix.html")
	public ModelAndView thirdPartyCookieFix(HttpServletResponse response, @PathParam("redirect") String redirect) {
		session.setAttribute("third_party_cookie_fixed", "true");
		Cookie cookie = new Cookie("third_party_cookie_fixed", "true");
		cookie.setPath("/");
		response.addCookie(cookie);
		response.setHeader("X-XSS-Protection", "0");
		response.setHeader("P3P", "CP=\"IDC DSP COR ADM DEVi TAIi PSA` PSD IVAi IVDi CONi HIS OUR IND CNT\"");

		return new ModelAndView("redirect:" + redirect);
	}

	@RequestMapping(value = "/third_party_cookie_fix_step1")
	public void thirdPartyCookieFixStep1(HttpServletResponse response) throws IOException {
		response.setHeader("Content-Type", "application/javascript; charset=UTF-8");
		response.setHeader("Cache-Control", "no-store");
		Cookie cookie = new Cookie("third_party_cookie_fixed", "true");
		response.addCookie(cookie);
		OutputStream os = response.getOutputStream();
		os.write("window.thirdPartyCookieStep1()".getBytes());
		os.close();
		os.flush();
	}

	@RequestMapping(value = "/third_party_cookie_fix_step2")
	public void thirdPartyCookieFixStep2(HttpServletResponse response, @CookieValue(value = "third_party_cookie_fixed", required = false, defaultValue = "false") String thirdPartyCookieFixed) throws IOException {
		response.setHeader("Content-Type", "application/javascript; charset=UTF-8");
		response.setHeader("Cache-Control", "no-store");
		OutputStream os = response.getOutputStream();
		os.write(("window.thirdPartyCookieStep2(" + thirdPartyCookieFixed + ")").getBytes());
		os.close();
		os.flush();
	}

	private void fbAuthOpenapi(JSONObject params) throws JSONException, UnirestException {
		FbApi fbApi = new FbApi(params.getString("access_token"));
		JSONObject fbInfo = new JSONObject(fbApi.callGraphApi("me", HttpMethod.GET, new HashMap<String, Object>() {
														  {
															  put("fields", "id,first_name,last_name,birthday,gender,email");
														  }
													  }));

		UserInfo.UserInfoBuilder userInfoBuilder = UserInfo.UserInfoBuilder.fb(fbInfo.getString("id"))
				.withLastName(fbInfo.optString("last_name"))
				.withFirstName(fbInfo.optString("first_name"))
				.withSexMaleFemale(fbInfo.optString("gender"))
				.withPhoto("https://graph.facebook.com/" + fbInfo.getString("id") + "/picture?type=large")
				.withBirthday(fbInfo.optString("birthday"), "M/d/yyyy");

		usersManager.registerOrUpdate(session, response, userInfoBuilder.build(), params.optBoolean("autologin"));
	}

	private boolean okAuthIframe(JSONObject params) throws JSONException {
		JSONObject okInfo = params.getJSONObject("user_info");
		if (!OkApi.checkAuth(okInfo.getString("uid"), params.getString("session_key"), vars.OK_APP_SECRET, params.getString("auth_sig"))) {
			return false;
		}

		UserInfo.UserInfoBuilder userInfoBuilder = UserInfo.UserInfoBuilder.ok(okInfo.getString("uid"))
				.withLastName(okInfo.optString("last_name"))
				.withFirstName(okInfo.optString("first_name"))
				.withSexMaleFemale(okInfo.optString("gender"))
				.withPhoto(okInfo.optString("pic128x128"))
				.withBirthday(okInfo.optString("birthday"), "yyyy-M-d");;

		usersManager.registerOrUpdate(session, response, userInfoBuilder.build(), params.optBoolean("autologin"));
		return true;
	}

	private boolean vkAuthOpenapi(JSONObject params) {
		String uid;
		if (params.getJSONObject("session").has("auth_key")) { // iframe
			if (!VkApi.checkAuth(params.getJSONObject("session").optString("api_id"),
								 params.getJSONObject("session").getString("viewer_id"),
								 params.getJSONObject("session").getString("auth_key"),
								 vars.VK_APP_IFRAME_SECRET)) {
				return false;
			}
			uid = params.getJSONObject("session").getString("viewer_id");
		} else {
			if (!VkApi.checkAuth(params.getJSONObject("session").optString("expire"),
								 params.getJSONObject("session").getString("mid"),
								 params.getJSONObject("session").getString("secret"),
								 params.getJSONObject("session").getString("sid"),
								 params.getJSONObject("session").getString("sig"),
								 vars.VK_APP_WEBSITE_SECRET)) {
				return false;
			}
			uid = params.getJSONObject("session").getString("mid");
		}

		JSONObject vkInfo = params.getJSONObject("user_info");

		UserInfo.UserInfoBuilder userInfoBuilder = UserInfo.UserInfoBuilder.vk(uid)
				.withLastName(vkInfo.optString("last_name"))
				.withFirstName(vkInfo.optString("first_name"))
				.withSex(vkInfo.optInt("sex"))
				.withPhoto(vkInfo.optString("photo_100"))
				.withBirthday(vkInfo.optString("bdate"), "d.M.yyyy");

		usersManager.registerOrUpdate(session, response, userInfoBuilder.build(), params.optBoolean("autologin"));

		return true;
	}

}
