package de.berlin.lostberlin.service.mail;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import de.berlin.lostberlin.model.Order;

@Component
public class MailService {
	
	@Value("mail.from")
	private String fromMail;
	
	public Optional<MailSenderResponse> sendConfirmationMail(Order order) {
		MailSender sender = MailSenderFactory.getMailSender(MailTypes.CONFIRMATION.name());
		Params params = createConfirmationMailParams(order);
		return sender.send(params);
	}
	
	public void sendNotificationMail(Order order) {
		//TO DO
	}
	
	private Params createConfirmationMailParams(Order order) {
		String subject = "Lost in Berlin Order Confirmation Mail";
		String toMail = order.getEmail();
		Map<String, String> map = new HashMap<>();
		map.put("name", order.getName());
		map.put("orderNumber", order.getOrderNr());
		
		return new Params(fromMail, toMail, subject, map);
	}

}
