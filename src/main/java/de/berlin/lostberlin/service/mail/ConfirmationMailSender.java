package de.berlin.lostberlin.service.mail;

import com.sendgrid.Content;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@PropertySources({
        @PropertySource(value = "application.properties"),
        @PropertySource(value = "classpath:sendgrid.properties")
})
public class ConfirmationMailSender extends MailSender {

    @Value("${mail.template.confirmation}")
    private String confirmationMailTemplate;

    @Override
    public Content getContent(Params params) {
        return new Content(CONTENT_TYPE, populateContent(params.getPlaceholderValues()));
    }

    private String populateContent(Map<String, String> placeholders) {
        return StrSubstitutor.replace(confirmationMailTemplate, placeholders, "{", "}");
    }

    public MailTypes getSenderType() {
        return MailTypes.CONFIRMATION;
    }
}