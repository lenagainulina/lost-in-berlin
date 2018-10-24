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
