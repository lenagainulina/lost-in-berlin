package de.berlin.lostberlin.service.mail;

import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import de.berlin.lostberlin.model.business.persistence.Business;
import de.berlin.lostberlin.model.order.client.OrderFullDao;
import de.berlin.lostberlin.model.order.persistence.Order;
import de.berlin.lostberlin.model.order.persistence.StatusTypes;
import de.berlin.lostberlin.repository.BusinessRepository;
import de.berlin.lostberlin.service.BusinessService;
import de.berlin.lostberlin.service.BusinessServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class MailServiceTest {

    @Autowired
    private MailService service;

    @MockBean
    private SendGrid grid;

    @MockBean
    private BusinessRepository businessRepository;

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
        Business business = mockBusiness();

        when(businessRepository.findById(order.getBusinessId())).thenReturn(Optional.ofNullable(business));
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
    private Business mockBusiness() {
        Business business = new Business();
        business.setFName("Ada");
        business.setLName("Polkanova");
        business.setEMail("dogsledging@doggy-berlin.de");
        business.setPhone("017668558497");
        business.setDescription("Hi, I'm Ada, the dog");
        business.setServiceLocation("Berlin");
        business.setUsername("polkaner");
        business.setPassword("bones");
        business.setPhoto(null);
        return business;
    }
}