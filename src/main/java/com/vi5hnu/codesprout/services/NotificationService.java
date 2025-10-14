package com.vi5hnu.codesprout.services;

import com.vi5hnu.codesprout.entity.user.OtpModel;
import com.vi5hnu.codesprout.events.authEvents.*;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import java.io.UnsupportedEncodingException;

@RequiredArgsConstructor
@Service
public class NotificationService {
    private final JavaMailSender javaMailSender;


    @Async("cpuBoundExecutor")
    public void passwordUpdateEmail(PasswordUpdateInit passwordUpdateInit) {
        try {
            sendPasswordUpdateEmail(passwordUpdateInit.getUser().getFirstName(),passwordUpdateInit.getUser().getEmail(),passwordUpdateInit.getOtp());
        } catch (MailException | MessagingException | UnsupportedEncodingException e) {
            System.out.print("Failed to send email :");
            System.out.println(e.getMessage());
        }
    }


    @Async("cpuBoundExecutor")
    public void registrationEmail(RegistrationVerificationEvent event) {
        //build verification url to be sent to user
        final String url = event.getVerificationUrl()+ "?token=" + event.getToken();
        //send the email
        try {
            sendVerificationEmail(url,event.getUserModel().getFirstName(),event.getUserModel().getLastName(),event.getUserModel().getEmail());
        } catch (MailException | MessagingException | UnsupportedEncodingException e) {
            System.out.print("Faild to send email :");
            System.out.println(e.getMessage());
        }
    }

    @Async("cpuBoundExecutor")
    public void passwordUpdateComplete(PasswordUpdateComplete event) {
        //send the email
        try {
            sendVerificationEmail(event.getUserModel().getFirstName(),event.getUserModel().getLastName(),event.getUserModel().getEmail());
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    @Async("cpuBoundExecutor")
    public void otpEmail(OtpEvent otpEvent) {
        //send the email
        try {
            sendOtpEmail(otpEvent.getName(),otpEvent.getEmail(), otpEvent.getOtp());
        } catch (MailException | MessagingException | UnsupportedEncodingException e) {
            System.out.print("Faild to send email :");
            System.out.println(e.getMessage());
        }
    }

    @Async("cpuBoundExecutor")
    public void alterEmail(AlertEvent event) {
        //send the email
        try {
            sendAlertEmail(event.getName(), event.getEmail(), event.getAlertMessage());
        } catch (MailException | MessagingException | UnsupportedEncodingException e) {
            System.out.print("Faild to send email :");//failed to send email
            System.out.println(e.getMessage());
        }
    }

    private void sendAlertEmail(String name,String userEmail,String alertMessage) throws MailException, MessagingException, UnsupportedEncodingException {
        String subject = "Code Sprout 🕉️ - Alert";
        String senderName = "Code Sprout 🕉️";
        String mailContent = "<p> Hi, "+ name+", </p>"+
                "<p>"+alertMessage+"</p>"+
                "<p> Thank you <br> Code Sprout 🕉️";
        MimeMessage message = javaMailSender.createMimeMessage();
        var messageHelper = new MimeMessageHelper(message);
        messageHelper.setFrom("kumarvishnu1619@gmail.com", senderName);
        messageHelper.setTo(userEmail);
        messageHelper.setSubject(subject);
        messageHelper.setText(mailContent, true);
        javaMailSender.send(message);
    }

    private void sendOtpEmail(String name,String userEmail,String otp) throws MailException, MessagingException, UnsupportedEncodingException {
        String subject = "Code Sprout 🕉️ - Otp";
        String senderName = "Code Sprout 🕉️";
        String mailContent = "<p> Hi, "+ name+", </p>"+
                "</p>Here is your Otp <strong>"+ otp +"</strong> .</p>"+
                "The otp is valid only for "+ OtpModel.EXPIRE_AFTER_MINS+" mins"+
                "<p> Thank you <br> Code Sprout 🕉️";
        MimeMessage message = javaMailSender.createMimeMessage();
        var messageHelper = new MimeMessageHelper(message);
        messageHelper.setFrom("kumarvishnu1619@gmail.com", senderName);
        messageHelper.setTo(userEmail);
        messageHelper.setSubject(subject);
        messageHelper.setText(mailContent, true);
        javaMailSender.send(message);
    }

    private void sendVerificationEmail(String firstName,String lastName,String userEmail) throws MessagingException, UnsupportedEncodingException {
        String subject = "Code Sprout 🕉️ - Security Alert";
        String senderName = "Code Sprout 🕉️";
        String mailContent = "<p> Hi, "+ firstName+" "+ lastName + ", </p>"+
                "<p>Your password has been changed.</p>"+
                "<p> Thank you for using Code Sprout 🕉️";
        MimeMessage message = javaMailSender.createMimeMessage();
        var messageHelper = new MimeMessageHelper(message);
        messageHelper.setFrom("kumarvishnu1619@gmail.com", senderName);
        messageHelper.setTo(userEmail);
        messageHelper.setSubject(subject);
        messageHelper.setText(mailContent, true);
        javaMailSender.send(message);
    }

    private void sendVerificationEmail(String url,String firstName,String lastName,String userEmail) throws MailException, MessagingException, UnsupportedEncodingException {
        String subject = "Code Sprout 🕉️ - Account Verification";
        String senderName = "Code Sprout 🕉️";
        String mailContent = "<p> Hi, "+ firstName+" "+ lastName + ", </p>"+
                "<p>Thank you for registering with Code Sprout 🕉️ ,"+"<br>" +
                "Please, follow the link below to complete your registration.</p>"+
                "<a href=\"" +url+ "\">Verify your email to activate your account</a>"+
                "<p> Thank you <br> Code Sprout 🕉️ User Registration";
        MimeMessage message = javaMailSender.createMimeMessage();
        var messageHelper = new MimeMessageHelper(message);
        messageHelper.setFrom("kumarvishnu1619@gmail.com", senderName);
        messageHelper.setTo(userEmail);
        messageHelper.setSubject(subject);
        messageHelper.setText(mailContent, true);
        javaMailSender.send(message);
    }

    private void sendPasswordUpdateEmail(String name, String userEmail, String otp) throws MailException, MessagingException, UnsupportedEncodingException {
        String subject = "Code Sprout 🕉️ - Otp";
        String senderName = "Code Sprout 🕉️";
        String mailContent = "<p> Hi, "+ name+", </p>"+
                "</p>Here is your Otp <strong>"+ otp +"</strong> .</p>"+
                "The otp is valid only for "+ OtpModel.EXPIRE_AFTER_MINS+" mins"+
                "<p> Thank you <br> Code Sprout 🕉️";
        MimeMessage message = javaMailSender.createMimeMessage();
        var messageHelper = new MimeMessageHelper(message);
        messageHelper.setFrom("kumarvishnu1619@gmail.com", senderName);
        messageHelper.setTo(userEmail);
        messageHelper.setSubject(subject);
        messageHelper.setText(mailContent, true);
        javaMailSender.send(message);
    }
}
