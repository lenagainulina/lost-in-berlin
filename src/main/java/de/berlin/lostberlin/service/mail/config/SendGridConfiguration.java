package de.berlin.lostberlin.service.mail.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.sendgrid.SendGrid;

@Configuration
public class SendGridConfiguration {
	
	private static String API_KEY = "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";
	
	@Bean
	public SendGrid getSendGrid() {
		return new SendGrid(API_KEY);
	}

}
