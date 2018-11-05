package de.berlin.lostberlin.service.mail;

import java.util.Map;

public class MailSenderResponse {

	private int statusCode;
	private String body;
	private Map<String, String> headers;

	public MailSenderResponse(int statusCode, String body, Map<String, String> headers) {
		this.statusCode = statusCode;
		this.body = body;
		this.headers = headers;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public String getBody() {
		return body;
	}

	public Map<String, String> getHeaders() {
		return headers;
	}

	@Override
	public String toString() {
		return "MailSenderResponse [statusCode=" + statusCode + ", body=" + body + ", headers=" + headers + "]";
	}
	
	
}
