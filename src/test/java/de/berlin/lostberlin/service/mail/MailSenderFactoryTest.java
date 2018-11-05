package de.berlin.lostberlin.service.mail;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

import de.berlin.lostberlin.service.mail.exception.EmailTypeNotFoundException;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MailSenderFactoryTest {
		
	@Test
	public void testConfirmationMailSender() {
		MailSender mailSender = MailSenderFactory.getMailSender(MailTypes.CONFIRMATION.name());
		assertTrue(mailSender instanceof ConfirmationMailSender);
	}

	@Test
	public void testNotificationMailSender() {
		MailSender mailSender = MailSenderFactory.getMailSender(MailTypes.NOTIFICATION.name());
		assertTrue(mailSender instanceof NotificationMailSender);
	}
	
	@Test(expected = EmailTypeNotFoundException.class)
	public void testNoMailTypeFound() {
		MailSenderFactory.getMailSender("Non existing mail type");
	}

}
