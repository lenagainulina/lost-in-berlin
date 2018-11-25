package de.berlin.lostberlin.service.mail;

import java.util.Map;

public class Params {

    private String toMail;
    private String fromMail;
    private String subject;

    private Map<String, String> placeholderValues;

    public Params(String fromMail, String toMail, String subject, Map<String, String> placeholders) {
        this.fromMail = fromMail;
        this.toMail = toMail;
        this.subject = subject;
        this.placeholderValues = placeholders;
    }

    public String getSubject() {
        return subject;
    }

    public String getToEmail() {
        return toMail;
    }

    public String getFromMail() {
        return fromMail;
    }

    public Map<String, String> getPlaceholderValues() {
        return placeholderValues;
    }

}