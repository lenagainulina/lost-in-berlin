package de.berlin.lostberlin.service.mail;

import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.text.StrSubstitutor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import com.sendgrid.Content;

@Component
@PropertySource(value = "application.properties")
public class ConfirmationMailSender extends MailSender {
	
	@Value("${mail.template.confirmation}")
	private String confirmationMailTemplate;

	@Override
	public Content getContent(Params params) {
		return new Content(CONTENT_TYPE, populateContent(params.getPlaceholderValues()));
	}
	
	private String populateContent(Map<String, String> placeholders) {
		return StrSubstitutor.replace(confirmationMailTemplate, placeholders,"{", "}");
	}
	
	public MailTypes getSenderType() {
		return MailTypes.CONFIRMATION;
	}
}