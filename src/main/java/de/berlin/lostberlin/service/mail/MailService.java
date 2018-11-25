package de.berlin.lostberlin.service.mail;

import de.berlin.lostberlin.model.business.persistence.Business;
import de.berlin.lostberlin.model.order.persistence.Order;
import de.berlin.lostberlin.model.order.client.OrderFullDao;
import de.berlin.lostberlin.repository.BusinessRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
@PropertySources({
        @PropertySource(value = "application.properties"),
        @PropertySource(value = "classpath:sendgrid.properties")
})
public class MailService {

    @Autowired
    private BusinessRepository businessRepo;

    @Value("${mail.from}")
    private String fromMail;

    public Optional<MailSenderResponse> sendConfirmationMail(Order order) {
        MailSender sender = MailSenderFactory.getMailSender(MailTypes.CONFIRMATION.name());
        Params params = createConfirmationMailParams(order);
        return sender.send(params);
    }

    public Optional<MailSenderResponse> sendNotificationMail(OrderFullDao order) {
        MailSender sender = MailSenderFactory.getMailSender(MailTypes.NOTIFICATION.name());
        Params params = createNotificationMailParams(order);
        return sender.send(params);
    }

    private Params createConfirmationMailParams(Order order) {
        String subject = "Lost in Berlin order Confirmation Mail";
        String toMail = order.getEMail();
        Map<String, String> map = new HashMap<>();
        map.put("name", order.getName());
        map.put("orderNumber", order.getOrderNr());

        return new Params(fromMail, toMail, subject, map);
    }

    private Params createNotificationMailParams(OrderFullDao order) {
        Business business;
        Optional result = businessRepo.findById(order.getBusinessId());

        business = (Business) result.get();

        String subject = "Lost in Berlin order Notification Mail";
        String toMail = order.getEMail();
        Map<String, String> map = new HashMap<>();
        map.put("name", order.getName());
        map.put("businessName", business.getFName() + " " + business.getLName());
        map.put("orderNumber", order.getOrderNr());

        return new Params(fromMail, toMail, subject, map);
    }
}