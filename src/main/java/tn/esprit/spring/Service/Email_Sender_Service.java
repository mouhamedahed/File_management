package tn.esprit.spring.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service("emailSenderService")
public class Email_Sender_Service {

	 private JavaMailSender javaMailSender;

	    @Autowired
	    public Email_Sender_Service(JavaMailSender javaMailSender) {
	        this.javaMailSender = javaMailSender;
	    }

	    @Async
	    public void sendEmail(SimpleMailMessage email) {
	        javaMailSender.send(email);
	    }
}
