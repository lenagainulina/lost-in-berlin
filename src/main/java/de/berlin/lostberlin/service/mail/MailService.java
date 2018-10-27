package de.berlin.lostberlin.service.mail;

import de.berlin.lostberlin.model.Business;
import de.berlin.lostberlin.model.Order;
import de.berlin.lostberlin.repository.BusinessRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
@PropertySource(value = "application.properties")
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

    public Optional<MailSenderResponse> sendNotificationMail(Order order) {
        MailSender sender = MailSenderFactory.getMailSender(MailTypes.NOTIFICATION.name());
        Params params = createNotificationMailParams(order);
        return sender.send(params);
    }

    private Params createConfirmationMailParams(Order order) {
        String subject = "Lost in Berlin Order Confirmation Mail";
        String toMail = order.getEmail();
        Map<String, String> map = new HashMap<>();
        map.put("name", order.getName());
        map.put("orderNumber", order.getOrderNr());

        return new Params(fromMail, toMail, subject, map);
    }

    private Params createNotificationMailParams(Order order) {
        Business business = null;
        Optional result = businessRepo.findById(order.getBusinessId());
        if (result.isPresent()) try {
            business = (Business) result.get();
        } catch (Exception e) {
            e.getMessage();
        }

        String subject = "Lost in Berlin Order Notification Mail";
        String toMail = order.getEmail();
        Map<String, String> map = new HashMap<>();
        map.put("name", order.getName());
        map.put("businessName", business.getfName() + " " + business.getlName());
        map.put("orderNumber", order.getOrderNr());

        return new Params(fromMail, toMail, subject, map);
    }
}

