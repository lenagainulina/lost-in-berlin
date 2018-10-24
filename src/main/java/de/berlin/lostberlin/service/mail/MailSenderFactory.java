package de.berlin.lostberlin.service.mail;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.berlin.lostberlin.service.mail.exception.EmailTypeNotFoundException;

@Service
public class MailSenderFactory {
	
    @Autowired
    private List<MailSender> senders;
    
    private static final Map<String, MailSender> sendersCache = new HashMap<>();
    
    @PostConstruct
    public void initMailSenders() {
    	for(MailSender sender: senders) {
    		sendersCache.put(sender.getSenderType().toString(), sender);
    	}
    }
	
	public static MailSender getMailSender(String type) {
		MailSender mailSender = sendersCache.get(type);
		if (mailSender == null) {
			throw new EmailTypeNotFoundException (String.format("Email type %s", type));
		}
		return mailSender;
	}

}
