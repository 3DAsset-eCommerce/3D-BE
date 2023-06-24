package com.phoenix.assetbe.core.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import javax.net.ssl.SSLSocketFactory;
import java.util.Properties;

@Configuration
@RequiredArgsConstructor
public class MyMailConfig {

    @Value("${MAIL_FROM_ADDRESS}")
    private String username;

    @Value("${MAIL_PASSWORD}")
    private String password;

    @Bean
    public JavaMailSender javaMailService() throws Exception {
        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
        javaMailSender.setHost("smtp.gmail.com");
        javaMailSender.setUsername(username);
        javaMailSender.setPassword(password);
        javaMailSender.setPort(465);

        Properties javaMailProperties = new Properties();

        javaMailProperties.put("mail.smtp.auth", true);
        javaMailProperties.put("mail.smtp.host", "smtp.gmail.com");
        javaMailProperties.put("mail.smtp.starttls.enable", true);
        javaMailProperties.put("mail.smtp.ssl.enable", true);
        javaMailProperties.put("mail.smtp.ssl.socketFactory", SSLSocketFactory.getDefault());

        javaMailProperties.put("mail.smtp.socketFactory.fallback", false);
        javaMailProperties.put("mail.smtp.socketFactory.port", 465);
        javaMailProperties.put("mail.smtp.port", 465);

        javaMailSender.setJavaMailProperties(javaMailProperties);
        javaMailSender.setDefaultEncoding("UTF-8");

        return javaMailSender;
    }
}