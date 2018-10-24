package de.berlin.lostberlin.service.mail;

import java.util.HashMap;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;

import de.berlin.lostberlin.model.Order;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MailServiceTest {
	
	@MockBean
	SendGrid grid;
	
	@Autowired
	MailService service;
	
	
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
	public void testConfirmationMailNotSent() {
		
	}
	
	private Order createTestOrder() {
		Order order = new Order();
		order.setEmail("test@server");
		order.setName("Customer Name");
		order.setOrderNr("12123");
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
