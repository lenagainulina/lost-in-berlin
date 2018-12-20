package de.berlin.lostberlin.service.mail;

import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import de.berlin.lostberlin.model.order.client.OrderFullDao;
import de.berlin.lostberlin.model.order.persistence.Order;
import de.berlin.lostberlin.model.order.persistence.StatusTypes;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class MailServiceTest {

    @MockBean
    private SendGrid grid;

    @Autowired
    private MailService service;


    @Test
    public void testConfirmationMailSent() throws Exception {
        Order order = createTestOrder();


        when(grid.api(Mockito.any(Request.class))).thenReturn(createTestResponse());

        Optional<MailSenderResponse> senderResponse = service.sendConfirmationMail(order);
        assertTrue(senderResponse.isPresent());
        assertEquals(123, senderResponse.get().getStatusCode());
        assertEquals("TestBody", senderResponse.get().getBody());
    }

    @Test
    public void testNotificationMailSent() throws Exception {
        OrderFullDao order = createTestOrderUpdate();

        when(grid.api(Mockito.any(Request.class))).thenReturn(createTestResponse());

        Optional<MailSenderResponse> senderResponse = service.sendNotificationMail(order);
        assertTrue(senderResponse.isPresent());
        assertEquals(123, senderResponse.get().getStatusCode());
        assertEquals("TestBody", senderResponse.get().getBody());
    }

    private Order createTestOrder() {
        Order order = new Order();
        order.setEMail("test@server");
        order.setName("Customer Name");
        order.setOrderNr("12123");
        return order;
    }

    private OrderFullDao createTestOrderUpdate() {
        OrderFullDao order = new OrderFullDao();
        order.setEMail("test@server");
        order.setName("Customer Name");
        order.setOrderNr("12123");
        order.setBusinessId(1L);
        order.setStatus(StatusTypes.CONFIRMED);
        return order;
    }

    private Response createTestResponse() {
        Response response = new Response();
        response.setBody("TestBody");
        response.setStatusCode(123);
        response.setHeaders(new HashMap<>());
        return response;
    }

}