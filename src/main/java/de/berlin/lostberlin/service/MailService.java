package de.berlin.lostberlin.service;

import com.sendgrid.*;
import de.berlin.lostberlin.model.Business;
import de.berlin.lostberlin.model.Order;
import de.berlin.lostberlin.repository.BusinessRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

public class MailService {
    @Autowired
    private static BusinessRepository businessRepo;

    public static Response sendEmail(Order order, MailTypes mailType) throws IOException, SQLException {
        Email from = new Email("aoneko@gmx.com");
        String subject = "";
        Email to = new Email(order.getEmail());
        Content content = null;
        if (mailType == MailTypes.CONFIRMATION) {
            subject = "Order confirmation";
            content = new Content("text/html", "<h1>Hi, " + order.getName() + "</h1 > \n" +
                    "Your order has been published among the guides, you've chosen." +
                    "Follow the link to check the current status of your order:" +
                    "https://www.lost-in-berlin.com/orders/" + order.getOrderNr() + ".");
        } else if (mailType == MailTypes.NOTIFICATION) {
            Optional result = businessRepo.findById(order.getBusinessId());
            if (result.isPresent()) {
                Business business = (Business) result.get();
                subject = "Order status notofication";
                content = new Content("text/html", "<h1>Hi, " + order.getName() + "</h1 > \n" +
                        "Your order has been taken by " + business.getlName() + business.getlName() +
                        "Follow the link to check the current status of your order:" +
                        "https://www.lost-in-berlin.com/orders/" + order.getOrderNr() + ".");
            } else {
                throw new SQLException("Business is not found" + order.getBusinessId());
            }
        }
        Mail mail = new Mail(from, subject, to, content);

        SendGrid sg = new SendGrid("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");
        Request request = new Request();

        request.setMethod(Method.POST);
        request.setEndpoint("mail/send");
        request.setBody(mail.build());
        return sg.api(request);
    }

    public enum MailTypes {
        CONFIRMATION,
        NOTIFICATION;
    }
}
