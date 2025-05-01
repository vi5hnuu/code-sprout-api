package com.vi5hnu.codesprout.events.listeners;

import com.vi5hnu.codesprout.entity.user.OtpModel;
import com.vi5hnu.codesprout.events.authEvents.OtpEvent;
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
public class OtpEventListener implements ApplicationListener<OtpEvent> {
    private final JavaMailSender javaMailSender;

    @Override
    public void onApplicationEvent(OtpEvent otpEvent) {
        //send the email
        try {
            sendOtpEmail(otpEvent.getName(),otpEvent.getEmail(), otpEvent.getOtp());
        } catch (MailException | MessagingException | UnsupportedEncodingException e) {
            System.out.print("Faild to send email :");
            System.out.println(e.getMessage());
        }
    }

    public void sendOtpEmail(String name,String userEmail,String otp) throws MailException, MessagingException, UnsupportedEncodingException {
        String subject = "Silent Whisper 🤫 - Otp";
        String senderName = "Silent Whisper 🤫";
        String mailContent = "<p> Hi, "+ name+", </p>"+
                "</p>Here is your Otp <strong>"+ otp +"</strong> .</p>"+
                "The otp is valid only for "+ OtpModel.EXPIRE_AFTER_MINS+" mins"+
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
