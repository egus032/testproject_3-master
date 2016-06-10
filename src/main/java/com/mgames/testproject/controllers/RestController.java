package com.mgames.testproject.controllers;

import com.mgames.testproject.Strings;
import com.mgames.testproject.Variables;
import com.mgames.testproject.controllers.response.ErrorResponse;
import com.mgames.testproject.controllers.response.ErrorResponse.Error;
import com.mgames.testproject.controllers.response.OkResponse;
import com.mgames.testproject.controllers.response.Response;
import com.mgames.testproject.managers.UsersManager;
import com.mgames.testproject.resolvers.FileSaverResolver;
import com.mgames.utils.filesaver.FileSaver;
import com.mgames.utils.filesaver.SavingPreset;
import java.io.IOException;
import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

/**
 <p>
 @author Andrey Tertichnikov
 */
@Controller
@RequestMapping("api")
public class RestController extends BaseController {

	private static final Logger log = LoggerFactory.getLogger(RestController.class.getName());

	@Autowired
	private FileSaverResolver fileSaverResolver;

	@Autowired
	private Strings strings;

	@Autowired
	private UsersManager usersManager;

	@Autowired
	private Variables vars;

	

	@RequestMapping(value = "/multipart", method = RequestMethod.POST)
	public ResponseEntity<String> multipart(@RequestParam(value = "textparam", required = false) final String textparam, @RequestParam(value = "fileparam", required = false) final MultipartFile file) {
		HttpHeaders customHeaders = new HttpHeaders();  // for IE
		customHeaders.add("Content-Type", "text/plain; charset=utf-8");
		customHeaders.add("X-XSS-Protection", "0");
		customHeaders.set("P3P", "CP=\"IDC DSP COR ADM DEVi TAIi PSA` PSD IVAi IVDi CONi HIS OUR IND CNT\"");
		if (textparam.isEmpty()) {
			return new ErrorResponse(Error.PARSE_ARGUMENTS_ERROR, "Поле textparam не заполнено. Пример обработки ошибки на сервере");
		}

		try {
			SavingPreset preset = SavingPreset.newBuilder()
					.folder("my_avas")
					.build();
			FileSaver fileSaver = fileSaverResolver.get();
			String filename = fileSaver.getHttpPath(fileSaverResolver.get().save(file.getInputStream(), file.getOriginalFilename(), preset), preset);

			return new OkResponse(new JSONObject()
					.put("textparam", textparam)
					.put("new_file", filename), customHeaders);
		} catch (JSONException ex) {
			log.error("", ex);
			return new ErrorResponse(Error.JSON_EXCEPTION);
		} catch (IOException ex) {
			log.error("", ex);
			return new ErrorResponse(Error.INTERNAL_ERROR);
		}
	}

	@RequestMapping(value = "/ping", method = RequestMethod.GET)
	public Response ping() {
		try {
			return new OkResponse(new JSONObject()
					.put("response", "Hello world!")
					.put("time", new DateTime().toString()));
		} catch (JSONException ex) {
			log.error("", ex);
			return new ErrorResponse(Error.JSON_EXCEPTION);
		}
	}

	@RequestMapping(value = "/reload_cache", method = RequestMethod.GET)
	public Response reload_cache() {
		try {
			vars.reload();
			strings.reload();
			usersManager.clear();

			return new OkResponse(new JSONObject()
					.put("response", "cache reloaded")
					.put("time", new DateTime().toString()));
		} catch (JSONException ex) {
			log.error("", ex);
			return new ErrorResponse(Error.JSON_EXCEPTION);
		}
	}

}
