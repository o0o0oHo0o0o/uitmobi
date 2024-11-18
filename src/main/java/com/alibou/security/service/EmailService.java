package com.alibou.security.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Service
public class EmailService {

    @Value("${spring.mail.sender.email}")
    private String fromEmail;

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    private final Map<String, String> verificationCodes = new ConcurrentHashMap<>();
    private final Map<String, Long> verificationTimestamps = new ConcurrentHashMap<>();
    private static final long CODE_VALIDITY_DURATION = TimeUnit.MINUTES.toMillis(5); // TTL: 5 phút

    public JavaMailSender getJavaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);
        mailSender.setUsername("1tihuygay@gmail.com");
        mailSender.setPassword("kepk uyox btrk dizo");

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.debug", "true");

        return mailSender;
    }

    public boolean sendVerificationEmail(String toEmail) {
        long currentTime = System.currentTimeMillis();

        // Nếu mã đã được gửi và còn hiệu lực, từ chối gửi lại
        if (verificationTimestamps.containsKey(toEmail) &&
                currentTime - verificationTimestamps.get(toEmail) < CODE_VALIDITY_DURATION) {
            return false;
        }

        String verificationCode = generateVerificationCode();
        verificationCodes.put(toEmail, verificationCode);
        verificationTimestamps.put(toEmail, currentTime);

        try {
            MimeMessage message = getJavaMailSender().createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("Email Verification Code");
            helper.setText("Your verification code is: " + verificationCode);

            getJavaMailSender().send(message);
            return true;
        } catch (MessagingException e) {
            logger.error("Error sending email: ", e);
            return false;
        }
    }

    public boolean isCodeAlreadySent(String email) {
        long currentTime = System.currentTimeMillis();
        return verificationTimestamps.containsKey(email) &&
                currentTime - verificationTimestamps.get(email) < CODE_VALIDITY_DURATION;
    }

    public long getRemainingTTL(String email) {
        if (!verificationTimestamps.containsKey(email)) {
            return 0;
        }
        long elapsed = System.currentTimeMillis() - verificationTimestamps.get(email);
        return CODE_VALIDITY_DURATION - elapsed;
    }

    public boolean verifyCode(String email, String code) {
        return verificationCodes.containsKey(email) && verificationCodes.get(email).equals(code);
    }

    private String generateVerificationCode() {
        Random random = new Random();
        return String.valueOf(100000 + random.nextInt(900000));
    }

    public boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email != null && email.matches(emailRegex);
    }
}
