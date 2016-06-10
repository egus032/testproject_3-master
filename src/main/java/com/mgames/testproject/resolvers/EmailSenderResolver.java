package com.mgames.testproject.resolvers;

import com.mgames.testproject.Variables;
import com.mgames.utils.email.IEmailSender;
import com.mgames.utils.email.MailgunApi;
import com.mgames.utils.email.SmtpSender;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Constantine Tretyak
 */
@Component
public class EmailSenderResolver {

	private MailgunApi mailgunApi;

	private SmtpSender smtpSender;

	@Autowired
	private Variables vars;

	public IEmailSender get() {
		if (vars.USE_MAILGUN) {
			if (mailgunApi == null) {
				mailgunApi = new MailgunApi(vars.MAILGUN_DOMAIN, vars.MAILGUN_APIKEY);
			}
			return mailgunApi;
		} else {
			if (smtpSender == null) {
				smtpSender = new SmtpSender(vars.SMTP_HOST, vars.SMTP_PORT, vars.SMTP_USER, vars.SMTP_PASSWORD, vars.SMTP_SSL);
			}
			return smtpSender;
		}
	}

	@PostConstruct
	private void postConstruct() {
		vars.addChangeVariableListener((String name, Object oldValue, Object newValue) -> {
			switch (name) {
				case "MAILGUN_DOMAIN":
				case "MAILGUN_APIKEY":
					mailgunApi = null;
					break;
				case "SMTP_HOST":
				case "SMTP_PORT":
				case "SMTP_USER":
				case "SMTP_PASSWORD":
				case "SMTP_SSL":
					smtpSender = null;
					break;
			}
		});
	}

}
