package de.berlin.lostberlin.service.mail;

import org.springframework.stereotype.Component;

import com.sendgrid.Content;

@Component
public class NotificationMailSender extends MailSender {

	@Override
	public Content getContent(Params params) {
		
		return null;
	}
	
	@Override
	public MailTypes getSenderType() {
		return MailTypes.NOTIFICATION;
	}
}
