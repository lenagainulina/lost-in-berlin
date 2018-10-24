package de.berlin.lostberlin.service.mail.exception;

public class EmailTypeNotFoundException extends RuntimeException {
	
	public EmailTypeNotFoundException (String message) {
		super(message);
	}

}
