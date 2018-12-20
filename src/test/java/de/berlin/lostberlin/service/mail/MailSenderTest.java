package de.berlin.lostberlin.service.mail;

import com.sendgrid.Content;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class MailSenderTest {

    private String CONFIRMATION_MAIL_TEXT = "<h1>Hi, Sergey</h1 >\nYour order has been published among the guides, you've chosen. Follow the link to check the current status of your order: https://www.de.berlin.lostberlin.com/orders/123.";

    private String NOTIFICATION_MAIL_TEXT = "<h1>Hi, Lena</h1 >\nYour order has been taken by Ada Polkanova. Follow the link to check the current status of your order: https://www.de.berlin.lostberlin.com/orders/123.";

    @Autowired
    ConfirmationMailSender senderConfirm;

    @Autowired
    NotificationMailSender senderNotify;

    @Test
    public void testConfirmationMailSender() {
        Map<String, String> holders = new HashMap<>();
        holders.put("name", "Sergey");
        holders.put("orderNumber", "123");
        Params params = new Params("from@email", "to@email", "subject", holders);

        Content content = senderConfirm.getContent(params);

        assertNotNull(content.getValue());
        assertEquals(content.getValue(), CONFIRMATION_MAIL_TEXT);

    }

    @Test
    public void testNotificationMailSender() {
        Map<String, String> holders = new HashMap<>();
        holders.put("name", "Lena");
        holders.put("businessName", "Ada Polkanova");
        holders.put("orderNumber", "123");

        Params params = new Params("from@email", "to@email", "subject", holders);

        Content content = senderNotify.getContent(params);

        assertNotNull(content.getValue());
        assertEquals(content.getValue(), NOTIFICATION_MAIL_TEXT);
    }

}