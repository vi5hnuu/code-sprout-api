package com.vi5hnu.codesprout.events.listeners;

import com.vi5hnu.codesprout.events.authEvents.AlertEvent;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;

@Slf4j
@Component
@RequiredArgsConstructor
public class AlertEventListener implements ApplicationListener<AlertEvent> {
    private final JavaMailSender javaMailSender;
    @Override
    public void onApplicationEvent(AlertEvent event) {
        //send the email
        try {
            sendVerificationEmail(event.getName(), event.getEmail(), event.getAlertMessage());
        } catch (MailException | MessagingException | UnsupportedEncodingException e) {
            System.out.print("Faild to send email :");//failed to send email
            System.out.println(e.getMessage());
        }
    }

    public void sendVerificationEmail(String name,String userEmail,String alertMessage) throws MailException, MessagingException, UnsupportedEncodingException {
        String subject = "Silent Whisper 🤫 - Alert";
        String senderName = "Silent Whisper 🤫";
        String mailContent = "<p> Hi, "+ name+", </p>"+
                "<p>"+alertMessage+"</p>"+
                "<p> Thank you <br> Silent Whisper 🤫";
        MimeMessage message = javaMailSender.createMimeMessage();
        var messageHelper = new MimeMessageHelper(message);
        messageHelper.setFrom("kumarvishnu1619@gmail.com", senderName);
        messageHelper.setTo(userEmail);
        messageHelper.setSubject(subject);
        messageHelper.setText(mailContent, true);
        javaMailSender.send(message);
    }
}
