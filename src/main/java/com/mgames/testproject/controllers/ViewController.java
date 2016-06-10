package com.mgames.testproject.controllers;

import com.mgames.testproject.Variables;
import com.mgames.testproject.controllers.response.OkResponse;
import com.mgames.testproject.controllers.response.Response;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 *
 * @author Constantine Tretyak
 */
@Controller
public class ViewController extends BaseController {

	private static final Logger log = LoggerFactory.getLogger(ViewController.class.getName());

	private Pattern crawlerPattern = Pattern.compile("bot|googlebot|crawler|spider|robot|crawling", Pattern.CASE_INSENSITIVE);

	@Autowired
	private Variables vars;

	@RequestMapping("{page:^(?!robots.txt$|favicon.ico$|netbeans-tomcat-status-test$).*}")
	public ModelAndView anyPage(ModelAndView mav,
								@PathVariable(value = "page") String page) {
		log.info("this page {}", page);
		addSettings(mav);
		addUser(mav);
		addGlobalData(mav);

		mav.setViewName("app");
		mav.addObject("PAGE", page);
		mav.addObject("PAGE_PATH", "static/app/" + page + "/" + page + ".html");
		if (crawlerPattern.matcher(getUserAgent()).find()) {
			mav.addObject("CRAWLER", true);
			log.info("Crawler detected. User agent: {}. Page {}", getUserAgent(), page);
		}
		mav.addObject("agent", getUserAgent());

		return mav;
	}

	@RequestMapping(value = "static/app/{page}/{page}.html")
	public ModelAndView getPageContent(ModelAndView mav, @PathVariable(value = "page") String page) {
		mav.setViewName("static/app/" + page + "/" + page);

		switch (page) {
			case "index":
				// add data to page's model
				break;
		}

		return mav;
	}

	@RequestMapping(value = "page_data/{page}", method = RequestMethod.GET)
	public Response getPageData(@PathVariable String page) {
		JSONObject pageData = new JSONObject();
		switch (page) {
			case "index":
				// add pageData
				break;
		}
		return new OkResponse(pageData);
	}

	@RequestMapping({"/", ""})
	public ModelAndView homePage(ModelAndView mav) {
		return anyPage(mav, "index");
	}

	private void addGlobalData(ModelAndView mav) {
		JSONObject globalData = new JSONObject();
		mav.addObject("globalData", globalData.toString());
	}

	private void addSettings(ModelAndView mav) {
		try {
			String base = vars.HOST;
			URL hostUrl = new URL(request.getScheme() + ":" + base);
			URL requestUrl = new URL(request.getRequestURL().toString());
			if (!hostUrl.getHost().equals(requestUrl.getHost())) {
				base = "//" + requestUrl.getHost() + "/";
			}

			Map settings = new HashMap<String, Object>() {
				{
					put("VK_APP_IFRAME_ID", vars.VK_APP_IFRAME_ID);
					put("VK_APP_IFRAME_PERMISSIONS", vars.VK_APP_IFRAME_PERMISSIONS);
					put("VK_APP_IFRAME_URL", vars.VK_APP_IFRAME_URL);

					put("VK_APP_WEBSITE_ID", vars.VK_APP_WEBSITE_ID);
					put("VK_APP_WEBSITE_PERMISSIONS", vars.VK_APP_WEBSITE_PERMISSIONS);
					put("VK_APP_API_VERISON", vars.VK_APP_API_VERISON);

					put("FB_APP_ID", vars.FB_APP_ID);
					put("FB_APP_PERMISSIONS", vars.FB_APP_PERMISSIONS);
					put("FB_APP_URL", vars.FB_APP_URL);
					put("FB_API_VERSION", vars.FB_API_VERSION);

					put("OK_APP_ID", vars.OK_APP_ID);
					put("OK_APP_SCOPE", vars.OK_APP_SCOPE);
					put("OK_APP_URL", vars.OK_APP_URL);

					put("HOST", vars.HOST);

					put("THIRD_PARTY_COOKIE_FIX", vars.THIRD_PARTY_COOKIE_FIX);
				}
			};
			settings.put("BASE", base);

			mav.addObject("settings", settings);
		} catch (MalformedURLException ex) {
			log.error("", ex);
		}
	}

	private void addUser(ModelAndView mav) {
		if (hasUser()) {
			mav.addObject("user", getUser());
		}
	}

}
