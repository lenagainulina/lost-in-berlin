package de.berlin.lostberlin.service.mail.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.sendgrid.SendGrid;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:sendgrid.properties")
public class SendGridConfiguration {

	@Value("${spring.sendgrid.api-key}")
	private String API_KEY;

	@Bean
	public SendGrid getSendGrid() {
		return new SendGrid(API_KEY);
	}
}
