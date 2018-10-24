package de.berlin.lostberlin.service.mail;

import java.io.IOException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.sendgrid.Content;
import com.sendgrid.Email;
import com.sendgrid.Mail;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;

public abstract class MailSender {

	protected static String CONTENT_TYPE = "text/html";
	
	@Autowired
	SendGrid sendGrid;
	
	@Value("mail.from")
	private String from;
	
	public Optional<MailSenderResponse> send(Params  params) {
		Mail mail = new Mail(new Email(from),
				params.getSubject(), 
				new Email(params.getToEmail()), 
				getContent(params));

		return createResponse(mail);
	}
	
	private Optional<MailSenderResponse> createResponse(Mail mail) {
		try {
			Request request = new Request();
			request.setMethod(Method.POST);
			request.setEndpoint("mail/send");
			request.setBody(mail.build());
			return Optional.of(createMailSenderResponse(sendGrid.api(request)));
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return Optional.empty();
	}
	
	private MailSenderResponse createMailSenderResponse(Response response) {
		return new MailSenderResponse(response.getStatusCode(), response.getBody(), response.getHeaders());
	}
	
	public abstract Content getContent(Params params);
	
	public abstract MailTypes getSenderType();

}
