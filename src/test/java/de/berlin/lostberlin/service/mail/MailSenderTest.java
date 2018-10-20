package de.berlin.lostberlin.service.mail;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.sendgrid.Content;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MailSenderTest {
	
	private String CONFIRMATION_MAIL_TEXT = "<h1>Hi, Sergey</h1 >\nYour order has been published among the guides, you've chosen. Follow the link to check the current status of your order: https://www.lost-in-berlin.com/orders/123.";
	
	@Autowired
	ConfirmationMailSender sender;
	
	@Test
	public void testConfirmationMailSender() {
		Map<String, String> holders = new HashMap<>();
		holders.put("name", "Sergey");
		holders.put("orderNumber", "123");
		
		Params params = new Params("from@email", "to@email", "subject", holders);

		Content content = sender.getContent(params);
		
		assertNotNull(content.getValue());
		assertEquals(content.getValue(), CONFIRMATION_MAIL_TEXT);
		
	}

}
