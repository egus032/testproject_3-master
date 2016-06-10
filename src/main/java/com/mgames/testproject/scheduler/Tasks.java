package com.mgames.testproject.scheduler;

import com.mgames.testproject.resolvers.EmailSenderResolver;
import com.mgames.testproject.Variables;
import com.mgames.testproject.dao.CommonsDao;
import com.mgames.utils.email.Email;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author den
 */
@Component
public class Tasks {

	private static final Logger log = LoggerFactory.getLogger(Tasks.class.getName());

	private static final SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyy");

	@Autowired
	private CommonsDao commonsDao;

	@Autowired
	private EmailSenderResolver emailSenderResolver;

	@Autowired
	private Variables variables;

	public void setCommonsDao(CommonsDao commonsDao) {
		this.commonsDao = commonsDao;
	}

	public void setVariables(Variables variables) {
		this.variables = variables;
	}

	public void sendLogbackEmailsJob() {
		List<String> emails = new ArrayList<>();
		emails.addAll(Arrays.asList(variables.LOGBACK_REPORT_EMAILS.split(",")));
		if (!emails.isEmpty()) {
			sendEmails(commonsDao.getLogbackPerDay(), emails);
		}
	}

	private void sendEmails(JSONObject logbackJSONObject, List<String> emails) {
		try {
			JSONArray errorsJSONArray = logbackJSONObject.getJSONArray("errors");
			if (errorsJSONArray.length() == 0) {
				return;
			}
			StringBuilder message = new StringBuilder("<h1 style=\"color: black;\">Отчет ошибок проекта '" + variables.HOST + "' за " + sdf.format(new Date(System.currentTimeMillis() - 1000 * 60 * 60 * 24)) + "</h1><br/>");
			message.append("<span style=\"color:black;\">");
			for (int i = 0; i < errorsJSONArray.length(); i++) {
				message.append("<p>")
						.append("EVENT_ID: ").append(errorsJSONArray.getJSONObject(i).getLong("event_id")).append("<br/>")
						.append("CALLER_CLASS: ").append(errorsJSONArray.getJSONObject(i).getString("caller_class")).append("<br/>")
						.append("CALLER_METHOD: ").append(errorsJSONArray.getJSONObject(i).getString("caller_method")).append("<br/>")
						.append("CALLER_LINE: ").append(errorsJSONArray.getJSONObject(i).getString("caller_line")).append("<br/>")
						.append("FORMATTED_MESSAGE: ").append(errorsJSONArray.getJSONObject(i).getString("formatted_message")).append("<br/>")
						.append("TIMESTMP: ").append(new Date(errorsJSONArray.getJSONObject(i).getLong("timestmp")).toString()).append("<br/>")
						.append("DESCRIPTION: ").append(errorsJSONArray.getJSONObject(i).getJSONArray("description").optString(0)).append("<br/>")
						.append("</p><br/><br/>");
			}
			message.append("</span>");

			emailSenderResolver.get().sendEmail(new Email.EmailBuilder("Logback report for "  + variables.HOST, "noreply@m-games-ltd.com").
					addRecipients(emails).withHtmlPart(message.toString()).build());
		} catch (JSONException ex) {
			log.error("", ex);
		}
	}

}
